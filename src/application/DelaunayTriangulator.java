package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelaunayTriangulator {
    private static final double EPSILON = 1.0 / 1048576.0;

    private static class Edge {
        final int a, b;
        Edge(int a, int b) {
            this.a = Math.min(a, b);
            this.b = Math.max(a, b);
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Edge)) return false;
            Edge edge = (Edge) o;
            return a == edge.a && b == edge.b;
        }
        @Override
        public int hashCode() {
            return 31 * a + b;
        }
    }

    private static class TriangleData {
        final int i, j, k;
        final double x, y, r;
        TriangleData(int i, int j, int k, double x, double y, double r) {
            this.i = i; this.j = j; this.k = k;
            this.x = x; this.y = y; this.r = r;
        }
    }

    private static List<Ponto> supertriangle(List<Ponto> vertices) {
        double xmin = Double.POSITIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;

        for (Ponto v : vertices) {
            if (v.x < xmin) xmin = v.x;
            if (v.x > xmax) xmax = v.x;
            if (v.y < ymin) ymin = v.y;
            if (v.y > ymax) ymax = v.y;
        }

        double dx = xmax - xmin;
        double dy = ymax - ymin;
        double dmax = Math.max(dx, dy);
        double xmid = xmin + dx * 0.5;
        double ymid = ymin + dy * 0.5;

        List<Ponto> st = new ArrayList<>();
        st.add(new Ponto(xmid - 20 * dmax, ymid - dmax));
        st.add(new Ponto(xmid, ymid + 20 * dmax));
        st.add(new Ponto(xmid + 20 * dmax, ymid - dmax));
        return st;
    }

    private static TriangleData circumcircle(List<Ponto> vertices, int i, int j, int k) {
        double x1 = vertices.get(i).x;
        double y1 = vertices.get(i).y;
        double x2 = vertices.get(j).x;
        double y2 = vertices.get(j).y;
        double x3 = vertices.get(k).x;
        double y3 = vertices.get(k).y;

        double fabsy1y2 = Math.abs(y1 - y2);
        double fabsy2y3 = Math.abs(y2 - y3);

        if (fabsy1y2 < EPSILON && fabsy2y3 < EPSILON) {
            throw new RuntimeException("Coincident points");
        }

        double xc, yc;

        if (fabsy1y2 < EPSILON) {
            double m2 = -((x3 - x2) / (y3 - y2));
            double mx2 = (x2 + x3) / 2.0;
            double my2 = (y2 + y3) / 2.0;
            xc = (x2 + x1) / 2.0;
            yc = m2 * (xc - mx2) + my2;
        } else if (fabsy2y3 < EPSILON) {
            double m1 = -((x2 - x1) / (y2 - y1));
            double mx1 = (x1 + x2) / 2.0;
            double my1 = (y1 + y2) / 2.0;
            xc = (x3 + x2) / 2.0;
            yc = m1 * (xc - mx1) + my1;
        } else {
            double m1 = -((x2 - x1) / (y2 - y1));
            double m2 = -((x3 - x2) / (y3 - y2));
            double mx1 = (x1 + x2) / 2.0;
            double mx2 = (x2 + x3) / 2.0;
            double my1 = (y1 + y2) / 2.0;
            double my2 = (y2 + y3) / 2.0;
            xc = (m1 * mx1 - m2 * mx2 + my2 - my1) / (m1 - m2);
            yc = (fabsy1y2 > fabsy2y3) ? 
                  m1 * (xc - mx1) + my1 : 
                  m2 * (xc - mx2) + my2;
        }

        double dx = x2 - xc;
        double dy = y2 - yc;
        double r = dx * dx + dy * dy;

        return new TriangleData(i, j, k, xc, yc, r);
    }

    private static void dedup(ArrayList<Integer> edges) {
        Map<Edge, Integer> counts = new HashMap<>();
        for (int i = 0; i < edges.size(); i += 2) {
            int a = edges.get(i);
            int b = edges.get(i + 1);
            Edge edge = new Edge(a, b);
            counts.put(edge, counts.getOrDefault(edge, 0) + 1);
        }

        edges.clear();
        for (Map.Entry<Edge, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == 1) {
                edges.add(entry.getKey().a);
                edges.add(entry.getKey().b);
            }
        }
    }

    public static ArrayList<Triangulo> triangulate(ArrayList<Ponto> arrPontos, int numeroPontosAtual) {
        int n = arrPontos.size();
        if (n < 3) return new ArrayList<>();

        List<Ponto> vertices = new ArrayList<>(arrPontos);
        List<Ponto> st = supertriangle(vertices);
        vertices.addAll(st);
        
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;
        
        Arrays.sort(indices, (i, j) -> {
            double diff = vertices.get(j).x - vertices.get(i).x;
            return (diff != 0) ? (int) Math.signum(diff) : i - j;
        });

        ArrayList<TriangleData> open = new ArrayList<>();
        open.add(circumcircle(vertices, n, n + 1, n + 2));
        
        ArrayList<TriangleData> closed = new ArrayList<>();
        ArrayList<Integer> edges = new ArrayList<>();

        for (int i = 0; i < indices.length; i++) {
            int c = indices[i];
            edges.clear();

            for (int j = open.size() - 1; j >= 0; j--) {
                TriangleData t = open.get(j);
                double dx = vertices.get(c).x - t.x;
                
                if (dx > 0 && dx * dx > t.r) {
                    closed.add(t);
                    open.remove(j);
                    continue;
                }
                
                double dy = vertices.get(c).y - t.y;
                if (dx * dx + dy * dy - t.r > EPSILON) {
                    continue;
                }
                
                edges.add(t.i); edges.add(t.j);
                edges.add(t.j); edges.add(t.k);
                edges.add(t.k); edges.add(t.i);
                open.remove(j);
            }
            
            dedup(edges);
            
            for (int e = 0; e < edges.size(); e += 2) {
                open.add(circumcircle(vertices, edges.get(e), edges.get(e + 1), c));
            }
        }
        
        closed.addAll(open);
        ArrayList<Triangulo> result = new ArrayList<>();
        for (TriangleData t : closed) {
            // Apenas triângulos cujos índices estão no intervalo [0, n-1]
            if (t.i < n && t.j < n && t.k < n) {
                Ponto p1 = vertices.get(t.i);
                Ponto p2 = vertices.get(t.j);
                Ponto p3 = vertices.get(t.k);
                result.add(new Triangulo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y));
            }
        }
        
        return result;
    }
}