package gui.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

@SuppressWarnings("restriction")
public class SudokuButton extends Button {
	
	private final String Font_Path = "src/main/java/gui/resources/TeachersStudent.ttf";
	
	private String BUTTON_STYLE;
	private String PRESSED_BUTTON_STYLE;
	
	private DIFFICULTY difficulty;
	
	public SudokuButton(String text) 
	{
		setText(text);
		setButtonFont();
		setPrefHeight(49);
		setPrefWidth(190);
		setDefaultStyle();
		initalizeButtonListeners();
	}

	public void setDifficulty(DIFFICULTY d) {
		difficulty = d;
	}
	
	public DIFFICULTY getDifficulty() { return difficulty; }
	
	private void setButtonFont() 
	{
		try {
			setFont(Font.loadFont(new FileInputStream(Font_Path), 23));
			
		} catch (FileNotFoundException e) {
			System.out.println("path non trovata!");
			setFont(Font.loadFont("Verdana", 23));
		}
	}

	private void setDefaultStyle() {
		BUTTON_STYLE = "-fx-background-color: transparent;"+
						"-fx-background-image: url('/gui/resources/blue_button.png');";
		PRESSED_BUTTON_STYLE = "-fx-background-color: transparent;"+
								"-fx-text-fill: White;"+
								"-fx-background-image: url('/gui/resources/blue_buttonPressed.png');";
		setStyle(BUTTON_STYLE);
	}

	public void setDifficultyStyle() {
		BUTTON_STYLE = "-fx-background-color: transparent;"+
						"-fx-background-image: url('" + difficulty.getUrlImageButton()+"');";
		PRESSED_BUTTON_STYLE = "-fx-background-color: transparent;"+
								"-fx-background-image: url('" + difficulty.getUrlImageButton()+"');";
		setStyle(BUTTON_STYLE);
	}

	private void setButtonPressedStyle() 
	{
		setStyle(PRESSED_BUTTON_STYLE);
		setPrefHeight(45);
		setLayoutY(getLayoutY() + 4);
	}

	private void setButtonReleasedStyle() 
	{
		setStyle(BUTTON_STYLE);
		setPrefHeight(49);
		setLayoutY(getLayoutY() -4);
	}

	private void initalizeButtonListeners() 
	{
		setOnMousePressed(new EventHandler<MouseEvent>() 
		{
			public void handle(MouseEvent event) 
			{
				if(event.getButton().equals(MouseButton.PRIMARY))
					setButtonPressedStyle();
			}
				
		});
			
		setOnMouseReleased(new EventHandler<MouseEvent>() 
		{
			public void handle(MouseEvent event) 
			{
				if(event.getButton().equals(MouseButton.PRIMARY))
					setButtonReleasedStyle();
			}
		});
			
		setOnMouseEntered(new EventHandler<MouseEvent>() 
		{
			public void handle(MouseEvent event) 
			{
				setEffect(new DropShadow());
			}
		});
			
		setOnMouseExited(new EventHandler<MouseEvent>() 
		{
			public void handle(MouseEvent event) 
			{
				setEffect(null);
			}
		});
	}
}
		