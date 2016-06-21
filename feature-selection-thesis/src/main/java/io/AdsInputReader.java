package io;

import helper.FSUtil;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.broadcast.Broadcast;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;
import org.jblas.ranges.IndicesRange;
import org.jblas.ranges.IntervalRange;
import scala.Tuple2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;


public class AdsInputReader extends FSInputReader
{
    private static final String FILE_NAME = "ad_transform.data";
    private static final String AD = "ad.";
    private static final String NON_AD = "nonad.";
    private static final int FEATURE_SIZE = 1558;
    private int numberOfInstances = 0;
    private int ad = 0;
    private int nonad = 0;
    private static Double yAd[] = new Double[2];
    private static Double yNonAd[] = new Double[2];
    private JavaRDD<XYMatrix> xyMatrix;

    /**
     * Initiate input file name to Internet Advertisements dataset
     * (https://archive.ics.uci.edu/ml/datasets/Internet+Advertisements)
     * from UCI Machine Learning Repository.
     */
    public AdsInputReader()
    {
        super(FILE_NAME);
    }

    /**
     * Run feature selection.
     */
    public void process(int loopNumber, String outputFileName)
    {
        JavaRDD<List<String[]>> rawData = getRawData().mapPartitions(iterator -> {
            List<String[]> list = new ArrayList<>();

            while(iterator.hasNext()){
                list.add(iterator.next().split(" "));
            }

            return Collections.singleton(list);
        });

        countClasses(rawData);
        printStats();
        DoubleMatrix score = computeFeatureScores(rawData);

        try {
            write(getBestFeatures(score, loopNumber), outputFileName);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    private void write(Set<Integer> ids, String outputName) throws Exception{
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputName))));
        StringBuffer buffIdx = new StringBuffer();
        StringBuffer buffValue = new StringBuffer();

        for(Integer id : ids){
            buffIdx.append(id);
            buffIdx.append(",");
        }

        //@TODO extract values

        buffIdx.deleteCharAt(buffIdx.length() - 1);
        System.out.println("Selected features: " + buffIdx.toString());

        writer.write(buffValue.toString());
        writer.flush();
        writer.close();
    }

    /**
     * Count number of instances in each class and compute the values for response matrix.
     * @param logData input data
     */
    private void countClasses(JavaRDD<List<String[]>> logData)
    {
        // map values in class column into pair of <class, 1>
        JavaPairRDD<String, Integer> pairs = logData.mapPartitionsToPair(iterator -> {
            List<Tuple2<String, Integer>> list = new ArrayList<>();
            Map<String, Integer> map = new HashMap<>();

            while(iterator.hasNext()){
                List<String[]> temp = iterator.next();

                for(String[] str : temp) {
                    String label = str[0];
                    int size = 0;
                    if (map.containsKey(label)) {
                        size = map.get(label) + 1;
                    }

                    map.put(label, size);
                }

                for(Map.Entry<String, Integer> entry : map.entrySet()){
                    list.add(new Tuple2<>(entry.getKey(), entry.getValue()));
                }
            }

            return list;
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

    /**
     * Compute feature scores E and v based on algorithm step 1-3
     * @param logData input data
     * @return s scores for each features in a feature matrix
     */
    private DoubleMatrix computeFeatureScores(JavaRDD<List<String[]>> logData)
    {
        xyMatrix = logData.mapPartitions(iterator -> {
            ArrayList<Double[]> featureMatrix = new ArrayList<>();
            ArrayList<Double[]> responseMatrix = new ArrayList<>();

            while(iterator.hasNext()) {
                List<String[]> list = iterator.next();
                for(String[] splittedLine : list) {
                    featureMatrix.add(getFeatures(splittedLine));

                    if (splittedLine[0].equals(AD)) {
                        responseMatrix.add(yAd);
                    } else {
                        responseMatrix.add(yNonAd);
                    }
                }
            }

            DoubleMatrix x = new DoubleMatrix(FSUtil.convertToDoubleArray(featureMatrix));
            DoubleMatrix y = new DoubleMatrix(FSUtil.convertToDoubleArray(responseMatrix));

            return Collections.singleton(new XYMatrix(x, y));
        }).cache();

        JavaRDD<FeatureScore> fScoreMatrix = xyMatrix.map(matrix -> {
            DoubleMatrix x = matrix.getX();
            DoubleMatrix y = matrix.getY();

            DoubleMatrix ones = DoubleMatrix.ones(x.getRows());

            return new FeatureScore(y.transpose().mmul(x), ones.transpose().mmul(x.mul(x)));
        });

        FeatureScore totalScore = fScoreMatrix.reduce((a, b) -> a.add(b));

        DoubleMatrix e = totalScore.getEMatrix();
        DoubleMatrix v = totalScore.getVMatrix();
        DoubleMatrix s = DoubleMatrix.ones(e.getRows()).transpose().mmul(e.mul(e));

//		System.out.println("Dimension E: " + e.getRows() + " x " + e.getColumns());
//		System.out.println("Dimension v: " + v.getRows() + " x " + v.getColumns());
//		System.out.println("Dimension ones: " + DoubleMatrix.ones(10).getRows() + " x " + DoubleMatrix.ones(10).getColumns());
//		System.out.println("s before division: " + s.get(s.columns+1));
//		System.out.println("V: " + v.toString());
//		System.out.println("#rows of v:" + v.rows);
//		System.out.println("#columns of v:" + v.columns);

        // Element-wise division on matrix
        s = s.divi(v);

//		System.out.println("s after division: " + s.get(s.columns+1));
        System.out.println("#rows of s:" + s.rows);
        System.out.println("#columns of s:" + s.columns);

        return s;
    }

    /**
     * Select best features based on precomputed scores.
     * @param score precomputed scores
     * @return index of selected features
     */
    private Set<Integer> getBestFeatures(DoubleMatrix score, int loopNumber)
    {
        Set<Integer> set = new HashSet<>();
        int maxIndex = score.argmax(), k = loopNumber, l = 1;
        set.add(maxIndex);

        //System.out.println("Max Index: " + maxIndex);

        DoubleMatrix cAcc = null;

        while(l < k){
            Broadcast broadcastIdx = getSparkContext().broadcast(maxIndex);

            // step 8
            JavaRDD<DoubleMatrix> ci = xyMatrix.map(matrix -> {
                DoubleMatrix x = matrix.getX();
                DoubleMatrix f = x.getColumn((int)broadcastIdx.value());
                DoubleMatrix c = x.transpose().mmul(f);
                return c;
            });

            // step 9
            if(cAcc == null) {
                cAcc = ci.reduce((a, b) -> a.add(b));
            } else{
                cAcc = DoubleMatrix.concatHorizontally(cAcc, ci.reduce((a, b) -> a.add(b)));
            }

            int selectedIndexes[] = new int[l];
            int unSelectedIndexes[] = new int[cAcc.rows - l];

            int i = 0, j = 0;
            for(Integer idx : set){
                selectedIndexes[i++] = idx;
            }

            for(i = 0; i < cAcc.rows; i++){
                if(!set.contains(i)) {
                    unSelectedIndexes[j++] = i;
                }
            }

            //System.out.println("unselected index: " + c.getRows() + " " + c.getColumns());
            DoubleMatrix s = getNextScore(selectedIndexes, unSelectedIndexes, xyMatrix);
            maxIndex = s.argmax();
            set.add(maxIndex);
            l++;

            //System.out.println(maxIndex + " " + l);
        }

        return set;
    }

    /**
     * Iteratively update feature score based on selected and unselected features.
     * @param selectedIndexes index of selected features
     * @param unselectedIndexes index of unselected features
     * @param logData input file
     * @return matrix of feature scores
     */
    private DoubleMatrix getNextScore(int selectedIndexes[], int unselectedIndexes[], JavaRDD<XYMatrix> logData)
    {
        Broadcast broadcastSelectedIndexes = getSparkContext().broadcast(selectedIndexes);
        Broadcast broadcastUnselectedIndexes = getSparkContext().broadcast(unselectedIndexes);

        System.out.println("Selected: " + selectedIndexes.length + " Unselected: " + unselectedIndexes.length);

        // step 10
        JavaRDD<FeatureMatrices> temp = logData.map(matrix -> {
            DoubleMatrix x = matrix.getX();
            DoubleMatrix y = matrix.getY();

            DoubleMatrix x1 = x.get(new IntervalRange(0, x.getRows()), new IndicesRange((int[])broadcastSelectedIndexes.getValue()));
            DoubleMatrix x2 = x.get(new IntervalRange(0, x.getRows()), new IndicesRange((int[])broadcastUnselectedIndexes.getValue()));

//			System.out.println("Dimension x1: " + x1.getRows() + " " + x1.getColumns());
//			System.out.println("Dimension x2: " + x2.getRows() + " " + x2.getColumns());

            DoubleMatrix ones = DoubleMatrix.ones(x.getRows());

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

        // step 11
        FeatureMatrices featureMatrices = temp.reduce((a, b) -> a.add(b));
        DoubleMatrix matrixB = Solve.pinv(featureMatrices.getMatrixA()).mmul(featureMatrices.getMatrixC12());
        DoubleMatrix matrixH = featureMatrices.getMatrixCY1().mmul(matrixB);
        DoubleMatrix matrixG = featureMatrices.getMatrixCY2().sub(matrixH);

        DoubleMatrix g = DoubleMatrix.ones(matrixG.getRows()).transpose().mmul(matrixG.mul(matrixG));
        DoubleMatrix w = featureMatrices.getMatrixV2().sub(DoubleMatrix.ones(
                featureMatrices.getMatrixC12().getRows()).transpose().mmul(
                featureMatrices.getMatrixC12().mul(matrixB)));

		//System.out.println("Dimension of G: " + g.getRows() + " x " + g.getColumns());
		//System.out.println("Dimension of w: " + w.getRows() + " x " + w.getColumns());
        DoubleMatrix s = g.divi(w);

        //System.out.println(matrixG.getRows() + " " + matrixG.getColumns());

        return s;
    }

    /**
     * Get all features per data point
     * @param cells cells per row in input file
     * @return features in a double array
     */
    private static Double[] getFeatures(String cells[])
    {
        Double features[] = new Double[FEATURE_SIZE];
        int j = 1;
        for(int i = 1; i < cells.length; i++) {
            String temp[] = cells[i].split(":");
            int featureId = Integer.parseInt(temp[0]);
            // To treat missing values, we convert them to zero.

            while(j < featureId){
                features[j-1] = 0.0;
                j++;
            }

            features[j-1] = Double.parseDouble(temp[1]);
            j++;
        }

        while(j <= FEATURE_SIZE){
            features[j-1] = 0.0;
            j++;
        }

        return features;
    }

    /**
     * Print out data statistics like number of instances and class distribution.
     */
    private void printStats()
    {
        System.out.println("# instances:" + numberOfInstances + "(ad:" + ad + ", non ad:" + nonad + ")");
        System.out.println("yAd: [" + yAd[0] + "," + yAd[1] + "]");
        System.out.println("yNonAd: [" + yNonAd[0] +"," + yNonAd[1] + "]");
    }
}
