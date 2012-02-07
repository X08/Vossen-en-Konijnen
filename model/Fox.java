package model;

import java.util.HashMap;
import java.util.List;
import java.util.Iterator;

import logic.Counter;
import logic.Field;
import logic.Location;
import main.MainProgram;


/**
 * A simple model of a fox.
 * Foxes age, move, eat rabbits, and die.
 * 
 * @author Ieme, Jermo, Yisong
 * @version 2012.01.29
 */
public class Fox extends Animal
{
    // Characteristics shared by all foxes (class variables).
    
    // The age at which a fox can start to breed.
    private static int breeding_age = 3;
    // The age to which a fox can live.
    private static int max_age = 150;
    // The likelihood of a fox breeding.
    private static double breeding_probability = 0.075;
    // The maximum number of births.
    private static int max_litter_size = 8;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a fox can go before it has to eat again.
    
    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Fox(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(getRandom().nextInt(max_age));
            setFoodLevel(getRandom().nextInt(RABBIT_FOOD_VALUE));
        }
        else {
            setAge(0);
            setFoodLevel(RABBIT_FOOD_VALUE);
        }
    }
    
    /**
     * This is what the fox does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born foxes.
     */
    public void act(List<Actor> newFoxes)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newFoxes);            
            // Move towards a source of food if found.
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
    
    /**
     * Zorgt er voor dat er geen nakomeling worden geboren als er te weinig voesel zijn.
     * @return true als genoeg voedsel zijn
     * @return false als niet genoeg voedsel zijn
     */
    @SuppressWarnings("rawtypes")
	public boolean survivalInstinct()
    {
    	int foxCount = 0;
    	int rabbitCount = 0;
    	HashMap<Class, Counter> classStats = MainProgram.getSimulator().getSimulatorView().getStats().getPopulation();
    	
    	for (Class c : classStats.keySet()) {
    		Counter info = classStats.get(c);
    		
    		if (info.getName().equals("model.Fox")) {
    			foxCount = info.getCount();
    		}
    		if (info.getName().equals("model.Rabbit")) {
    			rabbitCount = info.getCount();
    		}
    	}
    	if (1.5 *(foxCount + (foxCount * getBreedingProbability() * getMaxLitterSize())) >= rabbitCount) {
    			//foxCount >= rabbitCount * getBreedingProbability() * getMaxLitterSize() ) {
    		return false;
    	}	
    	return true;
    }
    
    /**
     * returns the maximum age of a fox can live
     * @return int maximum age of a fox can live
     */
    protected int getMaxAge()
    {
    	return max_age;
    }
    
    /**
     * Make this fox more hungry. This could result in the fox's death.
     */
    private void incrementHunger()
    {
        setFoodLevel(getFoodLevel()  - 1);
        if(getFoodLevel() <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) { 
                    rabbit.setDead();
                    setFoodLevel(RABBIT_FOOD_VALUE);
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFoxes A list to return newly born foxes.
     */
    private void giveBirth(List<Actor> newFoxes)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Fox young = new Fox(false, field, loc);
            newFoxes.add(young);
        }
    }

    /**
     * A fox can breed if it has reached the breeding age.
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
    		Fox.breeding_age = breeding_age;
    }
    
    
    /**
     * setter voor max_age
     * @param max_age
     */
    public static void setMaxAge(int max_age)
    {
    	if (max_age >= 1)
    		Fox.max_age = max_age;
    }
    
    /**
     * setter voor breeding_probability
     * @param breeding_probability
     */
    public static void setBreedingProbability(double breeding_probability)
    {
    	if (breeding_probability >= 0)
    		Fox.breeding_probability = breeding_probability;
    }
    
    /**
     * setter voor max_litter_size
     * @param max_litter_size
     */
    public static void setMaxLitterSize(int max_litter_size)
    {
    	if (max_litter_size >= 1)
    		Fox.max_litter_size = max_litter_size;
    }  
    
    /**
     * default settings
     */
    public static void setDefault()
    {
    	breeding_age = 3;
    	max_age = 150;
    	breeding_probability = 0.075;
    	max_litter_size = 8;
    } 
    
    /**
     * Getter om breeding_age op te halen
     * @return breeding_age breeding leeftijd
     */
    protected int getBreedingAge()
    {
    	return breeding_age;
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
}
