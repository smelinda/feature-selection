package io;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YoutubeInputReader extends FSInputReader {
    private static final String FILE_NAME = "ad.data";
    private int numberOfInstances = 0;
    private Map<String, ResponseMatrixValue> responseMatrixValueMap = new HashMap<>();

    /**
     * Initiate input file name to Youtube Multiview Video Games dataset
     * (https://archive.ics.uci.edu/ml/datasets/YouTube+Multiview+Video+Games+Dataset)
     * from UCI Machine Learning Repository.
     */
    public YoutubeInputReader(){
        super(FILE_NAME);
    }

    public void process(int loopNumber, String outputName){
        countClasses(getRawData());
    }

    /**
     * Count number of instances in each class and compute the values for response matrix.
     * @param logData input data
     */
    private void countClasses(JavaRDD<String> logData)
    {
        // map values in class column into pair of <class, 1>
        JavaPairRDD<String, Integer> pairs = logData.mapToPair(s -> {
            String temp[] = s.split(" ");
            return new Tuple2<>(temp[0], 1);
        });

        JavaPairRDD<String, Integer> counts = pairs.reduceByKey((a, b) -> a + b);
        List<Tuple2<String, Integer>> list = counts.collect();

        numberOfInstances = countNumberOfInstances(list);
        for(Tuple2<String, Integer> tuple : list){
            String classLabel = tuple._1();
            int numberOfClassOccurence = tuple._2();
            ResponseMatrixValue value = new ResponseMatrixValue(numberOfClassOccurence, numberOfInstances);
            responseMatrixValueMap.put(classLabel, value);
        }
    }

    private int countNumberOfInstances(List<Tuple2<String, Integer>> list){
        int counter = 0;

        for(Tuple2<String, Integer> tuple : list){
            counter += tuple._2();
        }

        return counter;
    }
}
