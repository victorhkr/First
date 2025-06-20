package application;

import java.util.ArrayList;

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
	 * Calculates the inverse of a square matrix using Gauss-Jordan elimination.
	 * @param n Matrix size
	 * @param a Matrix to invert
	 * @return Inverse matrix
	 */
	static double[][] inversa(int n, double[][] a) {
		double[][] matrizAumentada = new double[n][2*n];
		double[][] matrizInversa = new double[n][n];
		final double EPSILON = 1e-10;  // Tolerância para singularidade

		// Inicialização da matriz aumentada [A | I]
		for(int i = 0; i < n; i++) {
			System.arraycopy(a[i], 0, matrizAumentada[i], 0, n);
			for(int j = 0; j < n; j++) {
				matrizAumentada[i][j+n] = (i == j) ? 1 : 0;
			}
		}

		// Eliminação para frente com pivotação
		for(int i = 0; i < n; i++) {
			// Pivô máximo para estabilidade numérica
			int max = i;
			for(int k = i+1; k < n; k++) {
				if(Math.abs(matrizAumentada[k][i]) > Math.abs(matrizAumentada[max][i])) {
					max = k;
				}
			}

			// Verificar singularidade
			if(Math.abs(matrizAumentada[max][i]) < EPSILON) {
				throw new ArithmeticException("Matriz singular - não invertível");
			}

			// Trocar linhas se necessário
			if(max != i) {
				double[] temp = matrizAumentada[i];
				matrizAumentada[i] = matrizAumentada[max];
				matrizAumentada[max] = temp;
			}

			// Normalizar linha do pivô
			double pivot = matrizAumentada[i][i];
			for(int j = 0; j < 2*n; j++) {
				matrizAumentada[i][j] /= pivot;
			}

			// Eliminação
			for(int k = 0; k < n; k++) {
				if(k != i) {
					double factor = matrizAumentada[k][i];
					for(int j = 0; j < 2*n; j++) {
						matrizAumentada[k][j] -= factor * matrizAumentada[i][j];
					}
				}
			}
		}

		// Extrair matriz inversa
		for(int i = 0; i < n; i++) {
			System.arraycopy(matrizAumentada[i], n, matrizInversa[i], 0, n);
		}

		return matrizInversa;
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
				u = 100 * E[i][0] / Emax;
				v = 100 * E[i][1] / Emax;
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
		Vertice[] arrVertices = new Vertice[10000];
		Vertice[] temparrVertices = new Vertice[25000];
		Triangulo[] arrTriangulos = new Triangulo[20000];

		int i = 0, j = 0, k = 0, l=0, n=0;
		int numerovertices = 0;
		int numeroPontosTotal = 0;
		int numeroPontosContorno;

		// Ajustar para usar ArrayList
		this.arrTriangulosNorm = new ArrayList<>();
		this.arrpolygono = new ArrayList<>();
		this.arrVetorLinha = new ArrayList<>();

		// Point proximity flag for mesh refinement
		boolean pontoProximo = false;
		numeroPontosContorno = numeroPontosAtual - 3;

		// Generate grid points for the mesh, avoiding points too close to contour
		// Geração de pontos de malha:
		for (i = 0; i < 40; i++) {
		    for (j = 0; j < 30; j++) {
		        pontoProximo = false;
		        for (k = 3; k < numeroPontosContorno; k++) {
		            if (Math.sqrt(Math.pow(5 + 20 * i - arrPontos.get(k).x, 2) 
		                + Math.pow(5 + 20 * j - arrPontos.get(k).y, 2)) < 10) {
		                pontoProximo = true;
		            }
		        }
		        if (!pontoProximo) {
		            arrPontos.add(new Ponto(
		                5 + 20 * i - (Math.floor(Math.random() * 5)),
		                5 + 20 * j - (Math.floor(Math.random() * 5))
		            ));
		            numeroPontosAtual++;
		        }
		    }
		}
		numeroPontosTotal = numeroPontosAtual;

		// --- Triangulation initialization ---
		// Create super triangle (covers all points)
		arrTriangulos[0] = new Triangulo(
			    arrPontos.get(0).x, arrPontos.get(0).y,
			    arrPontos.get(1).x, arrPontos.get(1).y,
			    arrPontos.get(2).x, arrPontos.get(2).y
			);		int numerotriangulos = 1;

		// Add first contour point to create three new triangles
		arrTriangulos[1] = new Triangulo(arrTriangulos[0].x1,arrTriangulos[0].y1,arrTriangulos[0].x2,arrTriangulos[0].y2,arrPontos.get(3).x,arrPontos.get(3).y);
		arrTriangulos[2] = new Triangulo(arrTriangulos[0].x2,arrTriangulos[0].y2,arrTriangulos[0].x3,arrTriangulos[0].y3,arrPontos.get(3).x,arrPontos.get(3).y);
		arrTriangulos[3] = new Triangulo(arrTriangulos[0].x3,arrTriangulos[0].y3,arrTriangulos[0].x1,arrTriangulos[0].y1,arrPontos.get(3).x,arrPontos.get(3).y);
		numerotriangulos += 3;

		int[] deletarTriangulo = new int[1000];
		boolean achou_correspondencia = false;

		k = 0;
		// Main loop: add each point, updating mesh with Delaunay criterion
		for (i = numeroPontosTotal - 1; i >= 4; i--) {// process points from last to first
		    Ponto pontoAtual = arrPontos.get(i); 
			// Find triangles whose circumcircle contains the new point (need to be deleted)
			for(j= 1; j < numerotriangulos; j++) {
				if(dentroCirculo(pontoAtual,arrTriangulos[j])) {
					deletarTriangulo[k] = j; // mark triangle for deletion
					k++;
				}
			}
			// Collect unique boundary vertices from triangles to be deleted
			for(j =0;j<k;j++) {
				if(j==0) {
					// Add all three vertices from the first triangle to be deleted
					arrVertices[0] = new Vertice(arrTriangulos[deletarTriangulo[j]].x1,arrTriangulos[deletarTriangulo[j]].y1,pontoAtual);
					arrVertices[1] = new Vertice(arrTriangulos[deletarTriangulo[j]].x2,arrTriangulos[deletarTriangulo[j]].y2,pontoAtual);
					arrVertices[2] = new Vertice(arrTriangulos[deletarTriangulo[j]].x3,arrTriangulos[deletarTriangulo[j]].y3,pontoAtual);
					numerovertices = 3;
				}else{
					// Only add new vertices not already in arrVertices
					achou_correspondencia = false;
					for(n=0;n<numerovertices;n++) {
						if((arrTriangulos[deletarTriangulo[j]].x1==arrVertices[n].x) && (arrTriangulos[deletarTriangulo[j]].y1==arrVertices[n].y)){
							achou_correspondencia = true;
							break;
						}
					}
					if(!achou_correspondencia) {
						arrVertices[numerovertices] = new Vertice(arrTriangulos[deletarTriangulo[j]].x1,arrTriangulos[deletarTriangulo[j]].y1,pontoAtual);
						++numerovertices;
					}
					achou_correspondencia = false;
					for(n=0;n<numerovertices;n++) {
						if((arrTriangulos[deletarTriangulo[j]].x2==arrVertices[n].x) && (arrTriangulos[deletarTriangulo[j]].y2==arrVertices[n].y)){
							achou_correspondencia = true;
							break;
						}
					}
					if(!achou_correspondencia) {
						arrVertices[numerovertices] = new Vertice(arrTriangulos[deletarTriangulo[j]].x2,arrTriangulos[deletarTriangulo[j]].y2,pontoAtual);
						++numerovertices;
					}
					achou_correspondencia = false;
					for(n=0;n<numerovertices;n++) {
						if((arrTriangulos[deletarTriangulo[j]].x3==arrVertices[n].x) && (arrTriangulos[deletarTriangulo[j]].y3==arrVertices[n].y)){
							achou_correspondencia = true;
							break;
						}
					}
					if(!achou_correspondencia) {
						arrVertices[numerovertices] = new Vertice(arrTriangulos[deletarTriangulo[j]].x3,arrTriangulos[deletarTriangulo[j]].y3,pontoAtual);
						++numerovertices;
					}
				}
			}

			// Sort boundary vertices by angle to ensure correct triangle ordering
			double[] vetorParaSortear = new double[numerovertices];
			for(j =0;j<numerovertices;j++) {
				temparrVertices[j] = new Vertice(arrVertices[j].x,arrVertices[j].y);
				temparrVertices[j].anguloParaCentro = arrVertices[j].anguloParaCentro;
				vetorParaSortear[j]= arrVertices[j].anguloParaCentro;
			}
			QuickSort sortVetor = new QuickSort();
			sortVetor.ordenarVetor(vetorParaSortear);

			// Reorder arrVertices according to sorted angles

			for(j = 0; j < numerovertices; j++) {
				for(l = 0; l < numerovertices; l++) {
					if(Math.abs(vetorParaSortear[j] - temparrVertices[l].anguloParaCentro) < 1e-9) {
						arrVertices[j] = temparrVertices[l];
						break; // Evitar duplicatas
					}
				}
			}

			// Rebuild triangles with the new point
			if(k<numerovertices) {
				// For each boundary edge, build a new triangle with the new point
				for(j=0;j<k;j++) {
					arrTriangulos[deletarTriangulo[j]] = new Triangulo(arrVertices[j].x,arrVertices[j].y,arrVertices[j+1].x,arrVertices[j+1].y,pontoAtual.x,pontoAtual.y);
				}
				for(j =k;j<numerovertices;j++){
					if(j==(numerovertices-1)) {
						arrTriangulos[numerotriangulos] = new Triangulo(arrVertices[0].x,arrVertices[0].y,arrVertices[numerovertices-1].x,arrVertices[numerovertices-1].y,pontoAtual.x,pontoAtual.y);
					}else {
						arrTriangulos[numerotriangulos] = new Triangulo(arrVertices[j].x,arrVertices[j].y,arrVertices[j+1].x,arrVertices[j+1].y,pontoAtual.x,pontoAtual.y);
					}
					numerotriangulos += 1;
				}
			}
			else {      
				for(j=0;j<k-1;j++) {
					arrTriangulos[deletarTriangulo[j]] = new Triangulo(arrVertices[j].x,arrVertices[j].y,arrVertices[j+1].x,arrVertices[j+1].y,pontoAtual.x,pontoAtual.y);
				}
			}

			numerovertices=0;
			k=0;
		}

		// Filter out triangles that include super triangle vertices
		numeroTriangulosNorm = 0;

		for(j = 0; j < numerotriangulos; j++) {
			// Verifica se o triângulo contém algum vértice do triângulo super
			boolean hasSuperVertex = 
					isSuperTriangleVertex(arrTriangulos[j].x1, arrTriangulos[j].y1) ||
					isSuperTriangleVertex(arrTriangulos[j].x2, arrTriangulos[j].y2) ||
					isSuperTriangleVertex(arrTriangulos[j].x3, arrTriangulos[j].y3);

			// Só inclui se NÃO tiver nenhum vértice do triângulo super
			if (!hasSuperVertex) {
				Triangulo tri = new Triangulo(
						arrTriangulos[j].x1, arrTriangulos[j].y1,
						arrTriangulos[j].x2, arrTriangulos[j].y2,
						arrTriangulos[j].x3, arrTriangulos[j].y3
						);
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
		for (i = 3; i < numeroPontosTotal; i++) {
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

		for (i =0;i<numeroTriangulosNorm;i++)
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
			for(i=0;i<=2;i++)
			{
				for(j=0;j<=2;j++)
				{
					C[elemento][i][j]=1*(q[elemento][i]*q[elemento][j]+r[elemento][i]*r[elemento][j])/(2*D[elemento]);
				}
			}
		}
		numeroPontosTotal=numeroPontosTotal-3;
		double[][] Cglobal = new double[numeroPontosTotal][numeroPontosTotal];
		for(i=0;i<numeroPontosTotal;i++)
		{
			for(j=0;j<numeroPontosTotal;j++)
			{
				Cglobal[i][j]=0;
			}
		}

		// Build connectivity matrix for assembling the global matrix
		ArrayList<int[]> matrizdeconectividade = new ArrayList<>();

		for(j = 0; j < numeroTriangulosNorm; j++) {
		    int[] conectividade = new int[4];
		    matrizdeconectividade.add(conectividade);

			matrizdeconectividade.get(j)[3] = 1;
			for(i = 0; i < numeroPontosTotal; i++) {
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
		for(i=0;i<numeroTriangulosNorm;i++)
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
		for(i=0;i<numeroPontosTotal;i++)
		{
			tensao[i]=0;
			if(arrPontosNorm.get(i).pontoContorno == true)
			{
				tensao[i]=arrPontosNorm.get(i).valorT;
				Cglobal[i][i]=1;
				for(j=0;j<numeroPontosTotal;j++)
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
		for(i=0;i<numeroPontosTotal;i++)
		{
			for(j=0;j<numeroPontosTotal;j++)
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

		for(i=0;i<numeroPontosTotal;i++)
		{
			produtomatriz[i] =0;
			for(j=0;j<numeroPontosTotal;j++)
			{
				produtomatriz[i] += inverse[i][j]*tensao[j];
			}
		}

		// Compute field (e.g., electric field) for each triangle
		System.out.println("Campo Elétrico nos elementos:");
		E = new double[numeroTriangulosNorm][2]; // Cada elemento é double[2]
		for(i=0;i<numeroTriangulosNorm;i++)
		{
			E[i][0]=-(q[i][0]*produtomatriz[matrizdeconectividade.get(i)[0]]+q[i][1]*produtomatriz[matrizdeconectividade.get(i)[1]]+q[i][2]*produtomatriz[matrizdeconectividade.get(i)[2]])/D[i];
			E[i][1]=-(r[i][0]*produtomatriz[matrizdeconectividade.get(i)[0]]+r[i][1]*produtomatriz[matrizdeconectividade.get(i)[1]]+r[i][2]*produtomatriz[matrizdeconectividade.get(i)[2]])/D[i];
			System.out.print("Elemento " + i + ":\tEx:\t" + E[i][0] + "\tEy:\t" + E[i][1] + "\n");
		}
		System.out.println("fim");

		// Optionally draw vectors on screen
		if(checkbox1.isSelected())
			desenharVetores(arrTriangulosNorm, E, arrVetorLinha, root);
	}
}