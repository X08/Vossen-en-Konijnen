package main;

import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

import view.SimulatorView;

import logic.Field;
import logic.Location;
import logic.Randomizer;

import model.Actor;
import model.Animal;
import model.Bear;
import model.Fox;
import model.Hunter;
import model.Rabbit;
import model.Wolf;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 * 
 * @author Ieme, Jermo, Yisong
 * @version 2012.01.29
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a bear will be created in any given grid position.    
    private static double bear_creation_probability = 0.020;
    // The probability that a hunter will be created in any given grid position.
    private static double wolf_creation_probability = 0.020;
    // The probability that a fox will be created in any given grid position.
    private static double fox_creation_probability = 0.09;
    // The probability that a rabbit will be created in any given grid position.
    private static double rabbit_creation_probability = 0.4;    
    // The probability that a hunter will be created in any given grid position.
    private static double hunter_creation_probability = 0.001;

    
    //	animation speed of the thread
    private static int animationSpeed = 100;
    // List of Actors in the field.
    private List<Actor> actors;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step = 0;
    // A graphical view of the simulation.
    private SimulatorView view;

    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
//        thread	=	new Thread(this);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
//            thread	=	new Thread(this);
        }
        
        actors = new ArrayList<Actor>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Rabbit.class, Color.ORANGE);
        view.setColor(Fox.class, Color.BLUE);
        view.setColor(Bear.class, Color.CYAN);
        view.setColor(Hunter.class, Color.RED);
        view.setColor(Wolf.class, Color.GRAY);
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;

        // Provide space for newborn actors.
        List<Actor> newActors = new ArrayList<Actor>();
        
        // avoid being being interrupted
        try{
	        	if (actors != null)
	        	{
				    // Let all actors act.
				    for(Iterator<Actor> it = actors.iterator(); it.hasNext();) {
				        Actor actor = it.next();
				        actor.act(newActors);
				        if (actor instanceof Animal)		//	check if actor is an animal
				        {
				        	Animal animal = (Animal) actor;
				        	if(! animal.isAlive()) {
				                it.remove();
				            }
				        }
				    }
	        	}
        	}
    		catch (ConcurrentModificationException e)
    		{	
    			simulateOneStep();
        	}
        // Add the newly born foxes and rabbits to the main lists.
        actors.addAll(newActors);

        view.showStatus(step, field);
    }
    
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
		view.getThreadRunner().stop();			
        step = 0;
        actors.clear();
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step, field);
    }
    
    /**
     * setter voor bear_creation_probability
     * @param bear_creation_probability
     */
    public static void setBearCreationProbability(double bear_creation_probability)
    {
    	if (bear_creation_probability >= 0)
    		Simulator.bear_creation_probability = bear_creation_probability;
    }
    
    /**
     * setter voor wolf_creation_probability
     * @param wolf_creation_probability
     */
    public static void setWolfCreationProbability(double wolf_creation_probability)
    {
    	if (wolf_creation_probability >= 0)
    		Simulator.wolf_creation_probability = wolf_creation_probability;
    }
    
    /**
     * setter voor fox_creation_probability
     * @param fox_creation_probability
     */
    public static void setFoxCreationProbability(double fox_creation_probability)
    {
    	if (fox_creation_probability >= 0)
    		Simulator.fox_creation_probability = fox_creation_probability;
    }
    
    /**
     * setter voor rabbit_creation_probability
     * @param rabbit_creation_probability
     */
    public static void setRabbitCreationProbability(double rabbit_creation_probability)
    {
    	if (rabbit_creation_probability >= 0)
    		Simulator.rabbit_creation_probability = rabbit_creation_probability;
    }
    
    /**
     * setter voor hunter_creation_probability
     * @param hunter_creation_probability
     */
    public static void setHunterCreationProbability(double hunter_creation_probability)
    {
    	if (hunter_creation_probability >= 0)
    		Simulator.hunter_creation_probability = hunter_creation_probability;
    }    
    
    /**
     * Getter voor view
     * @return view van het type SimulatorView
     */
    public SimulatorView getSimulatorView()
    {
    	return view;
    }
    
    /**
     * Getter voor field
     * @return field van het type Field
     */
    public Field getField()
    {
    	return field;
    }
    
    /**
     * getter voor step
     * @return steps aantal step
     */
    public int getStep()
    {
    	return step;
    }
    
    /**
     * getter voor animationSpeed()
     * @return animationSpeed of the thread
     */
    public int getAnimationSpeed()
    {
    	return animationSpeed;
    }
    
    /**
     * setter voor animationSpeed
     * @param animationSpeed
     */
    public static void setAnimationSpeed(int animationSpeed)
    {
    	if (animationSpeed >= 0 && animationSpeed <= 1000)
    		Simulator.animationSpeed = animationSpeed;
    }
    
    /**
     * default settings
     */
    public static void setDefault()
    {
    	animationSpeed = 100;
    	bear_creation_probability = 0.020;
        wolf_creation_probability = 0.020;
        fox_creation_probability = 0.09;
        rabbit_creation_probability = 0.4;    
        hunter_creation_probability = 0.001;
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= fox_creation_probability) {
                    Location location = new Location(row, col);
                    Fox fox = new Fox(true, field, location);
                    actors.add(fox);
                }
                else if(rand.nextDouble() <= rabbit_creation_probability) {
                    Location location = new Location(row, col);
                    Rabbit rabbit = new Rabbit(true, field, location);
                    actors.add(rabbit);
                }
                else if(rand.nextDouble() <= bear_creation_probability) {
                    Location location = new Location(row, col);
                    Bear bear = new Bear(true, field, location);
                    actors.add(bear);
                }
                else if (rand.nextDouble() <= hunter_creation_probability) {
                    Location location = new Location(row, col);
                    Hunter hunter = new Hunter(field, location);
                    actors.add(hunter);
                }
                else if(rand.nextDouble() <= wolf_creation_probability) {
                    Location location = new Location(row, col);
                    Wolf wolf = new Wolf(true, field, location);
                    actors.add(wolf);
                }
                // else leave the location empty.
            }
        }
    }
}