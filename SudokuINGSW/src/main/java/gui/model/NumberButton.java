package gui.model;

import javafx.scene.control.Button;

@SuppressWarnings("restriction")
public class NumberButton extends Button {

	public NumberButton(String name) {
		setText(name);
		setScaleX(1.2);
		setScaleY(1.2);
		setFullColor();
	}
	
	public void setFullColor() {setStyle("-fx-background-color: MediumSeaGreen");;}
}
