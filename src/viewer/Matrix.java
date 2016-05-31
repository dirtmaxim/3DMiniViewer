package viewer;

public class Matrix {
    public float data[][];

    public Matrix(int row, int column) {
        data = new float[row][column];
    }

    public int getRows() {
        return data.length;
    }

    public int getColumns() {
        return data[0].length;
    }

    public static Matrix add(Matrix a, Matrix b) {
        Matrix c = new Matrix(a.getRows(), a.getColumns());
        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < a.getColumns(); j++) {
                c.data[i][j] = a.data[i][j] + b.data[i][j];
            }
        }
        return c;
    }

    public static Matrix subtract(Matrix a, Matrix b) {
        Matrix c = new Matrix(a.getRows(), a.getColumns());
        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < a.getColumns(); j++) {
                c.data[i][j] = a.data[i][j] - b.data[i][j];
            }
        }
        return c;
    }

    public static Matrix product(Matrix a, Matrix b) {
        Matrix c = new Matrix(a.getRows(), b.getColumns());
        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < b.getColumns(); j++) {
                for (int k = 0; k < a.getColumns(); k++) {
                    c.data[i][j] = c.data[i][j] + a.data[i][k] * b.data[k][j];
                }
            }
        }
        return c;
    }

    public static Matrix getTransposeMatrix(Matrix matrix) {
        Matrix result = new Matrix(matrix.getColumns(), matrix.getRows());
        for (int i = 0; i < matrix.getRows(); i++) {
            for(int j = 0; j < matrix.getColumns(); j++) {
                result.data[j][i] = matrix.data[i][j];
            }
        }
        return result;
    }

    public static Matrix getInverseMatrix(Matrix matrix) {
        Matrix result = new Matrix(matrix.getRows(), 2 * matrix.getColumns());
        for (int i = 0; i < matrix.getRows(); i++) {
            for (int j = 0; j < matrix.getColumns(); j++) {
                result.data[i][j] = matrix.data[i][j];
            }
        }
        for (int i = 0; i < matrix.getRows(); i++) {
            result.data[i][i + matrix.getColumns()] = 1;
        }
        for (int i = 0; i < matrix.getRows() - 1; i++) {
            for (int j = result.getColumns() - 1; j >= 0; j--) {
                result.data[i][j] = result.data[i][j] / result.data[i][i];
            }
            for (int k = i + 1; k < matrix.getRows(); k++) {
                float coefficient = result.data[k][i];
                for (int j = 0; j < result.getColumns(); j++) {
                    result.data[k][j] = result.data[k][j] - result.data[i][j] * coefficient;
                }
            }
        }
        for (int i = result.getColumns() - 1; i >= matrix.getRows() - 1; i--) {
            result.data[matrix.getRows() - 1][i] = result.data[matrix.getRows() - 1][i] / result.data[matrix.getRows() - 1][matrix.getRows() - 1];
        }
        for (int i = matrix.getRows() - 1; i > 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                float coefficient = result.data[j][i];
                for (int k = 0; k < result.getColumns(); k++) {
                    result.data[j][k] = result.data[i][k] - result.data[i][k] * coefficient;
                }
            }
        }
        Matrix truncatedResult = new Matrix(matrix.getRows(), matrix.getColumns());
        for(int i = 0; i < matrix.getRows(); i++) {
            for(int j = 0; j < matrix.getColumns(); j++) {
                truncatedResult.data[i][j] = result.data[i][j] + matrix.getColumns();
            }
        }
        return truncatedResult;
    }

    public static Matrix getIdentityMatrix(int size) {
        Matrix identityMatrix = new Matrix(size, size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    identityMatrix.data[i][j] = 1;
                }
            }
        }
        return identityMatrix;
    }

    public static Vector3D matrixToVector(Matrix matrix) {
        Vector3D vector = new Vector3D();
        vector.setX(matrix.data[0][0] / matrix.data[3][0]);
        vector.setY(matrix.data[1][0] / matrix.data[3][0]);
        vector.setZ(matrix.data[2][0] / matrix.data[3][0]);
        return vector;
    }

    public static Matrix vertexToMatrix(Vertex vertex) {
        Matrix matrix = new Matrix(4, 1);
        matrix.data[0][0] = vertex.getX();
        matrix.data[1][0] = vertex.getY();
        matrix.data[2][0] = vertex.getZ();
        matrix.data[3][0] = 1;
        return matrix;
    }
}