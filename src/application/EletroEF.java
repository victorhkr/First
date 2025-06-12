package application;  

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;  
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;  
import javafx.stage.Stage;
import quicksortpckg.QuickSort;  
import javafx.scene.control.CheckBox;  
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;  
import javafx.scene.shape.Rectangle;  
import javafx.scene.shape.Circle;  

public class EletroEF extends Application {

	Triangulacao triangulacaoObj;
	CheckBox checkbox1;
	CheckBox checkbox2;
	TextField tf1;
	Ponto[] arrPontos = new Ponto[20000];
	Circle[] arrCircle = new Circle[1000];
	//Line[][] arrVetorLinha = new Line[6000][3];
	int i=0;
	int numeroPontosAtual=0;

	double inicio_linha_x = 0;
	double inicio_linha_y = 0;
	boolean existeTriangulacao = false;
	enum Abacaxi {Feio , Doce, amrgo};
	Abacaxi baxi = Abacaxi.Feio;

	@Override  
	public void start(Stage primarystage) {

		final double PI = 3.14159265358979323846;

		Group root = new Group();
		Scene scene = new Scene(root,1000,600);

		primarystage.setScene(scene);
		primarystage.setTitle("Electrostatics Finite Element Calculus");
		Button btn1=new Button("Start Triangulation");
		btn1.setOnAction(new EventHandler<ActionEvent>(){

			@Override  
			public void handle(ActionEvent arg0) {  
				// TODO Auto-generated method stub  
				System.out.println("hello world motherfucker");
				if(triangulacaoObj!=null) {
					Triangulacao.deletartriangulos(root, triangulacaoObj.arrpolygono, triangulacaoObj.numeroTriangulosNorm);
					Triangulacao.deletarVetores(triangulacaoObj.arrVetorLinha, triangulacaoObj.numeroTriangulosNorm, root);
				}
				triangulacaoObj = new Triangulacao(root,arrPontos,numeroPontosAtual,checkbox1,checkbox2);// cria o objeto
			} 
		}); 

		btn1.setLayoutX(820);
		btn1.setLayoutY(0);
		root.getChildren().add(btn1);

		Button btn2=new Button("Delete all points");  
		btn2.setOnAction(new EventHandler<ActionEvent>() {  

			@Override  
			public void handle(ActionEvent arg0) {  
				// TODO Auto-generated method stub  
				for(i=3;i<numeroPontosAtual;i++) {
					root.getChildren().remove(arrCircle[i]);
					System.out.println(i);
				}
				if(triangulacaoObj!=null) {
					Triangulacao.deletarVetores(triangulacaoObj.arrVetorLinha, triangulacaoObj.numeroTriangulosNorm, root);
					Triangulacao.deletartriangulos(root, triangulacaoObj.arrpolygono, triangulacaoObj.numeroTriangulosNorm);
					
				}
				numeroPontosAtual=3;
			} 
		});
		btn2.setLayoutX(820);
		btn2.setLayoutY(30);
		root.getChildren().add(btn2);

		Button btn3=new Button("Delete last point");  
		btn3.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0){
				// TODO Auto-generated method stub
				if(numeroPontosAtual>3){
					root.getChildren().remove(arrCircle[numeroPontosAtual-1]);
					numeroPontosAtual--;
				}
			}
		});

		btn3.setLayoutX(820);
		btn3.setLayoutY(60);
		root.getChildren().add(btn3);

		checkbox1 = new CheckBox("Draw Field Vectors"); 
		checkbox1.setLayoutX(820);
		checkbox1.setLayoutY(90);
		checkbox1.setSelected(true);
		root.getChildren().add(checkbox1);

		checkbox2 = new CheckBox("Draw Triangles");
		checkbox2.setLayoutX(820);
		checkbox2.setLayoutY(120);
		checkbox2.setSelected(true);
		root.getChildren().add(checkbox2);

		CheckBox checkbox3;
		checkbox3 = new CheckBox("Add Points");
		checkbox3.setLayoutX(820);
		checkbox3.setLayoutY(150);
		root.getChildren().add(checkbox3);

		CheckBox checkbox4;
		checkbox4 = new CheckBox("Add Line");
		checkbox4.setLayoutX(820);
		checkbox4.setLayoutY(180);
		root.getChildren().add(checkbox4);

		CheckBox checkbox5;
		checkbox5 = new CheckBox("Add Circle");
		checkbox5.setLayoutX(820);
		checkbox5.setLayoutY(210);
		root.getChildren().add(checkbox5);

		tf1=new TextField("200");
		tf1.setLayoutX(820);
		tf1.setLayoutY(240);
		root.getChildren().add(tf1);

		Rectangle areaDesenho=new Rectangle(); //instantiating Rectangle 
		areaDesenho.setX(0); //setting the X coordinate of upper left //corner of rectangle
		areaDesenho.setY(0); //setting the Y coordinate of upper left //corner of rectangle
		areaDesenho.setWidth(800); //setting the width of rectangle
		areaDesenho.setHeight(600); // setting the height of rectangle
		areaDesenho.setFill(Color.GREY);
		root.getChildren().add(areaDesenho);

		////////////////////////////super triangulo
		arrPontos[0] = new Ponto(0,0);
		arrPontos[1] = new Ponto(2000,0);
		arrPontos[2] = new Ponto(0,2000);
		numeroPontosAtual = 3;
		///////////////////////////////////////

		inicio_linha_x = 300;
		inicio_linha_y = 300;

		double x, y,dist;
		double n = 0;

		x = 500-inicio_linha_x;
		y = 300-inicio_linha_y;
		dist = Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));

		/*//circulo teste
			double r;

			r = dist;
			dist = 2*PI*r;
			n = dist/20;
			for(i =0;i<n-1;i++){

				double centerX= inicio_linha_x+Math.cos(i*20/r)*x+Math.sin(i*20/r)*y;
				double centerY= inicio_linha_y-Math.sin(i*20/r)*x+Math.cos(i*20/r)*y;

				arrCircle[numeroPontosAtual]= new Circle();
				arrCircle[numeroPontosAtual].setCenterX(centerX);
				arrCircle[numeroPontosAtual].setCenterY(centerY);
				arrCircle[numeroPontosAtual].setRadius(2);
				arrCircle[numeroPontosAtual].setFill(Color.RED);
				root.getChildren().add(arrCircle[numeroPontosAtual]);
				arrPontos[numeroPontosAtual] = new Ponto(centerX,centerY,Integer.parseInt(tf1.getText()));
				numeroPontosAtual++;
				System.out.println(centerX);
				System.out.println(centerY);
				System.out.println(numeroPontosAtual);
			}


		 */

		checkbox1.setOnAction(new EventHandler<ActionEvent>() {  

			@Override  
			public void handle(ActionEvent arg0){  
				if ((checkbox1.isSelected())&&(triangulacaoObj!=null)){
					Triangulacao.desenharVetores(triangulacaoObj.arrTriangulosNorm,triangulacaoObj.E,triangulacaoObj.arrVetorLinha,triangulacaoObj.numeroTriangulosNorm,root);
				}else if((!checkbox1.isSelected())&&(triangulacaoObj!=null)){
					Triangulacao.deletarVetores(triangulacaoObj.arrVetorLinha, triangulacaoObj.numeroTriangulosNorm, root);
				}

			}  
		}); 

		checkbox2.setOnAction(new EventHandler<ActionEvent>() {  

			@Override  
			public void handle(ActionEvent arg0) {  
				if ((checkbox2.isSelected())&&(triangulacaoObj!=null)) {
					Triangulacao.desenhartriangulos(triangulacaoObj.arrTriangulosNorm, root, triangulacaoObj.arrpolygono,triangulacaoObj.numeroTriangulosNorm );
				}else if((!checkbox2.isSelected())&&(triangulacaoObj!=null)) {
					Triangulacao.deletartriangulos(root, triangulacaoObj.arrpolygono, triangulacaoObj.numeroTriangulosNorm);
				}

			}  
		}); 

		checkbox3.setOnAction(new EventHandler<ActionEvent>() {  

			@Override  
			public void handle(ActionEvent arg0) {  
				if ((checkbox4.isSelected())) {
					checkbox4.setSelected(false);
				}
				if ((checkbox5.isSelected())) {
					checkbox5.setSelected(false);
				}
			}  
		}); 
		checkbox4.setOnAction(new EventHandler<ActionEvent>() {  

			@Override  
			public void handle(ActionEvent arg0) {  
				if ((checkbox3.isSelected())) {
					checkbox3.setSelected(false);
				}
				if ((checkbox5.isSelected())) {
					checkbox5.setSelected(false);
				}
			}  
		}); 
		checkbox5.setOnAction(new EventHandler<ActionEvent>() {  

			@Override  
			public void handle(ActionEvent arg0) {  
				if ((checkbox4.isSelected())) {
					checkbox4.setSelected(false);
				}
				if ((checkbox3.isSelected())) {
					checkbox3.setSelected(false);
				}
			}  
		}); 


		//Scene scene2 = new Scene(root,800,600);
		areaDesenho.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if(checkbox3.isSelected()){
					System.out.println("mouse click detected! " + mouseEvent.getSceneX()+" "+ mouseEvent.getSceneY());
					arrCircle[numeroPontosAtual]= new Circle();  
					arrCircle[numeroPontosAtual].setCenterX(mouseEvent.getSceneX());  
					arrCircle[numeroPontosAtual].setCenterY(mouseEvent.getSceneY());  
					arrCircle[numeroPontosAtual].setRadius(2);  
					arrCircle[numeroPontosAtual].setFill(Color.RED); 
					root.getChildren().add(arrCircle[numeroPontosAtual]);
					arrPontos[numeroPontosAtual] = new Ponto(mouseEvent.getSceneX(),mouseEvent.getSceneY(),Integer.parseInt(tf1.getText()));
					numeroPontosAtual++;
					System.out.println(numeroPontosAtual);

				}
			}
		});



		areaDesenho.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if(checkbox4.isSelected()||checkbox5.isSelected()){
					System.out.println("mouse click detected! " + mouseEvent.getSceneX()+" "+ mouseEvent.getSceneY());
					inicio_linha_x =  mouseEvent.getSceneX();
					inicio_linha_y =  mouseEvent.getSceneY();
				}
			}
		});

		areaDesenho.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				double x, y,dist;
				double n = 0;
				x = mouseEvent.getSceneX()-inicio_linha_x;
				y = mouseEvent.getSceneY()-inicio_linha_y;
				dist = Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));

				if(checkbox4.isSelected()){

					int i =0;

					//n = (mouseEvent.getSceneX()-inicio_linha_x)/delta;
					x = mouseEvent.getSceneX()-inicio_linha_x;
					y = mouseEvent.getSceneY()-inicio_linha_y;
					dist = Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
					x = x/dist;
					y = y/dist;
					n = dist/5;

					System.out.println("mouse click detected! " + x +" "+ y);

					for(i =0;i<n;i++){
						arrCircle[numeroPontosAtual]= new Circle();
						arrCircle[numeroPontosAtual].setCenterX(inicio_linha_x+5*i*x);
						arrCircle[numeroPontosAtual].setCenterY(inicio_linha_y+5*i*y);
						arrCircle[numeroPontosAtual].setRadius(2);
						arrCircle[numeroPontosAtual].setFill(Color.RED);
						root.getChildren().add(arrCircle[numeroPontosAtual]);
						arrPontos[numeroPontosAtual] = new Ponto(inicio_linha_x+5*i*x,inicio_linha_y+5*i*y,Integer.parseInt(tf1.getText()));
						numeroPontosAtual++;

						System.out.println(numeroPontosAtual);
					}
				}

				if(checkbox5.isSelected()){
					double r;

					r = dist;
					dist = 2*PI*r;
					n = dist/5;
					for(i =0;i<n;i++){

						double centerX= inicio_linha_x+Math.cos(i*5/r)*x+Math.sin(i*5/r)*y;
						double centerY= inicio_linha_y-Math.sin(i*5/r)*x+Math.cos(i*5/r)*y;

						arrCircle[numeroPontosAtual]= new Circle();
						arrCircle[numeroPontosAtual].setCenterX(centerX);
						arrCircle[numeroPontosAtual].setCenterY(centerY);
						arrCircle[numeroPontosAtual].setRadius(2);
						arrCircle[numeroPontosAtual].setFill(Color.RED);
						root.getChildren().add(arrCircle[numeroPontosAtual]);
						arrPontos[numeroPontosAtual] = new Ponto(centerX,centerY,Integer.parseInt(tf1.getText()));
						numeroPontosAtual++;
						System.out.println(centerX);
						System.out.println(centerY);
						System.out.println(numeroPontosAtual);
					}




				}
			}
		});

		primarystage.show();  
	}  

	public static void main(String[] args) {  
		launch(args);  
	}  
}  