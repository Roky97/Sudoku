package logic;

import java.util.ArrayList;

public class ScoreCollection {
	private ArrayList<Score> scores;
	
	
	public ScoreCollection() {
		scores = new ArrayList<Score>();
		
	}
	
	public void addScore(Score s) {
		sort();
		if(scores.size() == 10)
		{
			Score temp = scores.get(scores.size() -1);
			if(s.getTime().isMinus(temp.getTime()))
				pop();
		}
		
		scores.add(s);
	}
	
	public void pop() {
		scores.remove(scores.size() -1);
	}
	
	public void sort() {
		scores.sort(null);
	}
	
	public boolean isEmpty() {
		return scores.isEmpty();
	}
	
	@Override
	public String toString() {
		String text = "";
		for (Score score : scores) {
			text += score.toString();	
		}
		
		return text;
	}
	
	public boolean isHighScore(Score s) {
		sort();
		Score temp = scores.get(0);
		if(temp.getTime().isMinus(s.getTime()))
		{
			return false;
		}
		return true;
		
		
	}
	
	
	
//	public static void main(String ... args) {
//		
//		ScoreCollection scores = new ScoreCollection();
//		
//		TimeScored time1 = new  TimeScored(2, 2, 2);
//		TimeScored time2 = new  TimeScored(1, 1, 1);
//		TimeScored time3 = new  TimeScored(3, 3, 3);
//
//		
//		scores.addScore(new Score("ciao",new TimeScored(3,3,3),DIFFICULTY.EASY));
//		scores.addScore(new Score("bu",new TimeScored(2,2,1),DIFFICULTY.NORMAL));
//		scores.addScore(new Score("gatta",new TimeScored(0,0,3),DIFFICULTY.HARD));
//		
//		System.out.println(scores);
//		scores.sort();
//		System.out.println(scores);
//	}
	

}

