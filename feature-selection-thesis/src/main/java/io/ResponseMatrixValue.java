package io;

/**
 * Response matrix which corresponds to Y matrix in step 1
 */
public class ResponseMatrixValue
{

    private double classValue;
    private double otherClassesValue;

    /**
     * Response matrix value for certain class is proportion of data points belong to certain class compared to
     * total number of instances.
     * @param numberOfClassOccurence number of data points that belong to certain class
     * @param numberOfInstances number of instances in the whole data set
     */
    public ResponseMatrixValue(int numberOfClassOccurence, int numberOfInstances){
        classValue = - Math.sqrt(numberOfClassOccurence) / numberOfInstances;
        otherClassesValue = 1.0 / Math.sqrt(numberOfClassOccurence) + classValue;
    }

    /**
     * Get the value of response matrix (Y) for this class.
     * @return value of response matrix (Y) for this class
     */
    public double getClassValue() {
        return classValue;
    }

    /**
     * Set the value of response matrix (Y) for this class
     * @param classValue value of response matrix (Y) for this class
     */
    public void setClassValue(double classValue) {
        this.classValue = classValue;
    }

    /**
     * Get the value of response matrix (Y) for other classes relative to this class.
     * @return value of response matrix (Y) for other classes relative to this class
     */
    public double getOtherClassesValue() {
        return otherClassesValue;
    }

    /**
     * Set the value of response matrix (Y) for other classes relative to this class
     * @param otherClassesValue value of response matrix (Y) for other classes relative to this class
     */
    public void setOtherClassesValue(double otherClassesValue) {
        this.otherClassesValue = otherClassesValue;
    }
}
