    package application;   
    import javafx.application.Application;  
    import javafx.event.ActionEvent;  
    import javafx.event.EventHandler;  
    import javafx.scene.Scene;  
    import javafx.scene.control.Button;  
    import javafx.stage.Stage;  
    import javafx.scene.layout.StackPane;  
    import javafx.scene.shape.Polygon;
    
    public class ButtonTest extends Application{  
      
        @Override  
        public void start(Stage primaryStage) throws Exception {  
            // TODO Auto-generated method stub  
            Button btn1=new Button("Say, Hello World");  
            btn1.setOnAction(new EventHandler<ActionEvent>() {  
                  
                @Override  
                public void handle(ActionEvent arg0) {  
                    // TODO Auto-generated method stub  
                    System.out.println("hello world motherfucker");  
                }  
            });  
            StackPane root=new StackPane();  
            
            Polygon polygon = new Polygon();  
            polygon.getPoints().addAll(new Double[]{  
                0.0, 0.0,  
                100.0, 200.0,  
                200.0, 100.0 });  
              
            root.getChildren().add(polygon);  
            root.getChildren().add(btn1);

            
            
            Scene scene=new Scene(root,300,400);      
            primaryStage.setTitle("First JavaFX Application");  
            primaryStage.setScene(scene);  
            primaryStage.show();  
        }  
        public static void main (String[] args)  
        {  
            launch(args);  
        }  
      
    }  