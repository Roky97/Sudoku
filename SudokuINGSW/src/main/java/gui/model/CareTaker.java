package gui.model;

import java.util.ArrayList;

public class CareTaker {

	private ArrayList<SudokuGrid> listMemento = new ArrayList<>();
	
	public void add(SudokuGrid memento) 
	{
		System.out.println("adding grid to the caretaker");
		listMemento.add(memento);
		
		for(SudokuGrid g : listMemento) {
			System.out.print("#" + listMemento.indexOf(g) + " ");
			System.out.println(g.getCells().toString());
		}
	}
	
	public SudokuGrid get(int index) {
		System.out.println("restituisco");
		System.out.println(listMemento.get(index).getCells().toString());
		return listMemento.get(index);
	}
	
}
