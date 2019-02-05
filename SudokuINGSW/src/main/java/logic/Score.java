package logic;

import gui.model.DIFFICULTY;

public class Score implements Comparable<Score>{
	private String user;
	private TimeScored time;
	private DIFFICULTY difficulty;
	
	public Score() {
		user = "";
		time = null;
		difficulty = null;
	}
	
	public Score(String u, TimeScored t, DIFFICULTY d) {
		user = u;
		time = t;
		difficulty = d;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public TimeScored getTime() {
		return time;
	}

	public void setTime(TimeScored time) {
		this.time = time;
	}

	public DIFFICULTY getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(DIFFICULTY difficulty) {
		this.difficulty = difficulty;
	}
	
	@Override
	public String toString() {
		if(difficulty == DIFFICULTY.EASY)
			return user + " \t Easy \t" + time + "\n";
		
		if(difficulty == DIFFICULTY.NORMAL)
			return user + " \t Normal \t" + time + "\n";
		
		if(difficulty == DIFFICULTY.HARD)
			return user +  "\t Hard \t" + time + "\n";
		
		
		return "";
	}

	@Override
	public int compareTo(Score o) {
		
		int compareHours = ((Score) o).getTime().getHours(); 
		int compareMinutes = ((Score) o).getTime().getMinutes(); 
		int compareSeconds = ((Score) o).getTime().getSeconds(); 
		
		if (time.getHours() - compareHours == 0)
		{
			if (time.getMinutes() - compareMinutes == 0)
			{
				return time.getSeconds() - compareSeconds;
			}
			else
			{
				return time.getMinutes() - compareMinutes;
			}
		}
		else
		{
			return time.getHours() - compareHours;
		}
		
		
	}
	
	
	
	
	

}
