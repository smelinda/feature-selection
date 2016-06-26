import io.AdsInputReader;
import io.FSInputReader;

public class FeatureSelector
{
	/**
	 * Run feature selection with program arguments:
	 * 0: (String) Complete path to input file (libsvm format)
	 * 1: (String) Dataset name ("ads" for ads dataset)
	 * 2: (Integer) Number of features selected
	 * @param args Program arguments as above.
	 */
	public static void main(String args[]) throws Exception
	{
		String filename = args[0];
		String datasetName = args[1];
		int numberOfSelectedFeature = Integer.parseInt(args[2]);

		String selectedFeaturesFile;

		FSInputReader reader = new AdsInputReader(filename, datasetName);

		if(datasetName.equals("ads"))
			selectedFeaturesFile = reader.getOutputPath() + "ad_selected_" + numberOfSelectedFeature + ".data";
		else
			selectedFeaturesFile = reader.getOutputPath() + "dorothea_selected_" + numberOfSelectedFeature + ".data";

		long startTime = System.currentTimeMillis();

		reader.process(numberOfSelectedFeature, selectedFeaturesFile);

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Time: " +  totalTime/1000 + " s " + totalTime%1000 + " ms");

	}
}
