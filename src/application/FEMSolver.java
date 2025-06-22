package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FEMSolver {

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
                    C[elemento][i][j] = (q[elemento][i] * q[elemento][j] + r[elemento][i] * r[elemento][j]) / (2 * D[elemento]);
                }
            }
        }

        // Matriz global de rigidez (n x n)
        double[][] Cglobal = new double[numeroPontosTotal][numeroPontosTotal]; // Inicializada com zeros

        // Mapa para indexação eficiente dos pontos (evita O(n^2))
        Map<String, Integer> pontoParaIndice = new HashMap<>();
        for (int i = 0; i < numeroPontosTotal; i++) {
            Ponto p = arrPontosNorm.get(i);
            pontoParaIndice.put(p.x + "," + p.y, i);
        }

        // Matriz de conectividade: índice global dos vértices de cada triângulo
        int[][] matrizConectividade = new int[numeroTriangulosNorm][3];
        for (int j = 0; j < numeroTriangulosNorm; j++) {
            Triangulo tri = arrTriangulosNorm.get(j);
            matrizConectividade[j][0] = pontoParaIndice.get(tri.x1 + "," + tri.y1);
            matrizConectividade[j][1] = pontoParaIndice.get(tri.x2 + "," + tri.y2);
            matrizConectividade[j][2] = pontoParaIndice.get(tri.x3 + "," + tri.y3);
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

        // Vetor de cargas (condições de contorno)
        double[] tensao = new double[numeroPontosTotal]; // Inicializado com zeros
        for (int i = 0; i < numeroPontosTotal; i++) {
            if (arrPontosNorm.get(i).pontoContorno) {
                tensao[i] = arrPontosNorm.get(i).valorT;
                // Modificar linha da matriz para condição de contorno
                for (int j = 0; j < numeroPontosTotal; j++) {
                    Cglobal[i][j] = (i == j) ? 1.0 : 0.0;
                }
            }
        }

        // Resolver sistema linear (substituir por método eficiente)
        double[][] solucao;
        
        try {
            // Substitua por: MatrixOperations.solveLinearSystem(Cglobal, tensao);
            solucao = new double[][]{MatrixOperations.solve(Cglobal, tensao)}; // Ilustrativo
        } catch (Exception e) {
            System.err.println("Erro na resolução do sistema: " + e.getMessage());
            return null;
        }

        // Calcular gradiente (campo elétrico) em cada elemento
        double[][] campoEletrico = new double[numeroTriangulosNorm][2];
        for (int i = 0; i < numeroTriangulosNorm; i++) {
            int idx0 = matrizConectividade[i][0];
            int idx1 = matrizConectividade[i][1];
            int idx2 = matrizConectividade[i][2];
            
            // Gradiente em x: -Σ(q_k * u_k)/D
            campoEletrico[i][0] = -(q[i][0] * solucao[0][idx0] 
                                 + q[i][1] * solucao[0][idx1] 
                                 + q[i][2] * solucao[0][idx2]) / D[i];
            
            // Gradiente em y: -Σ(r_k * u_k)/D
            campoEletrico[i][1] = -(r[i][0] * solucao[0][idx0] 
                                 + r[i][1] * solucao[0][idx1] 
                                 + r[i][2] * solucao[0][idx2]) / D[i];
        }
        
        return campoEletrico;
    }
}