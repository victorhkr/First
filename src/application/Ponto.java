package application;

public class Ponto {
	double x;
	double y;
	double valorT = 0;
	boolean pontoContorno = false;

	public Ponto(double x, double y) {
		// TODO Auto-generated constructor stub
		this.x = x;
		this.y = y;
		
	}
	
	public Ponto(double x, double y, double valorT) {
		// TODO Auto-generated constructor stub
		this.x = x;
		this.y = y;
		this.valorT = valorT;
		pontoContorno = true;
	}
	
    // Construtor de cópia para preservar propriedades
    public Ponto(Ponto original) {
        this.x = original.x;
        this.y = original.y;
        this.valorT = original.valorT;
        this.pontoContorno = original.pontoContorno;
    }
}