package src.model;

import java.util.Iterator;
import java.util.List;



import src.logic.Field;
import src.logic.Location;

/**
 * A simple model of a rabbit.
 * Rabbits age, move, breed, and die.
 * 
 * @author Ieme, Jermo, Yisong
 * @version 2012.01.29
 */
public class Rabbit extends Animal
{
    // Characteristics shared by all rabbits (class variables).

    // The age at which a rabbit can start to breed.
    private static int breeding_age = 1;
    // The age to which a rabbit can live.
    private static int max_age = 100;
    // The likelihood of a rabbit breeding.
    private static double breeding_probability = 0.04;
    // The maximum number of births.
    private static int max_litter_size = 12;
    // kans op infectie
    private double rabbit_infection_chance = 0.9;

    
    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    
    public Rabbit(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(getRandom().nextInt(max_age));
            setFoodLevel(getRandom().nextInt(GRASS_FOOD_VALUE));
        }
        else {
            setAge(0);
            setFoodLevel(GRASS_FOOD_VALUE);
        }
    }
    
    /**
     * This is what the rabbit does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newRabbits A list to return newly born rabbits.
     */
    public void act(List<Actor> newRabbits)
    {
        incrementAge();
        if(isAlive()) {
            isThereVirus();
        	giveBirth(newRabbits);            
        	Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }
    
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if(plant instanceof Grass) {
                Grass grass = (Grass) plant;
                if(grass.isAlive()) { 
                    grass.setDead();
                    setFoodLevel(GRASS_FOOD_VALUE);
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Zorgt er voor dat er geen nakomeling worden geboren als er te weinig voesel zijn.
     * @return true als genoeg voedsel zijn
     * @return false als niet genoeg voedsel zijn
     */
    public boolean survivalInstinct()
    {
//    	MainProgram.getSimulator().getSimulatorView().getStats().getPopulation();
    	return true;
    }
    
	/**
	 * @return true if infected
	 * @return false if not infected
	 */
	public boolean isInfected()
	{
		int random = (rand.nextInt(100) + 1)/100;
		if (random <= rabbit_infection_chance)
		{
			setInfected(true);
			return true;
		}
		return false;
	}
	
    /**
     * check if rabbit is surround by rabbitvirus.
     * @return true if it is
     */
    private boolean isThereVirus()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.getInfected()) {
                	isInfected();
                }
            }
        }
        return false;
    }

    /**
     * Check whether or not this rabbit is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newRabbits A list to return newly born rabbits.
     */
    private void giveBirth(List<Actor> newRabbits)
    {
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            //boolean inf = isInfected();
            Rabbit young = new Rabbit(false, field, loc);
            newRabbits.add(young);
        }
    }

    /**
     * A rabbit can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return getAge() >= getBreedingAge();
    }

    /**
     * setter voor breeding_age
     * @param breeding_age
     */
    public static void setBreedingAge(int breeding_age)
    {
    	if (breeding_age >= 0)
    		Rabbit.breeding_age = breeding_age;
    }
    
    
    /**
     * setter voor max_age
     * @param max_age
     */
    public static void setMaxAge(int max_age)
    {
    	if (max_age >= 1)
    		Rabbit.max_age = max_age;
    }
    
    /**
     * setter voor breeding_probability
     * @param breeding_probability
     */
    public static void setBreedingProbability(double breeding_probability)
    {
    	if (breeding_probability >= 0)
    		Rabbit.breeding_probability = breeding_probability;
    }
    
    /**
     * setter voor max_litter_size
     * @param max_litter_size
     */
    public static void setMaxLitterSize(int max_litter_size)
    {
    	if (max_litter_size >= 1)
    		Rabbit.max_litter_size = max_litter_size;
    }  
    
    /**
     * default settings
     */
    public static void setDefault()
    {
    	breeding_age = 1;
    	max_age = 100;
    	breeding_probability = 0.1;
    	max_litter_size = 12;
    }
    
    /**
     * Getter om breeding_age op te halen
     */
    protected int getBreedingAge()
    {
    	return breeding_age;
    }
    
    /**
     * returns the maximum age of a rabbit can live
     * @return int maximum age of a rabbit can live
     */
    protected int getMaxAge()
    {
    	return max_age;
    }
    
    /**
     * Getter om max_litter_size op te halen
     * @return max_litter_size maximum litter
     */
    protected int getMaxLitterSize()
    {
    	return max_litter_size;
    }
    
    /**
     * Getter om breeding_probability op te halen
     * @return breeding_probability breeding kansen
     */
    protected double getBreedingProbability()
    {
    	return breeding_probability;
    }
    
    /**
     * getter om rabbit_infection op te halen
     * @return rabbit_infection
     */
    protected double getRabbitInfectionChance()
    {
    	return rabbit_infection_chance;
    }
}
