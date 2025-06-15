package application;

public class Inversa {

    /**
     * Prints a square matrix of size n x n.
     */
    static void imprimirMatriz(int n, double[][] a) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(a[i][j] + "\t");
            }
            System.out.println();
        }
    }

    /**
     * Calculates the inverse of a square matrix a of size n x n using Gauss-Jordan elimination with partial pivoting.
     * 
     * @param n Size of the matrix (number of rows/columns)
     * @param a The input matrix to be inverted
     * @return The inverse matrix if invertible
     * @throws ArithmeticException if the matrix is singular or nearly singular
     */
    static double[][] inversa(int n, double[][] a) {
        // Create an augmented matrix [A | I], where I is the identity matrix
        double[][] aug = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            // Copy the original matrix A into the left side of aug
            System.arraycopy(a[i], 0, aug[i], 0, n);
            // Fill the right side with the identity matrix
            for (int j = n; j < 2 * n; j++)
                aug[i][j] = (i == (j - n)) ? 1 : 0;
        }

        // Forward elimination and partial pivoting
        for (int i = 0; i < n; i++) {
            // Find the row with the largest absolute value in the current column (pivot)
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(aug[k][i]) > Math.abs(aug[maxRow][i])) {
                    maxRow = k;
                }
            }
            // Swap the current row with the maxRow (partial pivoting)
            double[] temp = aug[i];
            aug[i] = aug[maxRow];
            aug[maxRow] = temp;

            // Check if the pivot element is zero (or nearly zero) -> non-invertible
            if (Math.abs(aug[i][i]) < 1e-12) 
                throw new ArithmeticException("Matrix is singular or nearly singular");

            // Make the pivot element aug[i][i] equal to 1 and eliminate all other elements in column i
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    // Calculate the factor to eliminate element aug[k][i]
                    double factor = aug[k][i] / aug[i][i];
                    // Subtract factor * current row from k-th row to make aug[k][i] zero
                    for (int j = 0; j < 2 * n; j++)
                        aug[k][j] -= factor * aug[i][j];
                }
            }

            // Normalize the pivot row so that aug[i][i] becomes exactly 1
            double div = aug[i][i];
            for (int j = 0; j < 2 * n; j++)
                aug[i][j] /= div;
        }

        // Extract the right half of the augmented matrix as the inverse
        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++)
            System.arraycopy(aug[i], n, inv[i], 0, n);

        return inv;
    }

    public static void main(String[] args) {
        double[][] a = {
            {0,-2,67,0,1},
            {0,20,10,2,1},
            {0,23,2,0,1},
            {0,-20,-20,1,1},
            {-7,-2,-20,1,1}
        };

        double[][] b = inversa(5, a);

        imprimirMatriz(5, a);
        System.out.println("\nInverse:");
        imprimirMatriz(5, b);
    }
}