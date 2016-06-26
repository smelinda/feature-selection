package preprocess;
import java.io.*;

public class TransformInput
{
    public static void main(String args[]) throws Exception {
        String transformedFile;

        if(args[0].equals("ads"))
        {
            String rawInputFile = "data/ad.data";
            double[] binarizationThreshold = new double[]{320, 320, 320};
            transformedFile = "data/ad_transform.data";

            transformAds(rawInputFile, true, transformedFile, binarizationThreshold);

        } else {
            String dorotheaDataFile = "data/dorothea_train.data";
            String dorotheaLabelFile = "data/dorothea_train.labels";
            transformedFile = "data/dorothea_transform.data";

            transformDorothea(dorotheaDataFile, dorotheaLabelFile, transformedFile);
        }
    }


    public static void countThreshold(String fileName) throws Exception{
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));

        String line;
        double threshold[] = new double[3];

        while ((line = in.readLine()) != null) {
            String temp[] = line.split(",");

            for(int i = 0; i < 3; i++) {

                if(!temp[i].trim().equals("?")){
                    double max = Double.parseDouble(temp[i]);

                    if (max > threshold[i])
                        threshold[i] = max;
                }
            }
        }

        System.out.println(threshold[0] + " " + threshold[1] + " " + threshold[2]);
    }

    public static void transformAds(String fileName, boolean binaryMode, String outputName, double threshold[]) throws Exception
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));
        String line;

        StringBuffer buffer = new StringBuffer();
        while ((line = in.readLine()) != null) {
            String temp[] = line.split(",");
            String category = temp[temp.length - 1];
            String labelWord = category.substring(0, category.length() - 1); // to remove "." at the end

            if(binaryMode) {
                binarize(temp, threshold);

                if(labelWord.equals("ad"))
                    buffer.append("1");
                else
                    buffer.append("0");
            } else {
                buffer.append(labelWord);
            }

            for (int i = 0; i < temp.length - 1; i++) {
                String value = temp[i].trim();

                if (!value.equals("0") && !value.equals("?")) {
                    buffer.append(" ");
                    buffer.append(i + 1);
                    buffer.append(":");
                    buffer.append(value);
                }
            }

            buffer.append("\n");
        }

        buffer.deleteCharAt(buffer.length() - 1);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputName))));
        writer.write(buffer.toString());
        writer.flush();
        writer.close();
    }

    public static void transformDorothea(String inputFileName, String labelName, String outputFileName) throws Exception{
        BufferedReader inData = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputFileName))));
        BufferedReader inLabel = new BufferedReader(new InputStreamReader(new FileInputStream(new File(labelName))));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputFileName))));

        String line;

        while ((line = inData.readLine()) != null) {
            StringBuffer buffer = new StringBuffer();
            String temp[] = line.split(" ");

            String label = inLabel.readLine().equals("1") ? "1" : "0";
            buffer.append(label);

            for(int i = 0; i < temp.length; i++){
                buffer.append(" ");
                buffer.append(temp[i]);
                buffer.append(":");
                buffer.append("1");
            }

            buffer.append("\n");
            writer.write(buffer.toString());
        }

        writer.flush();
        writer.close();
    }

    private static void binarize(String str[], double threshold[]){
        for(int i = 0; i < threshold.length; i++){
            if(str[i].trim().equals("?")){
                str[i] = "0";
            } else {
                double temp = Double.parseDouble(str[i]);
                int binary = (temp > threshold[i]) ? 1 : 0;

                str[i] = binary + "";
            }
        }
    }
}
