import io.AdsInputReader;
import io.FSInputReader;

public class FeatureSelector
{
	public static void main(String args[]) throws Exception
	{
		// parameter
		int loopNumber = 20;

		long startTime = System.currentTimeMillis();
		FSInputReader reader = new AdsInputReader();
		reader.process(loopNumber);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Time: " +  totalTime/1000 + " s " + totalTime%1000 + " ms");
	}
}
