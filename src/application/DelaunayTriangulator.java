package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelaunayTriangulator {
    private static final double EPSILON = 1.0 / 1048576.0; // Small epsilon value for floating-point comparisons

    // Edge class to represent an edge between two vertices
    private static class Edge {
        final int a, b; // Vertex indices

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

    // Class to hold triangle data and circumcircle information
    private static class TriangleData {
        final int i, j, k; // Vertex indices
        final double x, y, r; // Circumcircle center (x,y) and squared radius

        TriangleData(int i, int j, int k, double x, double y, double r) {
            this.i = i; this.j = j; this.k = k;
            this.x = x; this.y = y; this.r = r;
        }
    }

    // Creates a super triangle that contains all given vertices
    private static List<Ponto> supertriangle(List<Ponto> vertices) {
        double xmin = Double.POSITIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;

        // Find bounding box of vertices
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

        // Create super triangle vertices (large enough to contain all points)
        List<Ponto> st = new ArrayList<>();
        st.add(new Ponto(xmid - 20 * dmax, ymid - dmax));
        st.add(new Ponto(xmid, ymid + 20 * dmax));
        st.add(new Ponto(xmid + 20 * dmax, ymid - dmax));
        return st;
    }

    // Computes circumcircle of three points using robust determinant method
    private static TriangleData circumcircle(List<Ponto> vertices, int i, int j, int k) {
        double x1 = vertices.get(i).x;
        double y1 = vertices.get(i).y;
        double x2 = vertices.get(j).x;
        double y2 = vertices.get(j).y;
        double x3 = vertices.get(k).x;
        double y3 = vertices.get(k).y;

        // Vector calculations
        double A = x2 - x1;
        double B = y2 - y1;
        double C = x3 - x1;
        double D = y3 - y1;
        double E = A * (x1 + x2) + B * (y1 + y2);
        double F = C * (x1 + x3) + D * (y1 + y3);
        double G = 2.0 * (A * (y3 - y2) - B * (x3 - x2));

        // Check for collinear points (degenerate triangle)
        if (Math.abs(G) < EPSILON) {
            throw new RuntimeException("Collinear points detected");
        }

        // Calculate circumcircle center
        double xc = (D * E - B * F) / G;
        double yc = (A * F - C * E) / G;
        double dx = x1 - xc;
        double dy = y1 - yc;
        double r = dx * dx + dy * dy; // Squared radius

        return new TriangleData(i, j, k, xc, yc, r);
    }

    // Removes duplicate edges (keeps only edges with odd counts)
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
            if (entry.getValue() == 1) { // Only keep unique edges
                edges.add(entry.getKey().a);
                edges.add(entry.getKey().b);
            }
        }
    }

    // Main triangulation method
    public static ArrayList<Triangulo> triangulate(ArrayList<Ponto> arrPontos, int numeroPontosAtual) {
        int n = arrPontos.size();
        if (n < 3) return new ArrayList<>(); // Not enough points

        List<Ponto> vertices = new ArrayList<>(arrPontos);
        List<Ponto> st = supertriangle(vertices); // Create super triangle
        vertices.addAll(st); // Add super triangle vertices
        
        // Create and sort indices by x-coordinate (ascending), then y
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;
        
        Arrays.sort(indices, new Comparator<Integer>() {
            @Override
            public int compare(Integer i1, Integer i2) {
                int cmp = Double.compare(vertices.get(i1).x, vertices.get(i2).x);
                if (cmp != 0) return cmp;
                return Double.compare(vertices.get(i1).y, vertices.get(i2).y);
            }
        });

        ArrayList<TriangleData> open = new ArrayList<>(); // Active triangles
        open.add(circumcircle(vertices, n, n + 1, n + 2)); // Add super triangle
        
        ArrayList<TriangleData> closed = new ArrayList<>(); // Completed triangles
        ArrayList<Integer> edges = new ArrayList<>(); // Temporary edge buffer

        // Process each point in sorted order
        for (int idx : indices) {
            edges.clear();

            // Check triangles in reverse order
            for (int j = open.size() - 1; j >= 0; j--) {
                TriangleData t = open.get(j);
                
                // Check if point is outside circumcircle
                double dx = vertices.get(idx).x - t.x;
                if (dx > 0 && dx * dx > t.r) {
                    closed.add(t); // Triangle is complete
                    open.remove(j);
                    continue;
                }
                
                // Check if point is inside circumcircle
                double dy = vertices.get(idx).y - t.y;
                if (dx * dx + dy * dy > t.r + EPSILON) {
                    continue;
                }
                
                // Point is inside - add edges and remove triangle
                edges.add(t.i); edges.add(t.j);
                edges.add(t.j); edges.add(t.k);
                edges.add(t.k); edges.add(t.i);
                open.remove(j);
            }
            
            // Remove duplicate edges
            dedup(edges);
            
            // Create new triangles from unique edges
            for (int e = 0; e < edges.size(); e += 2) {
                open.add(circumcircle(vertices, edges.get(e), edges.get(e + 1), idx));
            }
        }
        
        // Move all remaining open triangles to closed
        closed.addAll(open);
        
        // Filter out triangles containing super triangle vertices
        ArrayList<Triangulo> result = new ArrayList<>();
        for (TriangleData t : closed) {
            if (t.i < n && t.j < n && t.k < n) { // Only original vertices
                Ponto p1 = vertices.get(t.i);
                Ponto p2 = vertices.get(t.j);
                Ponto p3 = vertices.get(t.k);
                result.add(new Triangulo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y));
            }
        }
        
        return result;
    }
}