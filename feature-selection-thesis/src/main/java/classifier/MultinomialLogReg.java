package classifier;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;
import scala.Tuple2;

/**
 * Multinomial Logistic Regression to train and predict multiclass classification problem.
 * Source: http://spark.apache.org/docs/latest/mllib-linear-methods.html#logistic-regression
 * @version 20 June 2016
 */
public class MultinomialLogReg {

    /**
     * Run classification with program arguments:
     * 0: (String) Complete path to input file (libsvm format)
     * 1: (Integer) Number of classes
     * 2: (Double) Proportion of training data from the whole input
     * @param args Program arguments as above.
     */
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("Multinomial LogReg");
        SparkContext sc = new SparkContext(conf);

        String path = args[0];
        int numClass = Integer.parseInt(args[1]);
        double trainPart = Double.parseDouble(args[2]);

        double[] proportion = {trainPart, 1.0 - trainPart};
        JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(sc, path).toJavaRDD();

        // Split initial RDD into two... [70% training data, 30% testing data].
        JavaRDD<LabeledPoint>[] splits = data.randomSplit(proportion, 11L);
        JavaRDD<LabeledPoint> training = splits[0].cache();
        JavaRDD<LabeledPoint> test = splits[1];

        // Run training algorithm to build the model.
        final LogisticRegressionModel model = new LogisticRegressionWithLBFGS()
                .setNumClasses(numClass)
                .run(training.rdd());

        // Compute raw scores on the test set.
        JavaRDD<Tuple2<Object, Object>> predictionAndLabels = test.map(
                new Function<LabeledPoint, Tuple2<Object, Object>>() {
                    public Tuple2<Object, Object> call(LabeledPoint p) {
                        Double prediction = model.predict(p.features());
                        return new Tuple2<Object, Object>(prediction, p.label());
                    }
                }
        );

        // Get evaluation metrics.
        MulticlassMetrics metrics = new MulticlassMetrics(predictionAndLabels.rdd());
        double precision = metrics.precision();
        System.out.println("Precision = " + precision);
    }
}
