package tmp;

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
	private static Handler handler;
	
	public SudokuGenerator(int r, int c) {}

	public ArrayList<Cell> generateSudoku() 
	{
		handler = new DesktopHandler(new DLVDesktopService("lib/dlv.mingw.exe"));
		InputProgram encoding = new ASPInputProgram();
		encoding.addFilesPath(encodingResource);
		handler.addProgram(encoding);
		
		//facts
		InputProgram facts = new ASPInputProgram();

		
		ArrayList<Cell> cells = new ArrayList<Cell>();
		cells = factsGenerator();
		try {
			for(int i = 0; i < cells.size(); i++)
			{
			System.out.println("cell("+cells.get(i).getRow()+","+cells.get(i).getColumn()+","+cells.get(i).getValue()+")");
				facts.addObjectInput(cells.get(i));
			}
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
//			cells.clear();
			System.out.println("Answer set find");
			AnswerSet firstAs = answer.getAnswersets().get(0);
			
				try {
					for (Object obj : firstAs.getAtoms()) 
					{
						if((obj instanceof Cell)) 
						{
							Cell cell = (Cell) obj;
							if(!(cells.contains(cell)))
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
		} else {
			System.out.println("No answer set");
		}
		return cells;
	}

	private ArrayList<Cell> factsGenerator() {
		
		ArrayList<Cell> cells = new ArrayList<Cell>();
		
		for(int v = 1; v < 10; v++) 
		{
			Cell c = new Cell(new Random().nextInt(9), new Random().nextInt(9), v);
			for(int i = 0; i < cells.size(); i++) 
			{
				if(cells.get(i).getRow() == c.getRow() && cells.get(i).getColumn() == c.getColumn()) 
				{
					c = new Cell(new Random().nextInt(9), new Random().nextInt(9), v);
					i = 0;
				}
			}
			cells.add(c);
		}
		
		return cells;
	}

}
