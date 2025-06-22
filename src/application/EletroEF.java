package application;  

import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;  
import javafx.stage.Stage;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;  
import javafx.scene.shape.Rectangle;  
import javafx.scene.shape.Circle;  
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.collections.ObservableList;

public class EletroEF extends Application {

	// Triangulation manager for current state
    TriangulationManager triangulationManager;
    
	// UI controls
	CheckBox checkbox1; // Draw field vectors
	CheckBox checkbox2; // Draw triangles
	TextField tf1;      // Text field for point value input
	// Arrays for points and their graphical representations
	  ArrayList<Ponto> arrPontos = new ArrayList<>();
	    ArrayList<Circle> arrCircle = new ArrayList<>();
	int i=0;
	int numeroPontosAtual=0; // Number of points currently on screen

	// Variables for mouse drawing logic
	double inicio_linha_x = 0;
	double inicio_linha_y = 0;
	boolean existeTriangulacao = false; // Not used, but intended to track triangulation state

	// Fields for dragging the whole scene
	double lastDragX = 0;
	double lastDragY = 0;

	@Override
	public void start(Stage primarystage) {
		final double PI = 3.14159265358979323846;

		// Main layout: BorderPane with drawing area center and controls on right
		// Sistema de camadas
	    BorderPane rootPane = new BorderPane();  // Contêiner principal
	    Pane drawingPane = new Pane();           // Camada de desenho FEM

	    // Configura área de desenho com fundo cinza
	    drawingPane.setStyle("-fx-background-color: grey;");
	    rootPane.setCenter(drawingPane);
	    
	    // Recorte para evitar que desenhos ultrapassem os limites
	    Rectangle clip = new Rectangle();
	    clip.widthProperty().bind(drawingPane.widthProperty());
	    clip.heightProperty().bind(drawingPane.heightProperty());
	    drawingPane.setClip(clip);
	    
	    Scene scene = new Scene(rootPane, 1000, 600);  // Cria a cena com o BorderPane principal
	    //para garantir que o drawingPane se expanda corretamente
	    drawingPane.prefWidthProperty().bind(scene.widthProperty().subtract(300));
	    drawingPane.prefHeightProperty().bind(scene.heightProperty());
	    
		// --- Controls VBox setup --- //
		VBox controls = new VBox(10);
		controls.setPadding(new Insets(10));
		controls.setPrefWidth(300);

		// Button: Start Triangulation
		Button btn1 = new Button("Start Triangulation");
		btn1.setMaxWidth(Double.MAX_VALUE);
		btn1.setOnAction(arg0 -> {
			// Remove previous triangulation if any
			System.out.println("Iniciando a triangulação para o cálculo dos elementos finitos.");
			if(triangulationManager!=null) {
				MeshDrawer.deletarTriangulos(drawingPane, triangulationManager.arrpolygono);
                MeshDrawer.deletarVetores(triangulationManager.arrVetorLinha, drawingPane);
			}
			// Create new triangulation object
            triangulationManager = new TriangulationManager(drawingPane, arrPontos, numeroPontosAtual, checkbox1, checkbox2);

		});

		// Button: Delete all points except initial 3
		Button btn2 = new Button("Delete all points");
		btn2.setMaxWidth(Double.MAX_VALUE);
		btn2.setOnAction(arg0 -> {
			// Remover círculos visuais (exceto os 3 iniciais)
            for(i = arrCircle.size() - 1; i >= 3; i--) {
                drawingPane.getChildren().remove(arrCircle.get(i));
                arrCircle.remove(i);
                arrPontos.remove(i);
            }
			if(triangulationManager!=null) {
				MeshDrawer.deletarTriangulos(drawingPane, triangulationManager.arrpolygono);
                MeshDrawer.deletarVetores(triangulationManager.arrVetorLinha, drawingPane);
			}
			triangulationManager = null;
			numeroPontosAtual=3;
		});

		// Button: Delete last point
		Button btn3 = new Button("Delete last point");
		btn3.setMaxWidth(Double.MAX_VALUE);
		btn3.setOnAction(arg0 -> {
            if(arrPontos.size() > 3) {
                drawingPane.getChildren().remove(arrCircle.get(arrCircle.size() - 1));
                arrCircle.remove(arrCircle.size() - 1);
                arrPontos.remove(arrPontos.size() - 1);
                numeroPontosAtual--;
            }
            triangulationManager = null;
		});

		// Field vector and triangle display toggles
		checkbox1 = new CheckBox("Draw Field Vectors");
		checkbox1.setSelected(true);

		checkbox2 = new CheckBox("Draw Triangles");
		checkbox2.setSelected(true);

		// Drawing mode toggles: add points, add line of points, add circle of points
		CheckBox checkbox3 = new CheckBox("Add Points");
		CheckBox checkbox4 = new CheckBox("Add Line");
		CheckBox checkbox5 = new CheckBox("Add Circle");

		// Explicação para a caixa de texto
		Label labelTensao = new Label("Tensão dos Pontos de Contorno (V):");
		labelTensao.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

		// Dica de ferramenta com explicação detalhada
		Tooltip tooltipTensao = new Tooltip(
		    "Define o valor do potencial elétrico para novos pontos de contorno\n\n" +
		    "• Valor padrão: 200 V\n" +
		    "• Para simular aterramento, use 0\n" +
		    "• Para eletrodos carregados, use valores positivos"
		);
		
		
		// Point value input in Volts
		tf1 = new TextField("200");
		Tooltip.install(tf1, tooltipTensao);
		Tooltip.install(labelTensao, tooltipTensao);
		
		// Add controls to VBox
		controls.getChildren().addAll(
			    btn1, btn2, btn3,
			    new Label("\nEscolha o que vai ser desenhado"),  // Separador
			    checkbox1, checkbox2, 
			    new Label("\nModos de Adição de Pontos:"),  // Separador
			    checkbox3, checkbox4, checkbox5,
			    new Label(" "),  // Espaçamento
			    labelTensao,     // Nova label explicativa
			    tf1              // Caixa de texto existente
			);
	    rootPane.setRight(controls);

		// --- Controls event handlers --- //

		// Toggle display of field vectors
		checkbox1.setOnAction(arg0 -> {
			if ((checkbox1.isSelected())&&(triangulationManager!=null)){
                MeshDrawer.desenharVetores(triangulationManager.arrTriangulosNorm, triangulationManager.E, triangulationManager.arrVetorLinha, drawingPane);
			} else if((!checkbox1.isSelected())&&(triangulationManager!=null)){
                MeshDrawer.deletarVetores(triangulationManager.arrVetorLinha, drawingPane);
			}
		});
		// Toggle display of triangles
		checkbox2.setOnAction(arg0 -> {
			if ((checkbox2.isSelected())&&(triangulationManager!=null)) {
                MeshDrawer.desenharTriangulos(triangulationManager.arrTriangulosNorm, drawingPane, triangulationManager.arrpolygono);
			}else if((!checkbox2.isSelected())&&(triangulationManager!=null)) {
                MeshDrawer.deletarTriangulos(drawingPane, triangulationManager.arrpolygono);
			}
		});
		// Mutually exclusive checkboxes for drawing modes
		checkbox3.setOnAction(arg0 -> {
			if ((checkbox4.isSelected())) checkbox4.setSelected(false);
			if ((checkbox5.isSelected())) checkbox5.setSelected(false);
		});
		checkbox4.setOnAction(arg0 -> {
			if ((checkbox3.isSelected())) checkbox3.setSelected(false);
			if ((checkbox5.isSelected())) checkbox5.setSelected(false);
		});
		checkbox5.setOnAction(arg0 -> {
			if ((checkbox4.isSelected())) checkbox4.setSelected(false);
			if ((checkbox3.isSelected())) checkbox3.setSelected(false);
		});

		// --- Drawing initialization --- //
		// The first 3 points are initialized for the triangulation base
        // Adicionar os pontos iniciais
   
	    
		// Adicionar os pontos iniciais
        arrPontos.add(new Ponto(0,0));
        arrPontos.add(new Ponto(2000,0));
        arrPontos.add(new Ponto(0,2000));
        
        
		numeroPontosAtual = 3;

        // Adicionar círculos visuais para os pontos iniciais
        for(Ponto ponto : arrPontos) {
            Circle circle = new Circle(ponto.x, ponto.y, 2);
            circle.setFill(Color.RED);
            drawingPane.getChildren().add(circle);
            arrCircle.add(circle);
        }
        numeroPontosAtual = 3;
        
		// --- Mouse event handlers for adding points/lines/circles --- //

		// Add single point at click
        drawingPane.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if(checkbox3.isSelected()) {
                Circle circle = new Circle(mouseEvent.getX(), mouseEvent.getY(), 2);
                circle.setFill(Color.RED);
                drawingPane.getChildren().add(circle);
                arrCircle.add(circle);
                
                int valorT;
                try {
                    valorT = Integer.parseInt(tf1.getText());
                } catch (NumberFormatException e) {
                    valorT = 200; // Valor padrão
                }
                
                arrPontos.add(new Ponto(mouseEvent.getX(), mouseEvent.getY(), valorT));
                numeroPontosAtual++;
            }
        });

		// Store start of line/circle for mouse drag
        drawingPane.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
			if(checkbox4.isSelected()||checkbox5.isSelected()){
				System.out.println("mouse click detected! " + mouseEvent.getX()+" "+ mouseEvent.getY());
				inicio_linha_x =  mouseEvent.getX();
				inicio_linha_y =  mouseEvent.getY();
			}
			// SCENE PAN: Store initial position if not in any drawing mode
			if(!checkbox3.isSelected() && !checkbox4.isSelected() && !checkbox5.isSelected()) {
				lastDragX = mouseEvent.getX();
				lastDragY = mouseEvent.getY();
			}
		});

		// On mouse release, add line or circle of points
        drawingPane.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
			double x, y, dist;
			double n = 0;
			x = mouseEvent.getX()-inicio_linha_x;
			y = mouseEvent.getY()-inicio_linha_y;
			dist = Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));

			// Add line of points
			if(checkbox4.isSelected()){
			    x = mouseEvent.getX()-inicio_linha_x;
			    y = mouseEvent.getY()-inicio_linha_y;
			    dist = Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
			    
			    if (dist < 1e-6) {
			        return; // Clique sem movimento
			    }
			    else {
			        x = x/dist; // Normalização X
			        y = y/dist; // Normalização Y
			    }
			    
			    int numPontosLinha = (int)(dist/5); // Calcular quantidade de pontos
			    
			    for(int i = 0; i < numPontosLinha; i++){
			        // Calcular posição do ponto
			        double pointX = inicio_linha_x + 5 * i * x;
			        double pointY = inicio_linha_y + 5 * i * y;
			        
			        // Criar círculo visual
			        Circle circle = new Circle(pointX, pointY, 2);
			        circle.setFill(Color.RED);
			        drawingPane.getChildren().add(circle);
			        arrCircle.add(circle);
			        
			        // Obter valor do campo com tratamento de erro
			        int valorT;
			        try {
			            valorT = Integer.parseInt(tf1.getText());
			        } catch (NumberFormatException e) {
			            valorT = 200; // Valor padrão
			        }
			        
			        // Adicionar ponto ao modelo
			        arrPontos.add(new Ponto(pointX, pointY, valorT));
			        numeroPontosAtual++;
			    }
			}

			// Add circle of points
			if(checkbox5.isSelected()){
			    double raio = dist; // Distância do centro ao ponto de soltura = raio
			    double perimetro = 2 * Math.PI * raio;
			    int numPontosCirculo = (int)(perimetro / 5); // Calcular quantidade de pontos
			    
			    for(int i = 0; i < numPontosCirculo; i++){
			        double angle = i * (2 * Math.PI / numPontosCirculo);
			        double centerX = inicio_linha_x + raio * Math.cos(angle);
			        double centerY = inicio_linha_y + raio * Math.sin(angle);
			        
			        // Criar círculo visual
			        Circle circle = new Circle(centerX, centerY, 2);
			        circle.setFill(Color.RED);
			        drawingPane.getChildren().add(circle);
			        arrCircle.add(circle);
			        
			        // Obter valor do campo com tratamento de erro
			        int valorT;
			        try {
			            valorT = Integer.parseInt(tf1.getText());
			        } catch (NumberFormatException e) {
			            valorT = 200; // Valor padrão
			        }
			        
			        // Adicionar ponto ao modelo
			        arrPontos.add(new Ponto(centerX, centerY, valorT));
			        numeroPontosAtual++;
			    }
			}
		});

		// SCENE PAN: Move everything when not in drawing mode
        drawingPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
			if(!checkbox3.isSelected() && !checkbox4.isSelected() && !checkbox5.isSelected()) {
				double dx = mouseEvent.getX() - lastDragX;
				double dy = mouseEvent.getY() - lastDragY;

				// Move points and circles
	            for (int idx = 0; idx < arrPontos.size(); idx++) {
	                Ponto ponto = arrPontos.get(idx);
	                Circle circle = arrCircle.get(idx);
	                
	                ponto.x += dx;
	                ponto.y += dy;
	                circle.setCenterX(circle.getCenterX() + dx);
	                circle.setCenterY(circle.getCenterY() + dy);
	            }

				// Move triangulation polygons (triangles)
				if (triangulationManager != null && triangulationManager.arrpolygono != null) {
					for (int i = 0; i < triangulationManager.numeroTriangulosNorm; i++) {
						Polygon poly = triangulationManager.arrpolygono.get(i);
						if (poly != null) {
							ObservableList<Double> points = poly.getPoints();
							for (int j = 0; j < points.size(); j += 2) {
								points.set(j, points.get(j) + dx);     // X
								points.set(j+1, points.get(j+1) + dy); // Y
							}
						}
					}
				}

				// Move field vector lines
				if (triangulationManager != null && triangulationManager.arrVetorLinha != null) {
		            for (int i = 0; i < triangulationManager.arrVetorLinha.size(); i++) {
		                Line[] innerArray = triangulationManager.arrVetorLinha.get(i); // CORREÇÃO: .get(i)
		                if (innerArray != null) {
		                    for (int j = 0; j < innerArray.length; j++) {
		                        Line vec = innerArray[j];
		                        if (vec != null) {
		                            vec.setStartX(vec.getStartX() + dx);
		                            vec.setStartY(vec.getStartY() + dy);
		                            vec.setEndX(vec.getEndX() + dx);
		                            vec.setEndY(vec.getEndY() + dy);
		                        }
		                    }
		                }
		            }
				}

				lastDragX = mouseEvent.getX();
				lastDragY = mouseEvent.getY();
			}
			// Após mover os pontos no arraste, deletar a triangulação para forçar uma nova:
			triangulationManager = null;
		}
		);

		// Show the main window
		primarystage.setScene(scene);
		primarystage.setTitle("Electrostatics Finite Element Calculus");
		primarystage.show();  
	}  

	/**
	 * Main entry point. Launches JavaFX application.
	 */
	public static void main(String[] args) {  
		launch(args);  
	}  
}