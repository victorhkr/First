package application;

public class Triangulo {

	double x1;
	double y1;
	double x2;
	double y2;
	double x3;
	double y3;
	double centerabx;
	double centeraby;
	double centerbcx;
	double centerbcy;
	double perp_incl_ab;
	double perp_incl_bc;
	double yc;
	double xc;
	double r;
	double centroideX;
	double centroideY;
	double anguloParaCentro1;
	double anguloParaCentro2;
	double anguloParaCentro3;
	final double PI = 3.14159265359;

	public double acharAngulo(double x, double y) {
		double angulo;
		
		angulo =( Math.atan((-centroideY+y)/ (-centroideX+x)))*180/PI;
		
		if((-centroideX+x) > 0 && angulo<0) {
			angulo = angulo + 360;
		}else if((-centroideX+x) < 0 && angulo<0) {
			angulo = angulo+180;
		}else if((-centroideX+x) < 0 && angulo>0) {
			angulo = 180 + angulo;
		}
		if((-centroideX+x) == 0 && angulo<0) {
			angulo = 360 + angulo;
		}
		if((-centroideY+y) == 0 && (-centroideX+x) > 0 ) {
			angulo = 0;
		}
		if((-centroideY+y) == 0 && (-centroideX+x) < 0 ) {
			angulo = 180;
		}
		return angulo;
	}
	public Triangulo(double x1, double y1, double x2, double y2, double x3, double y3){

		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.x3 = x3;
		this.y3 = y3;
		
		centroideX = (x1+x2+x3)/3;
		centroideY = (y1+y2+y3)/3;
		
		anguloParaCentro1 = acharAngulo(x1,y1);
		//System.out.println("angulo 1:"+anguloParaCentro1);
		anguloParaCentro2 = acharAngulo(x2,y2);
		//System.out.println("angulo 2:"+anguloParaCentro2);
		anguloParaCentro3 = acharAngulo(x3,y3);
		//System.out.println("angulo 3:"+anguloParaCentro3);

		if(anguloParaCentro1>anguloParaCentro2) {
			if(anguloParaCentro1> anguloParaCentro3) {
				this.x3 = x1;
				this.y3 = y1;
				if(anguloParaCentro2>anguloParaCentro3) {
					this.x2 = x2;
					this.y2 = y2;
					this.x1 = x3;
					this.y1 = y3;
				}
				else {
					this.x2 = x3;
					this.y2 = y3;
					this.x1 = x2;
					this.y1 = y2;
					
				}
			}else {
				this.x3 = x3;
				this.y3 = y3;
				this.x2 = x1;
				this.y2 = y1;
				this.x1 = x2;
				this.y1 = y2;
			}
		}else{
			if(anguloParaCentro2<anguloParaCentro3){
				this.x1 = x1;
				this.y1 = y1;
				this.x2 = x2;
				this.y2 = y2;
				this.x3 = x3;
				this.y3 = y3;
			}else {
				this.x1 = x1;
				this.y1 = y1;
				this.x2 = x3;
				this.y2 = y3;
				this.x3 = x2;
				this.y3 = y2;
			}
		}
	
		anguloParaCentro1 = acharAngulo(this.x1,this.y1);
		//System.out.println("angulo 1:"+anguloParaCentro1);
		anguloParaCentro2 = acharAngulo(this.x2,this.y2);
		//System.out.println("angulo 2:"+anguloParaCentro2);
		anguloParaCentro3 = acharAngulo(this.x3,this.y3);
		//System.out.println("angulo 3:"+anguloParaCentro3);
		
		
		if((this.y1-this.y2)==0) {
			centerbcx = (this.x2 + this.x3)/2;
			centerbcy = (this.y2 + this.y3)/2;
			xc = (this.x2 + this.x1)/2;
			perp_incl_bc = -(this.x3-this.x2)/(this.y3-this.y2);
			yc = perp_incl_bc*(xc-centerbcx)+centerbcy;
		}
		else if ((this.y3-this.y2)==0){
			centerabx = (this.x1 + this.x2)/2;
			centeraby = (this.y1 + this.y2)/2;
			xc = (this.x3 + this.x2)/2;
			perp_incl_ab = -(this.x2-this.x1)/(this.y2-this.y1);
			yc = perp_incl_ab*(xc-centerabx)+centeraby;
		}
		else {
			centerabx = (this.x1 + this.x2)/2;
			centeraby = (this.y1 + this.y2)/2;
			centerbcx = (this.x2 + this.x3)/2;
			centerbcy = (this.y2 + this.y3)/2;
			perp_incl_ab = -(this.x2-this.x1)/(this.y2-this.y1);
			perp_incl_bc = -(this.x3-this.x2)/(this.y3-this.y2);
			yc = (-perp_incl_bc*(centeraby-perp_incl_ab*centerabx)+perp_incl_ab*(centerbcy-perp_incl_bc*centerbcx))/(-perp_incl_bc+perp_incl_ab);
			xc = (-1*(centeraby-perp_incl_ab*centerabx)+1*(centerbcy-perp_incl_bc*centerbcx))/(-perp_incl_bc+perp_incl_ab);
		}

		r = Math.sqrt(Math.pow(this.x1-xc,2)+Math.pow(this.y1-yc,2));

		/*
		
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
		//System.out.println("Triangulo criado com as coordenadas: \t"+ this.x1 + "\t"+ this.y1 + "\t"+this.x2 + "\t"+this.y2 + "\t"+this.x3 + "\t"+this.y3);
	}
	
}