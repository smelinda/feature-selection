package io;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Abstract class to regulate input reader structure for feature selection.
 */
public abstract class FSInputReader
{

    /**
     * Raw data as RDD of String consist of features and class labels.
     */
    private JavaRDD<String> rawData;

    /**
     * SparkContext object to run Spark program.
     */
    private JavaSparkContext sc;


    /**
     * Constructor that regulates input reader structure for feature selection.
     * @param fileName input file name
     */
    public FSInputReader(String fileName, int numOfExecutors)
    {
        SparkConf conf = new SparkConf().setAppName("Feature Selector");
        sc = new JavaSparkContext(conf);
        rawData = sc.textFile(fileName, numOfExecutors).cache();
    }

    /**
     * Get text read from input file as RDD.
     * @return features and class labels from input file
     */
    protected JavaRDD<String> getRawData()
    {
        return rawData;
    }

    /**
     * Get Spark context.
     * @return Spark context
     */
    protected JavaSparkContext getSparkContext()
    {
        return sc;
    }

    /**
     * This method calls the actual feature selection.
     * @param loopNumber number of features to be selected
     * @param outputFileName name of the file with selected features only
     */
    abstract public void process(int loopNumber, String outputFileName, String datasetName, String bucketName);
}
