import io.AdsInputReader;
import io.FSInputReader;

public class FeatureSelector
{
	/**
	 * Run feature selection with program arguments:
	 * 0: (String) Complete path to input file (libsvm format)
	 * 1: (String) Dataset name ("ads" for ads dataset, "dorothea" for dorothea dataset)
	 * 2: (Integer) Number of features selected
	 * 3: (Integer) Number of executors in Spark
	 * @param args Program arguments as above.
	 */
	public static void main(String args[]) throws Exception
	{
		String fileName = args[0];
		String datasetName = args[1];
		int numOfSelectedFeatures = Integer.parseInt(args[2]);
		int numOfExecutors = Integer.parseInt(args[3]);
		String outputName = args[4];
		String bucketName = args[5];

		FSInputReader reader = new AdsInputReader(fileName, numOfExecutors);

		long startTime = System.currentTimeMillis();

		reader.process(numOfSelectedFeatures, outputName, datasetName, bucketName);

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Time: " +  totalTime/1000 + " s " + totalTime%1000 + " ms");

	}
}
