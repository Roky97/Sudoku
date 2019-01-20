package gui.view;

import javafx.stage.Stage;
import logic.ai.GameManager;

@SuppressWarnings("restriction")
public interface IView
{
	void createBackground();
	void createButtons();
	Stage getStage();
	void setGameManager(GameManager gameManager);
}
