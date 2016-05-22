import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.jblas.DoubleMatrix;
import scala.Tuple2;

import java.util.List;

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


	public static void main(String args[]) throws Exception
	{

		SparkConf conf = new SparkConf().setAppName("Feature Selector");
		JavaSparkContext sc = new JavaSparkContext(conf);

		// Read input data set
		JavaRDD<String> logData = sc.textFile(Config.pathToTrainingSet()).cache();
		countClasses(logData);

		printStats();

		computeFeatureScores(logData);

		// centralize features to have zero mean

		// generate response matrix Y (refer to formula 4)

		//while(true){
			//JavaPairRDD<String, Integer> pairs = logData.map();
		//}

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


	private static void computeFeatureScores(JavaRDD<String> logData)
	{
		JavaRDD<FeatureScore> fScoreMatrix = logData.map(s -> {
			String cells[] = s.split(",");
			double features[] = new double[cells.length - 1];
			for(int i = 0; i < features.length; i++){
				//@TODO how to treat missing values
				if(cells[i].trim().equals("?"))
					cells[i] = "0";

				features[i] = Double.parseDouble(cells[i]);
			}

			DoubleMatrix x = new DoubleMatrix(1, features.length, features);
			DoubleMatrix y;
			DoubleMatrix ones;

			if(cells[cells.length - 1].equals(AD)){
				y = new DoubleMatrix(1, 2, yAd);
			} else{
				y = new DoubleMatrix(1, 2, yNonAd);
			}

			ones = DoubleMatrix.ones(1);
			return new FeatureScore(y.transpose().mmul(x), ones.transpose().mmul(x.mul(x)));
		});

		FeatureScore totalScore = fScoreMatrix.reduce((a, b) -> a.add(b));
		numberOfFeatures = totalScore.getEMatrix().columns;

		DoubleMatrix e = totalScore.getEMatrix();
		DoubleMatrix v = totalScore.getVMatrix();
		DoubleMatrix s;
		s = DoubleMatrix.ones(1).transpose().mmul(e.mul(e));

//		System.out.println("s before division: " + s.get(s.columns+1));
//		System.out.println("V: " + v.toString());
//		System.out.println("#rows of v:" + v.rows);
//		System.out.println("#columns of v:" + v.columns);

		//@TODO check if this is element-wise division
		s = s.diviRowVector(v);

//		System.out.println("s after division: " + s.get(s.columns+1));
		System.out.println("#rows of s:" + s.rows);
		System.out.println("#columns of s:" + s.columns);

		startIndex = s.argmax();
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
