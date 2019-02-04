package gui.model;

import org.apache.commons.lang3.time.StopWatch;


public class TimerSudoku {
	
	private StopWatch stopwatch;
	private int hours;
	private int seconds;
	private int minutes;
	private int savedHours;
	private int savedMinutes;
	private int savedSeconds;
	private int tempSeconds;
	private int penality;
	
	public TimerSudoku() {
		tempSeconds = 0;
		hours = 00;
		seconds = 00;
		minutes = 00;
//		savedHours=00;
//		savedMinutes=00;
//		savedSeconds=00;
		stopwatch = new StopWatch();
	}
	
	public void start()
	{
		stopwatch.start();
	}
	
	public void resume()
	{
		stopwatch.resume();
	}
	
	public void stop()
	{
		stopwatch.suspend();
	}
	
	public void addPenality() {
		penality+=7;
	}
	
	
	public int getTotalHours() {
		int  totHours = 0;
		if(getTotalMinutes() > 59)
			totHours = getTotalMinutes()/60;
		

		return totHours+savedHours;
	}

	public int getTotalSeconds() {
		int totSeconds = 0 ;
		if (!stopwatch.isStopped()) //Da errore qua
		{
			
			totSeconds = (int) (stopwatch.getTime()/1000);
			tempSeconds = totSeconds+penality+savedSeconds;
			return tempSeconds;
		}
		
		tempSeconds = totSeconds+penality+savedSeconds;
		return tempSeconds;
	}
	
	

	public int getTotalMinutes() {
		int totMinutes = 0;
		if(getTotalSeconds() > 59)
			totMinutes = getTotalSeconds()/60;
		return totMinutes+savedMinutes;
	}

	public void aggiornaTimer()
	{
		
		if(getTotalSeconds() > 59)
			seconds = getTotalSeconds() % 60;
		else
			seconds = getTotalSeconds();
		
		if(getTotalMinutes() > 59)
			minutes = getTotalMinutes() % 60;
		else
			minutes = getTotalMinutes();
		
		hours = getTotalHours();
	}
	
	public void restart() {
		
		tempSeconds = 0;
		hours = 00;
		seconds = 00;
		minutes = 00;
	}
	
	public int getHours() {
		return hours;
	}

	public int getSeconds() {
		return seconds;
	}

	public int getMinutes() {
		return minutes;
	}
	
	public void penality() {
		this.seconds=this.seconds+7;
	}
	
	public void set(String timer){
		String[] array = timer.split("\\:",-1);
		savedHours=Integer.parseInt(array[0]);	
		savedMinutes=Integer.parseInt(array[1]);	
		savedSeconds=Integer.parseInt(array[2]);	
	}

}
