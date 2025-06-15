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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.collections.ObservableList;

public class EletroEF extends Application {

    // Triangulation object for current state
    Triangulacao triangulacaoObj;
    // UI controls
    CheckBox checkbox1; // Draw field vectors
    CheckBox checkbox2; // Draw triangles
    TextField tf1;      // Text field for point value input
    // Arrays for points and their graphical representations
    Ponto[] arrPontos = new Ponto[20000];
    Circle[] arrCircle = new Circle[1000];
    int i=0;
    int numeroPontosAtual=0; // Number of points currently on screen

    // Variables for mouse drawing logic
    double inicio_linha_x = 0;
    double inicio_linha_y = 0;
    boolean existeTriangulacao = false; // Not used, but intended to track triangulation state

    // Example enum (not used in logic)
    enum Abacaxi {Feio , Doce, amrgo};
    Abacaxi baxi = Abacaxi.Feio;

    // Fields for dragging the whole scene
    double lastDragX = 0;
    double lastDragY = 0;

    @Override  
    public void start(Stage primarystage) {
        final double PI = 3.14159265358979323846;

        // Main layout: BorderPane with drawing area center and controls on right
        BorderPane rootPane = new BorderPane();
        Scene scene = new Scene(rootPane, 1000, 600);

        // --- Drawing area setup --- //
        Group drawingGroup = new Group();
        Rectangle areaDesenho = new Rectangle();
        areaDesenho.setX(0);
        areaDesenho.setY(0);
        areaDesenho.setWidth(800);
        areaDesenho.setHeight(600);
        areaDesenho.setFill(Color.GREY);
        drawingGroup.getChildren().add(areaDesenho);
        rootPane.setCenter(drawingGroup);

        // Make drawing area resize with window (minus controls VBox width)
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            areaDesenho.setWidth(Math.max(0, width - 200)); // 200px for control VBox
        });
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            double height = newVal.doubleValue();
            areaDesenho.setHeight(height);
        });

        // --- Controls VBox setup --- //
        VBox controls = new VBox(10);
        controls.setPadding(new Insets(10));
        controls.setPrefWidth(200);

        // Button: Start Triangulation
        Button btn1 = new Button("Start Triangulation");
        btn1.setMaxWidth(Double.MAX_VALUE);
        btn1.setOnAction(arg0 -> {
            // Remove previous triangulation if any
            System.out.println("hello world motherfucker");
            if(triangulacaoObj!=null) {
                Triangulacao.deletartriangulos(drawingGroup, triangulacaoObj.arrpolygono, triangulacaoObj.numeroTriangulosNorm);
                Triangulacao.deletarVetores(triangulacaoObj.arrVetorLinha, triangulacaoObj.numeroTriangulosNorm, drawingGroup);
            }
            // Create new triangulation object
            triangulacaoObj = new Triangulacao(drawingGroup, arrPontos, numeroPontosAtual, checkbox1, checkbox2);
        });

        // Button: Delete all points except initial 3
        Button btn2 = new Button("Delete all points");
        btn2.setMaxWidth(Double.MAX_VALUE);
        btn2.setOnAction(arg0 -> {
            for(i=3;i<numeroPontosAtual;i++) {
                drawingGroup.getChildren().remove(arrCircle[i]);
                System.out.println(i);
            }
            if(triangulacaoObj!=null) {
                Triangulacao.deletarVetores(triangulacaoObj.arrVetorLinha, triangulacaoObj.numeroTriangulosNorm, drawingGroup);
                Triangulacao.deletartriangulos(drawingGroup, triangulacaoObj.arrpolygono, triangulacaoObj.numeroTriangulosNorm);
            }
            numeroPontosAtual=3;
        });

        // Button: Delete last point
        Button btn3 = new Button("Delete last point");
        btn3.setMaxWidth(Double.MAX_VALUE);
        btn3.setOnAction(arg0 -> {
            if(numeroPontosAtual>3){
                drawingGroup.getChildren().remove(arrCircle[numeroPontosAtual-1]);
                numeroPontosAtual--;
            }
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

        // Point value input
        tf1 = new TextField("200");

        // Add controls to VBox
        controls.getChildren().addAll(btn1, btn2, btn3, checkbox1, checkbox2, checkbox3, checkbox4, checkbox5, tf1);

        rootPane.setRight(controls);

        // --- Controls event handlers --- //

        // Toggle display of field vectors
        checkbox1.setOnAction(arg0 -> {
            if ((checkbox1.isSelected())&&(triangulacaoObj!=null)){
                Triangulacao.desenharVetores(triangulacaoObj.arrTriangulosNorm, triangulacaoObj.E, triangulacaoObj.arrVetorLinha, triangulacaoObj.numeroTriangulosNorm, drawingGroup);
            } else if((!checkbox1.isSelected())&&(triangulacaoObj!=null)){
                Triangulacao.deletarVetores(triangulacaoObj.arrVetorLinha, triangulacaoObj.numeroTriangulosNorm, drawingGroup);
            }
        });
        // Toggle display of triangles
        checkbox2.setOnAction(arg0 -> {
            if ((checkbox2.isSelected())&&(triangulacaoObj!=null)) {
                Triangulacao.desenhartriangulos(triangulacaoObj.arrTriangulosNorm, drawingGroup, triangulacaoObj.arrpolygono, triangulacaoObj.numeroTriangulosNorm );
            }else if((!checkbox2.isSelected())&&(triangulacaoObj!=null)) {
                Triangulacao.deletartriangulos(drawingGroup, triangulacaoObj.arrpolygono, triangulacaoObj.numeroTriangulosNorm);
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
        arrPontos[0] = new Ponto(0,0);
        arrPontos[1] = new Ponto(2000,0);
        arrPontos[2] = new Ponto(0,2000);
        numeroPontosAtual = 3;

        // --- Mouse event handlers for adding points/lines/circles --- //

        // Add single point at click
        areaDesenho.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if(checkbox3.isSelected()){
                if (numeroPontosAtual < arrCircle.length && numeroPontosAtual < arrPontos.length) {
                    arrCircle[numeroPontosAtual]= new Circle();  
                    arrCircle[numeroPontosAtual].setCenterX(mouseEvent.getX());  
                    arrCircle[numeroPontosAtual].setCenterY(mouseEvent.getY());  
                    arrCircle[numeroPontosAtual].setRadius(2);  
                    arrCircle[numeroPontosAtual].setFill(Color.RED); 
                    drawingGroup.getChildren().add(arrCircle[numeroPontosAtual]);
                    arrPontos[numeroPontosAtual] = new Ponto(mouseEvent.getX(),mouseEvent.getY(),Integer.parseInt(tf1.getText()));
                    numeroPontosAtual++;
                    System.out.println(numeroPontosAtual);
                } else {
                    System.out.println("Maximum number of points reached!");
                }
            }
        });

        // Store start of line/circle for mouse drag
        areaDesenho.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
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
        areaDesenho.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
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
                x = x/dist;
                y = y/dist;
                n = dist/5;
                for(int i =0;i<n;i++){
                    if (numeroPontosAtual < arrCircle.length && numeroPontosAtual < arrPontos.length) {
                        arrCircle[numeroPontosAtual]= new Circle();
                        arrCircle[numeroPontosAtual].setCenterX(inicio_linha_x+5*i*x);
                        arrCircle[numeroPontosAtual].setCenterY(inicio_linha_y+5*i*y);
                        arrCircle[numeroPontosAtual].setRadius(2);
                        arrCircle[numeroPontosAtual].setFill(Color.RED);
                        drawingGroup.getChildren().add(arrCircle[numeroPontosAtual]);
                        arrPontos[numeroPontosAtual] = new Ponto(inicio_linha_x+5*i*x,inicio_linha_y+5*i*y,Integer.parseInt(tf1.getText()));
                        numeroPontosAtual++;
                        System.out.println(numeroPontosAtual);
                    } else {
                        System.out.println("Maximum number of points reached!");
                        break;
                    }
                }
            }

            // Add circle of points
            if(checkbox5.isSelected()){
                double r;
                r = dist;
                dist = 2*PI*r;
                n = dist/5;
                for(int i =0;i<n;i++){
                    double centerX= inicio_linha_x+Math.cos(i*5/r)*x+Math.sin(i*5/r)*y;
                    double centerY= inicio_linha_y-Math.sin(i*5/r)*x+Math.cos(i*5/r)*y;
                    if (numeroPontosAtual < arrCircle.length && numeroPontosAtual < arrPontos.length) {
                        arrCircle[numeroPontosAtual]= new Circle();
                        arrCircle[numeroPontosAtual].setCenterX(centerX);
                        arrCircle[numeroPontosAtual].setCenterY(centerY);
                        arrCircle[numeroPontosAtual].setRadius(2);
                        arrCircle[numeroPontosAtual].setFill(Color.RED);
                        drawingGroup.getChildren().add(arrCircle[numeroPontosAtual]);
                        arrPontos[numeroPontosAtual] = new Ponto(centerX,centerY,Integer.parseInt(tf1.getText()));
                        numeroPontosAtual++;
                        System.out.println(centerX);
                        System.out.println(centerY);
                        System.out.println(numeroPontosAtual);
                    } else {
                        System.out.println("Maximum number of points reached!");
                        break;
                    }
                }
            }
        });

        // SCENE PAN: Move everything when not in drawing mode
        areaDesenho.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            if(!checkbox3.isSelected() && !checkbox4.isSelected() && !checkbox5.isSelected()) {
                double dx = mouseEvent.getX() - lastDragX;
                double dy = mouseEvent.getY() - lastDragY;

                // Move points and circles
                for (int idx = 0; idx < numeroPontosAtual; idx++) {
                    if (arrCircle[idx] != null) {
                        arrPontos[idx].x += dx;
                        arrPontos[idx].y += dy;
                        arrCircle[idx].setCenterX(arrCircle[idx].getCenterX() + dx);
                        arrCircle[idx].setCenterY(arrCircle[idx].getCenterY() + dy);
                    }
                }

             // Move triangulation polygons (triangles)
                if (triangulacaoObj != null && triangulacaoObj.arrpolygono != null) {
                    for (int i = 0; i < triangulacaoObj.numeroTriangulosNorm; i++) {
                        Polygon poly = triangulacaoObj.arrpolygono[i];
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
                if (triangulacaoObj != null && triangulacaoObj.arrVetorLinha != null) {
                    for (int i = 0; i < triangulacaoObj.arrVetorLinha.length; i++) {
                        if (triangulacaoObj.arrVetorLinha[i] != null) {
                            for (int j = 0; j < triangulacaoObj.arrVetorLinha[i].length; j++) {
                                Line vec = triangulacaoObj.arrVetorLinha[i][j];
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
        });

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