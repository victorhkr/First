package application;

public class Vertice {
	final double PI = 3.14159265359;

	double x;
	double y;
	double anguloParaCentro = 0;
	public Vertice(double x, double y, Ponto centro) {
		
		this.x = x;
		this.y = y;
		anguloParaCentro =( Math.atan((-centro.y+this.y)/ (-centro.x+this.x)))*180/PI;
		if((-centro.x+this.x) > 0 && anguloParaCentro<0) {
			anguloParaCentro = anguloParaCentro + 360;
		}else if((-centro.x+this.x) < 0 && anguloParaCentro<0) {
			anguloParaCentro = anguloParaCentro+180;
		}else if((-centro.x+this.x) < 0 && anguloParaCentro>0) {
			anguloParaCentro = 180 + anguloParaCentro;
		}
		if((-centro.x+this.x) == 0 && anguloParaCentro<0) {
			anguloParaCentro = 360 + anguloParaCentro;
		}
		if((-centro.y+this.y) == 0 && (-centro.x+this.x) > 0 ) {
			anguloParaCentro = 0;
		}
		if((-centro.y+this.y) == 0 && (-centro.x+this.x) < 0 ) {
			anguloParaCentro = 180;
		}
		//System.out.println("o angulo é " + anguloParaCentro);
		//System.out.println(-centro.x+this.x);
		//System.out.println(-centro.y+this.y);
		

		// TODO Auto-generated constructor stub
	}
	public Vertice(double x, double y) {
		
		this.x = x;
		this.y = y;
		// TODO Auto-generated constructor stub
	}

}
