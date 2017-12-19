package com.theincgi.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Test extends Application {

    public static void main(String[] args) {
        launch(args);
    }

	@Override
	public void start(Stage stage) {
	    stage.initStyle(StageStyle.TRANSPARENT);
	    
	    Circle c = new Circle(35);
	    c.setFill(Color.CORNFLOWERBLUE);
	    Scene s = new Scene(new Pane(c), 90, 90);
	    
	    s.setFill(null);
	    stage.setScene(s);
	    stage.show();
	}
}