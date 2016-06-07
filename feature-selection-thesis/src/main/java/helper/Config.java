package helper;

/**
 * Created by Gumokun on 02/06/16.
 */
public class Config
{
    private static final String INPUT_PATH = "data/";
    private static final String OUTPUT_PATH = "out/";

    private Config() {}

    public static String pathToTrainingSet() {
        return INPUT_PATH + "ad.data";
    }

    public static String pathToTestSet() {
        return INPUT_PATH + "test.tab";
    }

    public static String pathToOutput() {
        return OUTPUT_PATH + "result";
    }

    public static String pathToSums() {
        return OUTPUT_PATH + "sums";
    }

    public static String pathToConditionals() {
        return OUTPUT_PATH + "conditionals";
    }

    public static Long getSmoothingParameter() {
        return 1L;
    }

}