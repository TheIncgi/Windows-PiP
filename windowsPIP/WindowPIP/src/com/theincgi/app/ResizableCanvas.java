package com.theincgi.app;

import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

//https://stackoverflow.com/questions/24533556/how-to-make-canvas-resizable-in-javafx
class ResizableCanvas extends Canvas {

    public ResizableCanvas() {
        // Redraw canvas when size changes.
        widthProperty().addListener(evt -> drawPlain());
        heightProperty().addListener(evt -> drawPlain());
    }
    /**@author TheIncgi*/
   	public void drawPlain() {
        double width = getWidth();
        double height = getHeight();

        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.DARKSLATEGRAY);
        gc.clearRect(0, 0, width, height);
        gc.fillRect(0, 0, width, height);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }
    
    /**@author TheIncgi*/
	public void setDim(int wid, int hei) {
		setWidth(wid);
		setHeight(hei);
	}

	public void draw(BufferedImage bi) {
		double width = getWidth();
        double height = getHeight();

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        gc.drawImage(SwingFXUtils.toFXImage(bi, null), 0, 0, getWidth(), getHeight());
        
	}
}
