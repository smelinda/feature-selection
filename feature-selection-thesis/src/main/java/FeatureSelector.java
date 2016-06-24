import io.AdsInputReader;
import io.FSInputReader;
import preprocess.TransformInput;

public class FeatureSelector
{
	public static void main(String args[]) throws Exception
	{
		FSInputReader reader = new AdsInputReader();

		// If data is not in libsvm format, use the following block
		/*--------------------------------------------------------*/

		String rawInputFile = reader.getInputPath() + "ad.data";
		String transformedFile;

		// If class label is binary type (use true), else String type (use false)
		boolean binaryMode = true;

		if(binaryMode)
			transformedFile = reader.getInputPath() + "ad_transform_1.data";
		else
			transformedFile = reader.getInputPath() + "ad_transform.data";

		TransformInput.transform(rawInputFile, binaryMode, transformedFile);
		System.out.println("Successfully generated " + transformedFile + " file.");

		/*--------------------------------------------------------*/

		// Parameters for feature selection
		/*--------------------------------------------------------*/
		int loopNumber = 20;
		String selectedFeaturesFile = reader.getOutputPath() + "ad_selected_" + loopNumber + ".data";
		/*--------------------------------------------------------*/

		long startTime = System.currentTimeMillis();

		reader.process(loopNumber, selectedFeaturesFile);

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Time: " +  totalTime/1000 + " s " + totalTime%1000 + " ms");

	}
}
