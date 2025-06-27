package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FEMSolver {

    // Método auxiliar para busca tolerante
    private static int findPointIndex(ArrayList<Ponto> points, double x, double y) {
        final double TOL = 1e-4;
        for (int i = 0; i < points.size(); i++) {
            Ponto p = points.get(i);
            if (Math.abs(p.x - x) < TOL && Math.abs(p.y - y) < TOL) {
                return i;
            }
        }
        return -1;
    }
    
    public static double[][] solve(ArrayList<Triangulo> arrTriangulosNorm, ArrayList<Ponto> arrPontosNorm) {
        int numeroTriangulosNorm = arrTriangulosNorm.size();
        int numeroPontosTotal = arrPontosNorm.size();

        // Arrays para diferenças de coordenadas e área dos triângulos
        double[][] q = new double[numeroTriangulosNorm][3]; // Diferenças em y: [y2-y3, y3-y1, y1-y2]
        double[][] r = new double[numeroTriangulosNorm][3]; // Diferenças em x: [x3-x2, x1-x3, x2-x1]
        double[] D = new double[numeroTriangulosNorm];      // 2 * área do triângulo (com sinal)

        // Preencher q, r e D para cada triângulo
        for (int i = 0; i < numeroTriangulosNorm; i++) {
            Triangulo tri = arrTriangulosNorm.get(i);
            q[i][0] = tri.y2 - tri.y3;
            q[i][1] = tri.y3 - tri.y1;
            q[i][2] = tri.y1 - tri.y2;

            r[i][0] = tri.x3 - tri.x2;
            r[i][1] = tri.x1 - tri.x3;
            r[i][2] = tri.x2 - tri.x1;

            // Cálculo do determinante (2 * área com sinal)
            D[i] = tri.x2 * tri.y3 - tri.x3 * tri.y2
                  + tri.x3 * tri.y1 - tri.x1 * tri.y3
                  + tri.x1 * tri.y2 - tri.x2 * tri.y1;
            
            // Verificar degeneração do triângulo
            if (Math.abs(D[i]) < 1e-10) {
                throw new IllegalArgumentException("Triângulo degenerado encontrado na posição " + i);
            }
        }

        // Matriz de rigidez local para cada elemento (3x3)
        double[][][] C = new double[numeroTriangulosNorm][3][3];
        for (int elemento = 0; elemento < numeroTriangulosNorm; elemento++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    // Fórmula da matriz de rigidez: (q_i*q_j + r_i*r_j) / (2 * |Área|)
                	double area = Math.abs(D[elemento]);

                    C[elemento][i][j] = (q[elemento][i] * q[elemento][j] + r[elemento][i] * r[elemento][j]) / (2 * area);
                }
            }
        }

        // Matriz global de rigidez (n x n)
        double[][] Cglobal = new double[numeroPontosTotal][numeroPontosTotal]; // Inicializada com zeros

     // Mapa tolerante para indexação
        Map<String, Integer> pontoParaIndice = new HashMap<>();
        for (int i = 0; i < numeroPontosTotal; i++) {
            Ponto p = arrPontosNorm.get(i);
            String chave = String.format("%.4f,%.4f", p.x, p.y);
            pontoParaIndice.put(chave, i);
        }

        // Matriz de conectividade com tolerância
        int[][] matrizConectividade = new int[numeroTriangulosNorm][3];
        for (int j = 0; j < numeroTriangulosNorm; j++) {
            Triangulo tri = arrTriangulosNorm.get(j);
            
            // Gerar chaves com mesma precisão
            String chave1 = String.format("%.4f,%.4f", tri.x1, tri.y1);
            String chave2 = String.format("%.4f,%.4f", tri.x2, tri.y2);
            String chave3 = String.format("%.4f,%.4f", tri.x3, tri.y3);
            
            // Busca tolerante
            Integer idx1 = pontoParaIndice.get(chave1);
            Integer idx2 = pontoParaIndice.get(chave2);
            Integer idx3 = pontoParaIndice.get(chave3);
            
            if (idx1 == null || idx2 == null || idx3 == null) {
                // Fallback: busca linear tolerante
                idx1 = findPointIndex(arrPontosNorm, tri.x1, tri.y1);
                idx2 = findPointIndex(arrPontosNorm, tri.x2, tri.y2);
                idx3 = findPointIndex(arrPontosNorm, tri.x3, tri.y3);
                
                if (idx1 == -1 || idx2 == -1 || idx3 == -1) {
                    throw new IllegalStateException("Ponto do triângulo não encontrado: " +
                            chave1 + " | " + chave2 + " | " + chave3);
                }
            }
            
            matrizConectividade[j][0] = idx1;
            matrizConectividade[j][1] = idx2;
            matrizConectividade[j][2] = idx3;
        }

        // Montagem da matriz global
        for (int i = 0; i < numeroTriangulosNorm; i++) {
            int idx0 = matrizConectividade[i][0];
            int idx1 = matrizConectividade[i][1];
            int idx2 = matrizConectividade[i][2];

            // Diagonal principal
            Cglobal[idx0][idx0] += C[i][0][0];
            Cglobal[idx1][idx1] += C[i][1][1];
            Cglobal[idx2][idx2] += C[i][2][2];

            // Termos cruzados (simétricos)
            Cglobal[idx0][idx1] += C[i][0][1];
            Cglobal[idx1][idx0] += C[i][0][1]; // Simetria
            
            Cglobal[idx1][idx2] += C[i][1][2];
            Cglobal[idx2][idx1] += C[i][1][2]; // Simetria
            
            Cglobal[idx2][idx0] += C[i][2][0];
            Cglobal[idx0][idx2] += C[i][2][0]; // Simetria
        }

     // Passo 1: Identificar nós de contorno
        boolean[] isBoundary = new boolean[numeroPontosTotal];
        double[] boundaryValues = new double[numeroPontosTotal];
        for (int i = 0; i < numeroPontosTotal; i++) {
            if (arrPontosNorm.get(i).pontoContorno) {
                isBoundary[i] = true;
                boundaryValues[i] = arrPontosNorm.get(i).valorT;
            }
        }

        // Passo 2: Construir o vetor b ajustado
        double[] b = new double[numeroPontosTotal];
        for (int i = 0; i < numeroPontosTotal; i++) {
            if (isBoundary[i]) {
                b[i] = boundaryValues[i]; // Valor fixo no contorno
            } else {
                // Subtrai contribuições dos nós de contorno
                b[i] = 0; // RHS original é 0 (sem fontes)
                for (int j = 0; j < numeroPontosTotal; j++) {
                    if (isBoundary[j]) {
                        b[i] -= Cglobal[i][j] * boundaryValues[j];
                    }
                }
            }
        }

        // Passo 3: Modificar a matriz para nós de contorno
        for (int i = 0; i < numeroPontosTotal; i++) {
            if (isBoundary[i]) {
                for (int j = 0; j < numeroPontosTotal; j++) {
                    if (i == j) {
                        Cglobal[i][j] = 1; // Diagonal = 1
                    } else {
                        Cglobal[i][j] = 0; // Fora da diagonal = 0
                    }
                }
            }
        }

        // Passo 4: Resolver o sistema
        double[] solucao = MatrixOperations.solve(Cglobal, b); // Usa o vetor b corrigido
        
        // Calcular gradiente (campo elétrico) em cada elemento
        double[][] campoEletrico = new double[numeroTriangulosNorm][2];
        for (int i = 0; i < numeroTriangulosNorm; i++) {
            int idx0 = matrizConectividade[i][0];
            int idx1 = matrizConectividade[i][1];
            int idx2 = matrizConectividade[i][2];
        	double area = Math.abs(D[i]);

            // Gradiente em x: -Σ(q_k * u_k)/D
            campoEletrico[i][0] = -(q[i][0] * solucao[idx0] 
                                 + q[i][1] * solucao[idx1] 
                                 + q[i][2] * solucao[idx2]) / area;
            
            // Gradiente em y: -Σ(r_k * u_k)/D
            campoEletrico[i][1] = -(r[i][0] * solucao[idx0] 
                                 + r[i][1] * solucao[idx1] 
                                 + r[i][2] * solucao[idx2]) / area;
        }
        
        return campoEletrico;
    }
}