package gui.model;

import java.util.ArrayList;

public class CareTaker {

	private ArrayList<SudokuGrid> listMemento = new ArrayList<>();
	
	public void add(SudokuGrid memento) {
		System.out.println("adding grid to the caretaker");
		listMemento.add(memento);
	}
	
	public SudokuGrid get(int index) {
		return listMemento.get(index);
	}
	
}
