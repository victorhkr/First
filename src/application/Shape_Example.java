package application;  
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;  
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.shape.Polygon;  
import javafx.stage.Stage;  
public class Shape_Example extends Application {  

	@Override  
	public void start(Stage primarystage) {  
		Group root = new Group();  
		primarystage.setTitle("Pollygon Example"); 
		Button btn1=new Button("Say, Hello World");  
		btn1.setOnAction(new EventHandler<ActionEvent>() {  

			@Override  
			public void handle(ActionEvent arg0) {  
				// TODO Auto-generated method stub  
				System.out.println("hello world motherfucker");  
			}  
		});  

		Polygon polygon = new Polygon();  
		polygon.getPoints().addAll(new Double[]{  
				0.0, 0.0,  
				100.0, 200.0,  
				200.0, 100.0 });  

		root.getChildren().add(polygon);  
        root.getChildren().add(btn1);

		Scene scene = new Scene(root,300,400);  
		primarystage.setScene(scene);  
		primarystage.show();  
	}  

	public static void main(String[] args) {  
		launch(args);  
	}  
}  