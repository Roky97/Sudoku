package logic;

import gui.model.TimerSudoku;

public class TimeScored{
	
	private int hours;
	private int minutes; 
	private int seconds;
	
	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	
	public TimeScored(int h, int m, int s) {
		hours = h;
		minutes = m;
		seconds = s;
		
	}
	
	public TimeScored(TimerSudoku t)
	{
		hours = t.getHours();
		minutes = t.getMinutes();
		seconds = t.getSeconds();
		
				
	}
	
	
	
	@Override
	public String toString() {
		String text;
		String stringH = "";
		String stringM = "";
		String stringS = "";
		
		if(hours < 10)
			stringH = "0" + String.valueOf(hours);
		else 
			stringH = String.valueOf(hours);
		
		if(minutes < 10)
			stringM = "0" + String.valueOf(minutes);
		else
			stringM = String.valueOf(minutes);
		
		if(seconds < 10)
			stringS = "0" + String.valueOf(seconds);
		else
			stringS = String.valueOf(seconds);
		
		text = "[" + stringH+ ":" + stringM+":"+stringS+"]";
		
		
		return text;
		
	}

	public boolean isMinus(TimeScored time) {
		
		if (getHours() == time.getHours())
		{
			if (getMinutes() == time.getMinutes())
			{
				return getSeconds() < time.getSeconds();
			}
			else
			{
				return getMinutes() < time.getMinutes();
			}
		}
		else
		{
			return getHours() < time.getHours();
		}
	}
	

}
