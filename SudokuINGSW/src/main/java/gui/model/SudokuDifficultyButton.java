package gui.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

public class SudokuDifficultyButton extends Button{
	private final String Font_Path = "src/main/java/gui/resources/TeachersStudent.ttf";
//	private  String BUTTON_PRESSED_STYLE = "-fx-background-color: transparent; -fx-background-image: url('/gui/resources/easyButton.png');";
	private  String BUTTON_FREE_STYLE;
	DIFFICULTY diff;
	public SudokuDifficultyButton(String text, DIFFICULTY diff) {
			this.diff = diff;
			BUTTON_FREE_STYLE = "-fx-background-color: transparent; -fx-background-image: url('" + diff.getUrlImageButton()+"');";
			setText(text);
			setButtonFont();
			setPrefHeight(49);
			setPrefWidth(190);
			setStyle(BUTTON_FREE_STYLE);
			initalizeButtonListeners();
		}
	public DIFFICULTY getDifficulty() {
		return this.diff;
	}
		
		private void setButtonFont() {
			try {
				setFont(Font.loadFont(new FileInputStream(Font_Path), 23));
			} catch (FileNotFoundException e) {
				System.out.println("path non trovata!");
				setFont(Font.loadFont("Verdana", 23));
			}
		}
		
		private void setButtonPressedStyle() {
			setStyle(BUTTON_FREE_STYLE);
			setPrefHeight(45);
//			setLayoutY(getLayoutY() + 4);
		}
		
		private void setButtonReleasedStyle() {
			setStyle(BUTTON_FREE_STYLE);
			setPrefHeight(49);
//			setLayoutY(getLayoutY() -4);
		}
		
		private void initalizeButtonListeners() {
			setOnMousePressed(new EventHandler<MouseEvent>() {

				public void handle(MouseEvent event) {
					if(event.getButton().equals(MouseButton.PRIMARY))
						setButtonPressedStyle();
					
				}
				
			});
			
			setOnMouseReleased(new EventHandler<MouseEvent>() {

	
				public void handle(MouseEvent event) {
					if(event.getButton().equals(MouseButton.PRIMARY))
						setButtonReleasedStyle();
					
				}
				
			});
			
			setOnMouseEntered(new EventHandler<MouseEvent>() {

				
				public void handle(MouseEvent event) {

						setEffect(new DropShadow());
					
				}
				
			});
			
			setOnMouseExited(new EventHandler<MouseEvent>() {

		
				public void handle(MouseEvent event) {

						setEffect(null);
					
				}
				
			});
			
		}

}
