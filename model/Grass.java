package model;

import java.util.List;
import java.util.Random;

import view.Field;
//import java.util.Random;

import logic.Location;
import logic.Randomizer;

public class Grass implements Actor {
  // The field for grass
  private Field field;
  // The grass position in the field.
  private Location location;
  // Determine if the grass is alive
  private boolean alive;
  //Grass age.
  private int age;
  //The age at which grass can start to breed.
  private static int BREEDING_AGE = 3;
  // The age to which grass can live.
  private static int MAX_AGE = 1000;
  // The likelihood of grass breeding.
  private static double BREEDING_PROBABILITY = 0.5;
  // The maximum number of births.
  private static int MAX_LITTER_SIZE = 10;
  //A shared random number generator to control breeding.
  private static final Random rand = Randomizer.getRandom();


	public Grass(boolean randomAge, Field field, Location location) {
		this.field = field;
		this.location = location;
		setAge(0);
        if(randomAge) {
        	setAge(getRandom().nextInt(MAX_AGE));
        }
	}
	
	public Field getField() {
		return field;
	}
	
	public void setAge(int age) 
	{
		this.age = age;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public boolean isAlive()
    {
    	return alive;
    }
	
	public int getMaxAge()
    {
    	return MAX_AGE;
    }
	
	public void incrementAge()
    {
        setAge(getAge() + 1);
        if(getAge() > getMaxAge()) {
            setDead();
        }
    }
	
	protected int getAge() 
	{
		return age;
	}
	
	public Random getRandom()
	{
		return rand;
	}
		
	public void act(List<Actor> newGrass)
    {
		incrementAge();
        if(isAlive()) {
            giveBirth(newGrass);
        }
        else {
            // Overcrowding.
            setDead();
        }
    }
        
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }
        
    protected int breed()
    {
        int births = 0;
        if(canBreed() && getRandom().nextDouble() <= getBreedingProbability()) {
            births = getRandom().nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }
        
    private void giveBirth(List<Actor> newGrass)
    {
        // New grass is born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Grass young = new Grass(false, field, loc);
            newGrass.add(young);
        }
    }
    
    public void setLocation(Location newLocation)
    {
        newLocation = location;
    }
    
    public boolean canBreed()
    {
        return getAge() >= getBreedingAge();
    }
    
    public int getBreedingAge()
    {
    	return BREEDING_AGE;
    }
    
    public int getMaxLitterSize()
    {
    	return MAX_LITTER_SIZE;
    }
    
    public double getBreedingProbability()
    {
    	return BREEDING_PROBABILITY;
    }
    
}
