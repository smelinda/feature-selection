import org.jblas.DoubleMatrix;

import java.io.Serializable;

public class FeatureScore implements Serializable
{
    private DoubleMatrix eMatrix;
    private DoubleMatrix vMatrix;

    FeatureScore(DoubleMatrix eMatrix, DoubleMatrix vMatrix) {
        this.eMatrix = eMatrix;
        this.vMatrix = vMatrix;
    }

    public DoubleMatrix getEMatrix() {
        return eMatrix;
    }

    public DoubleMatrix getVMatrix() {
        return vMatrix;
    }

    public FeatureScore add(FeatureScore anotherScore) {
        DoubleMatrix newE = eMatrix.add(anotherScore.getEMatrix());
        DoubleMatrix newV = vMatrix.add(anotherScore.getVMatrix());
        return new FeatureScore(newE, newV);
    }
}
