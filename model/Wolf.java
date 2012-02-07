package model;

import java.util.HashMap;
import java.util.List;
import java.util.Iterator;



import logic.Counter;
import logic.Field;
import logic.Location;
import main.MainProgram;


/**
 * A simple model of a wolf.
 * Wolfs age, move, eat Foxes and rabbits, and die.
 * 
 * @author Ieme, Jermo, Yisong
 * @version 2012.01.29
 */
public class Wolf extends Animal
{
    // Characteristics shared by all wolfs (class variables).
    
    // The age at which a wolf can start to breed.
    private static int breeding_age = 5;
    // The age to which a wolf can live.
    private static int max_age = 200;
    // The likelihood of a wolf breeding.
    private static double breeding_probability = 0.05;
    // The maximum number of births.
    private static int max_litter_size = 3;

    
    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Wolf(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(getRandom().nextInt(max_age));
            setFoodLevel(getRandom().nextInt(RABBIT_FOOD_VALUE + FOX_FOOD_VALUE));
        }
        else {
            setAge(0);
            setFoodLevel(RABBIT_FOOD_VALUE + FOX_FOOD_VALUE);
        }
    }
    
    /**
     * This is what the wolf does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newWolfs A list to return newly born wolfs.
     */
    public void act(List<Actor> newWolfs)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newWolfs);            
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
    	int wolfCount = 0;
    	HashMap<Class, Counter> classStats = MainProgram.getSimulator().getSimulatorView().getStats().getPopulation();
    	for (Class c : classStats.keySet()) {    		
    		Counter info = classStats.get(c);
		
		if (info.getName().equals("model.Wolf")) {
			wolfCount = info.getCount();
		}
		if (info.getName().equals("model.Fox")) {
			foxCount = info.getCount();
		}
		if (info.getName().equals("model.Rabbit")) {
			rabbitCount = info.getCount();
		}
    		
    	}
    	if (1.5 *(wolfCount + (wolfCount * getBreedingProbability() * getMaxLitterSize())) >= rabbitCount + foxCount) {
    		//	wolfCount >= rabbitCount * getBreedingProbability() * getMaxLitterSize() + foxCount * getBreedingProbability() * getMaxLitterSize()) {
    		return false;
    	}	
    	return true;
    }
    
    /**
     * returns the maximum age of a wolf can live
     * @return int maximum age of a wolf can live
     */
    protected int getMaxAge()
    {
    	return max_age;
    }
    
    /**
     * Make this wolf more hungry. This could result in the wolf's death.
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
            else if (animal instanceof Fox)
            {
                Fox fox = (Fox) animal;
                if(fox.isAlive()) 
                { 
                    fox.setDead();
                    setFoodLevel(FOX_FOOD_VALUE);
                    return where;
                }
            	
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this wolf is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newWolfs A list to return newly born wolfs.
     */
    private void giveBirth(List<Actor> newWolfs)
    {
        // New wolfs are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Wolf young = new Wolf(false, field, loc);
            newWolfs.add(young);
        }
    }

    /**
     * A wolf can breed if it has reached the breeding age.
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
    		Wolf.breeding_age = breeding_age;
    }
    
    
    /**
     * setter voor max_age
     * @param max_age
     */
    public static void setMaxAge(int max_age)
    {
    	if (max_age >= 1)
    		Wolf.max_age = max_age;
    }
    
    /**
     * setter voor breeding_probability
     * @param breeding_probability
     */
    public static void setBreedingProbability(double breeding_probability)
    {
    	if (breeding_probability >= 0)
    		Wolf.breeding_probability = breeding_probability;
    }
    
    /**
     * setter voor max_litter_size
     * @param max_litter_size
     */
    public static void setMaxLitterSize(int max_litter_size)
    {
    	if (max_litter_size >= 1)
    		Wolf.max_litter_size = max_litter_size;
    }  
    
    /**
     * default settings
     */
    public static void setDefault()
    {
    	breeding_age = 5;
    	max_age = 200;
    	breeding_probability = 0.05;
    	max_litter_size = 3;
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
