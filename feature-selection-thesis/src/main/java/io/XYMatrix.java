package io;

import org.jblas.DoubleMatrix;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Matrix that represents features (X) and class labels as values (Y).
 */
public class XYMatrix implements Serializable
{
    private DoubleMatrix x;
    private DoubleMatrix y;
    private ArrayList<String> labels;

    /**
     * Matrix that represents features (X) and class labels as values (Y).
     * @param x feature matrix
     * @param y response matrix
     * @param labels class labels
     */
    public XYMatrix(DoubleMatrix x, DoubleMatrix y, ArrayList<String> labels) {
        this.x = x;
        this.y = y;
        this.labels = labels;
    }

    /**
     * Get the feature matrix
     * @return feature matrix
     */
    public DoubleMatrix getX() {
        return x;
    }

    /**
     * Set the feature matrix
     * @param x feature matrix
     */
    public void setX(DoubleMatrix x) {
        this.x = x;
    }

    /**
     * Get the response matrix
     * @return response matrix
     */
    public DoubleMatrix getY() {
        return y;
    }

    /**
     * Set the response matrix
     * @param y response matrix
     */
    public void setY(DoubleMatrix y) {
        this.y = y;
    }

    /**
     * Get the labels
     * @return labels
     */
    public ArrayList<String> getLabels() {
        return labels;
    }

    /**
     * Set the labels
     * @param labels labels
     */
    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }
}
