package logic;

import java.util.ArrayList;

@SuppressWarnings("serial")
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
		sort();
		String text = "User \t Diffilcuty \t Time\n";
		for (Score score : scores) {
			text += score.toString();	
		}
		return text;
	}
	
	public boolean isHighScore(Score s) {
		sort();
		if(scores.isEmpty())
			return true;
		Score temp = scores.get(0);
		if(temp.getTime().isMinus(s.getTime()))
		{
			return false;
		}
		return true;
	}

	public ArrayList<Score> getScores() {return scores;}
}

