package io;

import org.jblas.DoubleMatrix;

import java.io.Serializable;

/**
 * Matrix that represents features (X) and class labels as values (Y).
 */
public class XYMatrix implements Serializable
{
    private DoubleMatrix x;
    private DoubleMatrix y;

    /**
     * Matrix that represents features (X) and class labels as values (Y).
     * @param x feature matrix
     * @param y class labels as response matrix
     */
    public XYMatrix(DoubleMatrix x, DoubleMatrix y) {
        this.x = x;
        this.y = y;
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
}
