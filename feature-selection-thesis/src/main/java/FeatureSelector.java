import io.AdsInputReader;
import io.FSInputReader;

import java.io.File;
import java.security.CodeSource;

public class FeatureSelector
{
	/**
	 * Run feature selection with program arguments:
	 * 0: (String) Complete path to input file (libsvm format)
	 * 1: (String) Dataset name ("ads" for ads dataset)
	 * 2: (Integer) Number of features selected
	 * 3: (Integer) Number of executors in Spark
	 * @param args Program arguments as above.
	 */
	public static void main(String args[]) throws Exception
	{
		String filename = args[0];
		String datasetName = args[1];
		int numberOfSelectedFeature = Integer.parseInt(args[2]);
		int numOfExecutors = Integer.parseInt(args[3]);

		String selectedFeaturesFile;

		CodeSource codeSource = FeatureSelector.class.getProtectionDomain().getCodeSource();
		File jarFile = new File(codeSource.getLocation().toURI().getPath());

		// NOTE: for IBM cluster, use the first line, else use the second line
		//String jarDir = jarFile.getParentFile().getPath();
		String jarDir = jarFile.getParentFile().getParentFile().getPath();

		FSInputReader reader = new AdsInputReader(jarDir, filename, numOfExecutors);

		if(datasetName.equals("ads")) {
			selectedFeaturesFile = jarDir + "/" + reader.getOutputPath() + "ad_selected_" + numberOfSelectedFeature + ".data";
		} else {
			selectedFeaturesFile = jarDir + "/" + reader.getOutputPath() + "dorothea_selected_" + numberOfSelectedFeature + ".data";
		}

		long startTime = System.currentTimeMillis();

		reader.process(numberOfSelectedFeature, selectedFeaturesFile);

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Time: " +  totalTime/1000 + " s " + totalTime%1000 + " ms");

	}
}
