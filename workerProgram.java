//
//public class ThreadAnimation implements Runnable
//{
//	private Simulator simulator;
//	private int numSteps;
//	private Field field;
//	private SimulatorView view;
//	
//	public ThreadAnimation(Simulator s, int numSteps)
//	{
//		this.simulator	=	s;
//		this.view		=	simulator.getSimulatorView();
//		this.field		=	simulator.getField();
//		this.numSteps	=	numSteps;
//	}
//	
//	public void run()
//	{
//        for(int step = 1; step <= numSteps && view.isViable(field); step++)
//            simulator.simulateOneStep();
//	}
//}
