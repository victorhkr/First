package application;

import java.util.Arrays;

public class MatrixOperations {
	
	
	// Resolve o sistema linear Ax = b usando decomposição LU com pivoteamento parcial
    public static double[] solve(double[][] A, double[] b) {
        int n = b.length;
        
        // Fatoração LU com pivoteamento parcial
        int[] piv = new int[n];
        double[][] LU = new double[n][n];
        
        // Inicializar LU e pivô
        for (int i = 0; i < n; i++) {
            piv[i] = i;
            System.arraycopy(A[i], 0, LU[i], 0, n);
        }

        // Vetor de escala para pivoteamento
        double[] s = new double[n];
        for (int i = 0; i < n; i++) {
            double max = 0;
            for (int j = 0; j < n; j++) {
                double val = Math.abs(LU[i][j]);
                if (val > max) max = val;
            }
            if (max == 0) throw new RuntimeException("Matriz singular");
            s[i] = max;
        }

        // Decomposição
        for (int k = 0; k < n - 1; k++) {
            // Seleção de pivô
            int maxRow = k;
            double maxVal = Math.abs(LU[k][k]) / s[k];
            for (int i = k + 1; i < n; i++) {
                double val = Math.abs(LU[i][k]) / s[i];
                if (val > maxVal) {
                    maxVal = val;
                    maxRow = i;
                }
            }
            
            // Troca de linhas se necessário
            if (maxRow != k) {
                double[] temp = LU[k];
                LU[k] = LU[maxRow];
                LU[maxRow] = temp;
                
                double t = s[k];
                s[k] = s[maxRow];
                s[maxRow] = t;
                
                int tmp = piv[k];
                piv[k] = piv[maxRow];
                piv[maxRow] = tmp;
            }

            // Eliminação
            if (Math.abs(LU[k][k]) < 1e-12) {
                throw new RuntimeException("Matriz singular ou mal condicionada");
            }
            
            for (int i = k + 1; i < n; i++) {
                LU[i][k] /= LU[k][k];
                for (int j = k + 1; j < n; j++) {
                    LU[i][j] -= LU[i][k] * LU[k][j];
                }
            }
        }

        // Aplicar permutações ao vetor b
        double[] pb = new double[n];
        for (int i = 0; i < n; i++) {
            pb[i] = b[piv[i]];
        }

        // Substituição direta (Ly = pb)
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0;
            for (int j = 0; j < i; j++) {
                sum += LU[i][j] * y[j];
            }
            y[i] = pb[i] - sum;
        }

        // Substituição reversa (Ux = y)
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0;
            for (int j = i + 1; j < n; j++) {
                sum += LU[i][j] * x[j];
            }
            x[i] = (y[i] - sum) / LU[i][i];
        }

        return x;
    }
    
    public static double[][] inversa(int n, double[][] a) {
        final double EPSILON = 1e-10;
        int[] perm = new int[n];
        double[][] lu = new double[n][n];

        for (int i = 0; i < n; i++) {
            perm[i] = i;
            System.arraycopy(a[i], 0, lu[i], 0, n);
        }

        for (int k = 0; k < n; k++) {
            int maxRow = k;
            double maxVal = Math.abs(lu[k][k]);
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(lu[i][k]) > maxVal) {
                    maxVal = Math.abs(lu[i][k]);
                    maxRow = i;
                }
            }

            if (maxVal < EPSILON) {
                throw new ArithmeticException("Matriz singular - não invertível");
            }

            if (maxRow != k) {
                double[] temp = lu[k];
                lu[k] = lu[maxRow];
                lu[maxRow] = temp;
                int tempIdx = perm[k];
                perm[k] = perm[maxRow];
                perm[maxRow] = tempIdx;
            }

            for (int i = k + 1; i < n; i++) {
                lu[i][k] /= lu[k][k];
                for (int j = k + 1; j < n; j++) {
                    lu[i][j] -= lu[i][k] * lu[k][j];
                }
            }
        }

        double[][] inv = new double[n][n];
        double[] col = new double[n];

        for (int k = 0; k < n; k++) {
            Arrays.fill(col, 0);
            col[k] = 1;

            double[] permCol = new double[n];
            for (int i = 0; i < n; i++) {
                permCol[i] = col[perm[i]];
            }

            double[] y = new double[n];
            for (int i = 0; i < n; i++) {
                y[i] = permCol[i];
                for (int j = 0; j < i; j++) {
                    y[i] -= lu[i][j] * y[j];
                }
            }

            double[] x = new double[n];
            for (int i = n - 1; i >= 0; i--) {
                x[i] = y[i];
                for (int j = i + 1; j < n; j++) {
                    x[i] -= lu[i][j] * x[j];
                }
                x[i] /= lu[i][i];
            }

            for (int i = 0; i < n; i++) {
                inv[i][k] = x[i];
            }
        }
        return inv;
    }

    public static void imprimirMatriz(int n, double[][] a) {
        System.out.println("");
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                System.out.print(a[u][v] + "\t");
            }
            System.out.println("");
        }
    }

    public static double determinant(int n, double[][] a) {
        double det = 1;
        int swapCount = 0;
        int m = n;
        for (int i = 0; i < m - 1; i++) {
            for (int k = i + 1; k < m; k++) {
                if ((Math.abs(a[i][i]) < Math.abs(a[k][i]))) {
                    swapCount++;
                    for (int j = 0; j < n; j++) {
                        double temp = a[i][j];
                        a[i][j] = a[k][j];
                        a[k][j] = temp;
                    }
                }
            }
            for (int k = i + 1; k < m; k++) {
                double term = 0;
                if (a[i][i] != 0)
                    term = a[k][i] / a[i][i];
                for (int j = 0; j < n; j++) {
                    a[k][j] = a[k][j] - term * a[i][j];
                }
            }
        }
        for (int i = 0; i < n; i++) {
            det = det * a[i][i];
        }
        det = det * Math.pow(-1, swapCount);
        return det;
    }
}