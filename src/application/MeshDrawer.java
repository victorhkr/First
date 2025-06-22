package application;

import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.layout.Pane;

public class MeshDrawer {
    public static void desenharTriangulos(ArrayList<Triangulo> objTriangulo, Pane root, ArrayList<Polygon> arrpolygono) {
        arrpolygono.clear();
        for (Triangulo triangulo : objTriangulo) {
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

    public static void deletarTriangulos(Pane root, ArrayList<Polygon> arrpolygono) {
        for (Polygon poly : arrpolygono) {
            root.getChildren().remove(poly);
        }
        arrpolygono.clear();
    }

    public static void desenharVetores(ArrayList<Triangulo> arrTriangulosNorm, double[][] E, ArrayList<Line[]> arrVetorLinha, Pane root) {
        double Emax = 0;
        int numeroTriangulos = arrTriangulosNorm.size();

        for (int i = 0; i < numeroTriangulos; i++) {
            double magnitude = Math.sqrt(Math.pow(E[i][0], 2) + Math.pow(E[i][1], 2));
            if (magnitude > Emax) {
                Emax = magnitude;
            }
        }

        for (int i = 0; i < numeroTriangulos; i++) {
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

            vetorLines[0] = new Line(x1, y1, x2, y2);
            vetorLines[0].setStrokeWidth(1);
            vetorLines[0].setStroke(Color.DARKRED);

            vetorLines[1] = new Line(x2, y2, x2 - difx - dify, y2 - dify + difx);
            vetorLines[1].setStrokeWidth(2);
            vetorLines[1].setStroke(Color.DARKRED);

            vetorLines[2] = new Line(x2, y2, x2 - difx + dify, y2 - dify - difx);
            vetorLines[2].setStrokeWidth(2);
            vetorLines[2].setStroke(Color.DARKRED);

            if (i < arrVetorLinha.size()) {
                Line[] oldLines = arrVetorLinha.get(i);
                if (oldLines != null) {
                    for (Line line : oldLines) {
                        if (line != null) root.getChildren().remove(line);
                    }
                }
                arrVetorLinha.set(i, vetorLines);
            } else {
                arrVetorLinha.add(vetorLines);
            }

            root.getChildren().addAll(vetorLines);
        }
    }

    public static void deletarVetores(ArrayList<Line[]> arrVetorLinha, Pane root) {
        for (Line[] lines : arrVetorLinha) {
            for (Line line : lines) {
                root.getChildren().remove(line);
            }
        }
        arrVetorLinha.clear();
    }
}