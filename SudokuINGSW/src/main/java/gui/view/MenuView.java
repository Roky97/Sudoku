package gui.view;

import static org.bytedeco.javacpp.opencv_core.CV_8UC1;
import static org.bytedeco.javacpp.opencv_core.CV_8UC3;
import static org.bytedeco.javacpp.opencv_core.CV_PI;
import static org.bytedeco.javacpp.opencv_core.bitwise_not;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_GRAY2BGR;
import static org.bytedeco.javacpp.opencv_imgproc.CV_AA;
import static org.bytedeco.javacpp.opencv_imgproc.CV_FILLED;
import static org.bytedeco.javacpp.opencv_imgproc.Canny;
import static org.bytedeco.javacpp.opencv_imgproc.GaussianBlur;
import static org.bytedeco.javacpp.opencv_imgproc.HoughLines;
import static org.bytedeco.javacpp.opencv_imgproc.THRESH_BINARY_INV;
import static org.bytedeco.javacpp.opencv_imgproc.adaptiveThreshold;
import static org.bytedeco.javacpp.opencv_imgproc.circle;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.line;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_FRAME_HEIGHT;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_FRAME_WIDTH;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_videoio.VideoCapture;
import org.bytedeco.javacv.CanvasFrame;
import org.deeplearning4j.clustering.cluster.Cluster;
import org.deeplearning4j.clustering.cluster.ClusterSet;
import org.deeplearning4j.clustering.kmeans.KMeansClustering;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emaraic.sudoku.SudokuSolver;
import com.emaraic.utils.LinesComparator;
import com.emaraic.utils.OpenCVUtilsJava;
import com.emaraic.utils.Sudoku;

import gui.model.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import logic.ai.Cell;
import logic.ai.GameManager;
import logic.ai.Scanner;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.VBox;

@SuppressWarnings("restriction")
public class MenuView extends ViewManager implements IView {

	private Stage stage;
    
	private static final int MENU_BUTTON_X = 50;
	private static final int MENU_BUTTON_Y = 150;

	private ArrayList<SudokuButton> menuButtons;

	private SudokuSubScene difficultySubScene;
	private SudokuSubScene scannerSubScene;
	private boolean scanComplete = false;
	private SudokuSubScene loadSubScene;
	private boolean gioca;

	// Campi da usare per lo scanner
	private ArrayList<SudokuCell> sudokuCells;
	private ArrayList<Cell> cells;
	private String action;

	public MenuView() {
		stage = new Stage();

		setMenuButtons(new ArrayList<SudokuButton>());
		createBackground();
		createSubScenes();
		createButtons();

		stage.setScene(scene);
	}

	public void createBackground() {
		Image backgroundImg = new Image("/gui/resources/texture.png");
		BackgroundImage background = new BackgroundImage(backgroundImg, BackgroundRepeat.REPEAT,
				BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
		pane.setBackground(new Background(background));

		ImageView logo = new ImageView("gui/resources/sudokuLogo.png");
		logo.setLayoutX(WIDTH / 2 - 100);
		logo.setLayoutY(150);
		logo.setScaleX(0.9);
		logo.setScaleY(0.9);
		pane.getChildren().add(logo);
	}

	public void createButtons() {
		SudokuButton playBtn = new SudokuButton("PLAY");
		playBtn.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				for (SudokuButton btn : menuButtons) {
					if (btn != playBtn) {
						btn.setDisable(!btn.isDisable());
					}
				}
				difficultySubScene.moveSubScene();
			}
		});
		menuButtons.add(playBtn);

		SudokuButton loadBtn = new SudokuButton("LOAD GAME");
		loadBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if (gameManager.loadGame()) {
					GameView game = new GameView(gameManager.getSudokuCells());
					game.setDifficulty(gameManager.getDifficulty());
					game.createSubScene();
					game.hideStage(stage);
				} else {
					for (SudokuButton btn : menuButtons) {
						if (btn != loadBtn) {
							btn.setDisable(!btn.isDisable());
						}
					}
					loadSubScene.moveSubScene();
				}
			}
		});
		menuButtons.add(loadBtn);

		SudokuButton scannerBtn = new SudokuButton("SCANNER");
		scannerBtn.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				for (SudokuButton btn : menuButtons) {
					if (btn != scannerBtn) {
						btn.setDisable(!btn.isDisable());
					}
				}
				scannerSubScene.moveSubScene();
			}
		});
		menuButtons.add(scannerBtn);

		SudokuButton exitBtn = new SudokuButton("EXIT");
		exitBtn.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				stage.close();
			}
		});
		menuButtons.add(exitBtn);

		addButtons();
	}

	private void addButtons() {
		for (SudokuButton button : menuButtons) {
			button.setLayoutX(MENU_BUTTON_X);
			button.setLayoutY(MENU_BUTTON_Y + menuButtons.indexOf(button) * 100);
			pane.getChildren().add(button);
		}
	}

	private void createSubScenes() {
		difficultySubScene = new SudokuSubScene();
		difficultySubScene.setLabel("CHOOSE DIFFICULTY");
		difficultySubScene.getLabel().setStyle("-fx-text-fill : Gold;");

		ArrayList<SudokuButton> buttons = new ArrayList<SudokuButton>();
		final SudokuButton easy = new SudokuButton("EASY");
		easy.setDifficulty(DIFFICULTY.EASY);
		easy.setDifficultyStyle();
		easy.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				playSudoku(easy);
			}

		});
		buttons.add(easy);
		final SudokuButton medium = new SudokuButton("MEDIUM");
		medium.setDifficulty(DIFFICULTY.NORMAL);
		medium.setDifficultyStyle();
		medium.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				playSudoku(medium);
			}
		});
		buttons.add(medium);
		final SudokuButton hard = new SudokuButton("HARD");
		hard.setDifficulty(DIFFICULTY.HARD);
		hard.setDifficultyStyle();
		hard.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				playSudoku(hard);
			}
		});
		buttons.add(hard);

		difficultySubScene.addButtons(buttons);

		pane.getChildren().add(difficultySubScene);

		VBox buttonsBox = new VBox();
		buttonsBox.setSpacing(20);
		buttonsBox.setAlignment(Pos.CENTER_RIGHT);
		buttonsBox.getChildren().addAll(difficultySubScene.getButtons());
		buttonsBox.setLayoutX(130);
		buttonsBox.setLayoutY(100);

		difficultySubScene.getPane().getChildren().add(difficultySubScene.getLabel());
		difficultySubScene.getPane().getChildren().add(buttonsBox);

		// load
		loadSubScene = new SudokuSubScene();
		loadSubScene.setLabel("NESSUNA PARTITA SALVATA");
		loadSubScene.setLabelLayout(20, 35);
		loadSubScene.setLayout(1500, 300);
		loadSubScene.getLabel().setStyle("-fx-text-fill : Gold;");
		loadSubScene.backgroundSettings(350, 100);
		loadSubScene.setTransitionCoordinate(-1110, 0);
		loadSubScene.getPane().getChildren().add(loadSubScene.getLabel());

		pane.getChildren().add(loadSubScene);

		// scanner
		scannerSubScene = new SudokuSubScene();
		scannerSubScene.setLabel("SCANSIONE SUDOKU DA IMMAGINE");
		scannerSubScene.getLabel().setStyle("-fx-text-fill : Gold;");

		ArrayList<SudokuButton> buttons2 = new ArrayList<SudokuButton>();
		SudokuButton cameraBtn = new SudokuButton("CAMERA");
		cameraBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Scanner scanner = new Scanner();
				if (scanner.enableCamera()) {
					scanner.manageDisplayTransition(stage);
					scanner.startScanning();
				}
			}
		});
		buttons2.add(cameraBtn);

		SudokuButton galleryBtn = new SudokuButton("GALLERIA");
		galleryBtn.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {

				
				//if (scanner.enableCamera()) {
//				while(true) {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Open Resource File");
					fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
					File selectedFile = fileChooser.showOpenDialog(stage);
					Mat colorimg = OpenCVUtilsJava.loadOrExit(selectedFile);
					Scanner scanner = new Scanner();
				    scanner.initGallery(fileChooser);
					scanner.manageDisplayTransition(stage);
					scanner.startScanning(colorimg);
					fileChooser.showOpenDialog(null);
//				}
				//}
			}

		});
		buttons2.add(galleryBtn);
		scannerSubScene.addButtons(buttons2);

		VBox buttonsBox2 = new VBox();
		buttonsBox2.setSpacing(20);
		buttonsBox2.setAlignment(Pos.CENTER_RIGHT);
		buttonsBox2.getChildren().addAll(scannerSubScene.getButtons());
		buttonsBox2.setLayoutX(130);
		buttonsBox2.setLayoutY(130);
		scannerSubScene.setLabelLayout(30, 30);
		scannerSubScene.getPane().getChildren().add(scannerSubScene.getLabel());
		scannerSubScene.getPane().getChildren().add(buttonsBox2);

		pane.getChildren().add(scannerSubScene);
	}

	public void createSudokuCellFromImage(double[] puzzle, String action) {
		sudokuCells = new ArrayList<SudokuCell>();
		int r = 0, c = 0;
		for (int i = 0; i < puzzle.length; i++) {
			SudokuCell cell = new SudokuCell(r, c, (int) puzzle[i]);
			if (puzzle[i] != 0) {
				cell.showContent();
				sudokuCells.add(cell);
			}
			c++;
			if (isMultiple(i + 1)) {
				r++;
				c = 0;
			}
		}
		this.action = action;
		if (sudokuCells.size() > 0)
			scanComplete = true;
	}

	public void createCellFromImage(double[] puzzle, String action) {
		cells = new ArrayList<Cell>();
		int r = 0, c = 0;
		for (int i = 0; i < puzzle.length; i++) {
			if (puzzle[i] != 0) {
				Cell cell = new Cell(r, c, (int) puzzle[i]);
				cells.add(cell);
			}
			c++;
			if (isMultiple(i + 1)) {
				r++;
				c = 0;
			}
		}
		this.action = action;
		if (cells.size() > 0)
			scanComplete = true;
	}

	private boolean isMultiple(int i) {
		for (int j = 1; j <= 81; j++)
			if (9 * j == i)
				return true;
		return false;
	}

	private void playSudoku(SudokuButton button) {
		GameView game = new GameView(button.getDifficulty());
		game.hideStage(stage);
	}

	private void playSudokuFromImage() {
		if (this.action.equals("PLAY")) {
			GameView gameView = new GameView(sudokuCells);
			gameView.hideStage(stage);
		} else {
			// GameManager gameManager = new GameManager();
			// gameManager.getSolution(cells);
			// GameView gameView = new
			// GameView(gameManager.parseToSudokuCells(gameManager.getGrid()));
			// gameView.hideStage(stage);
		}
	}

	public Stage getStage() {
		return stage;
	}

	public ArrayList<SudokuButton> getMenuButtons() {
		return menuButtons;
	}

	public void setMenuButtons(ArrayList<SudokuButton> menuButtons) {
		this.menuButtons = menuButtons;
	}

	public void setGameManager(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public SudokuSubScene getSubscene() {
		return difficultySubScene;
	}

}
