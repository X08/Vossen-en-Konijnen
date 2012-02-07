package runner;

import main.MainProgram;
import main.Simulator;
/**
 * Allow the animation to be shown.
 * 
 * @author Ieme, Jermo, Yisong
 * @version 2012.01.29
 */
public class ThreadRunner implements Runnable
{	
	
	private int numSteps = 0;
	private boolean infinite = false;
	private boolean threadRun;
	
	
	/**
	 * leeg constructor
	 */
	public ThreadRunner() 
	{
	}
	
    /**
     * Run the simulation from its current state for a reasonably long period
     */
    public void startRun(int numSteps)
    {
    	if (numSteps == 0)
    	{
    		this.numSteps = 1;
    		infinite = true;
    	}
    	else
    	{
//    		this.numSteps = numSteps;	//	voor niet stack gevallen
    		this.numSteps += numSteps;	//	voor als het wel moet stacken
    	}
    	
//    	try{
    		if (!threadRun && Thread.currentThread().isAlive())
    		{
    			new Thread(this).start();
    		}
//    	} 
//    	catch (IllegalThreadStateException e)
//    	{
//    		infinite = false;
//        	System.out.println("InterruptedException");
//    	}
	}

    /**
     * Pauze de animatie
     */
	public void stop() 
	{
		numSteps = 0;
		threadRun = false;
		infinite = false;
	}
	
	/**
	 * Deze methode wordt alleen uitgevoerd als je de methode .start() gebruikt van de klasse Thread.
	 * Zonder de klasse (thread), wordt deze methode niet juist uitgevoerd.
	 * De methode zorgt ervoor dat de thread door het aantal numSteps heen loopt.
	 */
	@Override
	public void run() 
	{
		threadRun = true;
		Simulator simulator = MainProgram.getSimulator();
		
		while(threadRun && numSteps > 0 && simulator.getSimulatorView().isViable(simulator.getField()))
		{
			MainProgram.getSimulator().simulateOneStep();
			numSteps--;
			while(infinite && numSteps == 0)
			{
				numSteps++;
			}
			
			try {
				Thread.sleep(simulator.getAnimationSpeed());
			} 
			catch (Exception e) 
			{
            	System.out.println("InterruptedException");
			}
		}
		threadRun = false;
	}
}
