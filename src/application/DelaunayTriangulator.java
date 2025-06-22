package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DelaunayTriangulator {
    public static boolean dentroCirculo(Ponto a, Triangulo b) {
        return Math.sqrt(Math.pow(b.xc - a.x, 2) + Math.pow(b.yc - a.y, 2)) <= b.r;
    }

    public static boolean pontosIguais(double x1, double y1, double x2, double y2) {
        final double EPSILON = 1e-6;
        return Math.abs(x1 - x2) < EPSILON && Math.abs(y1 - y2) < EPSILON;
    }

    private static boolean verticeExists(ArrayList<Vertice> list, Vertice v) {
        for (Vertice existing : list) {
            if (Math.abs(existing.x - v.x) < 1e-6 &&
                Math.abs(existing.y - v.y) < 1e-6) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSuperTriangleVertex(double x, double y) {
        final double EPSILON = 1e-6;
        if (Math.abs(x - 0) < EPSILON && Math.abs(y - 0) < EPSILON) return true;
        if (Math.abs(x - 0) < EPSILON && Math.abs(y - 2000) < EPSILON) return true;
        if (Math.abs(x - 2000) < EPSILON && Math.abs(y - 0) < EPSILON) return true;
        return false;
    }

    public static ArrayList<Triangulo> triangulate(ArrayList<Ponto> arrPontos, int numeroPontosAtual) {
        ArrayList<Triangulo> arrTriangulosList = new ArrayList<>();

        int numeroPontosTotal = arrPontos.size();

        // Super triangle
        Triangulo superTri = new Triangulo(
            arrPontos.get(0).x, arrPontos.get(0).y,
            arrPontos.get(1).x, arrPontos.get(1).y,
            arrPontos.get(2).x, arrPontos.get(2).y
        );

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

        for (int i = numeroPontosTotal - 1; i >= 4; i--) {
            Ponto pontoAtual = arrPontos.get(i);

            ArrayList<Triangulo> triangulosInvalidos = new ArrayList<>();
            ArrayList<Vertice> arrVerticesList = new ArrayList<>();

            for (Triangulo tri : arrTriangulosList) {
                if (dentroCirculo(pontoAtual, tri)) {
                    triangulosInvalidos.add(tri);

                    Vertice v1 = new Vertice(tri.x1, tri.y1, pontoAtual);
                    if (!verticeExists(arrVerticesList, v1)) arrVerticesList.add(v1);

                    Vertice v2 = new Vertice(tri.x2, tri.y2, pontoAtual);
                    if (!verticeExists(arrVerticesList, v2)) arrVerticesList.add(v2);

                    Vertice v3 = new Vertice(tri.x3, tri.y3, pontoAtual);
                    if (!verticeExists(arrVerticesList, v3)) arrVerticesList.add(v3);
                }
            }

            arrTriangulosList.removeAll(triangulosInvalidos);

            Collections.sort(arrVerticesList, Comparator.comparingDouble(v -> v.anguloParaCentro));

            int numVertices = arrVerticesList.size();
            for (int j = 0; j < numVertices; j++) {
                int next = (j + 1) % numVertices;
                Triangulo novoTri = new Triangulo(
                    arrVerticesList.get(j).x, arrVerticesList.get(j).y,
                    arrVerticesList.get(next).x, arrVerticesList.get(next).y,
                    pontoAtual.x, pontoAtual.y
                );
                arrTriangulosList.add(novoTri);
            }
        }

        ArrayList<Triangulo> arrTriangulosNorm = new ArrayList<>();
        for (Triangulo tri : arrTriangulosList) {
            if (!isSuperTriangleVertex(tri.x1, tri.y1) &&
                !isSuperTriangleVertex(tri.x2, tri.y2) &&
                !isSuperTriangleVertex(tri.x3, tri.y3)) {
                arrTriangulosNorm.add(tri);
            }
        }
        return arrTriangulosNorm;
    }
}