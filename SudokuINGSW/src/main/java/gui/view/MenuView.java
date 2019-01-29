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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
import com.emaraic.utils.Sudoku;

import gui.model.*;

import javafx.stage.Stage;
import logic.ai.Cell;
import logic.ai.GameManager;
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
	
	
	public MenuView() 
	{
		stage = new Stage();
		
		setMenuButtons(new ArrayList<SudokuButton>());

		createBackground();
		createSubScenes();
		createButtons();
		
		stage.setScene(scene);
	}
	
	public void createBackground() 
	{
		Image backgroundImg = new Image("/gui/resources/texture.png");
		BackgroundImage background = new BackgroundImage(backgroundImg, BackgroundRepeat.REPEAT,BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
		pane.setBackground(new Background(background));
		
		ImageView logo = new ImageView("gui/resources/sudokuLogo.png");
		logo.setLayoutX(WIDTH/2 - 100);
		logo.setLayoutY(150);
		logo.setScaleX(0.9);
		logo.setScaleY(0.9);
		pane.getChildren().add(logo);
	}
	
	public void createButtons() 
	{
		SudokuButton playBtn = new SudokuButton("PLAY");
		playBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			public void handle(ActionEvent event) {
				for(SudokuButton btn : menuButtons) {
					if(btn != playBtn) {
						btn.setDisable(!btn.isDisable());
					}
				}
				difficultySubScene.moveSubScene();
			}
		});
		menuButtons.add(playBtn);
		SudokuButton loadBtn = new SudokuButton("LOAD GAME");
		loadBtn.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) 
			{
				if(gameManager.loadGame()) 
				{
					GameView game = new GameView(gameManager.getSudokuCells());
					game.setDifficulty(gameManager.getDifficulty());
					game.hideStage(stage);
				}
				else {
					for(SudokuButton btn : menuButtons) {
						if(btn != loadBtn) {
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
			
			public void handle(ActionEvent event) 
			{
				for(SudokuButton btn : menuButtons) {
					if(btn != scannerBtn) {
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

	private void addButtons() 
	{
		for(SudokuButton button : menuButtons) 
		{
			button.setLayoutX(MENU_BUTTON_X);
			button.setLayoutY(MENU_BUTTON_Y + menuButtons.indexOf(button) * 100);
			pane.getChildren().add(button);
		}
	}

	private void createSubScenes() 
	{
		difficultySubScene = new SudokuSubScene();
		difficultySubScene.setLabel("CHOOSE DIFFICULTY");
		difficultySubScene.getLabel().setStyle("-fx-text-fill : Gold;");
		
		ArrayList<SudokuButton> buttons = new ArrayList<SudokuButton>();
		final SudokuButton easy = new SudokuButton("EASY");
		easy.setDifficulty(DIFFICULTY.EASY);
		easy.setDifficultyStyle();
		easy.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) {
				playSudoku(easy);
			}

		});
		buttons.add(easy);
		final SudokuButton medium = new SudokuButton("MEDIUM");
		medium.setDifficulty(DIFFICULTY.NORMAL);
		medium.setDifficultyStyle();
		medium.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) {
				playSudoku(medium);
			}
		});
		buttons.add(medium);
		final SudokuButton hard = new SudokuButton("HARD");
		hard.setDifficulty(DIFFICULTY.HARD);
		hard.setDifficultyStyle();
		hard.setOnAction(new EventHandler<ActionEvent>() 
		{
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
		
		//load
		loadSubScene = new SudokuSubScene();
		loadSubScene.setLabel("NESSUNA PARTITA SALVATA");
		loadSubScene.setLabelLayout(20,35);
		loadSubScene.setLayout(1500,300);
		loadSubScene.getLabel().setStyle("-fx-text-fill : Gold;");
		loadSubScene.backgroundSettings(350,100);
		loadSubScene.setTransitionCoordinate(-1110, 0);
		loadSubScene.getPane().getChildren().add(loadSubScene.getLabel());
		
		pane.getChildren().add(loadSubScene);
		
		//scanner
		scannerSubScene = new SudokuSubScene();
		scannerSubScene.setLabel("SCANSIONE SUDOKU DA IMMAGINE");
		scannerSubScene.getLabel().setStyle("-fx-text-fill : Gold;");
		
		ArrayList<SudokuButton> buttons2 = new ArrayList<SudokuButton>();
		SudokuButton cameraBtn = new SudokuButton("CAMERA");
		cameraBtn.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) 
			{
				/*Load Pre-trained Network */
				final Logger log = LoggerFactory.getLogger(SudokuSolver.class);
				SudokuSolver.NETWORK = SudokuSolver.loadNetwork();
 
		        final AtomicReference<VideoCapture> capture = new AtomicReference<VideoCapture>(new VideoCapture());
		        capture.get().set(CV_CAP_PROP_FRAME_WIDTH, 1280);
		        capture.get().set(CV_CAP_PROP_FRAME_HEIGHT, 720);

		        if (!capture.get().open(0)) {
		            SudokuSolver.log.error("Can not open the cam !!!");
		        }

		        final AtomicReference<Boolean> start = new AtomicReference<Boolean>(true);

		        Mat colorimg = new Mat();

		        CanvasFrame mainframe = new CanvasFrame("SCANNER");
		        mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		        mainframe.setCanvasSize(500,500);
		        mainframe.setLocationRelativeTo(null);
		        mainframe.setLayout(new BoxLayout(mainframe.getContentPane(), BoxLayout.Y_AXIS));
		        final JButton control = new JButton("STOP");//start and pause camera capturing
		        JPanel panel = new JPanel();
		        panel.setLayout(new FlowLayout());
		        panel.setBackground(Color.WHITE);
		        control.setBackground(new Color(55,135,255));
		        control.setForeground(Color.WHITE);
		        control.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(java.awt.event.ActionEvent e) 
					{
						if(control.getText() == "RIPETI") {
							panel.removeAll();
							panel.add(control);
							panel.repaint();
						}
	                    if (start.get() == true && capture.get().isOpened()) 
	                    {
	                        start.set(false);
	                        capture.get().release();
	                        control.setText("START");
	                    } else {
	                        start.set(true);
	                        capture.set(new VideoCapture());
	                        capture.get().open(0);
	                        control.setText("STOP");
	                    }
					}
				});
		        panel.add(control);
		        mainframe.add(panel, BorderLayout.SOUTH);
		        mainframe.pack();
		        mainframe.setVisible(true);
		        
		        CanvasFrame procframe = new CanvasFrame("Processed Frames");
		        procframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		        procframe.setCanvasSize(200, 200);
		        procframe.setLocation(0, 0);
//		        procframe.setVisible(false);
		        
		        CanvasFrame result = new CanvasFrame("Result ");
		        result.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		        result.setCanvasSize(200, 200);
		        result.setLocation(0, 440);
//		        result.setVisible(false);

		        while (!scanComplete) 
		        {
		            while (start.get() && capture.get().read(colorimg)) 
		            {
		                if (mainframe.isVisible()) 
		                {
		                    /*Convert to grayscale mode*/
		                    Mat sourceGrey = new Mat(colorimg.size(), CV_8UC1);
		                    cvtColor(colorimg, sourceGrey, COLOR_BGR2GRAY);

		                    /*Apply Gaussian Filter*/
		                    Mat blurimg = new Mat(colorimg.size(), CV_8UC1);
		                    GaussianBlur(sourceGrey, blurimg, new Size(5, 5), 0);

		                    /*Binarising Image*/
		                    Mat binimg = new Mat(colorimg.size());
		                    adaptiveThreshold(blurimg, binimg, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY_INV, 19, 3);

		                    Rect r = SudokuSolver.getLargestRect(binimg);
		                    Mat procimg = SudokuSolver.warpPrespectivePuzzle(binimg.clone());

		                    Mat color = new Mat(colorimg);
		                    if (SudokuSolver.isSudokuExist(binimg)) 
		                    {
		                    	//IL SUDOKU VIENE RICONOSCIUTO NELL'INQUADRATURA
		                        SudokuSolver.printCornerPoints(r, colorimg);
		                        //IL MAIN FRAME MOSTRA GLI ANGOLI
		                        mainframe.showImage(SudokuSolver.converter.convert(colorimg));
		                        bitwise_not(procimg, procimg);
		                        Mat clonedf = new Mat(procimg.clone());
		                        Mat canimg = new Mat(procimg.size());
		                        Canny(procimg, canimg, 30, 90);
		                        //imwrite("canny.jpg", canimg);

		                        /* Apply Standard Hough Line Transform */
		                        Mat lines = new Mat();//vector stores the parameters (rho,theta) of the detected lines
		                        //HoughLines(canimg, lines, 1, CV_PI / 180, 70,1,1, 0, CV_PI);
		                        HoughLines(canimg, lines, 1, CV_PI / 180, 100);

		                        FloatRawIndexer srcIndexer = lines.createIndexer();

		                        /*Horizontal lines and one for vertical lines*/
		                        List<org.deeplearning4j.clustering.cluster.Point> hpoints = new ArrayList<org.deeplearning4j.clustering.cluster.Point>();
		                        List<org.deeplearning4j.clustering.cluster.Point> vpoints = new ArrayList<org.deeplearning4j.clustering.cluster.Point>();

		                        for (int i = 0; i < srcIndexer.rows(); i++) {
		                            float[] data = new float[2]; //data[0] is rho and data[1] is theta
		                            srcIndexer.get(0, i, data);
		                            double d[] = {data[0], data[1]};
		                            if (Math.sin(data[1]) > 0.8) {//horizontal lines have a sin value equals 1, I just considered >.8 is horizontal line.
		                                hpoints.add(new org.deeplearning4j.clustering.cluster.Point("hrho" + Math.sin(data[1]), "hrho", d));
		                            } else if (Math.cos(data[1]) > 0.8) {//vertical lines have a cos value equals 1,
		                                vpoints.add(new org.deeplearning4j.clustering.cluster.Point("vrho" + Math.cos(data[1]), "vrho", d));
		                            }
		                        }

		                        /*Cluster vertical and horizontal lines into 10 lines for each using kmeans with 10 iterations*/
		                        KMeansClustering kmeans = KMeansClustering.setup(10, 10, "euclidean");

		                        log.info("Lines Number " + vpoints.size() + " " + hpoints.size());
		                        if (vpoints.size() >= 10 && hpoints.size() >= 10) {
		                            ClusterSet hcs = kmeans.applyTo(hpoints);
		                            List<Cluster> hlines = hcs.getClusters();
		                            Collections.sort(hlines, new LinesComparator());

		                            ClusterSet vcs = kmeans.applyTo(vpoints);
		                            List<Cluster> vlines = vcs.getClusters();
		                            Collections.sort(vlines, new LinesComparator());
		                            if (SudokuSolver.checkLines(vlines, hlines)) {
		                                List<Point> points = SudokuSolver.getPoint(vlines, hlines);
		                                if (points.size() != 100) {
		                                    //break to get another image if number of points not equal 100
		                                    break;
		                                }

		                                /*Print vertical lines, horizontal lines, and the intersection between them */
		                                for (Point point : points) {
		                                    circle(procimg, point, 10, new Scalar(0, 0, 0, 255), CV_FILLED, 8, 0);
		                                }

		                                vlines.addAll(hlines);//appen hlines to vlines to print them in one for loop
		                                for (int i = 0; i < vlines.size(); i++) 
		                                {
		                                    Cluster get = vlines.get(i);
		                                    double rho = get.getCenter().getArray().getDouble(0);
		                                    double theta = get.getCenter().getArray().getDouble(1);
		                                    double a = Math.cos(theta), b = Math.sin(theta);
		                                    double x0 = a * rho, y0 = b * rho;
		                                    CvPoint pt1 = cvPoint((int) Math.round(x0 + 1000 * (-b)), 
		                                    						(int) Math.round(y0 + 1000 * (a))), 
		                                    						  pt2 = cvPoint((int) Math.round(x0 - 1000 * (-b)), 
		                                    						    (int) Math.round(y0 - 1000 * (a)));
		                                    line(procimg, new Point(pt1.x(), pt1.y()),
		                                    				new Point(pt2.x(), pt2.y()), 
		                                    				  new Scalar(0, 0, 0, 0), 3, CV_AA, 0);

		                                }

		                                double puzzle[] = new double[81];
		                                int j = 0;
		                                //Form rectangles of 81 cells from the 100 intersection points
		                                List<Rect> rects = new ArrayList<Rect>();
		                                for (int i = 0; i < points.size() - 11; i++) 
		                                {
		                                    int ri = i / 10;
		                                    int ci = i % 10;
		                                    if (ci != 9 && ri != 9) 
		                                    {
		                                        Point get = points.get(i);
		                                        Point get2 = points.get(i + 11);
		                                        Rect r1 = new Rect(get, get2);
		                                        if ((r1.x() + r1.width() <= clonedf.cols()) && (r1.y() + r1.height() <= clonedf.rows()) && r1.x() >= 0 && r1.y() >= 0) 
		                                        {
		                                            Mat s = SudokuSolver.detectDigit(clonedf.apply(r1));
		                                            rects.add(r1);
		                                            if (s.cols() == 28 && s.rows() == 28)
		                                                puzzle[j] = SudokuSolver.recogniseDigit(s);
		                                            else
		                                                puzzle[j] = 0;
		                                            j++;
		                                        }
		                                    }
		                                }
		                                
		                                imwrite("procimg.jpg", procimg);
		                                INDArray pd = Nd4j.create(puzzle);
		                                INDArray puz = pd.reshape(new int[]{9, 9});
		                                INDArray solvedpuz = puz.dup();
		                                
		                                if (Sudoku.isValid(puzzle)) 
		                                {
		                                    //this code section is reponsible for if the solution of sudoku takes more than 5 second, break it.
		                                    ExecutorService service = Executors.newSingleThreadExecutor();
		                                    try {
												Future<Object> solver = (Future<Object>) service.submit(() -> {Sudoku.solve(0, 0, solvedpuz);});
		                                        System.out.println(solver.get(5, TimeUnit.SECONDS));
		                                    } catch (final TimeoutException e) {
		                                        log.info("It takes a lot of time to solve, Going to break!!");
		                                        /*break to get another image if sudoku solution takes more than 5 seconds
		                                        sometime it takes long time for solving sudoku as a result of incorrect digit recognition.
		                                        Mostely you face this when you rotate the puzzle */
		                                        break;
		                                    } catch (final Exception e) {
		                                        log.error(e.getMessage());
		                                    } finally {
		                                        service.shutdown();
		                                    }

		                                    if (SudokuSolver.isContainsZero(solvedpuz)) {
		                                        break; //break to get another image if solution is invalid
		                                    } else {
		                                        color = new Mat(procimg.size(), CV_8UC3);
		                                        cvtColor(procimg, color, COLOR_GRAY2BGR);
		                                        SudokuSolver.printResult(color, solvedpuz, puz, rects);
		                                    }
		                                } else {//break to get another image if sudoku is invalid
		                                    break;
		                                }
		                                start.set(Boolean.FALSE);
		                                capture.get().release();

		                                JButton solutionBtn = new JButton("SOLUZIONE");
		                                solutionBtn.setBackground(new Color(55,135,255));
		                                solutionBtn.setForeground(Color.white);
		                                solutionBtn.addActionListener(new ActionListener() {
											
											@Override
											public void actionPerformed(java.awt.event.ActionEvent e) {
												playSudokuFromImage(createSudokuCellFromImage(puzzle));
											}
										});
		                                JButton playBtn = new JButton("GIOCA");
		                                playBtn.setBackground(new Color(55,135,255));
		                                playBtn.setForeground(Color.white);
		                                playBtn.addActionListener(new ActionListener() {
											
											@Override
											public void actionPerformed(java.awt.event.ActionEvent e) {
												playSudokuFromImage(createSudokuCellFromImage(puzzle));
											}

										});
		                		        panel.add(solutionBtn);
		                		        panel.add(playBtn);
		                                control.setText("RIPETI");
		                                
		                            }//End if checkLines
		                        }
		                        procframe.showImage(SudokuSolver.converter.convert(procimg));
		                        result.showImage(SudokuSolver.converter.convert(color));

		                    } else {//End If sudoku puzzle exists
		                        mainframe.showImage(SudokuSolver.converter.convert(colorimg));
		                        procframe.showImage(SudokuSolver.converter.convert(procimg));
		                        result.showImage(SudokuSolver.converter.convert(color));
		                    }
		                } else {//End if grabbed image equal null
		                    System.out.println("Error!!!!");
		                    System.exit(1);
		                }
		                try {
		                    Thread.sleep(150);
		                } catch (InterruptedException ex) {
		                    log.error(ex.getMessage());
		                }
		            }//End While Start's Condition
		            try {
		                Thread.sleep(400);
		            } catch (InterruptedException ex) {
		                log.error(ex.getMessage());
		            }
		        }//End While !Continue
		    }
		});
		buttons2.add(cameraBtn);
		SudokuButton galleryBtn = new SudokuButton("GALLERIA");
		galleryBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			public void handle(ActionEvent event) {
				
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
		scannerSubScene.setLabelLayout(30,30);
		scannerSubScene.getPane().getChildren().add(scannerSubScene.getLabel());
		scannerSubScene.getPane().getChildren().add(buttonsBox2);
		
		pane.getChildren().add(scannerSubScene);
	}

	public ArrayList<SudokuCell> createSudokuCellFromImage(double[] puzzle) 
	{
		ArrayList<SudokuCell> cells = new ArrayList<SudokuCell>();
		int r = 0, c = 0;
		for(int i = 0; i < puzzle.length; i++) 
		{
			if(puzzle[i] != 0) {
				SudokuCell cell = new SudokuCell(r,c,(int)puzzle[i]);
				cell.showContent();
				cells.add(cell);
			}
			c++;
        	if(isMultiple(i+1)) {
        		r++;
        		c = 0;
        	}
        }
		if(cells.size() > 0)
	        scanComplete = true;

		return cells;
	}

	private boolean isMultiple(int i) 
	{
		for(int j = 1; j <= 81; j++)
			if(9*j == i)
				return true;
		return false;
	}
	
	private void playSudoku(SudokuButton button) 
	{
		GameView game = new GameView(button.getDifficulty());
		game.hideStage(stage);
	}
	
	private void playSudokuFromImage(ArrayList<SudokuCell> cells) {
		GameView game = new GameView(cells);
		game.hideStage(stage);
	}
	
	public Stage getStage() { return stage; }

	public ArrayList<SudokuButton> getMenuButtons() {
		return menuButtons;
	}

	public void setMenuButtons(ArrayList<SudokuButton> menuButtons) {
		this.menuButtons = menuButtons;
	}

	public void setGameManager(GameManager gameManager) {
		this.gameManager = gameManager;
	}

}
