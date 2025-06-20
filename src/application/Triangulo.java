package application;

/**
 * Represents a triangle in 2D space with geometric utilities.
 */
public class Triangulo {

    // Coordinates of the three triangle vertices
    double x1;
    double y1;
    double x2;
    double y2;
    double x3;
    double y3;

    // Midpoints and inclinations for circumcenter calculations
    double centerabx;
    double centeraby;
    double centerbcx;
    double centerbcy;
    double perp_incl_ab;
    double perp_incl_bc;

    // Coordinates and radius of the triangle's circumcenter
    double yc;
    double xc;
    double r;

    // Coordinates of the centroid
    double centroideX;
    double centroideY;

    // Angles from centroid to each vertex
    double anguloParaCentro1;
    double anguloParaCentro2;
    double anguloParaCentro3;

    // Constant for pi
    final double PI = Math.PI;

    /**
     * Calculates the angle between the centroid and a point (x, y) in degrees.
     * Used for sorting triangle vertices in a consistent order.
     */
    public double acharAngulo(double x, double y) {
        double dx = x - centroideX;
        double dy = y - centroideY;
        double anguloRad = Math.atan2(dy, dx);
        double anguloGraus = Math.toDegrees(anguloRad);
        return (anguloGraus < 0) ? anguloGraus + 360 : anguloGraus;
    }

    /**
     * Constructs a triangle from three points (x1, y1), (x2, y2), (x3, y3).
     * Computes centroid, angles, and circumcenter properties.
     * Also normalizes the order of vertices for consistent orientation.
     */
    public Triangulo(double x1, double y1, double x2, double y2, double x3, double y3) {

        // Store the coordinates
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;


        // Calcular a área do triângulo
        double area = Math.abs((x1*(y2-y3) + x2*(y3-y1) + x3*(y1-y2)))/2.0;

        if (area < 1e-10) {
            // Triângulo degenerado (área zero)
            this.xc = 0;
            this.yc = 0;
            this.r = Double.MAX_VALUE; // Raio infinito para pontos colineares
            return; // Pular cálculos adicionais
        }
        
        // Compute centroid (average of all points)
        centroideX = (x1 + x2 + x3) / 3;
        centroideY = (y1 + y2 + y3) / 3;

        // Compute angles from centroid to each vertex
        anguloParaCentro1 = acharAngulo(x1, y1);
        anguloParaCentro2 = acharAngulo(x2, y2);
        anguloParaCentro3 = acharAngulo(x3, y3);

        // Normalize the order of the vertices for consistent orientation
        if (anguloParaCentro1 > anguloParaCentro2) {
            if (anguloParaCentro1 > anguloParaCentro3) {
                this.x3 = x1;
                this.y3 = y1;
                if (anguloParaCentro2 > anguloParaCentro3) {
                    this.x2 = x2;
                    this.y2 = y2;
                    this.x1 = x3;
                    this.y1 = y3;
                } else {
                    this.x2 = x3;
                    this.y2 = y3;
                    this.x1 = x2;
                    this.y1 = y2;
                }
            } else {
                this.x3 = x3;
                this.y3 = y3;
                this.x2 = x1;
                this.y2 = y1;
                this.x1 = x2;
                this.y1 = y2;
            }
        } else {
            if (anguloParaCentro2 < anguloParaCentro3) {
                this.x1 = x1;
                this.y1 = y1;
                this.x2 = x2;
                this.y2 = y2;
                this.x3 = x3;
                this.y3 = y3;
            } else {
                this.x1 = x1;
                this.y1 = y1;
                this.x2 = x3;
                this.y2 = y3;
                this.x3 = x2;
                this.y3 = y2;
            }
        }

        // Recalculate angles with possibly reordered vertices
        anguloParaCentro1 = acharAngulo(this.x1, this.y1);
        anguloParaCentro2 = acharAngulo(this.x2, this.y2);
        anguloParaCentro3 = acharAngulo(this.x3, this.y3);

        // Calculate circumcenter and radius
        // Uses perpendicular bisectors of triangle sides
        if ((this.y1 - this.y2) == 0) {
            // Special case: side AB is horizontal
            centerbcx = (this.x2 + this.x3) / 2;
            centerbcy = (this.y2 + this.y3) / 2;
            xc = (this.x2 + this.x1) / 2;
            perp_incl_bc = -(this.x3 - this.x2) / (this.y3 - this.y2);
            yc = perp_incl_bc * (xc - centerbcx) + centerbcy;
        } else if ((this.y3 - this.y2) == 0) {
            // Special case: side BC is horizontal
            centerabx = (this.x1 + this.x2) / 2;
            centeraby = (this.y1 + this.y2) / 2;
            xc = (this.x3 + this.x2) / 2;
            perp_incl_ab = -(this.x2 - this.x1) / (this.y2 - this.y1);
            yc = perp_incl_ab * (xc - centerabx) + centeraby;
        } else {
            // General case: compute intersection of two perpendicular bisectors
            centerabx = (this.x1 + this.x2) / 2;
            centeraby = (this.y1 + this.y2) / 2;
            centerbcx = (this.x2 + this.x3) / 2;
            centerbcy = (this.y2 + this.y3) / 2;
            perp_incl_ab = -(this.x2 - this.x1) / (this.y2 - this.y1);
            perp_incl_bc = -(this.x3 - this.x2) / (this.y3 - this.y2);
            yc = (-perp_incl_bc * (centeraby - perp_incl_ab * centerabx) + perp_incl_ab * (centerbcy - perp_incl_bc * centerbcx)) / (-perp_incl_bc + perp_incl_ab);
            xc = (-1 * (centeraby - perp_incl_ab * centerabx) + 1 * (centerbcy - perp_incl_bc * centerbcx)) / (-perp_incl_bc + perp_incl_ab);
        }

        // Compute circumradius (distance from circumcenter to any vertex)
        r = Math.sqrt(Math.pow(this.x1 - xc, 2) + Math.pow(this.y1 - yc, 2));

        /*
        // Alternative (commented) circumcenter calculation
        if((this.y1-this.y2)==0) {
            yc= (this.y1+this.y2)/2;
            centerabx = (this.x1 + this.x3)/2;
            centeraby = (this.y1 + this.y3)/2;
            perpgradab = -(this.x1-this.x2)/(this.y1-this.y3);
        }else {
            yc= (this.y1+this.y3)/2;
            centerabx = (this.x1 + this.x2)/2;
            centeraby = (this.y1 + this.y2)/2;
            perpgradab = -(this.x1-this.x2)/(this.y1-this.y2);
        }
        r = Math.sqrt(Math.pow(this.x1-xc,2)+Math.pow(this.y1-yc,2));
        xc = (yc - centeraby + perpgradab*centerabx)/perpgradab;
        */

        // System.out.println debug outputs (commented)
        /*
        System.out.println(centerabx);
        System.out.println(centeraby);
        System.out.println(perp_incl_ab);
        System.out.println(centerbcx);
        System.out.println(centerbcy);
        System.out.println(perp_incl_bc);
        System.out.println(r);
        System.out.println(xc);
        System.out.println(yc);
        */

        // System.out.println("Triangulo criado com as coordenadas: \t"+ this.x1 + "\t"+ this.y1 + "\t"+this.x2 + "\t"+this.y2 + "\t"+this.x3 + "\t"+this.y3);
    }

}