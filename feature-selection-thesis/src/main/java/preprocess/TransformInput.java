package preprocess;

import java.io.*;

public class TransformInput
{
    public static void transform(String fileName, boolean binaryMode, String outputName) throws Exception
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));
        String line;

        StringBuffer buffer = new StringBuffer();
        while ((line = in.readLine()) != null) {
            String temp[] = line.split(",");
            String category = temp[temp.length - 1];
            String labelWord = category.substring(0, category.length() - 1); // to remove "." at the end

            if(binaryMode) {
                if(labelWord.equals("ad"))
                    buffer.append("1");
                else
                    buffer.append("0");
            } else {
                buffer.append(labelWord);
            }

            for (int i = 0; i < temp.length - 1; i++) {
                String value = temp[i].trim();
                value = value.equals("?") ? "0" : value;

                if (!value.equals("0")) {
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
}
