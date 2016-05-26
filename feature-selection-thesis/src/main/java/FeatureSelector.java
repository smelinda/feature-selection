import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;
import org.jblas.ranges.IndicesRange;
import org.jblas.ranges.IntervalRange;
import scala.Tuple2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FeatureSelector
{
	private static final String AD = "ad.";
	private static final String NON_AD = "nonad.";
	private static int numberOfFeatures = 0;
	private static int numberOfInstances = 0;
	private static int ad = 0;
	private static int nonad = 0;
	private static double yAd[] = new double[2];
	private static double yNonAd[] = new double[2];
	private static int startIndex = 0;
	private static JavaSparkContext sc;

	public static void main(String args[]) throws Exception
	{

		SparkConf conf = new SparkConf().setAppName("Feature Selector");
		sc = new JavaSparkContext(conf);

		// Read input data set
		JavaRDD<String> logData = sc.textFile(Config.pathToTrainingSet()).cache();
		countClasses(logData);

		printStats();

		DoubleMatrix score = computeFeatureScores(logData);

		System.out.println(getBestFeatures(score, logData));

		// centralize features to have zero mean

		// generate response matrix Y (refer to formula 4)

		//while(true){
			//JavaPairRDD<String, Integer> pairs = logData.map();
		//}

	}

	private static Set<Integer> getBestFeatures(DoubleMatrix score, JavaRDD<String> logData){
		Set<Integer> set = new HashSet<>();
		int maxIndex = score.argmax(), k = 10, l = 1;
		set.add(maxIndex);

		//System.out.println("Max Index: " + maxIndex);

		Broadcast broadcastIdx = sc.broadcast(maxIndex);

		while(l < k){
			JavaRDD<DoubleMatrix> ci = logData.map(s -> {
				String cells[] = s.split(",");
				double features[] = getFeatures(cells);

				DoubleMatrix x = new DoubleMatrix(features).transpose();
				DoubleMatrix f = new DoubleMatrix(new double[]{x.get(0, (int)broadcastIdx.value())});
				DoubleMatrix c = x.mmul(f);

				return c;
			});

			DoubleMatrix c = ci.reduce((a,b) -> a.add(b));

//			Range lRange = new IntervalRange(0, l);
//
//			DoubleMatrix cOfSelectedFeatures = c.get(lRange, lRange);



			//temp.reduce((a,b) -> a);

			int selectedIndexes[] = new int[set.size()];
			int unSelectedIndexes[] = new int[c.getColumns() - selectedIndexes.length];
			boolean sign[] = new boolean[c.getColumns()];
			int i = 0;

			for(Integer idx : set){
				selectedIndexes[i] = idx;
				sign[i] = true;

				i++;

				//System.out.println(idx);
			}

			int j = 0;

			for(i = 0; i < sign.length; i++){
				if(sign[i] == false){
					sign[i] = true;
					unSelectedIndexes[j++] = i;
				}
			}

			//System.out.println("unselected index: " + c.getRows() + " " + c.getColumns());
			DoubleMatrix s = getNextScore(selectedIndexes, unSelectedIndexes, logData);
			maxIndex = s.argmax();
			set.add(maxIndex);
			l++;

			System.out.println(maxIndex + " " + l);
		}

		return set;
	}

	private static int getMaxFeatureScoreIndex(DoubleMatrix score){
		double max = 0;
		int idx = 0;

		for(int i = 0; i < score.getColumns(); i++){
			if(score.get(0, i) > max){
				max = score.get(0, i);
				idx = i;
			}
		}

		return idx;
	}

	private static DoubleMatrix getNextScore(int selectedIndexes[], int unselectedIndexes[], JavaRDD<String> logData){
		Broadcast broadcastSelectedIndexes = sc.broadcast(selectedIndexes);
		Broadcast broadcastUnselectedIndexes = sc.broadcast(unselectedIndexes);

		JavaRDD<FeatureMatrices> temp = logData.map(s -> {
			String cells[] = s.split(",");
			double features[] = getFeatures(cells);

			DoubleMatrix x = new DoubleMatrix(features).transpose();
			DoubleMatrix x1 = x.get(new IntervalRange(0, x.getRows()), new IndicesRange((int[])broadcastSelectedIndexes.getValue()));
			DoubleMatrix x2 = x.get(new IntervalRange(0, x.getRows()), new IndicesRange((int[])broadcastUnselectedIndexes.getValue()));

//			System.out.println("Dimension x1: " + x1.getRows() + " " + x1.getColumns());
//			System.out.println("Dimension x2: " + x2.getRows() + " " + x2.getColumns());
			DoubleMatrix y;
			DoubleMatrix ones;

			if(cells[cells.length - 1].equals(AD)){
				y = new DoubleMatrix(1, 2, yAd);
			} else{
				y = new DoubleMatrix(1, 2, yNonAd);
			}


			ones = DoubleMatrix.ones(x.getRows());

			DoubleMatrix matrixA = x1.transpose().mmul(x1);
			DoubleMatrix matrixCY1 = y.transpose().mmul(x1);
			DoubleMatrix matrixCY2 = y.transpose().mmul(x2);
			DoubleMatrix matrixC12 = x1.transpose().mmul(x2);
			DoubleMatrix matrixV2 = ones.transpose().mmul(x2.mul(x2));

//			System.out.println(matrixA.getRows() + " " + matrixA.getColumns());
//			System.out.println(matrixCY1.getRows() + " " + matrixCY1.getColumns());
//			System.out.println(matrixCY2.getRows() + " " + matrixCY2.getColumns());
//			System.out.println(matrixC12.getRows() + " " + matrixC12.getColumns());
//			System.out.println(matrixV2.getRows() + " " + matrixV2.getColumns());

			return new FeatureMatrices(matrixA, matrixCY1, matrixCY2, matrixC12, matrixV2);
		});

		FeatureMatrices featureMatrices = temp.reduce((a, b) -> a.add(b));
		DoubleMatrix matrixB = Solve.pinv(featureMatrices.getMatrixA()).mmul(featureMatrices.getMatrixC12());
		DoubleMatrix matrixH = featureMatrices.getMatrixCY1().mmul(matrixB);
		DoubleMatrix matrixG = featureMatrices.getMatrixCY2().sub(matrixH);

		DoubleMatrix g = DoubleMatrix.ones(matrixG.getRows()).transpose().mmul(matrixG.mul(matrixG));
		DoubleMatrix w = featureMatrices.getMatrixV2().sub(DoubleMatrix.ones(featureMatrices.getMatrixC12().getRows()).transpose().mmul(featureMatrices.getMatrixC12().mul(matrixB)));

//		System.out.println(g.getRows() + " " + g.getColumns());
//		System.out.println(w.getRows() + " " + w.getColumns());
		DoubleMatrix s = g.divi(w);

		//System.out.println(matrixG.getRows() + " " + matrixG.getColumns());

		return s;
	}


	/**
	 * Count number of instances in each class and compute the values for response matrix.
	 * @param logData input data
     */
	private static void countClasses(JavaRDD<String> logData)
	{
		// map values in class column into pair of <class, 1>
		JavaPairRDD<String, Integer> pairs = logData.mapToPair(s -> {
			String temp[] = s.split(",");
			return new Tuple2<>(temp[temp.length - 1], 1);
		});

		JavaPairRDD<String, Integer> counts = pairs.reduceByKey((a, b) -> a + b);
		List<Tuple2<String, Integer>> list = counts.collect();

		// specific for ad/nonad classes
		if(list.get(0)._1().equals(AD)){
			ad = list.get(0)._2();
			nonad = list.get(1)._2();
		} else{
			ad = list.get(1)._2();
			nonad = list.get(0)._2();
		}

		// as formula (4) in the paper.
		numberOfInstances = ad + nonad;

		yAd[1] = - Math.sqrt(ad) / numberOfInstances;
		yAd[0] = 1.0 / Math.sqrt(ad) + yAd[1];

		yNonAd[1] = - Math.sqrt(nonad) / numberOfInstances;
		yNonAd[0] = 1.0 / Math.sqrt(nonad) + yNonAd[1];

	}


	private static DoubleMatrix computeFeatureScores(JavaRDD<String> logData)
	{
		JavaRDD<FeatureScore> fScoreMatrix = logData.map(s -> {
			String cells[] = s.split(",");
			double features[] = getFeatures(cells);

			DoubleMatrix x = new DoubleMatrix(1, features.length, features);
			DoubleMatrix y;
			DoubleMatrix ones;

			if(cells[cells.length - 1].equals(AD)){
				y = new DoubleMatrix(1, 2, yAd);
			} else{
				y = new DoubleMatrix(1, 2, yNonAd);
			}

			ones = DoubleMatrix.ones(x.getRows());
			return new FeatureScore(y.transpose().mmul(x), ones.transpose().mmul(x.mul(x)));
		});

		FeatureScore totalScore = fScoreMatrix.reduce((a, b) -> a.add(b));
		numberOfFeatures = totalScore.getEMatrix().columns;

		DoubleMatrix e = totalScore.getEMatrix();
		DoubleMatrix v = totalScore.getVMatrix();
		DoubleMatrix s;
		s = DoubleMatrix.ones(e.getRows()).transpose().mmul(e.mul(e));

//		System.out.println("Dimension E: " + e.getRows() + " x " + e.getColumns());
//		System.out.println("Dimension v: " + v.getRows() + " x " + v.getColumns());
//		System.out.println("Dimension ones: " + DoubleMatrix.ones(10).getRows() + " x " + DoubleMatrix.ones(10).getColumns());
//		System.out.println("s before division: " + s.get(s.columns+1));
//		System.out.println("V: " + v.toString());
//		System.out.println("#rows of v:" + v.rows);
//		System.out.println("#columns of v:" + v.columns);

		//@TODO check if this is element-wise division
		s = s.diviRowVector(v);

//		System.out.println("s after division: " + s.get(s.columns+1));
		System.out.println("#rows of s:" + s.rows);
		System.out.println("#columns of s:" + s.columns);

		return s;
	}

	private static double[] getFeatures(String cells[]){
		double features[] = new double[cells.length - 1];
		for(int i = 0; i < features.length; i++){
			//@TODO how to treat missing values
			if(cells[i].trim().equals("?"))
				cells[i] = "0";

			features[i] = Double.parseDouble(cells[i]);
		}

		return features;
	}

	public static void printStats()
	{
		System.out.println("ad:" + ad);
		System.out.println("non ad:" + nonad);
		System.out.println("# instances:" + numberOfInstances);
		System.out.println("yAd:" + yAd[0] + "," + yAd[1]);
		System.out.println("yNonAd:" + yNonAd[0] +"," + yNonAd[1]);
	}


}
