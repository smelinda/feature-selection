import org.jblas.DoubleMatrix;

import java.io.Serializable;

/**
 * Created by Gumokun on 26/05/16.
 */
public class FeatureMatrices implements Serializable{
    private DoubleMatrix matrixA;
    private DoubleMatrix matrixCY1;
    private DoubleMatrix matrixCY2;
    private DoubleMatrix matrixC12;
    private DoubleMatrix matrixV2;

    public FeatureMatrices(DoubleMatrix matrixA, DoubleMatrix matrixCY1, DoubleMatrix matrixCY2, DoubleMatrix matrixC12, DoubleMatrix matrixV2) {
        this.matrixA = matrixA;
        this.matrixCY1 = matrixCY1;
        this.matrixCY2 = matrixCY2;
        this.matrixC12 = matrixC12;
        this.matrixV2 = matrixV2;
    }

    public DoubleMatrix getMatrixA() {
        return matrixA;
    }

    public void setMatrixA(DoubleMatrix matrixA) {
        this.matrixA = matrixA;
    }

    public DoubleMatrix getMatrixCY1() {
        return matrixCY1;
    }

    public void setMatrixCY1(DoubleMatrix matrixCY1) {
        this.matrixCY1 = matrixCY1;
    }

    public DoubleMatrix getMatrixCY2() {
        return matrixCY2;
    }

    public void setMatrixCY2(DoubleMatrix matrixCY2) {
        this.matrixCY2 = matrixCY2;
    }

    public DoubleMatrix getMatrixC12() {
        return matrixC12;
    }

    public void setMatrixC12(DoubleMatrix matrixC12) {
        this.matrixC12 = matrixC12;
    }

    public DoubleMatrix getMatrixV2() {
        return matrixV2;
    }

    public void setMatrixV2(DoubleMatrix matrixV2) {
        this.matrixV2 = matrixV2;
    }

    public FeatureMatrices add(FeatureMatrices featureMatrices){
        matrixA = matrixA.add(featureMatrices.getMatrixA());
        matrixCY1 = matrixCY1.add(featureMatrices.getMatrixCY1());
        matrixCY2 = matrixCY2.add(featureMatrices.getMatrixCY2());
        matrixC12 = matrixC12.add(featureMatrices.getMatrixC12());
        matrixV2 = matrixV2.add(featureMatrices.getMatrixV2());

        return this;
    }
}
