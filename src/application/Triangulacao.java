package application;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import quicksortpckg.QuickSort;
import javafx.scene.control.CheckBox;  
import javafx.scene.shape.Circle;  


public class Triangulacao {

	static double[][] inversa(int n, double[][] a) {
		double[][] matrizAumentada = new double[n][2*n];
		double[][] matrizInversa = new double[n][2*n];

		int i,j,m,k;
		double temp,term;

		m=n;
		//cria a matriz aumentada
		for(i=0;i<n;i++){
			for(j=0;j<n;j++) {
				matrizAumentada[i][j] = a[i][j];
			}
			for(j=0;j<n;j++) {
				if(i==j)
					matrizAumentada[i][j+n] = 1;
				else
					matrizAumentada[i][j+n] = 0;
			}
		}
		//imprimirMatriz2(n,matrizAumentada);
		//////////

		for(i=0;i<m-1;i++){

			//imprimirMatriz(n, a);

			//Partial Pivoting
			for(k=i+1;k<m;k++){
				//If diagonal element(absolute value) is smaller than any of the terms below it
				if((Math.abs(matrizAumentada[i][i])<Math.abs(matrizAumentada[k][i]))){//&&(Math.abs(a[k][i])!=0)
					//Swap the rows
					for(j=0;j<2*n;j++)
					{
						temp=matrizAumentada[i][j];
						matrizAumentada[i][j]=matrizAumentada[k][j];
						matrizAumentada[k][j]=temp;
					}
				}
			}
			//imprimirMatriz2(n, matrizAumentada);

			//System.out.println("Begin Gauss Elimination");
			//Begin Gauss Elimination
			for(k=i+1;k<m;k++){
				term = 0;
				if(matrizAumentada[i][i]!=0)
					term=matrizAumentada[k][i]/ matrizAumentada[i][i];

				for(j=0;j<2*n;j++){
					matrizAumentada[k][j]=matrizAumentada[k][j]-term*matrizAumentada[i][j];
				}
			}

		}
		//Reverse Gauss Elimination
		//System.out.println("Begin Reverse Gauss Elimination");
		for(i=m-1;i>0;i--){
			//Begin Reverse Gauss Elimination

			for(k=i-1;k>=0;k--){
				term=matrizAumentada[k][i]/matrizAumentada[i][i];

				for(j=2*n-1;j>=0;j--){
					matrizAumentada[k][j]=matrizAumentada[k][j]-term*matrizAumentada[i][j];
				}

			}

			//imprimirMatriz2(n, matrizAumentada);

		}

		//imprimirMatriz2(n, matrizAumentada);

		for(i=0;i<n;i++){
			for(j=0;j<n;j++) {
				matrizInversa[i][j] = matrizAumentada[i][j+n]/matrizAumentada[i][i];
			}
		}
		//imprimirMatriz(n, matrizInversa);

		return matrizInversa;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////

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

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*******
	Function that calculates the determinant of a square matrix using Gauss-Elimination :
	Pass the square matrix as a parameter, and calculate and return the dete
	Parameters: order(n),matrix[n][n]
	 ********/

	static double determinant(int n, double[][] a){
		double det=1;
		int i,j,k,m,u,v;
		int swapCount=0;
		m=n;

		/*
	    System.out.println("determinante para calcular");
	    System.out.print("{");
	    for(i=0;i<n;i++)
	    {
	    	System.out.print("{");
	  	  for(j=0;j<n;j++)
	  	  {
	  		System.out.print(a[i][j]+",");
	  	  }
	  	System.out.print("},");
	    }
	    System.out.print("}");
		 */

		for(i=0;i<m-1;i++){

			//imprimirMatriz(n, a);

			//Partial Pivoting
			for(k=i+1;k<m;k++){
				//If diagonal element(absolute value) is smaller than any of the terms below it
				if((Math.abs(a[i][i])<Math.abs(a[k][i]))){//&&(Math.abs(a[k][i])!=0)
					//Swap the rows
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

			//imprimirMatriz(n, a);

			//Begin Gauss Elimination
			for(k=i+1;k<m;k++){
				double  term = 0;
				if(a[i][i]!=0)
					term=a[k][i]/ a[i][i];

				for(j=0;j<n;j++){
					a[k][j]=a[k][j]-term*a[i][j];
				}
			}

		}
		//imprimirMatriz(n, a);
		for(i=0;i<n;i++)
		{
			det =det*a[i][i];
			//System.out.println("determinante: "+ det);
		}
		det=det*Math.pow(-1,swapCount);
		//printf("\ndeterminante e%f\n",det);
		return det;

	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * Desenha os triangulos.
 * @param objTriangulo array de objetos Triangulo
 * @param root
 * @param arrpolygono
 * @param numeroTriangulosNorm
 */
	public static void desenhartriangulos(Triangulo[] objTriangulo, Group root, Polygon[] arrpolygono, int numeroTriangulosNorm){
		int j=0;
		for(j=0;j<numeroTriangulosNorm;j++) {
			arrpolygono[j] = new Polygon();
			root.getChildren().remove(arrpolygono[j]);
			Double[] triangle = {objTriangulo[j].x1, objTriangulo[j].y1, objTriangulo[j].x2,objTriangulo[j].y2,objTriangulo[j].x3,objTriangulo[j].y3};
			arrpolygono[j].getPoints().addAll(triangle);
			arrpolygono[j].setFill(Color.TRANSPARENT);
			arrpolygono[j].setStroke(Color.BLACK);
			root.getChildren().add(arrpolygono[j]); 
		}

	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void deletartriangulos(Group root, Polygon[] arrpolygono, int numeroTriangulosNorm){
		int j=0;
		for(j=0;j<numeroTriangulosNorm;j++) {
			root.getChildren().remove(arrpolygono[j]); 	
		}

	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	static void desenharPaintListener(PaintListener pl, Triangulo objTriangulo, Group root) {


		pl = new PaintListener(){
			public void paintControl(PaintEvent e){

				e.gc.setLineWidth(1);

				int[] triangle = {(int)objTriangulo.x1, (int)objTriangulo.y1, (int)objTriangulo.x2, (int)objTriangulo.y2, (int) objTriangulo.x3, (int) objTriangulo.y3};
				e.gc.drawPolygon(triangle);
			}
		};
		objShell.addPaintListener(pl);




	}
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	static void desenharPonto(Ponto objPonto, Group root) {
		Point point = new Point
		objShell.addPaintListener(new PaintListener(){
			public void paintControl(PaintEvent e){

				e.gc.drawPoint((int)objPonto.x,(int) objPonto.y);
				e.gc.drawText("O",(int)objPonto.x,(int) objPonto.y);
			}
		});
	}
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////

	static boolean dentroCirculo(Ponto a, Triangulo b) {

		if(Math.sqrt(Math.pow(b.xc-a.x,2)+Math.pow(b.yc-a.y,2))<=b.r)		
			return true;
		else 
			return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////


	public static void desenharVetores(Triangulo[] arrTriangulosNorm, double[][] E, Line[][] arrVetorLinha, int numeroTriangulosNormFinal, Group root) {
		//Desenho dos vetores de campo
		int i;

		double x,y,u,v,x1,x2,y1,y2,difx,dify,Emax=0;

		for(i=0;i<numeroTriangulosNormFinal;i++){
			x = Math.sqrt(Math.pow(E[i][0], 2)+Math.pow(E[i][1],2));
			if(x>Emax)
				Emax = x;
		}

		for(i=0;i<numeroTriangulosNormFinal;i++){

			x = arrTriangulosNorm[i].centroideX;
			y = arrTriangulosNorm[i].centroideY;
			//System.out.print("x " + x);
			//System.out.println(" y " + y);
			u = 30*E[i][0]/Emax;
			v = 30*E[i][1]/Emax;
			//System.out.print("u" + u);
			//System.out.println(" v " + v);
			x1 = x;
			x2 = x+u;
			y1 = y;
			y2 = y+v;			

			difx= (x2 -x1)/3;
			dify = (y2 -y1)/3;

			arrVetorLinha[i][0] = new Line(); //instantiating Line class   
			arrVetorLinha[i][1] = new Line(); //instantiating Line class
			arrVetorLinha[i][2] = new Line(); //instantiating Line class 

			arrVetorLinha[i][0].setStrokeWidth(1);
			arrVetorLinha[i][1].setStrokeWidth(2);
			arrVetorLinha[i][2].setStrokeWidth(2);
			
			arrVetorLinha[i][0].setStroke(Color.DARKRED);
			arrVetorLinha[i][1].setStroke(Color.DARKRED);
			arrVetorLinha[i][2].setStroke(Color.DARKRED);


			arrVetorLinha[i][0].setStartX(x1); //setting starting X point of Line  
			arrVetorLinha[i][0].setStartY(y1); //setting starting Y point of Line   
			arrVetorLinha[i][0].setEndX(x2); //setting ending X point of Line   
			arrVetorLinha[i][0].setEndY(y2); //setting ending Y point of Line   

			arrVetorLinha[i][1].setStartX(x2); //setting starting X point of Line  
			arrVetorLinha[i][1].setStartY(y2); //setting starting Y point of Line   
			arrVetorLinha[i][1].setEndX(x2-difx-dify); //setting ending X point of Line   
			arrVetorLinha[i][1].setEndY(y2-dify+difx); //setting ending Y point of Line   

			arrVetorLinha[i][2].setStartX(x2); //setting starting X point of Line  
			arrVetorLinha[i][2].setStartY(y2); //setting starting Y point of Line   
			arrVetorLinha[i][2].setEndX(x2-difx +dify); //setting ending X point of Line   
			arrVetorLinha[i][2].setEndY(y2-dify-difx); //setting ending Y point of Line

			root.getChildren().add(arrVetorLinha[i][0]); //adding the class object //to the group 
			root.getChildren().add(arrVetorLinha[i][1]); //adding the class object //to the group  
			root.getChildren().add(arrVetorLinha[i][2]); //adding the class object //to the group 
		}

	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////


	public static void deletarVetores(Line[][] arrVetorLinha, int numeroTriangulosNorm, Group root) {
		int i = 0;
		for(i=0;i<numeroTriangulosNorm;i++){
			//System.out.println(triangulacaoObj.numeroTriangulosNorm);
			root.getChildren().remove(arrVetorLinha[i][0]);
			root.getChildren().remove(arrVetorLinha[i][1]);
			root.getChildren().remove(arrVetorLinha[i][2]);
		}

	}

	int numeroTriangulosNorm;
	Triangulo[] arrTriangulosNorm = new Triangulo[20000];
	Line[][] arrVetorLinha = new Line[6000][3];
	Polygon[] arrpolygono = new Polygon[20000];
	double[][] E;

	Triangulacao(Group root,Ponto[] arrPontos, int numeroPontosAtual, CheckBox checkbox1, CheckBox checkbox2) {

		//Ponto[] arrPontos = new Ponto[20000];
		Ponto[] arrPontosNorm = new Ponto[20000];
		Vertice[] arrVertices = new Vertice[10000];
		Vertice[] temparrVertices = new Vertice[25000];
		Triangulo[] arrTriangulos = new Triangulo[20000];


		int i = 0;
		int j = 0;
		int k = 0;
		int l=0;
		int n=0;
		int numerovertices = 0;
		int numeroPontosTotal = 0;
		//int numeroPontosAtual = 0;
		int numeroPontosContorno;

		/*
		////////////////////////////super triangulo 

		arrPontos[0] = new Ponto(0,0);
		arrPontos[1] = new Ponto(2000,0);
		arrPontos[2] = new Ponto(0,2000);
		numeroPontosAtual = 3;
		///////////////////////////////////////

		// pontos de contorno

		///////////////////////////////////////
		///////////////////////////////////////
		///////////////////////////////////////

		arrPontos[3] = new Ponto(200,200,0);
		arrPontos[4] = new Ponto(250,200,0);
		arrPontos[5] = new Ponto(300,200,0);
		arrPontos[6] = new Ponto(350,200,0);
		arrPontos[7] = new Ponto(400,200,0);
		arrPontos[8] = new Ponto(450,200,0);
		arrPontos[9] = new Ponto(500,200,0);
		arrPontos[10] = new Ponto(200,300,200);
		arrPontos[11] = new Ponto(250,300,200);
		arrPontos[12] = new Ponto(300,300,200);
		arrPontos[13] = new Ponto(350,300,200);
		arrPontos[14] = new Ponto(400,300,200);
		arrPontos[15] = new Ponto(450,300,200);
		arrPontos[16] = new Ponto(500,300,200);

		///////////////////////////////////////
		///////////////////////////////////////
		///////////////////////////////////////
		///////////////////////////////////////


		 */
		boolean pontoProximo = false;
		numeroPontosContorno = numeroPontosAtual - 3;
		//numeroPontosAtual = 17;

		for (i = 0; i< 40; i++){
			for (j = 0; j< 30; j++){
				pontoProximo=false;
				for(k=3;k<numeroPontosContorno-3;k++){
					if(Math.sqrt(Math.pow(5+20*i-arrPontos[k].x,2)+Math.pow(5+20*j-arrPontos[k].y,2))<10) {
						pontoProximo=true;
					}
				}
				if(!pontoProximo){
					//arrPontos[numeroPontosAtual] = new Ponto(5+20*i,5+20*j);
					arrPontos[numeroPontosAtual] = new Ponto(5+20*i-(Math.floor(Math.random()*(5)+0)),5+20*j-(Math.floor(Math.random()*(5)+0)));
					numeroPontosAtual++;
				}
			}

		}

		numeroPontosTotal = numeroPontosAtual;



		/*
		for (i = numeroPontosAtuall; i< numeroPontosTotal; i++) {

			arrPontos[i] = new Ponto(Math.floor(Math.random()*(500)+100),Math.floor(Math.random()*(300)+0));
		}
		 */
		for (i = 3; i< numeroPontosTotal; i++) {
			//desenharPonto(arrPontos[i],shell1);
		}

		arrTriangulos[0] = new Triangulo(arrPontos[0].x,arrPontos[0].y,arrPontos[1].x,arrPontos[1].y,arrPontos[2].x,arrPontos[2].y);
		int numerotriangulos = 1;		

		arrTriangulos[1] = new Triangulo(arrTriangulos[0].x1,arrTriangulos[0].y1,arrTriangulos[0].x2,arrTriangulos[0].y2,arrPontos[3].x,arrPontos[3].y);
		arrTriangulos[2] = new Triangulo(arrTriangulos[0].x2,arrTriangulos[0].y2,arrTriangulos[0].x3,arrTriangulos[0].y3,arrPontos[3].x,arrPontos[3].y);
		arrTriangulos[3] = new Triangulo(arrTriangulos[0].x3,arrTriangulos[0].y3,arrTriangulos[0].x1,arrTriangulos[0].y1,arrPontos[3].x,arrPontos[3].y);
		numerotriangulos += 3;
		//System.out.println("numero de triangulos " + numerotriangulos);

		int[] deletarTriangulo = new int[1000];
		boolean achou_correspondencia = false;



		k = 0;
		//começar o teste pelos últimos pontos
		for (i =numeroPontosTotal-1;i>=4;i--){ //numeroPontosTotal 

			//System.out.println("começou mais um teste para o ponto  " + i);

			for(j= 1; j < numerotriangulos; j++) {
				//System.out.println("teste do triangulo " + j + " com o ponto " + i);
				if(dentroCirculo(arrPontos[i],arrTriangulos[j])) {
					//System.out.println("ponto " + i + " dentro do circulo " + j);

					deletarTriangulo[k] = j; //salva o triangulo que precisa ser deletado

					//System.out.println(deletarTriangulo[k]);
					k++;
					//System.out.println("valor de k"+k);
					//conta o numero de triangulos para deletar

				}

			}

			//System.out.println(" ");
			//System.out.println(" ");
			//System.out.println("incluir vertices ");
			//montar matriz de vertices
			for(j =0;j<k;j++) {
				if(j==0) {
					//System.out.println("incluir 3 primeiros vertices do triangulo " + deletarTriangulo[j]);
					arrVertices[0] = new Vertice(arrTriangulos[deletarTriangulo[j]].x1,arrTriangulos[deletarTriangulo[j]].y1,arrPontos[i]); //salva os 3 pontos do primeiro triangulo
					//System.out.println(arrVertices[0].x);
					//System.out.println(arrVertices[0].y);
					arrVertices[1] = new Vertice(arrTriangulos[deletarTriangulo[j]].x2,arrTriangulos[deletarTriangulo[j]].y2,arrPontos[i]);
					//System.out.println(arrVertices[1].x);
					//System.out.println(arrVertices[1].y);
					arrVertices[2] = new Vertice(arrTriangulos[deletarTriangulo[j]].x3,arrTriangulos[deletarTriangulo[j]].y3,arrPontos[i]);
					//System.out.println(arrVertices[2].x);
					//System.out.println(arrVertices[2].y);
					numerovertices = 3; //conta o numero de vertices salvos
				}else{

					//System.out.println("verificar e incluir outros vertices ");
					for(n=0;n<numerovertices;n++) {
						//System.out.println("n " + n);
						if((arrTriangulos[deletarTriangulo[j]].x1==arrVertices[n].x) && (arrTriangulos[deletarTriangulo[j]].y1==arrVertices[n].y)){
							//System.out.println("achou correspondencia");
							achou_correspondencia = true;
							break;
						}
						//verifica se existe algum vertice do triangulo a ser deletado que já está no array de vertices
					}
					if(!achou_correspondencia) {
						arrVertices[numerovertices] = new Vertice(arrTriangulos[deletarTriangulo[j]].x1,arrTriangulos[deletarTriangulo[j]].y1,arrPontos[i]);
						//System.out.println(arrTriangulos[deletarTriangulo[j]].x1);
						//System.out.println(arrVertices[n].x);
						//System.out.println(arrTriangulos[deletarTriangulo[j]].y1);
						//System.out.println(arrVertices[n].y);
						//System.out.println("mais um vertice 1 ");
						//System.out.println("incluir um vertice do triangulo " + deletarTriangulo[j]);
						++numerovertices;

					}
					achou_correspondencia = false;
					for(n=0;n<numerovertices;n++) {
						//System.out.println("n " + n);
						if((arrTriangulos[deletarTriangulo[j]].x2==arrVertices[n].x) && (arrTriangulos[deletarTriangulo[j]].y2==arrVertices[n].y)){
							achou_correspondencia = true;
							break;
						}
						//verifica se existe algum vertice do triangulo a ser deletado que já está no array de vertices
					}
					if(!achou_correspondencia) {
						arrVertices[numerovertices] = new Vertice(arrTriangulos[deletarTriangulo[j]].x2,arrTriangulos[deletarTriangulo[j]].y2,arrPontos[i]);
						//System.out.println(arrTriangulos[deletarTriangulo[j]].x2);
						//System.out.println(arrVertices[n].x);
						//System.out.println(arrTriangulos[deletarTriangulo[j]].y2);
						//System.out.println(arrVertices[n].y);
						//System.out.println("mais um vertice 2 ");
						//System.out.println("incluir um vertice do triangulo " + deletarTriangulo[j]);
						++numerovertices;

					}
					achou_correspondencia = false;
					for(n=0;n<numerovertices;n++) {
						//System.out.println("n " + n);
						if((arrTriangulos[deletarTriangulo[j]].x3==arrVertices[n].x) && (arrTriangulos[deletarTriangulo[j]].y3==arrVertices[n].y)){
							achou_correspondencia = true;
							break;
						}
						//verifica se existe algum vertice do triangulo a ser deletado que já está no array de vertices
					}
					if(!achou_correspondencia) {

						arrVertices[numerovertices] = new Vertice(arrTriangulos[deletarTriangulo[j]].x3,arrTriangulos[deletarTriangulo[j]].y3,arrPontos[i]);
						//System.out.println(arrTriangulos[deletarTriangulo[j]].x3);
						//System.out.println(arrVertices[n].x);
						//System.out.println(arrTriangulos[deletarTriangulo[j]].y3);
						//System.out.println(arrVertices[n].y);
						//System.out.println("mais um vertice 3 ");
						//System.out.println("incluir um vertice do triangulo " + deletarTriangulo[j]);
						++numerovertices;

					}
					achou_correspondencia = false;


				}
				//System.out.println("numero de vertices: " + numerovertices);

				//j=2;
				//System.out.println(arrVertices[j].x);
			}
			//System.out.println(" ");
			//System.out.println(" ");

			double[] vetorParaSortear = new double[numerovertices];

			//System.out.println("numero de vertices: "+ numerovertices);
			for(j =0;j<numerovertices;j++) {
				temparrVertices[j] = new Vertice(arrVertices[j].x,arrVertices[j].y);
				temparrVertices[j].anguloParaCentro = arrVertices[j].anguloParaCentro;
				vetorParaSortear[j]= arrVertices[j].anguloParaCentro;
				//System.out.println("x " + arrVertices[j].x + " y " + arrVertices[j].y);

			}

			QuickSort sortVetor = new QuickSort();
			sortVetor.ordenarVetor(vetorParaSortear);


			for(j =0;j<numerovertices;j++) {
				for(l =0;l<numerovertices;l++) {
					//System.out.println(" " + vetorParaSortear[j] + " " + temparrVertices[l].anguloParaCentro);
					if(vetorParaSortear[j]==temparrVertices[l].anguloParaCentro) {
						//System.out.println("                     " + vetorParaSortear[j]);
						arrVertices[j].x = temparrVertices[l].x;
						arrVertices[j].y = temparrVertices[l].y;
						arrVertices[j].anguloParaCentro = temparrVertices[l].anguloParaCentro;					
					}
				}
			}

			//System.out.println("numero vertices " + numerovertices);

			//for(j=0;j<numerovertices;j++) {
			//System.out.println(j);
			//System.out.println("triangulo para deletar " + deletarTriangulo[j]);
			//System.out.println("vertice x " + arrVertices[j].x);
			//}

			//System.out.println("o valor de k e " + k);

			if(k<numerovertices) {
				for(j=0;j<k;j++) {
					//System.out.println(j);
					//System.out.println(arrVertices[j].x);
					arrTriangulos[deletarTriangulo[j]] = new Triangulo(arrVertices[j].x,arrVertices[j].y,arrVertices[j+1].x,arrVertices[j+1].y,arrPontos[i].x,arrPontos[i].y);
					//System.out.println("triangulo  " + deletarTriangulo[j] + " refeito");

				}

				for(j =k;j<numerovertices;j++){

					if(j==(numerovertices-1)) {
						arrTriangulos[numerotriangulos] = new Triangulo(arrVertices[0].x,arrVertices[0].y,arrVertices[numerovertices-1].x,arrVertices[numerovertices-1].y,arrPontos[i].x,arrPontos[i].y);
						//System.out.println("a" + numerotriangulos );
					}else {
						arrTriangulos[numerotriangulos] = new Triangulo(arrVertices[j].x,arrVertices[j].y,arrVertices[j+1].x,arrVertices[j+1].y,arrPontos[i].x,arrPontos[i].y);
						//System.out.println("b" + numerotriangulos);
					}

					numerotriangulos += 1;
					//System.out.println("Triangulo criado, numero de triangulos: " + numerotriangulos);

				}
			}
			else {		
				for(j=0;j<k-1;j++) {
					//System.out.println(j);
					//System.out.println(arrVertices[j].x);
					arrTriangulos[deletarTriangulo[j]] = new Triangulo(arrVertices[j].x,arrVertices[j].y,arrVertices[j+1].x,arrVertices[j+1].y,arrPontos[i].x,arrPontos[i].y);
					//System.out.println("triangulo  " + deletarTriangulo[j] + " refeito");

				}

			}

			numerovertices=0;
			k=0;



			/*
			for(j=0;j<numerotriangulos;j++){

				//shell1.removePaintListener(arrpaintLis[j]);
				//PaintListener arrpaintLis[j] = new PaintListener(null);
				//PaintListener arrpaintLis[j] = 0;
				if(!((arrTriangulos[j].x1== 0)&&(arrTriangulos[j].y1== 0)||(arrTriangulos[j].x2== 0)&&(arrTriangulos[j].y2== 0)||(arrTriangulos[j].x3== 0)&&(arrTriangulos[j].y3== 0)))
					if(!((arrTriangulos[j].x1== 0)&&(arrTriangulos[j].y1== 2000)||(arrTriangulos[j].x2== 0)&&(arrTriangulos[j].y2== 2000)||(arrTriangulos[j].x3== 0)&&(arrTriangulos[j].y3== 2000)))
						if(!((arrTriangulos[j].x1== 2000)&&(arrTriangulos[j].y1== 0)||(arrTriangulos[j].x2== 2000)&&(arrTriangulos[j].y2== 0)||(arrTriangulos[j].x3== 2000)&&(arrTriangulos[j].y3== 0)))
						{
							arrpolygono[j] = new Polygon();
							//desenhartriangulo(arrTriangulos[j], root,arrpolygono[j]);
							//primarystage.show();  
						}
			}


			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			 */


		}//for


		/*//desenha todos os triângulos
		for(j=0;j<numerotriangulos;j++) {
			arrTriangulosNorm[j] = new Triangulo(arrTriangulos[j].x1,arrTriangulos[j].y1,arrTriangulos[j].x2,arrTriangulos[j].y2,arrTriangulos[j].x3,arrTriangulos[j].y3);

			arrpolygono[j] = new Polygon();
			if(checkbox2.isSelected())
				desenhartriangulo(arrTriangulosNorm[j], root,arrpolygono[j]);

		}
		 */





		numeroTriangulosNorm = 0;
		for(j=0;j<numerotriangulos;j++) {
			if(!((arrTriangulos[j].x1== 0)&&(arrTriangulos[j].y1== 0)||(arrTriangulos[j].x2== 0)&&(arrTriangulos[j].y2== 0)||(arrTriangulos[j].x3== 0)&&(arrTriangulos[j].y3== 0)))
				if(!((arrTriangulos[j].x1== 0)&&(arrTriangulos[j].y1== 2000)||(arrTriangulos[j].x2== 0)&&(arrTriangulos[j].y2== 2000)||(arrTriangulos[j].x3== 0)&&(arrTriangulos[j].y3== 2000)))
					if(!((arrTriangulos[j].x1== 2000)&&(arrTriangulos[j].y1== 0)||(arrTriangulos[j].x2== 2000)&&(arrTriangulos[j].y2== 0)||(arrTriangulos[j].x3== 2000)&&(arrTriangulos[j].y3== 0)))
					{
						arrTriangulosNorm[numeroTriangulosNorm] = new Triangulo(arrTriangulos[j].x1,arrTriangulos[j].y1,arrTriangulos[j].x2,arrTriangulos[j].y2,arrTriangulos[j].x3,arrTriangulos[j].y3);
						numeroTriangulosNorm++;
					}
		}

		if(checkbox2.isSelected())
			desenhartriangulos(arrTriangulosNorm, root,arrpolygono,numeroTriangulosNorm);



		System.out.println("_________________________________________________________________________Triangulação acabou________________________________________");




		for (i = 3; i< numeroPontosTotal; i++) {
			if(arrPontos[i].pontoContorno == true)
				arrPontosNorm[i-3] = new Ponto(arrPontos[i].x,arrPontos[i].y,arrPontos[i].valorT);
			else
				arrPontosNorm[i-3] = new Ponto(arrPontos[i].x,arrPontos[i].y);

		}

		System.out.println(numeroTriangulosNorm);

		double[][] q = new double[numeroTriangulosNorm][3];
		double[][] r = new double[numeroTriangulosNorm][3];
		double[] D = new double[numeroTriangulosNorm];

		for (i =0;i<numeroTriangulosNorm;i++)
		{
			q[i][0] = arrTriangulosNorm[i].y2  - arrTriangulosNorm[i].y3;//coordenaday3; //y2-y3
			q[i][1] = arrTriangulosNorm[i].y3 - arrTriangulosNorm[i].y1; //y3-y1
			q[i][2] = arrTriangulosNorm[i].y1 - arrTriangulosNorm[i].y2; //y1-y2

			r[i][0] = arrTriangulosNorm[i].x3 - arrTriangulosNorm[i].x2; //x3-x2
			r[i][1] = arrTriangulosNorm[i].x1 - arrTriangulosNorm[i].x3; //x1-x3
			r[i][2] = arrTriangulosNorm[i].x2 - arrTriangulosNorm[i].x1; //x2-x1

			D[i] = arrTriangulosNorm[i].x2*arrTriangulosNorm[i].y3 - arrTriangulosNorm[i].x3*arrTriangulosNorm[i].y2 + arrTriangulosNorm[i].x3*arrTriangulosNorm[i].y1 - arrTriangulosNorm[i].x1*arrTriangulosNorm[i].y3 + arrTriangulosNorm[i].x1*arrTriangulosNorm[i].y2 - arrTriangulosNorm[i].x2*arrTriangulosNorm[i].y1;
			//System.out.println("D:"+ D[i]);
			//printf("q1,q2,q3 do elemento %d: %f,%f,%f\n",i,q[i][0],q[i][1],q[i][2]);
			//printf("r1,r2,r3 do elemento %d: %f,%f,%f\n",i,r[i][0],r[i][1],r[i][2]);

			//printf("D do elemento %d: %f\n",i,D[i]);
		}

		double[][][] C = new double[numeroTriangulosNorm][3][3];
		for(int elemento = 0;elemento<numeroTriangulosNorm;elemento++)
		{
			//printf("matriz do elemento:%d \n",elemento);
			for(i=0;i<=2;i++)
			{
				for(j=0;j<=2;j++)
				{
					C[elemento][i][j]=1*(q[elemento][i]*q[elemento][j]+r[elemento][i]*r[elemento][j])/(2*D[elemento]);
					//printf("%f ",C[elemento][i][j]);

				}
				//printf("\n");

			}
			//printf("\n");
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

		int[][] matrizdeconectividade = new int[numeroTriangulosNorm][4];
		for(j=0;j<numeroTriangulosNorm;j++) {
			matrizdeconectividade[j][3] = 1;
			for(i=0;i<numeroPontosTotal;i++){

				if((arrTriangulosNorm[j].x1==arrPontosNorm[i].x)&&(arrTriangulosNorm[j].y1==arrPontosNorm[i].y)) {
					matrizdeconectividade[j][0] = i;
					//System.out.println("achou ponto 1");
				}
				if((arrTriangulosNorm[j].x2==arrPontosNorm[i].x)&&(arrTriangulosNorm[j].y2==arrPontosNorm[i].y)) {
					matrizdeconectividade[j][1] = i;
					//System.out.println("achou ponto 2");
				}
				if((arrTriangulosNorm[j].x3==arrPontosNorm[i].x)&&(arrTriangulosNorm[j].y3==arrPontosNorm[i].y)) {
					matrizdeconectividade[j][2] = i;
					//System.out.println("achou ponto 3");
				}

			}
		}

		//for(j=0;j<numeroTriangulosNorm;j++) {
		//	System.out.println("elemento "+j+ " 0: " + matrizdeconectividade[j][0] + " 1: " + matrizdeconectividade[j][1]+" 2: " + matrizdeconectividade[j][2]);
		//}

		for(i=0;i<numeroTriangulosNorm;i++)
		{
			Cglobal[matrizdeconectividade[i][0]][matrizdeconectividade[i][0]] += C[i][0][0] ;
			Cglobal[matrizdeconectividade[i][1]][matrizdeconectividade[i][1]] += C[i][1][1] ;
			Cglobal[matrizdeconectividade[i][2]][matrizdeconectividade[i][2]] += C[i][2][2] ;

			Cglobal[matrizdeconectividade[i][0]][matrizdeconectividade[i][1]] += C[i][0][1] ;
			Cglobal[matrizdeconectividade[i][1]][matrizdeconectividade[i][2]] += C[i][1][2] ;
			Cglobal[matrizdeconectividade[i][2]][matrizdeconectividade[i][0]] += C[i][2][0] ;

			Cglobal[matrizdeconectividade[i][1]][matrizdeconectividade[i][0]] += C[i][0][1] ;
			Cglobal[matrizdeconectividade[i][2]][matrizdeconectividade[i][1]] += C[i][1][2] ;
			Cglobal[matrizdeconectividade[i][0]][matrizdeconectividade[i][2]] += C[i][2][0] ;

		}

		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////

		double[] tensao = new double[numeroPontosTotal];

		for(i=0;i<numeroPontosTotal;i++)
		{
			tensao[i]=0;
			if(arrPontosNorm[i].pontoContorno == true)
			{
				tensao[i]=arrPontosNorm[i].valorT;
				//printf("%d %f\n",i,tensao[i]);
				Cglobal[i][i]=1;
				for(j=0;j<numeroPontosTotal;j++)
				{
					if(i!=j)
						Cglobal[i][j]=0;
				}
			}

		}
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////

		/*
		for(i=0;i<numeroPontosTotal;i++)
		{
			for(j=0;j<numeroPontosTotal;j++)
			{   System.out.print(" ");
				System.out.print(Cglobal[i][j]);
			}
			System.out.println("");
		}
		 */

		double  d=0;
		double[][] inverse = new double[numeroPontosTotal][numeroPontosTotal];

		double[][] Teste= new double[numeroPontosTotal][numeroPontosTotal];
		System.out.println("");

		//printf("\n");
		for(i=0;i<numeroPontosTotal;i++)
		{
			for(j=0;j<numeroPontosTotal;j++)
			{
				Teste[i][j]=Cglobal[i][j];
				//System.out.println(Teste[i][j]);
			}
			//System.out.println("");
		}

		d = determinant(numeroPontosTotal,Teste);

		// printf("%f\n",d);

		//System.out.println(d);
		if (d == 0) {
			System.out.println("Inverse of Entered Matrix is not possible");
		}
		else{
			inverse=inversa(numeroPontosTotal,Cglobal);
			//imprimirMatriz(numeroPontosTotal,inverse);
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

		System.out.println("Campo Elétrico nos elementos:");

		E= new double[numeroTriangulosNorm][2];

		for(i=0;i<numeroTriangulosNorm;i++)
		{
			E[i][0]=-(q[i][0]*produtomatriz[matrizdeconectividade[i][0]]+q[i][1]*produtomatriz[matrizdeconectividade[i][1]]+q[i][2]*produtomatriz[matrizdeconectividade[i][2]])/D[i];
			E[i][1]=-(r[i][0]*produtomatriz[matrizdeconectividade[i][0]]+r[i][1]*produtomatriz[matrizdeconectividade[i][1]]+r[i][2]*produtomatriz[matrizdeconectividade[i][2]])/D[i];
			//printf("\t%f %f\n",q[i][0],tensao[matrizdeconectividade[i][0]-1] );
			System.out.print("Elemento " + i + ":\tEx:\t" + E[i][0] + "\tEy:\t" + E[i][1] + "\n");
		}

		System.out.println("fim");


		if(checkbox1.isSelected())
			desenharVetores(arrTriangulosNorm,E,arrVetorLinha,numeroTriangulosNorm,root);

	}

}
