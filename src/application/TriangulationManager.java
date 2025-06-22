package application;

import java.util.ArrayList;
import javafx.scene.layout.Pane;
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

        // 2. Delaunay triangulation
        arrTriangulosNorm = DelaunayTriangulator.triangulate(arrPontos, arrPontos.size());
        numeroTriangulosNorm = arrTriangulosNorm.size();

        // 3. Optionally draw triangles
        if (checkbox2.isSelected())
            MeshDrawer.desenharTriangulos(arrTriangulosNorm, root, arrpolygono);

        // 4. Prepare FEM nodes (skip super triangle and contour logic)
        ArrayList<Ponto> arrPontosNorm = new ArrayList<>();
        for (int i = 3; i < arrPontos.size(); i++) {
            Ponto pontoAtual = arrPontos.get(i);
            if (pontoAtual.pontoContorno) {
                arrPontosNorm.add(new Ponto(pontoAtual.x, pontoAtual.y, pontoAtual.valorT));
            } else {
                arrPontosNorm.add(new Ponto(pontoAtual.x, pontoAtual.y));
            }
        }

        // 5. Solve FEM
        E = FEMSolver.solve(arrTriangulosNorm, arrPontosNorm);

        // 6. Optionally draw field vectors
        if (checkbox1.isSelected())
            MeshDrawer.desenharVetores(arrTriangulosNorm, E, arrVetorLinha, root);
    }
}