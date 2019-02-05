package logic.ai;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

import it.unical.mat.embasp.base.Handler;
import it.unical.mat.embasp.base.InputProgram;
import it.unical.mat.embasp.base.OptionDescriptor;
import it.unical.mat.embasp.base.Output;
import it.unical.mat.embasp.languages.asp.ASPInputProgram;
import it.unical.mat.embasp.languages.asp.AnswerSet;
import it.unical.mat.embasp.languages.asp.AnswerSets;
import it.unical.mat.embasp.platforms.desktop.DesktopHandler;
import it.unical.mat.embasp.specializations.dlv.DLVAnswerSets;
import it.unical.mat.embasp.specializations.dlv.desktop.DLVDesktopService;

public class SudokuGenerator {

	private static String encodingResource = "encoding/sudokugenerator";
	private static String executable;
	private static Handler handler;
	
	private ArrayList<Cell> cells;
	
	public SudokuGenerator() 
	{
		cells = new ArrayList<Cell>();
		String os = System.getProperty("os.name");
		
		if(os.contains("Windows"))
			executable = "lib/dlv.mingw.exe";
		else if(os.contains("Linux"))
			executable = "lib/dlv.x86-64-linux-elf-static.bin";
		else
			executable = "lib/dlvApple.bin";
	}

	public boolean generateSudoku(ArrayList<Cell> generatedCell) 
	{
		handler = new DesktopHandler(new DLVDesktopService(executable));
		
		InputProgram encoding = new ASPInputProgram();
		encoding.addFilesPath(encodingResource);
		handler.addProgram(encoding);
		
		InputProgram facts = new ASPInputProgram();
		
		cells = generatedCell;
				
		try {
			for(Cell cell : cells)
				facts.addObjectInput(cell);
		} catch (Exception e) {
			e.printStackTrace();
		}

		handler.addProgram(facts);
		OptionDescriptor filter = new OptionDescriptor("-n=1 ");
		handler.addOption(filter);
		Output out = handler.startSync();
		AnswerSets answer = (DLVAnswerSets) out;

		if(answer.getAnswersets().size() > 0) 
		{
			cells.clear();
			System.out.println("Answer set find");
			AnswerSet firstAs = answer.getAnswersets().get(0);
			
				try {
					for (Object obj : firstAs.getAtoms()) 
					{
						if((obj instanceof Cell)) 
						{
							Cell cell = (Cell) obj;
							cells.add(cell);
						}
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				}
				return true;
		} else {
			System.out.println("No answer set");
			cells.clear();
		}
		return false;
	}

	//inizializzazione casuale di una sottomatrice per avere delle celle da passare come fatti al risolutore
	public ArrayList<Cell> factsGenerator() 
	{
		ArrayList<Cell> cells = new ArrayList<Cell>();
		ArrayList<Integer> values = new ArrayList<Integer>();
		for(int i=1; i<10; i++)
		{
			values.add(i);
		}
		
		int[] coordinate = {0,3,6};
		int x_index = coordinate[new Random().nextInt(coordinate.length)];
		int y_index = coordinate[new Random().nextInt(coordinate.length)];
		for(int i=x_index; i<(x_index+3); i++) 
		{
			for(int j=y_index; j<(y_index+3); j++)
			{
				int index = new Random().nextInt(values.size());
				cells.add(new Cell(i,j,values.get(index)));
				values.remove(index);
			}
		}
		return cells;
	}

	public ArrayList<Cell> getGrid() { return cells; }

	public boolean solveSudoku(ArrayList<Cell> grid) 
	{
		return generateSudoku(grid);
	}

}
