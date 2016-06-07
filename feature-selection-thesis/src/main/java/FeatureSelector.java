import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vectors;

public class FeatureSelector
{
	public static void main(String args[]) throws Exception
	{
		String fileName = "/Users/Gumokun/Desktop/test-data/latest3.txt";
		// Read input data set
//		FSInputReader reader = new AdsInputReader();
//		reader.process();

		SparkConf conf = new SparkConf().setAppName("Feature Selector");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<String> rows = sc.textFile(fileName);

		JavaRDD<List<Double>> values = rows.map(s -> {
			String str[] = s.split(" ");
			Double temp[] = new Double[str.length];

			for(int i = 0; i < str.length; i++){
				temp[i] = Double.parseDouble(str[i]);
			}

			return temp;
		});

		V

		System.out.println(pos.toString());

		// centralize features to have zero mean

		// generate response matrix Y (refer to formula 4)

		//while(true){
			//JavaPairRDD<String, Integer> pairs = logData.map();
		//}
	}
}
