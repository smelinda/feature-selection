package io;

import org.jblas.DoubleMatrix;

import java.io.Serializable;

/**
 * Representation of feature score that consist of E matrix and v vector.
 */
public class FeatureScore implements Serializable
{
    private DoubleMatrix eMatrix;
    private DoubleMatrix vMatrix;

    /**
     * Constructor of FeatureScore initiates its E matrix and v vector.
     * @param eMatrix E matrix
     * @param vMatrix v vector
     */
    FeatureScore(DoubleMatrix eMatrix, DoubleMatrix vMatrix) {
        this.eMatrix = eMatrix;
        this.vMatrix = vMatrix;
    }

    /**
     * Get E matrix
     * @return E matrix
     */
    public DoubleMatrix getEMatrix() {
        return eMatrix;
    }

    /**
     * Get v vector
     * @return v vector
     */
    public DoubleMatrix getVMatrix() {
        return vMatrix;
    }

    /**
     * One feature score can be added with another feature score.
     * @param anotherScore Another feature score that is to be added to this feature score
     * @return A feature score object that consist of updated E matrix and v vector after addition operation
     */
    public FeatureScore add(FeatureScore anotherScore) {
        DoubleMatrix newE = eMatrix.add(anotherScore.getEMatrix());
        DoubleMatrix newV = vMatrix.add(anotherScore.getVMatrix());
        return new FeatureScore(newE, newV);
    }
}
