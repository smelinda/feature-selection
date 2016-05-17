import org.apache.spark.api.java.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

public class FeatureSelector{
	private static final String INPUT_FILE = "data/ad.data";
	private static final String AD = "ad.";
	private static final String NON_AD = "nonad.";
	private static int ad = 0;
	private static int nonad = 0;
	private static double yAd[] = new double[2];
	private static double yNonAd[] = new double[2];

	public static void main(String args[]) throws Exception {

		SparkConf conf = new SparkConf().setAppName("Feature Selector");
		JavaSparkContext sc = new JavaSparkContext(conf);

		//System.out.println("Number of workers: " + sc.getExe)
		JavaRDD<String> logData = sc.textFile(INPUT_FILE).cache();

		countClasses(logData);

		while(true){
			//JavaPairRDD<String, Integer> pairs = logData.map();
		}

	}

	private static void countClasses(JavaRDD<String> logData){
		JavaPairRDD<String, Integer> pairs = logData.mapToPair(s -> {
			String temp[] = s.split(",");
			return new Tuple2<>(temp[temp.length - 1], 1);
		});

		JavaPairRDD<String, Integer> counts = pairs.reduceByKey((a, b) -> a + b);
		List<Tuple2<String, Integer>> list = counts.collect();

		if(list.get(0)._1().equals(AD)){
			ad = list.get(0)._2();
			nonad = list.get(1)._2();
		} else{
			ad = list.get(1)._2();
			nonad = list.get(0)._2();
		}

		int n = ad + nonad;

		yAd[1] = - Math.sqrt(ad) / n;
		yAd[0] = 1.0 / Math.sqrt(ad) + yAd[1];

		yNonAd[1] = - Math.sqrt(nonad) / n;
		yNonAd[0] = 1.0 / Math.sqrt(nonad) + yNonAd[1];
	}

	private static void buildResponseMatrix() throws Exception{
		BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(new File(INPUT_FILE))));
		String str;
		int ad, nonad;

		while((str = input.readLine()) != null){
			String temp[] = str.split(",");

			//if(temp[temp.length - 1].equals())
		}
	}
}
