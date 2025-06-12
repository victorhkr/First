package application;

public class Inversa {

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
	static void imprimirMatriz2(int n, double[][] a) {

		int u,v;

		System.out.println("");

		for(u=0;u<n;u++)
		{
			for(v=0;v<2*n;v++)
			{
				System.out.print(a[u][v]+"\t");
			}
			System.out.println("");
		}

	}

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

	public static void main(String[] args) {

		double[][] a = {{0,-2,67,0,1},{0,20,10,2,1},{0,23,2,0,1},{0,-20,-20,1,1},{-7,-2,-20,1,1}};

		//System.out.print(determinant(4,a));

		double[][] b = inversa(5,a);

		imprimirMatriz(5,a);
		imprimirMatriz(5,b);

	}

}
