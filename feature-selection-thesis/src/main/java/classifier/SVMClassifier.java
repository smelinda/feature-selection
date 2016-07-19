package classifier;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.classification.SVMWithSGD;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;
import scala.Tuple2;

/**
 * SVM Classifier to train and predict binary classification problem.
 * Source: http://spark.apache.org/docs/latest/mllib-linear-methods.html#linear-support-vector-machines-svms
 * @version 28 June 2016
 */
public class SVMClassifier
{
    /**
     * Run classification with program arguments:
     * 0: (String) Complete path to input file (libsvm format)
     * 1: (Double) Proportion of training data from the whole input
     * @param args Program arguments as above.
     */
    public static void main(String[] args)
    {
        SparkConf conf = new SparkConf().setAppName("SVM Classifier");
        SparkContext sc = new SparkContext(conf);

        String path = args[0];
        double trainPart = Double.parseDouble(args[1]);

        JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(sc, path).toJavaRDD();

        // Split initial RDD into two... [60% training data, 40% testing data].
        JavaRDD<LabeledPoint> training = data.sample(false, trainPart, 11L);
        training.cache();
        JavaRDD<LabeledPoint> test = data.subtract(training);

        // Run training algorithm to build the model.
        int numIterations = 100;
        final SVMModel model = SVMWithSGD.train(training.rdd(), numIterations);

        // Clear the default threshold.
        model.clearThreshold();

        // Compute raw scores on the test set.
        JavaRDD<Tuple2<Object, Object>> scoreAndLabels = test.map(
                (LabeledPoint p) -> {
                    Double score = model.predict(p.features());
                    return new Tuple2<>(score, p.label());
                }
        );

        // Get evaluation metrics.
        BinaryClassificationMetrics metrics = new BinaryClassificationMetrics(JavaRDD.toRDD(scoreAndLabels));
        double auROC = metrics.areaUnderROC();
        double auPR = metrics.areaUnderPR();

        System.out.println("Area under ROC = " + auROC);
        System.out.println("Area under PR = " + auPR);

    }
}
