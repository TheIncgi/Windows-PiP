package com.theincgi.app;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**An OCD friendly plain rectangle with a frame<br>
 * @author TheIncgi*/
public class PixelPerfectRectangle extends StackPane{
	/**
	 * @param c1 frameColor
	 * @param c2 fillColor
	 * @param side effects pixel alignment
	 * */
	public PixelPerfectRectangle(Color c1, Color c2, Side side) {
		this.setMaxWidth(20);
		this.setMaxHeight(20);
		this.setMinWidth(20);
		this.setMinHeight(20);
		Rectangle r1, r2;
		r1 = new Rectangle(20, 20, c1);

		int sizeBoost = 0;
		int offset = -2;
		switch (side) {
		case Left:
			sizeBoost = 1;
			offset= 0;
			break;
		case Middle:
			offset = 0;
			sizeBoost=2;
			break;
		case Right:
			r1.setWidth(22);
			offset=-1;
			sizeBoost=1;
			break;
		case Center:
			r2 = new Rectangle(16, 16, c2);
			this.getChildren().addAll(r1, r2);
			return;
		}

		r2 = new Rectangle(16+sizeBoost, 18, c2);
		this.getChildren().addAll(r1, r2);
		r2.setTranslateY(1);
		r2.setTranslateX(offset);
	}

	public static enum Side{
		Left,
		Middle,
		Right,
		Center;
	}
}
