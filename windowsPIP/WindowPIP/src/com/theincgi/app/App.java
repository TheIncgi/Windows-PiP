package com.theincgi.app;

import java.awt.image.BufferedImage;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.theincgi.app.PixelPerfectRectangle.Side;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**@author TheIncgi*/
public class App extends Application{
	/**
	 * Primary stage for the gui
	 * */
	private Stage stage;
	
	/**
	 * Window handles for the gui window and the target window
	 * */
	private static HWND myWindowHandle, captureHandle;
	
	/**
	 * Idle message when no target is selected
	 * */
	Text waitingLabel = new Text("Waiting for screen selection");
	
	/**
	 * This is the green button that lets you choose a new target window
	 * */
	PixelPerfectRectangle reSelect = new PixelPerfectRectangle(Color.BLACK, Color.DARKSEAGREEN, PixelPerfectRectangle.Side.Left);
	/**
	 * This is the gray button that lets you grab and move the window
	 * */
	PixelPerfectRectangle move = new PixelPerfectRectangle(Color.BLACK, Color.GRAY, PixelPerfectRectangle.Side.Middle);
	
	/**
	 * This is the blue button that minimizes the window
	 * */
	PixelPerfectRectangle minimize = new PixelPerfectRectangle(Color.BLACK, Color.CORNFLOWERBLUE, PixelPerfectRectangle.Side.Middle);
	
	/**
	 * This is the red button that closes the application
	 * */
	PixelPerfectRectangle close = new PixelPerfectRectangle(Color.BLACK, Color.SALMON, PixelPerfectRectangle.Side.Right);
	
	/**
	 * This is a solid black rectangle behind all the other elements<br>
	 * I've used it to create a boarder for the main part of the window<br>
	 * <br>
	 * <b>Note:</b> I didn't use {@link Rectangle#setStroke(javafx.scene.paint.Paint)} or {@link Rectangle#setStrokeWidth(double)} options<br>
	 * because I found that they didn't provide an exact pixel width to match the rest of the gui nicely
	 * */
	Rectangle frame = new Rectangle(200, 200);
	/**
	 * This {@link BorderPane} is used to keep the control buttons at the top
	 * */
	BorderPane bp = new BorderPane();
	/**
	 * This {@link HBox} houses the control buttons at the top of the window
	 * */
	HBox controls = new HBox();
	/**
	 * This {@link StackPane} keeps the canvas and elements over it together
	 * */
	StackPane stackPane = new StackPane();
	/**
	 * This is the {@link ResizableCanvas} used to draw the captured image
	 * */
	ResizableCanvas resizableCanvas = new ResizableCanvas();
	/**
	 * This is the orange box in the bottom right, it is used to resize the window
	 * */
	PixelPerfectRectangle resize = new PixelPerfectRectangle(Color.BLACK, Color.DARKORANGE, Side.Center);
	
	/**
	 * This {@link Point} is used to keep track of where the last mouse event happened to<br>
	 * calculate the change in mouse position
	 * */
	Point anchor = null;

	/**
	 * The main method, everyone's favorite!
	 * */
	public static void main(String[] args) {
		System.out.println("WindowPIP - Version 1.0.0");
		launch(args);
	}

	
	/**
	 * Start method<br>
	 * called from {@link Application#launch(String...)}<br>
	 * All the gui setup is in this section.
	 * */
	@Override
	public void start(Stage stage) throws Exception {
		stage.initStyle(StageStyle.TRANSPARENT);
		Scene s= new Scene(bp, 200, 220);
		this.stage = stage;
		

		waitingLabel.setFill(Color.LIGHTBLUE);
		waitingLabel.setFont(new Font("Consolas", 12));

		bp.setTop(controls);
		controls.getChildren().addAll(reSelect, move, minimize, close);
		controls.setAlignment(Pos.TOP_RIGHT);
		bp.setCenter(stackPane);
		Pane resizeContainer = new Pane(resize);
		stackPane.getChildren().addAll(frame, resizableCanvas, waitingLabel, resizeContainer);
		stackPane.setMinWidth(0); stackPane.setMinHeight(0);
		
		resize.translateXProperty().bind(stage.widthProperty().subtract(20));
		resize.translateYProperty().bind(stage.heightProperty().subtract(40));
		
		resize.setOpacity(0);

		resizableCanvas.setDim(196, 196);

		close.setOnMouseClicked(e->{
			stage.close();
		});
		minimize.setOnMouseClicked(e->{
			stage.setIconified(true);
		});
		move.setOnMousePressed(e->{
			anchor = new Point(e.getScreenX(), e.getScreenY());
		});
		move.setOnMouseDragged(e->{
			if(anchor==null) return;
			double dx, dy;
			dx = e.getScreenX() - anchor.x;
			dy = e.getScreenY() - anchor.y;
			stage.setX(stage.getX() + dx);
			stage.setY(stage.getY() + dy);
			anchor.x = e.getScreenX();
			anchor.y = e.getScreenY();
		});
		move.setOnMouseDragReleased(e->{
			if(anchor==null) return;
			double dx, dy;
			dx = e.getScreenX() - anchor.x;
			dy = e.getScreenY() - anchor.y;
			stage.setX(stage.getX() + dx);
			stage.setY(stage.getY() + dy);
			anchor = null;
		});
		reSelect.setOnMouseClicked(e->{
			if(e.isShiftDown() || e.isSecondaryButtonDown())
				try {
					reSelect.setVisible( false );
					Thread.sleep(5000);
				} catch (InterruptedException e1) {}
			pickWindow();
		});
		
		resize.setOnMousePressed(e->{
			anchor = new Point(e.getScreenX(), e.getScreenY());
		});
		resize.setOnMouseDragged(e->{
			if(anchor==null) return;
			double dx, dy;
			dx = e.getScreenX() - anchor.x;
			dy = e.getScreenY() - anchor.y;
			adjust(dx, dy);
			anchor.x = e.getScreenX();
			anchor.y = e.getScreenY();
		});
		resize.setOnMouseDragReleased(e->{
			if(anchor==null) return;
			double dx, dy;
			dx = e.getScreenX() - anchor.x;
			dy = e.getScreenY() - anchor.y;
			adjust(dx, dy);
			anchor = null;
		});
		
		FadeTransition fadeOut = new FadeTransition(Duration.millis(500), resize);
		fadeOut.setFromValue(1); fadeOut.setToValue(0);
		FadeTransition fadeIn = new FadeTransition(Duration.millis(500), resize);
		fadeIn.setFromValue(0); fadeIn.setToValue(1);
		
		resize.setOnMouseEntered(e->{
			fadeIn.play();
		});
		resize.setOnMouseExited(e->{
			fadeOut.play();
		});
		

		s.setFill(null);
		stage.setScene(s);
		stage.setAlwaysOnTop(true);
		stage.show();
		//primaryStage.requestFocus();

		

		myWindowHandle = User32.INSTANCE.GetForegroundWindow();
		System.out.println("Application window handle: "+formatHandle(myWindowHandle));
		pickWindow();

		AnimationTimer timer = new AnimationTimer() {
			long lastFrame = System.currentTimeMillis()-100;
			double FRAME_RATE = 1000/8; //8 fps
			@Override
			public void handle(long now) {
				if(System.currentTimeMillis()-lastFrame > FRAME_RATE) {
					if(captureHandle!=null)
						try {
							BufferedImage bi = WindowCapturer.capture(captureHandle);
							resizableCanvas.draw(bi);

							lastFrame = System.currentTimeMillis();
						}catch (IllegalArgumentException e) {
							captureHandle = null;
							reSelect.getOnMouseClicked().handle(null);

						}
					else
						resizableCanvas.drawPlain();
				}
			}
		};
		timer.start();
	}
	
	/**
	 * Adjust the stage to fit into the new dimensions<br>
	 * */
	public void adjust(double w, double h) {
		stage.setWidth(Math.max(80, stage.getWidth()+w));
		stage.setHeight(Math.max(100, stage.getHeight()+h));
		resizableCanvas.setWidth(stage.getWidth()-4);
		resizableCanvas.setHeight(stage.getHeight()-24);
		frame.setWidth(stage.getWidth());
		frame.setHeight(stage.getHeight()-20);
	}
	
	/**
	 * Wait for the user to select another window other than this one<br>
	 * that window then becomes the target window
	 * */
	public void pickWindow() {
		//System.out.println(Thread.currentThread().getId()+": Picking window...");
		reSelect.setVisible(false);
		waitingLabel.setVisible(true);
		reSelect.setDisable(true);
		captureHandle = null;
		Thread t = new Thread(()->{
			//System.out.println(Thread.currentThread().getId()+": trying...");
			try {
				HWND temp = myWindowHandle;
				while(temp.equals(myWindowHandle)) {
					Thread.sleep(300);
					temp = User32.INSTANCE.GetForegroundWindow();
				}
				captureHandle = temp;
				System.out.println("Target handle: "+formatHandle(captureHandle));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally{
				//System.out.println(Thread.currentThread().getId()+": Finishing...");
				Platform.runLater(()->{
					reSelect.setDisable(false);
					reSelect.setVisible(true);
					waitingLabel.setVisible(false);
				});
			}

		});
		t.start();
		//System.out.println(Thread.currentThread().getId()+": Method exit");
	}
	
	/**
	 * Removes "native@" from the toString basicly
	 * */
	public static String formatHandle(HWND handle) {
		return handle.toString().substring("native@".length());
	}
	
	/**
	 * Basic point class with two doubles (x and y)
	 * */
	private class Point{
		double x, y;

		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

	}
}
