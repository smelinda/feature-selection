package helper;

import java.util.List;

/**
 * Utility class for Feature Selector
 */
public final class FSUtil
{
    /**
     * Convert list of array to double array.
     * @param list list of array.
     * @return double array.
     */
    public static double[][] convertToDoubleArray(List<Double[]> list)
    {
        int size = list.size();
        double matrix[][] = new double[size][list.get(0).length];

        for(int i = 0; i < size; i++){
            Double temp[] = list.get(i);

            for(int j = 0; j < matrix[i].length; j++){
                matrix[i][j] = temp[j];
            }
        }

        return matrix;
    }

    public static double[] convertToPrimitiveType(Double[] array){
        double temp[] = new double[array.length];

        for(int i = 0; i < array.length; i++){
            temp[i] = array[i];
        }

        return temp;
    }
}
