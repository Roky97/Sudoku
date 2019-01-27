package gui.model;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.animation.TranslateTransition;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.text.Font;
import javafx.util.Duration;

@SuppressWarnings("restriction")
public class SudokuSubScene extends SubScene {

	private final static String BACKGROUND_IMAGE = "gui/resources/blue_panel.png";
	private boolean isHidden;
	private Label label;
	private ArrayList<SudokuButton> buttons;
	private Point transition_xy;
	
	public SudokuSubScene() 
	{
		super(new AnchorPane(), 600, 400);
		setDimension(600,400);
		
		label = new Label();
		backgroundSettings();
		
		buttons = new ArrayList<SudokuButton>();
		transition_xy = new Point(-725, 0);
		setLayoutX(1050);
		setLayoutY(150);
		isHidden = true;
	}
	
	public void setDimension(int width, int height) {
		setWidth(width);
		setHeight(height);
	}

	public AnchorPane getPane() { return (AnchorPane) this.getRoot(); }
	
	public void moveSubScene() 
	{
		TranslateTransition transition = new TranslateTransition();
		transition.setDuration(Duration.seconds(0.3));
		transition.setNode(this);
		if(isHidden)
		{
			transition.setToX(transition_xy.getX());
			isHidden = false;
		}
		else
		{
			transition.setToX(transition_xy.getY());
			isHidden = true;
		}
		
		transition.play();
	}

	public void setLabel(String text) { 
		label.setText(text);
		labelSettings();
	}
	
	public Label getLabel() { return label; }
	
	private void labelSettings() 
	{
		label.setLayoutX(120);
		label.setLayoutY(30);
		try {
			label.setFont(Font.loadFont(new FileInputStream(new File("src/main/java/gui/resources/TeachersStudent.ttf")), 35));
		} catch (FileNotFoundException e) {
			System.err.println("FONT NON TROVATO!");
			label.setFont(Font.loadFont("Verdana", 23));			
		};
	}
	
	private void backgroundSettings() 
	{
		BackgroundImage background = new BackgroundImage(new Image(BACKGROUND_IMAGE,450,350,false,true),BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,BackgroundPosition.DEFAULT,null);
		AnchorPane root = (AnchorPane) this.getRoot();
		root.setBackground(new Background(background));
	}

	public void addButtons(ArrayList<SudokuButton> buttons) 
	{
		for(SudokuButton button : buttons) {
			this.buttons.add(button);
		}
	}

	public ArrayList<SudokuButton> getButtons() { return buttons; }

	public void setTransitionCoordinate(int x, int y) {
		transition_xy.setLocation(x, y);
	}

}
