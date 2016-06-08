package io;


public class ResponseMatrixValue {
    private double classValue;
    private double otherClassesValue;

    public ResponseMatrixValue(int numberOfClassOccurence, int numberOfInstances){
        classValue = - Math.sqrt(numberOfClassOccurence) / numberOfInstances;
        otherClassesValue = 1.0 / Math.sqrt(numberOfClassOccurence) + classValue;
    }

    public double getClassValue() {
        return classValue;
    }

    public void setClassValue(double classValue) {
        this.classValue = classValue;
    }

    public double getOtherClassesValue() {
        return otherClassesValue;
    }

    public void setOtherClassesValue(double otherClassesValue) {
        this.otherClassesValue = otherClassesValue;
    }
}
