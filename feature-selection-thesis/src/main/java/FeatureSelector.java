import io.AdsInputReader;
import io.FSInputReader;
import preprocess.TransformInput;

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

		String transformedFile, selectedFeaturesFile;

		FSInputReader reader = new AdsInputReader(filename, datasetName);

		/*--------------------------------------------------------*/
		// If data is not in libsvm format, use the following block
		/*--------------------------------------------------------*/
		if(datasetName.equals("ads"))
		{
			String rawInputFile = reader.getInputPath() + "ad.data";
			double[] binarizationThreshold = new double[]{320, 320, 320};
			transformedFile = reader.getInputPath() + "ad_transform_2.data";

			TransformInput.transformAds(rawInputFile, true, transformedFile, binarizationThreshold);

		} else {
			String dorotheaDataFile = reader.getInputPath() + "dorothea_train.data";
			String dorotheaLabelFile = reader.getInputPath() + "dorothea_train.labels";
			transformedFile = reader.getInputPath() + "dorothea_transform.data";

			TransformInput.transformDorothea(dorotheaDataFile, dorotheaLabelFile, transformedFile);
		}

		System.out.println("Successfully generated " + transformedFile + " file.");


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
