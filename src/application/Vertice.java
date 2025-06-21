package application;

public class Vertice {

	double x;
	double y;
	double anguloParaCentro = 0;
	public Vertice(double x, double y, Ponto centro) {
		
		this.x = x;
		this.y = y;
		
	    double dx = x - centro.x;
	    double dy = y - centro.y;
	    
	    //Trate caso onde o ponto é o próprio centro
	    	
	    if (dx == 0 && dy == 0) {
	        this.anguloParaCentro = 0; // ou um valor padrão
	        return; // encerra o cálculo
	    }
	    
	    // Calcula ângulo em radianos e converte para graus
        double anguloGraus = Math.toDegrees(Math.atan2(dy, dx));
	    
	    // Ajusta para [0, 360)
	    if (anguloGraus < 0) {
	        anguloGraus += 360;
	    }
	    this.anguloParaCentro = anguloGraus;
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
