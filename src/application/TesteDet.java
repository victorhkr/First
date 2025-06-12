package application;

public class TesteDet {



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


	
	
	
	static double determinant(int n, double[][] a){
		double det=1;
		int i,j,k,m,u,v;
		int swapCount=0;
		
		m=n;

		
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
		 


		for(i=0;i<m-1;i++){

			imprimirMatriz(n, a);
			
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
			
			imprimirMatriz(n, a);
		    
			//Begin Gauss Elimination
			for(k=i+1;k<m;k++){
				double  term = 0;
				if(a[i][i]!=0)
					term=a[k][i]/ a[i][i];

				for(j=0;j<n;j++){
					a[k][j]=a[k][j]-term*a[i][j];
				}
			}
			
			imprimirMatriz(n, a);
		}


		//printf("swap count %d",swapCount);
		/*
	    printf("\n");
	    for(i=0;i<n;i++)
	    {
	  	  for(j=0;j<n;j++)
	  	  {
	  		  printf("%.3f\t",a[i][j]);
	  	  }
	  	  printf("\n");
	    }

		 */


		for(i=0;i<n;i++)
		{
			det =det*a[i][i];
			//System.out.println("determinante: "+ det);
		}
		det=det*Math.pow(-1,swapCount);
		//printf("\ndeterminante e%f\n",det);
		return det;

	}

	public static void main(String[] args) {
		
		double[][] a = {{0,-20,37,0},{-1,2,10,0},{-1,2,2,0},{-1,-2,-20,0}};
		
		System.out.print(determinant(4,a));
		
	}

}
