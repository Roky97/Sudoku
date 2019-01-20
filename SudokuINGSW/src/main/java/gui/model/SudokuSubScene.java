package gui.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import javafx.util.Duration;

import gui.view.GameView;

import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

@SuppressWarnings("restriction")
public class SudokuSubScene extends SubScene {

	private final static String BACKGROUND_IMAGE = "gui/resources/blue_panel.png";
	private boolean isHidden;
	private Label label;
	private ArrayList<SudokuButton> buttons;
	
	public SudokuSubScene() 
	{
		super(new AnchorPane(), 600, 400);
		prefHeight(400);
		prefWidth(600);
		
		label = new Label();
		backgroundSettings();
		
		buttons = new ArrayList<SudokuButton>();
		
		setLayoutX(1050);
		setLayoutY(150);
		isHidden = true;
	}
	
	public AnchorPane getPane() { return (AnchorPane) this.getRoot(); }
	
	public void moveSubScene() 
	{
		TranslateTransition transition = new TranslateTransition();
		transition.setDuration(Duration.seconds(0.3));
		transition.setNode(this);
		if(isHidden)
		{
			transition.setToX(-725);
			isHidden = false;
		}
		else
		{
			transition.setToX(0);
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

//	public DIFFICULTY getDifficulty() {
//		return difficulty;
//	}
//
//	public void setDifficulty(DIFFICULTY difficulty) {
//		this.difficulty = difficulty;
//	}

}
