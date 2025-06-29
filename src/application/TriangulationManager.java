package application;

import javafx.scene.layout.Pane;

import java.util.ArrayList;

import javafx.scene.control.CheckBox;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Line;

public class TriangulationManager {
    int numeroTriangulosNorm;
    ArrayList<Triangulo> arrTriangulosNorm = new ArrayList<>();
    ArrayList<Polygon> arrpolygono = new ArrayList<>();
    ArrayList<Line[]> arrVetorLinha = new ArrayList<>();
    double[][] E;

    public TriangulationManager(Pane root, ArrayList<Ponto> arrPontosList, int numeroPontosAtual, CheckBox checkbox1, CheckBox checkbox2) {
    	
        // 1. Mesh refinement
        ArrayList<Ponto> arrPontos = MeshRefiner.refineMesh(arrPontosList, numeroPontosAtual);

        // 2. Delaunay triangulation (já filtra pontos do super-triângulo)
        arrTriangulosNorm = DelaunayTriangulator.triangulate(arrPontos, arrPontos.size());
        numeroTriangulosNorm = arrTriangulosNorm.size();

        // 3. Usar TODOS os pontos (já refinados) para o FEM
        ArrayList<Ponto> arrPontosNorm = new ArrayList<>(arrPontos); // Não remova pontos!

        // 4. Solve FEM
        E = FEMSolver.solve(arrTriangulosNorm, arrPontosNorm);

        // 5. Optionally draw triangles
        if (checkbox2.isSelected())
        	MeshDrawer.desenharTriangulos(arrTriangulosNorm, root, arrpolygono);

        // 6. Optionally draw field vectors
        if (checkbox1.isSelected())
            MeshDrawer.desenharVetores(arrTriangulosNorm, E, arrVetorLinha, root);
    }
}