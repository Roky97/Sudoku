package logic.ai;

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
import java.awt.event.ActionEvent;
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
import javax.swing.JOptionPane;
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

import gui.model.SudokuCell;
import gui.view.GameView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

@SuppressWarnings("restriction")
public class Scanner {

	private Stage hideStage;
	private ArrayList<SudokuCell> sudokuCells;
	
	final Logger log;
	private CanvasFrame mainframe;
	private JPanel panel;
	private JButton control;
	private Mat colorimg ;
	private boolean scanComplete;
    private Mat color;
	private AtomicReference<Boolean> start;
	private AtomicReference<VideoCapture> capture;
	private CanvasFrame result;
	private boolean in=true;
    private boolean selected=false;
	private GameManager gameManager = new GameManager();
	
	public Scanner() {
		/* Load Pre-trained Network */
		log = LoggerFactory.getLogger(SudokuSolver.class);
		SudokuSolver.NETWORK = SudokuSolver.loadNetwork();
		scanComplete = false;
		colorimg = new Mat();
		hideStage = new Stage();
	}
	
	public boolean enableCamera() 
	{
		in=true;
		capture = new AtomicReference<VideoCapture>(new VideoCapture());
		capture.get().set(CV_CAP_PROP_FRAME_WIDTH, 1280);
		capture.get().set(CV_CAP_PROP_FRAME_HEIGHT, 720);

		if (!capture.get().open(0)) {
			SudokuSolver.log.error("Can not open the cam !!!");
			return false;
		}
		
		start = new AtomicReference<Boolean>(true);

		mainframe = new CanvasFrame("SCANNER");
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainframe.setCanvasSize(500, 500);
		mainframe.setLocationRelativeTo(null);
		mainframe.setLayout(new BoxLayout(mainframe.getContentPane(), BoxLayout.Y_AXIS));
		result = new CanvasFrame("SOLUTION");
		result.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		result.setCanvasSize(500, 500);
		result.setLocationRelativeTo(null);;
		result.setVisible(false);
		control = new JButton("STOP");// start and pause camera capturing
		control.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(control.getText() == "REPEAT") 
				{
					control.setText("STOP");
					panel.removeAll();
					panel.add(control);
					panel.repaint();
					result.setVisible(false);
					scanComplete = false;
					start.set(true);
					if (!capture.get().open(0)) {
						SudokuSolver.log.error("Can not open the cam !!!");
					}
				}
				else if (start.get() == true && capture.get().isOpened()) {
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
		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setBackground(Color.WHITE);
		control.setBackground(new Color(55, 135, 255));
		control.setForeground(Color.WHITE);
		
		panel.add(control);
		mainframe.add(panel, BorderLayout.SOUTH);
		mainframe.pack();
		mainframe.setVisible(true);
		
		return true;
	}
	
	@SuppressWarnings({ "unchecked", "resource" })
	public void startScanning() 
	{
		while (!scanComplete) 
		{
			while (start.get() && capture.get().read(colorimg)) 
			{
				if (mainframe.isVisible()) 
				{
					/* Convert to grayscale mode */
					Mat sourceGrey = new Mat(colorimg.size(), CV_8UC1);
					cvtColor(colorimg, sourceGrey, COLOR_BGR2GRAY);

					/* Apply Gaussian Filter */
					Mat blurimg = new Mat(colorimg.size(), CV_8UC1);
					GaussianBlur(sourceGrey, blurimg, new Size(5, 5), 0);

					/* Binarising Image */
					Mat binimg = new Mat(colorimg.size());
					adaptiveThreshold(blurimg, binimg, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY_INV, 19, 3);

					Rect r = SudokuSolver.getLargestRect(binimg);
					Mat procimg = SudokuSolver.warpPrespectivePuzzle(binimg.clone());

					Mat color = new Mat(colorimg);
					if (SudokuSolver.isSudokuExist(binimg)) 
					{
						// IL SUDOKU VIENE RICONOSCIUTO NELL'INQUADRATURA
						SudokuSolver.printCornerPoints(r, colorimg);
						// IL MAIN FRAME MOSTRA GLI ANGOLI
						mainframe.showImage(SudokuSolver.converter.convert(colorimg));
						bitwise_not(procimg, procimg);
						Mat clonedf = new Mat(procimg.clone());
						Mat canimg = new Mat(procimg.size());
						Canny(procimg, canimg, 30, 90);
						// imwrite("canny.jpg", canimg);

						/* Apply Standard Hough Line Transform */
						Mat lines = new Mat();// vector stores the parameters (rho,theta) of the detected lines
						// HoughLines(canimg, lines, 1, CV_PI / 180, 70,1,1, 0, CV_PI);
						HoughLines(canimg, lines, 1, CV_PI / 180, 100);

						FloatRawIndexer srcIndexer = lines.createIndexer();

						/* Horizontal lines and one for vertical lines */
						List<org.deeplearning4j.clustering.cluster.Point> hpoints = new ArrayList<org.deeplearning4j.clustering.cluster.Point>();
						List<org.deeplearning4j.clustering.cluster.Point> vpoints = new ArrayList<org.deeplearning4j.clustering.cluster.Point>();

						for (int i = 0; i < srcIndexer.rows(); i++) 
						{
							float[] data = new float[2]; // data[0] is rho and data[1] is theta
							srcIndexer.get(0, i, data);
							double d[] = { data[0], data[1] };
							if (Math.sin(data[1]) > 0.8) 
							{// horizontal lines have a sin value equals 1, I just
							 // considered >.8 is horizontal line.
								hpoints.add(new org.deeplearning4j.clustering.cluster.Point(
										"hrho" + Math.sin(data[1]), "hrho", d));
							} 
							else if (Math.cos(data[1]) > 0.8) {
								// vertical lines have a cos value equals 1,
								vpoints.add(new org.deeplearning4j.clustering.cluster.Point("vrho" + Math.cos(data[1]), "vrho", d));
							}
						}

						/* Cluster vertical and horizontal lines into 10 lines for each using kmeans
						 * with 10 iterations
						 */
						KMeansClustering kmeans = KMeansClustering.setup(10, 10, "euclidean");

						log.info("Lines Number " + vpoints.size() + " " + hpoints.size());
						
						if (vpoints.size() >= 10 && hpoints.size() >= 10) 
						{
							ClusterSet hcs = kmeans.applyTo(hpoints);
							List<Cluster> hlines = hcs.getClusters();
							Collections.sort(hlines, new LinesComparator());
							ClusterSet vcs = kmeans.applyTo(vpoints);
							List<Cluster> vlines = vcs.getClusters();
							Collections.sort(vlines, new LinesComparator());
							if (SudokuSolver.checkLines(vlines, hlines)) 
							{
								List<Point> points = SudokuSolver.getPoint(vlines, hlines);
								if (points.size() != 100) {
									// break to get another image if number of points not equal 100
									break;
								}
								/* Print vertical lines, horizontal lines, and the intersection between them */
								for (Point point : points) {
									circle(procimg, point, 10, new Scalar(0, 0, 0, 255), CV_FILLED, 8, 0);
								}
								vlines.addAll(hlines);// appen hlines to vlines to print them in one for loop
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
									line(procimg, new Point(pt1.x(), pt1.y()), new Point(pt2.x(), pt2.y()),
											new Scalar(0, 0, 0, 0), 3, CV_AA, 0);
								}

								double puzzle[] = new double[81];
								int j = 0;
								// Form rectangles of 81 cells from the 100 intersection points
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
										if ((r1.x() + r1.width() <= clonedf.cols())
												&& (r1.y() + r1.height() <= clonedf.rows()) && r1.x() >= 0
												&& r1.y() >= 0) {
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
								INDArray puz = pd.reshape(new int[] { 9, 9 });
								INDArray solvedpuz = puz.dup();

								if (Sudoku.isValid(puzzle)) 
								{
//this code section is reponsible for if the solution of sudoku takes more than 5 second, break it.
									ExecutorService service = Executors.newSingleThreadExecutor();
									try {
										Future<Object> solver = (Future<Object>) service.submit(() -> {
											Sudoku.solve(0, 0, solvedpuz);
										});
										System.out.println(solver.get(5, TimeUnit.SECONDS));
									} catch (final TimeoutException e) {
										log.info("It takes a lot of time to solve, Going to break!!");
										/* break to get another image if sudoku solution takes more than 5
										 * seconds sometime it takes long time for solving sudoku as a result of
										 * incorrect digit recognition. Mostely you face this when you rotate
										 * the puzzle
										 */
										break;
									} catch (final Exception e) {
										log.error(e.getMessage());
									} finally {
										service.shutdown();
									}

									if (SudokuSolver.isContainsZero(solvedpuz)) {
										break; // break to get another image if solution is invalid
									} else {
										color = new Mat(procimg.size(), CV_8UC3);
										cvtColor(procimg, color, COLOR_GRAY2BGR);
										SudokuSolver.printResult(color, solvedpuz, puz, rects);
									}
								} else {
									// break to get another image if sudoku is invalid
									break;
								}
								start.set(Boolean.FALSE);
								capture.get().release();
								
								modifyPanel(puzzle);
							}
							result.showImage(SudokuSolver.converter.convert(color));
						} 
					}else {
						// End If sudoku puzzle exists
						mainframe.showImage(SudokuSolver.converter.convert(colorimg));
					}
				} else {
					// End if grabbed image equal null
					System.out.println("Error!!!!");
					System.exit(1);
				}
				try {
					Thread.sleep(150);
				} catch (InterruptedException ex) {
					log.error(ex.getMessage());
				}
			}
			try {
				Thread.sleep(400);
			} catch (InterruptedException ex) {
				log.error(ex.getMessage());
			}
		} // End While !scanComplete
		if(gameManager.getSolution(gameManager.parseToCell(sudokuCells))) 
		{
			playSudokuFromImage();
			mainframe.setVisible(false);
			result.setVisible(false);
		}else {
			JOptionPane.showMessageDialog(mainframe,"This Sudoku has no solution!");
		}
	}
	
	public boolean initGallery() 
	{
		in=false;
		start = new AtomicReference<Boolean>(true);

		mainframe = new CanvasFrame("SCANNER");
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainframe.setCanvasSize(500, 500);
		mainframe.setLocationRelativeTo(null);
		mainframe.setLayout(new BoxLayout(mainframe.getContentPane(), BoxLayout.Y_AXIS));
		result = new CanvasFrame("SOLUTION");
		result.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		result.setCanvasSize(500, 500);
		result.setLocationRelativeTo(null);;
		result.setVisible(false);
		control = new JButton("STOP");// start and pause camera capturing
		control.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				control.setText("STOP");
				panel.removeAll();
				panel.add(control);
				panel.repaint();
				result.setVisible(false);
				scanComplete = false;
				start.set(true);
			}
		});
		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setBackground(Color.WHITE);
		control.setBackground(new Color(55, 135, 255));
		control.setForeground(Color.WHITE);
		
		panel.add(control);
		mainframe.add(panel, BorderLayout.SOUTH);
		mainframe.pack();
		mainframe.setVisible(true);
		
		return true;
	}
	
	@SuppressWarnings({ "resource", "unchecked" })
	public void startScanning(Mat galleryimg) 
	{
		colorimg=galleryimg;
		while (!scanComplete) 
		{
			while (start.get()) 
			{
				if (mainframe.isVisible()) 
				{
					/* Convert to grayscale mode */
					Mat sourceGrey = new Mat(colorimg.size(), CV_8UC1);
					cvtColor(colorimg, sourceGrey, COLOR_BGR2GRAY);

					/* Apply Gaussian Filter */
					Mat blurimg = new Mat(colorimg.size(), CV_8UC1);
					GaussianBlur(sourceGrey, blurimg, new Size(5, 5), 0);

					/* Binarising Image */
					Mat binimg = new Mat(colorimg.size());
					adaptiveThreshold(blurimg, binimg, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY_INV, 19, 3);

					Rect r = SudokuSolver.getLargestRect(binimg);
					Mat procimg = SudokuSolver.warpPrespectivePuzzle(binimg.clone());

					color = new Mat(colorimg);
					if (SudokuSolver.isSudokuExist(binimg)) 
					{
						// IL SUDOKU VIENE RICONOSCIUTO NELL'INQUADRATURA
						SudokuSolver.printCornerPoints(r, colorimg);
						// IL MAIN FRAME MOSTRA GLI ANGOLI
						mainframe.showImage(SudokuSolver.converter.convert(colorimg));
						bitwise_not(procimg, procimg);
						Mat clonedf = new Mat(procimg.clone());
						Mat canimg = new Mat(procimg.size());
						Canny(procimg, canimg, 30, 90);
						// imwrite("canny.jpg", canimg);

						/* Apply Standard Hough Line Transform */
						Mat lines = new Mat();// vector stores the parameters (rho,theta) of the detected lines
						// HoughLines(canimg, lines, 1, CV_PI / 180, 70,1,1, 0, CV_PI);
						HoughLines(canimg, lines, 1, CV_PI / 180, 100);

						FloatRawIndexer srcIndexer = lines.createIndexer();

						/* Horizontal lines and one for vertical lines */
						List<org.deeplearning4j.clustering.cluster.Point> hpoints = new ArrayList<org.deeplearning4j.clustering.cluster.Point>();
						List<org.deeplearning4j.clustering.cluster.Point> vpoints = new ArrayList<org.deeplearning4j.clustering.cluster.Point>();

						for (int i = 0; i < srcIndexer.rows(); i++) 
						{
							float[] data = new float[2]; // data[0] is rho and data[1] is theta
							srcIndexer.get(0, i, data);
							double d[] = { data[0], data[1] };
							if (Math.sin(data[1]) > 0.8) 
							{// horizontal lines have a sin value equals 1, I just
							 // considered >.8 is horizontal line.
								hpoints.add(new org.deeplearning4j.clustering.cluster.Point(
										"hrho" + Math.sin(data[1]), "hrho", d));
							} 
							else if (Math.cos(data[1]) > 0.8) {
								// vertical lines have a cos value equals 1,
								vpoints.add(new org.deeplearning4j.clustering.cluster.Point("vrho" + Math.cos(data[1]), "vrho", d));
							}
						}

						/* Cluster vertical and horizontal lines into 10 lines for each using kmeans
						 * with 10 iterations
						 */
						KMeansClustering kmeans = KMeansClustering.setup(10, 10, "euclidean");

						log.info("Lines Number " + vpoints.size() + " " + hpoints.size());
						
						if (vpoints.size() >= 10 && hpoints.size() >= 10) 
						{
							ClusterSet hcs = kmeans.applyTo(hpoints);
							List<Cluster> hlines = hcs.getClusters();
							Collections.sort(hlines, new LinesComparator());
							ClusterSet vcs = kmeans.applyTo(vpoints);
							List<Cluster> vlines = vcs.getClusters();
							Collections.sort(vlines, new LinesComparator());
							if (SudokuSolver.checkLines(vlines, hlines)) 
							{
								List<Point> points = SudokuSolver.getPoint(vlines, hlines);
								if (points.size() != 100) {
									// break to get another image if number of points not equal 100
									break;
								}
								/* Print vertical lines, horizontal lines, and the intersection between them */
								for (Point point : points) {
									circle(procimg, point, 10, new Scalar(0, 0, 0, 255), CV_FILLED, 8, 0);
								}
								vlines.addAll(hlines);// appen hlines to vlines to print them in one for loop
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
									line(procimg, new Point(pt1.x(), pt1.y()), new Point(pt2.x(), pt2.y()),
											new Scalar(0, 0, 0, 0), 3, CV_AA, 0);
								}

								double puzzle[] = new double[81];
								int j = 0;
								// Form rectangles of 81 cells from the 100 intersection points
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
										if ((r1.x() + r1.width() <= clonedf.cols())
												&& (r1.y() + r1.height() <= clonedf.rows()) && r1.x() >= 0
												&& r1.y() >= 0) {
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
								INDArray puz = pd.reshape(new int[] { 9, 9 });
								INDArray solvedpuz = puz.dup();

								if (Sudoku.isValid(puzzle)) 
								{
//this code section is reponsible for if the solution of sudoku takes more than 5 second, break it.
									ExecutorService service = Executors.newSingleThreadExecutor();
									try {
										Future<Object> solver = (Future<Object>) service.submit(() -> {
											Sudoku.solve(0, 0, solvedpuz);
										});

										System.out.println(solver.get(50, TimeUnit.SECONDS));

										

									} catch (final TimeoutException e) {
										log.info("It takes a lot of time to solve, Going to break!!");
										/* break to get another image if sudoku solution takes more than 5
										 * seconds sometime it takes long time for solving sudoku as a result of
										 * incorrect digit recognition. Mostely you face this when you rotate
										 * the puzzle
										 */
										break;
									} catch (final Exception e) {
										log.error(e.getMessage());
									} finally {
										service.shutdown();
									}

									if (SudokuSolver.isContainsZero(solvedpuz)) {
										break; // break to get another image if solution is invalid
									} else {
										color = new Mat(procimg.size(), CV_8UC3);
										cvtColor(procimg, color, COLOR_GRAY2BGR);
										SudokuSolver.printResult(color, solvedpuz, puz, rects);
									}
								} else {
									//createSudokuCellFromImage(puzzle);
									//start.set(false);
									//scanComplete = true;
									// break to get another image if sudoku is invalid
									break;
								}
								start.set(Boolean.FALSE);
								modifyPanel(puzzle);
							}
							
						} 
					}else {
						// End If sudoku puzzle exists
						mainframe.showImage(SudokuSolver.converter.convert(colorimg));
					}
				} else {
					// End if grabbed image equal null
					System.out.println("Error!!!!");
					System.exit(1);
				}
				try {
					Thread.sleep(150);
				} catch (InterruptedException ex) {
					log.error(ex.getMessage());
				}
			}
			try {
				Thread.sleep(400);
			} catch (InterruptedException ex) {
				log.error(ex.getMessage());
			}
		} // End While !scanComplete
		playSudokuFromImage();
		mainframe.setVisible(false);
		result.setVisible(false);
		/*if(gameManager.getSolution(gameManager.parseToCell(sudokuCells))) 
		{
			playSudokuFromImage();
			mainframe.setVisible(false);
			result.setVisible(false);
		}else {
			JOptionPane.showMessageDialog(mainframe,"This Sudoku has no solution!");
		}*/
	}

	private void modifyPanel(double[] puzzle) 
	{
		JButton solutionBtn = new JButton("SOLUTION");
		solutionBtn.setBackground(new Color(55, 135, 255));
		solutionBtn.setForeground(Color.white);
		solutionBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) 
			{
				if(in)result.setVisible(!result.isVisible());
				else{
					if(!selected) {
						mainframe.showImage(SudokuSolver.converter.convert(color));
						selected=true;
					}
					else {
						mainframe.showImage(SudokuSolver.converter.convert(colorimg));
						selected=false;
					}
				}
			}
		});
		JButton playBtn = new JButton("PLAY GAME");
		playBtn.setBackground(new Color(55, 135, 255));
		playBtn.setForeground(Color.white);
		playBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) 
			{
				result.setVisible(false);
				createSudokuCellFromImage(puzzle);
			}
		});
		control.setText("REPEAT");
		panel.add(solutionBtn);
		panel.add(playBtn);
		panel.repaint();
	}

	private void createSudokuCellFromImage(double[] puzzle) 
	{
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
		if (sudokuCells.size() > 0) {
			scanComplete = true;
		}
	}

	private boolean isMultiple(int i) {
		for (int j = 1; j <= 81; j++)
			if (9 * j == i)
				return true;
		return false;
	}
	
	private void playSudokuFromImage() 
	{
		GameView gameView = new GameView(sudokuCells);
		gameView.createTimerLabel();
		gameView.createSubScene();
		gameView.hideStage(hideStage);
	}

	
	public void manageDisplayTransition(Stage stage) {
		stage.hide();
		hideStage = stage;
	}
			
}
