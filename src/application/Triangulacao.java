package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import quicksortpckg.QuickSort;
import javafx.scene.control.CheckBox;  
import javafx.scene.shape.Circle;  
import javafx.scene.layout.Pane;

/**
 * Class responsible for triangulation (mesh generation) from a set of points.
 * Handles mesh creation, rendering, and geometric preparations for FEM calculations.
 */
public class Triangulacao {

	/**
	 * Calculates the inverse of a square matrix using LU decomposition.
	 * @param n Matrix size
	 * @param a Matrix to invert
	 * @return Inverse matrix
	 */
	static double[][] inversa(int n, double[][] a) {
	    final double EPSILON = 1e-10;  // Tolerância para singularidade
	    
	    // Fatoração LU com pivotamento
	    int[] perm = new int[n];
	    double[][] lu = new double[n][n];
	    
	    // Inicialização
	    for (int i = 0; i < n; i++) {
	        perm[i] = i;
	        System.arraycopy(a[i], 0, lu[i], 0, n);
	    }

	    // Decomposição LU
	    for (int k = 0; k < n; k++) {
	        // Pivotamento parcial
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
	        
	        // Troca de linhas
	        if (maxRow != k) {
	            double[] temp = lu[k];
	            lu[k] = lu[maxRow];
	            lu[maxRow] = temp;
	            
	            int tempIdx = perm[k];
	            perm[k] = perm[maxRow];
	            perm[maxRow] = tempIdx;
	        }

	        // Fatoração
	        for (int i = k + 1; i < n; i++) {
	            lu[i][k] /= lu[k][k];
	            for (int j = k + 1; j < n; j++) {
	                lu[i][j] -= lu[i][k] * lu[k][j];
	            }
	        }
	    }

	    // Resolver sistemas para cada coluna da matriz identidade
	    double[][] inv = new double[n][n];
	    double[] col = new double[n];
	    
	    for (int k = 0; k < n; k++) {
	        // Construir coluna k da matriz identidade
	        Arrays.fill(col, 0);
	        col[k] = 1;
	        
	        // Aplicar permutação
	        double[] permCol = new double[n];
	        for (int i = 0; i < n; i++) {
	            permCol[i] = col[perm[i]];
	        }
	        
	        // Resolver Ly = Pb (substituição direta)
	        double[] y = new double[n];
	        for (int i = 0; i < n; i++) {
	            y[i] = permCol[i];
	            for (int j = 0; j < i; j++) {
	                y[i] -= lu[i][j] * y[j];
	            }
	        }
	        
	        // Resolver Ux = y (substituição reversa)
	        double[] x = new double[n];
	        for (int i = n - 1; i >= 0; i--) {
	            x[i] = y[i];
	            for (int j = i + 1; j < n; j++) {
	                x[i] -= lu[i][j] * x[j];
	            }
	            x[i] /= lu[i][i];
	        }
	        
	        // Armazenar coluna na matriz inversa
	        for (int i = 0; i < n; i++) {
	            inv[i][k] = x[i];
	        }
	    }
	    
	    return inv;
	}

	/**
	 * Prints a square matrix (for debug).
	 */
	static void imprimirMatriz(int n, double[][] a) {
		int u,v;
		System.out.println("");
		for(u=0;u<n;u++)
		{
			for(v=0;v<n;v++)
			{
				System.out.print(a[u][v]+"\t");
			}
			System.out.println("");
		}
	}

	/**
	 * Calculates the determinant of a square matrix using Gauss elimination.
	 * @param n Matrix size
	 * @param a Matrix
	 * @return Determinant value
	 */
	static double determinant(int n, double[][] a){
		double det=1;
		int i,j,k,m,u,v;
		int swapCount=0;
		m=n;
		for(i=0;i<m-1;i++){
			// Partial pivoting
			for(k=i+1;k<m;k++){
				if((Math.abs(a[i][i])<Math.abs(a[k][i]))){
					swapCount++;
					for(j=0;j<n;j++)
					{
						double temp;
						temp=a[i][j];
						a[i][j]=a[k][j];
						a[k][j]=temp;
					}
				}
			}
			// Forward elimination
			for(k=i+1;k<m;k++){
				double  term = 0;
				if(a[i][i]!=0)
					term=a[k][i]/ a[i][i];
				for(j=0;j<n;j++){
					a[k][j]=a[k][j]-term*a[i][j];
				}
			}
		}
		for(i=0;i<n;i++)
		{
			det =det*a[i][i];
		}
		det=det*Math.pow(-1,swapCount);
		return det;
	}

	/**
	 * Draws all triangles in the array on the JavaFX scene.
	 * @param objTriangulo Array of triangles
	 * @param root JavaFX group to add to
	 * @param arrpolygono Polygon array for triangle shapes
	 * @param numeroTriangulosNorm Number of triangles to draw
	 */
	public static void desenhartriangulos(ArrayList<Triangulo> objTriangulo, Pane root, 
			ArrayList<Polygon> arrpolygono) {
		arrpolygono.clear();

		for(Triangulo triangulo : objTriangulo) {
			Polygon polygon = new Polygon();
			polygon.getPoints().addAll(
					triangulo.x1, triangulo.y1,
					triangulo.x2, triangulo.y2,
					triangulo.x3, triangulo.y3
					);
			polygon.setFill(Color.TRANSPARENT);
			polygon.setStroke(Color.BLACK);
			root.getChildren().add(polygon);
			arrpolygono.add(polygon);
		}
	}

	/**
	 * Removes all triangles from the JavaFX scene.
	 */
	public static void deletartriangulos(Pane root, ArrayList<Polygon> arrpolygono) {
		for(Polygon poly : arrpolygono) {
			root.getChildren().remove(poly);
		}
		arrpolygono.clear();
	}

	/**
	 * Checks if a point is inside the circumcircle of triangle b.
	 * Used for Delaunay triangulation logic.
	 */
	static boolean dentroCirculo(Ponto a, Triangulo b) {
		if(Math.sqrt(Math.pow(b.xc-a.x,2)+Math.pow(b.yc-a.y,2))<=b.r)      
			return true;
		else 
			return false;
	}

	// Adicione este método na classe Triangulacao
	private boolean pontosIguais(double x1, double y1, double x2, double y2) {
		final double EPSILON = 1e-6;  // Tolerância para diferenças
		return Math.abs(x1 - x2) < EPSILON && Math.abs(y1 - y2) < EPSILON;
	}
	
	// Método auxiliar para verificar existência
	private boolean verticeExists(ArrayList<Vertice> list, Vertice v) {
	    for (Vertice existing : list) {
	        if (Math.abs(existing.x - v.x) < 1e-6 && 
	            Math.abs(existing.y - v.y) < 1e-6) {
	            return true;
	        }
	    }
	    return false;
	}

	private boolean isSuperTriangleVertex(double x, double y) {
		final double EPSILON = 1e-6;  // Tolerância para diferenças

		// Verifica se é o vértice (0, 0)
		if (Math.abs(x - 0) < EPSILON && Math.abs(y - 0) < EPSILON) {
			return true;
		}
		// Verifica se é o vértice (0, 2000)
		if (Math.abs(x - 0) < EPSILON && Math.abs(y - 2000) < EPSILON) {
			return true;
		}
		// Verifica se é o vértice (2000, 0)
		if (Math.abs(x - 2000) < EPSILON && Math.abs(y - 0) < EPSILON) {
			return true;
		}

		return false;
	}

	/**
	 * Draws field vectors (e.g., electric field) at the centroid of each triangle.
	 * @param arrTriangulosNorm Array of triangles
	 * @param E Field vectors for each triangle
	 * @param arrVetorLinha Array to store drawn lines
	 * @param numeroTriangulosNormFinal Number of triangles
	 * @param root JavaFX group to add to
	 */
	public static void desenharVetores(ArrayList<Triangulo> arrTriangulosNorm, double[][] E, 
			ArrayList<Line[]> arrVetorLinha, Pane root) {
		double Emax = 0;
		int numeroTriangulos = arrTriangulosNorm.size();

		// 1. Encontrar magnitude máxima do campo para normalização
		for(int i = 0; i < numeroTriangulos; i++) {
			double magnitude = Math.sqrt(Math.pow(E[i][0], 2) + Math.pow(E[i][1], 2));
			if(magnitude > Emax) {
				Emax = magnitude;
			}
		}

		// 2. Desenhar vetores como linhas com setas
		for(int i = 0; i < numeroTriangulos; i++) {
			Triangulo triangulo = arrTriangulosNorm.get(i);

			double x = triangulo.centroideX;
			double y = triangulo.centroideY;
			double u, v;

			if (Emax < 1e-17) {
				u = 0; 
				v = 0;
			} else {
				u = 10 * E[i][0] / Emax;
				v = 10 * E[i][1] / Emax;
			}

			double x1 = x;
			double x2 = x + u;
			double y1 = y;
			double y2 = y + v;         

			double difx = (x2 - x1) / 3;
			double dify = (y2 - y1) / 3;

			Line[] vetorLines = new Line[3];

			// Linha principal
			vetorLines[0] = new Line(x1, y1, x2, y2);
			vetorLines[0].setStrokeWidth(1);
			vetorLines[0].setStroke(Color.DARKRED);

			// Setas (parte 1)
			vetorLines[1] = new Line(x2, y2, x2 - difx - dify, y2 - dify + difx);
			vetorLines[1].setStrokeWidth(2);
			vetorLines[1].setStroke(Color.DARKRED);

			// Setas (parte 2)
			vetorLines[2] = new Line(x2, y2, x2 - difx + dify, y2 - dify - difx);
			vetorLines[2].setStrokeWidth(2);
			vetorLines[2].setStroke(Color.DARKRED);

			// Gerenciamento dinâmico
			if (i < arrVetorLinha.size()) {
				// Remover linhas antigas se existirem
				Line[] oldLines = arrVetorLinha.get(i);
				if(oldLines != null) {
					for(Line line : oldLines) {
				        if(line != null) root.getChildren().remove(line);
					}
				}
				arrVetorLinha.set(i, vetorLines);
			} else {
				arrVetorLinha.add(vetorLines);
			}

			root.getChildren().addAll(vetorLines);
		}
	}

	/**
	 * Removes all field vector lines from the JavaFX scene.
	 */
	public static void deletarVetores(ArrayList<Line[]> arrVetorLinha, Pane root) {
		for(Line[] lines : arrVetorLinha) {
			for(Line line : lines) {
				root.getChildren().remove(line);
			}
		}
		arrVetorLinha.clear();
	}

	// Number of valid triangles (not boundary triangles)
	int numeroTriangulosNorm;
	// Arrays for triangles, vectors, and polygons
	ArrayList<Triangulo> arrTriangulosNorm = new ArrayList<>();
	ArrayList<Polygon> arrpolygono = new ArrayList<>();
	ArrayList<Line[]> arrVetorLinha = new ArrayList<>();
	double[][] E; // Field vector (e.g., electric field) for each triangle

	/**
	 * Main constructor that performs triangulation and prepares mesh for FEM.
	 * @param root JavaFX scene group
	 * @param arrPontos Array of input points
	 * @param numeroPontosAtual Number of input points
	 * @param checkbox1 Checkbox for drawing field vectors
	 * @param checkbox2 Checkbox for drawing triangles
	 */
	public Triangulacao(Pane root, ArrayList<Ponto> arrPontosList, int numeroPontosAtual, CheckBox checkbox1, CheckBox checkbox2) {        

		// Arrays for working points, vertices, and triangles
		ArrayList<Ponto> arrPontos = new ArrayList<>(arrPontosList);
		
		ArrayList<Triangulo> arrTriangulosList = new ArrayList<>();

		int numeroPontosTotal = 0;
		int numeroPontosContorno;

		// Ajustar para usar ArrayList
		this.arrTriangulosNorm = new ArrayList<>();
		this.arrpolygono = new ArrayList<>();
		this.arrVetorLinha = new ArrayList<>();

		numeroPontosContorno = numeroPontosAtual - 3;

		// Generate grid points for the mesh, avoiding points too close to contour
		// Geração de pontos de malha:
		
		// Parâmetros de refinamento
		final double RAIO_INFLUENCIA = 100;   // Raio total da área refinada
		final double ESPACAMENTO_MIN = 5;     // Menor espaçamento (próximo ao ponto)
		final double FATOR_EXPANSAO = 1.5;    // Fator de aumento do espaçamento
		final int CAMADAS = 5;                // Número de camadas concêntricas

		// Lista de pontos que receberão refinamento (excluindo super triângulo)
		ArrayList<Ponto> pontosRefinamento = new ArrayList<>();
		for (int idx = 3; idx < numeroPontosContorno; idx++) {
		    pontosRefinamento.add(arrPontos.get(idx));
		}

		// Gerar pontos com refinamento circular
		for (Ponto pontoRef : pontosRefinamento) {
		    double espacamentoAtual = ESPACAMENTO_MIN;
		    double raioAtual = espacamentoAtual;
		    
		    while (raioAtual < RAIO_INFLUENCIA) {
		        // Calcular número de pontos nesta camada
		        int pontosNaCamada = (int) (2 * Math.PI * raioAtual / espacamentoAtual);
		        if (pontosNaCamada < 8) pontosNaCamada = 8;  // Mínimo de pontos por camada
		        
		        // Gerar pontos na camada atual
		        for (int i = 0; i < pontosNaCamada; i++) {
		            double angulo = 2 * Math.PI * i / pontosNaCamada;
		            double x = pontoRef.x + raioAtual * Math.cos(angulo);
		            double y = pontoRef.y + raioAtual * Math.sin(angulo);
		            
		            // Verificar se o ponto é válido (dentro da área e não muito próximo)
		            boolean pontoValido = true;
		            for (Ponto existente : arrPontos) {
		                double distX = x - existente.x;
		                double distY = y - existente.y;
		                if (distX * distX + distY * distY < espacamentoAtual * espacamentoAtual) {
		                    pontoValido = false;
		                    break;
		                }
		            }
		            
		            // Adicionar ponto válido
		            if (pontoValido) {
		                arrPontos.add(new Ponto(x, y));
		                numeroPontosAtual++;
		            }
		        }
		        
		        // Preparar próxima camada
		        espacamentoAtual *= FATOR_EXPANSAO;
		        raioAtual += espacamentoAtual;
		    }
		}

		// Gerar pontos para o resto da área (malha mais grossa)
		final double ESPACAMENTO_GROSSO = 40;
		for (int i = 0; i < 40; i++) {
		    for (int j = 0; j < 30; j++) {
		        double x = 5 + ESPACAMENTO_GROSSO * i;
		        double y = 5 + ESPACAMENTO_GROSSO * j;
		        
		        // Verificar distância aos pontos existentes
		        boolean pontoProximo = false;		// Point proximity flag for mesh refinement

		        for (Ponto existente : arrPontos) {
		            double distX = x - existente.x;
		            double distY = y - existente.y;
		            if (distX * distX + distY * distY < 400) {  // 20^2 = 400
		                pontoProximo = true;
		                break;
		            }
		        }
		        
		        // Adicionar se não estiver próximo de outros pontos
		        if (!pontoProximo) {
		            arrPontos.add(new Ponto(x, y));
		            numeroPontosAtual++;
		        }
		    }
		}
		
		
		
		
		numeroPontosTotal = numeroPontosAtual;

		// --- Triangulation initialization ---
		// Create super triangle (covers all points)
		// Criar super triângulo
		Triangulo superTri = new Triangulo(
		    arrPontos.get(0).x, arrPontos.get(0).y,
		    arrPontos.get(1).x, arrPontos.get(1).y,
		    arrPontos.get(2).x, arrPontos.get(2).y
		);

		// Adicionar à lista
		arrTriangulosList.add(superTri);
		
		arrTriangulosList.add(new Triangulo(
			    superTri.x1, superTri.y1,
			    superTri.x2, superTri.y2,
			    arrPontos.get(3).x, arrPontos.get(3).y
			));

			arrTriangulosList.add(new Triangulo(
			    superTri.x2, superTri.y2,
			    superTri.x3, superTri.y3,
			    arrPontos.get(3).x, arrPontos.get(3).y
			));

			arrTriangulosList.add(new Triangulo(
			    superTri.x3, superTri.y3,
			    superTri.x1, superTri.y1,
			    arrPontos.get(3).x, arrPontos.get(3).y
			));

		// Main loop: add each point, updating mesh with Delaunay criterion
		for (int i = numeroPontosTotal - 1; i >= 4; i--) {
			
			Ponto pontoAtual = arrPontos.get(i);
		    
		    // 1. Identificar triângulos inválidos
		    ArrayList<Triangulo> triangulosInvalidos = new ArrayList<>();
		    ArrayList<Vertice> arrVerticesList = new ArrayList<>();
		    
		    for (Triangulo tri : arrTriangulosList) {
		        if (dentroCirculo(pontoAtual, tri)) {
		            triangulosInvalidos.add(tri);
		            
		            // Adicionar vértices (com verificação de duplicatas)
		            Vertice v1 = new Vertice(tri.x1, tri.y1, pontoAtual);
		            if (!verticeExists(arrVerticesList, v1)) arrVerticesList.add(v1);
		            
		            Vertice v2 = new Vertice(tri.x2, tri.y2, pontoAtual);
		            if (!verticeExists(arrVerticesList, v2)) arrVerticesList.add(v2);
		            
		            Vertice v3 = new Vertice(tri.x3, tri.y3, pontoAtual);
		            if (!verticeExists(arrVerticesList, v3)) arrVerticesList.add(v3);
		        }
		    }

		    // 2. Remover triângulos inválidos
		    arrTriangulosList.removeAll(triangulosInvalidos);
		    
		    // 3. Ordenar vértices angularmente
		    Collections.sort(arrVerticesList, Comparator.comparingDouble(v -> v.anguloParaCentro));
		    
		    // 4. Criar novos triângulos (conexão circular)
		    int numVertices = arrVerticesList.size();
		    for (int j = 0; j < numVertices; j++) {
		        int next = (j + 1) % numVertices; // Próximo vértice (volta ao início)
		        
		        Triangulo novoTri = new Triangulo(
		            arrVerticesList.get(j).x, arrVerticesList.get(j).y,
		            arrVerticesList.get(next).x, arrVerticesList.get(next).y,
		            pontoAtual.x, pontoAtual.y
		        );
		        
		        arrTriangulosList.add(novoTri);
		    }
					
		}
		
		// Filtragem final - remover triângulos com vértices do super triângulo
		for (Triangulo tri : arrTriangulosList) {
		    if (!isSuperTriangleVertex(tri.x1, tri.y1) &&
		        !isSuperTriangleVertex(tri.x2, tri.y2) &&
		        !isSuperTriangleVertex(tri.x3, tri.y3)) {
		        
		        arrTriangulosNorm.add(tri);
		    }
		}

		numeroTriangulosNorm = arrTriangulosNorm.size();


		// Optionally draw triangles on screen
		if(checkbox2.isSelected())
			desenhartriangulos(arrTriangulosNorm, root, arrpolygono);

		// --- FEM Preparation ---
		// Prepare points for field calculation
		ArrayList<Ponto> arrPontosNorm = new ArrayList<>();
		for (int i = 3; i < numeroPontosTotal; i++) {
			Ponto pontoAtual = arrPontos.get(i);
			if(pontoAtual.pontoContorno) {
				arrPontosNorm.add(new Ponto(pontoAtual.x, pontoAtual.y, pontoAtual.valorT));
			} else {
				arrPontosNorm.add(new Ponto(pontoAtual.x, pontoAtual.y));
			}
		}

		// Prepare local matrices for each triangle
		double[][] q = new double[numeroTriangulosNorm][3];
		double[][] r = new double[numeroTriangulosNorm][3];
		double[] D = new double[numeroTriangulosNorm];

		for (int i =0;i<numeroTriangulosNorm;i++)
		{
			q[i][0] = arrTriangulosNorm.get(i).y2  - arrTriangulosNorm.get(i).y3; // y2-y3
			q[i][1] = arrTriangulosNorm.get(i).y3 - arrTriangulosNorm.get(i).y1; // y3-y1
			q[i][2] = arrTriangulosNorm.get(i).y1 - arrTriangulosNorm.get(i).y2; // y1-y2

			r[i][0] = arrTriangulosNorm.get(i).x3 - arrTriangulosNorm.get(i).x2; // x3-x2
			r[i][1] = arrTriangulosNorm.get(i).x1 - arrTriangulosNorm.get(i).x3; // x1-x3
			r[i][2] = arrTriangulosNorm.get(i).x2 - arrTriangulosNorm.get(i).x1; // x2-x1

			D[i] = arrTriangulosNorm.get(i).x2*arrTriangulosNorm.get(i).y3 - arrTriangulosNorm.get(i).x3*arrTriangulosNorm.get(i).y2 + arrTriangulosNorm.get(i).x3*arrTriangulosNorm.get(i).y1 - arrTriangulosNorm.get(i).x1*arrTriangulosNorm.get(i).y3 + arrTriangulosNorm.get(i).x1*arrTriangulosNorm.get(i).y2 - arrTriangulosNorm.get(i).x2*arrTriangulosNorm.get(i).y1;
		}

		// Assemble element matrices into global matrix
		double[][][] C = new double[numeroTriangulosNorm][3][3];
		for(int elemento = 0;elemento<numeroTriangulosNorm;elemento++)
		{
			for(int i=0;i<=2;i++)
			{
				for(int j=0;j<=2;j++)
				{
					C[elemento][i][j]=1*(q[elemento][i]*q[elemento][j]+r[elemento][i]*r[elemento][j])/(2*D[elemento]);
				}
			}
		}
		numeroPontosTotal=numeroPontosTotal-3;
		double[][] Cglobal = new double[numeroPontosTotal][numeroPontosTotal];
		for(int i=0;i<numeroPontosTotal;i++)
		{
			for(int j=0;j<numeroPontosTotal;j++)
			{
				Cglobal[i][j]=0;
			}
		}

		// Build connectivity matrix for assembling the global matrix
		ArrayList<int[]> matrizdeconectividade = new ArrayList<>();

		for(int j = 0; j < numeroTriangulosNorm; j++) {
		    int[] conectividade = new int[4];
		    matrizdeconectividade.add(conectividade);

			matrizdeconectividade.get(j)[3] = 1;
			for(int i = 0; i < numeroPontosTotal; i++) {
				if(pontosIguais(arrTriangulosNorm.get(j).x1, arrTriangulosNorm.get(j).y1, arrPontosNorm.get(i).x, arrPontosNorm.get(i).y)) {
					matrizdeconectividade.get(j)[0] = i;
				}
				if(pontosIguais(arrTriangulosNorm.get(j).x2, arrTriangulosNorm.get(j).y2, arrPontosNorm.get(i).x, arrPontosNorm.get(i).y)) {
					matrizdeconectividade.get(j)[1] = i;
				}
				if(pontosIguais(arrTriangulosNorm.get(j).x3, arrTriangulosNorm.get(j).y3, arrPontosNorm.get(i).x, arrPontosNorm.get(i).y)) {
					matrizdeconectividade.get(j)[2] = i;
				}
			}
		}

		// Assemble element matrices into the global matrix
		for(int i=0;i<numeroTriangulosNorm;i++)
		{
			Cglobal[matrizdeconectividade.get(i)[0]][matrizdeconectividade.get(i)[0]] += C[i][0][0] ;
			Cglobal[matrizdeconectividade.get(i)[1]][matrizdeconectividade.get(i)[1]] += C[i][1][1] ;
			Cglobal[matrizdeconectividade.get(i)[2]][matrizdeconectividade.get(i)[2]] += C[i][2][2] ;

			Cglobal[matrizdeconectividade.get(i)[0]][matrizdeconectividade.get(i)[1]] += C[i][0][1] ;
			Cglobal[matrizdeconectividade.get(i)[1]][matrizdeconectividade.get(i)[2]] += C[i][1][2] ;
			Cglobal[matrizdeconectividade.get(i)[2]][matrizdeconectividade.get(i)[0]] += C[i][2][0] ;

			Cglobal[matrizdeconectividade.get(i)[1]][matrizdeconectividade.get(i)[0]] += C[i][0][1] ;
			Cglobal[matrizdeconectividade.get(i)[2]][matrizdeconectividade.get(i)[1]] += C[i][1][2] ;
			Cglobal[matrizdeconectividade.get(i)[0]][matrizdeconectividade.get(i)[2]] += C[i][2][0] ;
		}

		// Apply boundary conditions
		double[] tensao = new double[numeroPontosTotal];
		for(int i=0;i<numeroPontosTotal;i++)
		{
			tensao[i]=0;
			if(arrPontosNorm.get(i).pontoContorno == true)
			{
				tensao[i]=arrPontosNorm.get(i).valorT;
				Cglobal[i][i]=1;
				for(int j=0;j<numeroPontosTotal;j++)
				{
					if(i!=j)
						Cglobal[i][j]=0;
				}
			}
		}

		// Solve the linear system for the field values
		double  d=0;
		double[][] inverse = new double[numeroPontosTotal][numeroPontosTotal];
		double[][] Teste= new double[numeroPontosTotal][numeroPontosTotal];
		for(int i=0;i<numeroPontosTotal;i++)
		{
			for(int j=0;j<numeroPontosTotal;j++)
			{
				Teste[i][j]=Cglobal[i][j];
			}
		}
		d = determinant(numeroPontosTotal,Teste);

		if (d == 0) {
			System.out.println("Inverse of Entered Matrix is not possible");
		}
		else{
			inverse=inversa(numeroPontosTotal,Cglobal);
		}

		double[] produtomatriz= new double[numeroPontosTotal];

		for(int i=0;i<numeroPontosTotal;i++)
		{
			produtomatriz[i] =0;
			for(int j=0;j<numeroPontosTotal;j++)
			{
				produtomatriz[i] += inverse[i][j]*tensao[j];
			}
		}

		// Compute field (e.g., electric field) for each triangle
		System.out.println("Iniciando o cálculo do campo elétrico nos elementos.");
		E = new double[numeroTriangulosNorm][2]; // Cada elemento é double[2]
		for(int i=0;i<numeroTriangulosNorm;i++)
		{
			E[i][0]=-(q[i][0]*produtomatriz[matrizdeconectividade.get(i)[0]]+q[i][1]*produtomatriz[matrizdeconectividade.get(i)[1]]+q[i][2]*produtomatriz[matrizdeconectividade.get(i)[2]])/D[i];
			E[i][1]=-(r[i][0]*produtomatriz[matrizdeconectividade.get(i)[0]]+r[i][1]*produtomatriz[matrizdeconectividade.get(i)[1]]+r[i][2]*produtomatriz[matrizdeconectividade.get(i)[2]])/D[i];
			//System.out.print("Elemento " + i + ":\tEx:\t" + E[i][0] + "\tEy:\t" + E[i][1] + "\n");
		}
		System.out.println("Finalizado.");

		// Optionally draw vectors on screen
		if(checkbox1.isSelected())
			desenharVetores(arrTriangulosNorm, E, arrVetorLinha, root);
	}
}