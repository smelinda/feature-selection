import io.AdsInputReader;
import io.FSInputReader;

public class FeatureSelector
{
	public static void main(String args[]) throws Exception
	{
		FSInputReader reader = new AdsInputReader();
		reader.process();
	}
}
