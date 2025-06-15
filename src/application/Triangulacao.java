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
        double[][] matrizInversa = new double[n][2*n];

        int i,j,m,k;
        double temp,term;

        m=n;
        // Create augmented matrix [A|I]
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

        // Forward elimination with partial pivoting
        for(i=0;i<m-1;i++){
            for(k=i+1;k<m;k++){
                if((Math.abs(matrizAumentada[i][i])<Math.abs(matrizAumentada[k][i]))){
                    for(j=0;j<2*n;j++)
                    {
                        temp=matrizAumentada[i][j];
                        matrizAumentada[i][j]=matrizAumentada[k][j];
                        matrizAumentada[k][j]=temp;
                    }
                }
            }
            for(k=i+1;k<m;k++){
                term = 0;
                if(matrizAumentada[i][i]!=0)
                    term=matrizAumentada[k][i]/ matrizAumentada[i][i];
                for(j=0;j<2*n;j++){
                    matrizAumentada[k][j]=matrizAumentada[k][j]-term*matrizAumentada[i][j];
                }
            }
        }
        // Backward elimination
        for(i=m-1;i>0;i--){
            for(k=i-1;k>=0;k--){
                term=matrizAumentada[k][i]/matrizAumentada[i][i];
                for(j=2*n-1;j>=0;j--){
                    matrizAumentada[k][j]=matrizAumentada[k][j]-term*matrizAumentada[i][j];
                }
            }
        }
        // Extract inverse from augmented matrix
        for(i=0;i<n;i++){
            for(j=0;j<n;j++) {
                matrizInversa[i][j] = matrizAumentada[i][j+n]/matrizAumentada[i][i];
            }
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

    /**
     * Removes all triangles from the JavaFX scene.
     */
    public static void deletartriangulos(Group root, Polygon[] arrpolygono, int numeroTriangulosNorm){
        int j=0;
        for(j=0;j<numeroTriangulosNorm;j++) {
            root.getChildren().remove(arrpolygono[j]);     
        }
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

    /**
     * Draws field vectors (e.g., electric field) at the centroid of each triangle.
     * @param arrTriangulosNorm Array of triangles
     * @param E Field vectors for each triangle
     * @param arrVetorLinha Array to store drawn lines
     * @param numeroTriangulosNormFinal Number of triangles
     * @param root JavaFX group to add to
     */
    public static void desenharVetores(Triangulo[] arrTriangulosNorm, double[][] E, Line[][] arrVetorLinha, int numeroTriangulosNormFinal, Group root) {
        int i;
        double x,y,u,v,x1,x2,y1,y2,difx,dify,Emax=0;

        // Find max field magnitude for normalization
        for(i=0;i<numeroTriangulosNormFinal;i++){
            x = Math.sqrt(Math.pow(E[i][0], 2)+Math.pow(E[i][1],2));
            if(x>Emax)
                Emax = x;
        }

        // Draw vectors as lines with arrowheads
        for(i=0;i<numeroTriangulosNormFinal;i++){
            x = arrTriangulosNorm[i].centroideX;
            y = arrTriangulosNorm[i].centroideY;
            u = 30*E[i][0]/Emax;
            v = 30*E[i][1]/Emax;
            x1 = x;
            x2 = x+u;
            y1 = y;
            y2 = y+v;         

            difx= (x2 -x1)/3;
            dify = (y2 -y1)/3;

            arrVetorLinha[i][0] = new Line();
            arrVetorLinha[i][1] = new Line();
            arrVetorLinha[i][2] = new Line();

            arrVetorLinha[i][0].setStrokeWidth(1);
            arrVetorLinha[i][1].setStrokeWidth(2);
            arrVetorLinha[i][2].setStrokeWidth(2);

            arrVetorLinha[i][0].setStroke(Color.DARKRED);
            arrVetorLinha[i][1].setStroke(Color.DARKRED);
            arrVetorLinha[i][2].setStroke(Color.DARKRED);

            arrVetorLinha[i][0].setStartX(x1);
            arrVetorLinha[i][0].setStartY(y1);
            arrVetorLinha[i][0].setEndX(x2);
            arrVetorLinha[i][0].setEndY(y2);

            arrVetorLinha[i][1].setStartX(x2);
            arrVetorLinha[i][1].setStartY(y2);
            arrVetorLinha[i][1].setEndX(x2-difx-dify);
            arrVetorLinha[i][1].setEndY(y2-dify+difx);

            arrVetorLinha[i][2].setStartX(x2);
            arrVetorLinha[i][2].setStartY(y2);
            arrVetorLinha[i][2].setEndX(x2-difx +dify);
            arrVetorLinha[i][2].setEndY(y2-dify-difx);

            root.getChildren().add(arrVetorLinha[i][0]);
            root.getChildren().add(arrVetorLinha[i][1]);
            root.getChildren().add(arrVetorLinha[i][2]);
        }
    }

    /**
     * Removes all field vector lines from the JavaFX scene.
     */
    public static void deletarVetores(Line[][] arrVetorLinha, int numeroTriangulosNorm, Group root) {
        int i = 0;
        for(i=0;i<numeroTriangulosNorm;i++){
            root.getChildren().remove(arrVetorLinha[i][0]);
            root.getChildren().remove(arrVetorLinha[i][1]);
            root.getChildren().remove(arrVetorLinha[i][2]);
        }
    }

    // Number of valid triangles (not boundary triangles)
    int numeroTriangulosNorm;
    // Arrays for triangles, vectors, and polygons
    Triangulo[] arrTriangulosNorm = new Triangulo[20000];
    Line[][] arrVetorLinha = new Line[6000][3];
    Polygon[] arrpolygono = new Polygon[20000];
    double[][] E; // Field vector (e.g., electric field) for each triangle

    /**
     * Main constructor that performs triangulation and prepares mesh for FEM.
     * @param root JavaFX scene group
     * @param arrPontos Array of input points
     * @param numeroPontosAtual Number of input points
     * @param checkbox1 Checkbox for drawing field vectors
     * @param checkbox2 Checkbox for drawing triangles
     */
    Triangulacao(Group root, Ponto[] arrPontos, int numeroPontosAtual, CheckBox checkbox1, CheckBox checkbox2) {
        // Arrays for working points, vertices, and triangles
        Ponto[] arrPontosNorm = new Ponto[20000];
        Vertice[] arrVertices = new Vertice[10000];
        Vertice[] temparrVertices = new Vertice[25000];
        Triangulo[] arrTriangulos = new Triangulo[20000];

        int i = 0, j = 0, k = 0, l=0, n=0;
        int numerovertices = 0;
        int numeroPontosTotal = 0;
        int numeroPontosContorno;

        // Point proximity flag for mesh refinement
        boolean pontoProximo = false;
        numeroPontosContorno = numeroPontosAtual - 3;

        // Generate grid points for the mesh, avoiding points too close to contour
        for (i = 0; i< 40; i++){
            for (j = 0; j< 30; j++){
                pontoProximo=false;
                for(k=3;k<numeroPontosContorno-3;k++){
                    if(Math.sqrt(Math.pow(5+20*i-arrPontos[k].x,2)+Math.pow(5+20*j-arrPontos[k].y,2))<10) {
                        pontoProximo=true;
                    }
                }
                if(!pontoProximo){
                    arrPontos[numeroPontosAtual] = new Ponto(5+20*i-(Math.floor(Math.random()*(5)+0)),5+20*j-(Math.floor(Math.random()*(5)+0)));
                    numeroPontosAtual++;
                }
            }
        }
        numeroPontosTotal = numeroPontosAtual;

        // --- Triangulation initialization ---
        // Create super triangle (covers all points)
        arrTriangulos[0] = new Triangulo(arrPontos[0].x,arrPontos[0].y,arrPontos[1].x,arrPontos[1].y,arrPontos[2].x,arrPontos[2].y);
        int numerotriangulos = 1;

        // Add first contour point to create three new triangles
        arrTriangulos[1] = new Triangulo(arrTriangulos[0].x1,arrTriangulos[0].y1,arrTriangulos[0].x2,arrTriangulos[0].y2,arrPontos[3].x,arrPontos[3].y);
        arrTriangulos[2] = new Triangulo(arrTriangulos[0].x2,arrTriangulos[0].y2,arrTriangulos[0].x3,arrTriangulos[0].y3,arrPontos[3].x,arrPontos[3].y);
        arrTriangulos[3] = new Triangulo(arrTriangulos[0].x3,arrTriangulos[0].y3,arrTriangulos[0].x1,arrTriangulos[0].y1,arrPontos[3].x,arrPontos[3].y);
        numerotriangulos += 3;

        int[] deletarTriangulo = new int[1000];
        boolean achou_correspondencia = false;

        k = 0;
        // Main loop: add each point, updating mesh with Delaunay criterion
        for (i =numeroPontosTotal-1;i>=4;i--){ // process points from last to first
            // Find triangles whose circumcircle contains the new point (need to be deleted)
            for(j= 1; j < numerotriangulos; j++) {
                if(dentroCirculo(arrPontos[i],arrTriangulos[j])) {
                    deletarTriangulo[k] = j; // mark triangle for deletion
                    k++;
                }
            }
            // Collect unique boundary vertices from triangles to be deleted
            for(j =0;j<k;j++) {
                if(j==0) {
                    // Add all three vertices from the first triangle to be deleted
                    arrVertices[0] = new Vertice(arrTriangulos[deletarTriangulo[j]].x1,arrTriangulos[deletarTriangulo[j]].y1,arrPontos[i]);
                    arrVertices[1] = new Vertice(arrTriangulos[deletarTriangulo[j]].x2,arrTriangulos[deletarTriangulo[j]].y2,arrPontos[i]);
                    arrVertices[2] = new Vertice(arrTriangulos[deletarTriangulo[j]].x3,arrTriangulos[deletarTriangulo[j]].y3,arrPontos[i]);
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
                        arrVertices[numerovertices] = new Vertice(arrTriangulos[deletarTriangulo[j]].x1,arrTriangulos[deletarTriangulo[j]].y1,arrPontos[i]);
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
                        arrVertices[numerovertices] = new Vertice(arrTriangulos[deletarTriangulo[j]].x2,arrTriangulos[deletarTriangulo[j]].y2,arrPontos[i]);
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
                        arrVertices[numerovertices] = new Vertice(arrTriangulos[deletarTriangulo[j]].x3,arrTriangulos[deletarTriangulo[j]].y3,arrPontos[i]);
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
            for(j =0;j<numerovertices;j++) {
                for(l =0;l<numerovertices;l++) {
                    if(vetorParaSortear[j]==temparrVertices[l].anguloParaCentro) {
                        arrVertices[j].x = temparrVertices[l].x;
                        arrVertices[j].y = temparrVertices[l].y;
                        arrVertices[j].anguloParaCentro = temparrVertices[l].anguloParaCentro;                 
                    }
                }
            }

            // Rebuild triangles with the new point
            if(k<numerovertices) {
                // For each boundary edge, build a new triangle with the new point
                for(j=0;j<k;j++) {
                    arrTriangulos[deletarTriangulo[j]] = new Triangulo(arrVertices[j].x,arrVertices[j].y,arrVertices[j+1].x,arrVertices[j+1].y,arrPontos[i].x,arrPontos[i].y);
                }
                for(j =k;j<numerovertices;j++){
                    if(j==(numerovertices-1)) {
                        arrTriangulos[numerotriangulos] = new Triangulo(arrVertices[0].x,arrVertices[0].y,arrVertices[numerovertices-1].x,arrVertices[numerovertices-1].y,arrPontos[i].x,arrPontos[i].y);
                    }else {
                        arrTriangulos[numerotriangulos] = new Triangulo(arrVertices[j].x,arrVertices[j].y,arrVertices[j+1].x,arrVertices[j+1].y,arrPontos[i].x,arrPontos[i].y);
                    }
                    numerotriangulos += 1;
                }
            }
            else {      
                for(j=0;j<k-1;j++) {
                    arrTriangulos[deletarTriangulo[j]] = new Triangulo(arrVertices[j].x,arrVertices[j].y,arrVertices[j+1].x,arrVertices[j+1].y,arrPontos[i].x,arrPontos[i].y);
                }
            }

            numerovertices=0;
            k=0;
        }

        // Filter out triangles that include super triangle vertices
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

        // Optionally draw triangles on screen
        if(checkbox2.isSelected())
            desenhartriangulos(arrTriangulosNorm, root,arrpolygono,numeroTriangulosNorm);

        // --- FEM Preparation ---
        // Prepare points for field calculation
        for (i = 3; i< numeroPontosTotal; i++) {
            if(arrPontos[i].pontoContorno == true)
                arrPontosNorm[i-3] = new Ponto(arrPontos[i].x,arrPontos[i].y,arrPontos[i].valorT);
            else
                arrPontosNorm[i-3] = new Ponto(arrPontos[i].x,arrPontos[i].y);
        }

        // Prepare local matrices for each triangle
        double[][] q = new double[numeroTriangulosNorm][3];
        double[][] r = new double[numeroTriangulosNorm][3];
        double[] D = new double[numeroTriangulosNorm];

        for (i =0;i<numeroTriangulosNorm;i++)
        {
            q[i][0] = arrTriangulosNorm[i].y2  - arrTriangulosNorm[i].y3; // y2-y3
            q[i][1] = arrTriangulosNorm[i].y3 - arrTriangulosNorm[i].y1; // y3-y1
            q[i][2] = arrTriangulosNorm[i].y1 - arrTriangulosNorm[i].y2; // y1-y2

            r[i][0] = arrTriangulosNorm[i].x3 - arrTriangulosNorm[i].x2; // x3-x2
            r[i][1] = arrTriangulosNorm[i].x1 - arrTriangulosNorm[i].x3; // x1-x3
            r[i][2] = arrTriangulosNorm[i].x2 - arrTriangulosNorm[i].x1; // x2-x1

            D[i] = arrTriangulosNorm[i].x2*arrTriangulosNorm[i].y3 - arrTriangulosNorm[i].x3*arrTriangulosNorm[i].y2 + arrTriangulosNorm[i].x3*arrTriangulosNorm[i].y1 - arrTriangulosNorm[i].x1*arrTriangulosNorm[i].y3 + arrTriangulosNorm[i].x1*arrTriangulosNorm[i].y2 - arrTriangulosNorm[i].x2*arrTriangulosNorm[i].y1;
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
        int[][] matrizdeconectividade = new int[numeroTriangulosNorm][4];
        for(j=0;j<numeroTriangulosNorm;j++) {
            matrizdeconectividade[j][3] = 1;
            for(i=0;i<numeroPontosTotal;i++){
                if((arrTriangulosNorm[j].x1==arrPontosNorm[i].x)&&(arrTriangulosNorm[j].y1==arrPontosNorm[i].y)) {
                    matrizdeconectividade[j][0] = i;
                }
                if((arrTriangulosNorm[j].x2==arrPontosNorm[i].x)&&(arrTriangulosNorm[j].y2==arrPontosNorm[i].y)) {
                    matrizdeconectividade[j][1] = i;
                }
                if((arrTriangulosNorm[j].x3==arrPontosNorm[i].x)&&(arrTriangulosNorm[j].y3==arrPontosNorm[i].y)) {
                    matrizdeconectividade[j][2] = i;
                }
            }
        }

        // Assemble element matrices into the global matrix
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

        // Apply boundary conditions
        double[] tensao = new double[numeroPontosTotal];
        for(i=0;i<numeroPontosTotal;i++)
        {
            tensao[i]=0;
            if(arrPontosNorm[i].pontoContorno == true)
            {
                tensao[i]=arrPontosNorm[i].valorT;
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
        E= new double[numeroTriangulosNorm][2];
        for(i=0;i<numeroTriangulosNorm;i++)
        {
            E[i][0]=-(q[i][0]*produtomatriz[matrizdeconectividade[i][0]]+q[i][1]*produtomatriz[matrizdeconectividade[i][1]]+q[i][2]*produtomatriz[matrizdeconectividade[i][2]])/D[i];
            E[i][1]=-(r[i][0]*produtomatriz[matrizdeconectividade[i][0]]+r[i][1]*produtomatriz[matrizdeconectividade[i][1]]+r[i][2]*produtomatriz[matrizdeconectividade[i][2]])/D[i];
            System.out.print("Elemento " + i + ":\tEx:\t" + E[i][0] + "\tEy:\t" + E[i][1] + "\n");
        }
        System.out.println("fim");

        // Optionally draw vectors on screen
        if(checkbox1.isSelected())
            desenharVetores(arrTriangulosNorm,E,arrVetorLinha,numeroTriangulosNorm,root);
    }
}