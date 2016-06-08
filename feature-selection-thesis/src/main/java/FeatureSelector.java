import io.FSInputReader;
import io.AdsInputReader;

public class FeatureSelector
{
	public static void main(String args[]) throws Exception
	{
		String fileName = "/Users/Gumokun/Desktop/test-data/latest3.txt";
		// Read input data set
		FSInputReader reader = new AdsInputReader();
		reader.process();

		// centralize features to have zero mean

		// generate response matrix Y (refer to formula 4)

		//while(true){
			//JavaPairRDD<String, Integer> pairs = logData.map();
		//}
	}
}
