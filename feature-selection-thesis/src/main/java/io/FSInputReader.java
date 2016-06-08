package io;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;


public abstract class FSInputReader {
    private static final String INPUT_PATH = "data/";
    private static final String OUTPUT_PATH = "out/";
    private JavaRDD<String> rawData;
    private JavaSparkContext sc;
    
    public FSInputReader(String fileName){
        SparkConf conf = new SparkConf().setAppName("Feature Selector");
        sc = new JavaSparkContext(conf);
        rawData = sc.textFile(getPathToTrainingSet(fileName)).cache();
    }

    protected JavaRDD<String> getRawData(){
        return rawData;
    }

    protected JavaSparkContext getSparkContext(){
        return sc;
    }

    private String getPathToTrainingSet(String fileName){
        return INPUT_PATH + fileName;
    }

    abstract public void process();
}
