package src.model;

import java.util.HashMap;
import java.util.List;
import java.util.Iterator;



import src.logic.Counter;
import src.logic.Field;
import src.logic.Location;
import src.main.MainProgram;

/**
 * A simple model of a Bear.
 * Bear age, move, eat fox and wolf, and die.
 * 
 * @author Ieme, Jermo, Yisong
 * @version 2012.01.29
 */
public class Bear extends Animal
{
    // Characteristics shared by all bears (class variables).
    
    // The age at which a bear can start to breed.
    private static int breeding_age = 12;
    // The age to which a bear can live.
    private static int max_age = 300;
    // The likelihood of a bear breeding.
    private static double breeding_probability = 0.035;
    // The maximum number of births.
    private static int max_litter_size = 2;
    // The food value of a single wolf and a single fox. In effect, this is the
    // number of steps a bear can go before it has to eat again.
    

    /**
     * Create a bear. A bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Bear(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(getRandom().nextInt(max_age));
            setFoodLevel(getRandom().nextInt(FOX_FOOD_VALUE + WOLFS_FOOD_VALUE));
        }
        else{
	            setAge(0);
	            setFoodLevel(FOX_FOOD_VALUE + WOLFS_FOOD_VALUE);      
        	}
    }
    
    /**
     * This is what the bear does most of the time: it hunts for
     * foxes and wolfs. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newbears A list to return newly born bears.
     */
    public void act(List<Actor> newBears)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newBears);            
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
    	int wolfCount = 0;
    	int bearCount = 0;
//    	int rabbitCount= 0;
    	HashMap<Class, Counter> classStats = MainProgram.getSimulator().getSimulatorView().getStats().getPopulation();
    	for (Class c : classStats.keySet()) {
    		Counter info = classStats.get(c);
    		
    		if (info.getName().equals("src.model.Wolf")) {
    			wolfCount = info.getCount();
    		}
    		if (info.getName().equals("src.model.Fox")) {
    			foxCount = info.getCount();
    		}
    		if (info.getName().equals("src.model.Bear")) {
    			bearCount = info.getCount();
    		}
    	}
    	if (1.5 *(bearCount + (bearCount * getBreedingProbability() * getMaxLitterSize())) >= 0.5 * foxCount + wolfCount) {
    		return false;
    	}	
    	return true;
    }
    
	/**
	 * @return true if infected
	 * @return false if not infected
	 */
	public boolean isInfected()
	{
		return false;
	}
	
    /**
     * returns the maximum age of a bear can live
     * @return int maximum age of a bear can live
     */
    protected int getMaxAge()
    {
    	return max_age;
    }
    
    /**
     * Make this bear more hungry. This could result in the bear's death.
     */
    private void incrementHunger()
    {
        setFoodLevel(getFoodLevel() - 1);
        if(getFoodLevel() <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for foxes and wolfs adjacent to the current location.
     * Only the first live foxes or wolf is eaten.
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
            if(animal instanceof Wolf) 
            {
                Wolf wolf = (Wolf) animal;
                if(wolf.isAlive()) 
                { 
                    wolf.setDead();
                    setFoodLevel(WOLFS_FOOD_VALUE);
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
            else if (animal instanceof Rabbit)
            {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) 
                { 
                    rabbit.setDead();
                    setFoodLevel(RABBIT_FOOD_VALUE);
                    return where;
                }         	
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this bear is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newbearrs A list to return newly born bears.
     */
    private void giveBirth(List<Actor> newBears)
    {
        // New bears are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Bear young = new Bear(false, field, loc);
            newBears.add(young);
        }
    }

    /**
     * A bear can breed if it has reached the breeding age.
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
    		Bear.breeding_age = breeding_age;
    }
    
    
    /**
     * setter voor max_age
     * @param max_age
     */
    public static void setMaxAge(int max_age)
    {
    	if (max_age >= 1)
    		Bear.max_age = max_age;
    }
    
    /**
     * setter voor breeding_probability
     * @param breeding_probability
     */
    public static void setBreedingProbability(double breeding_probability)
    {
    	if (breeding_probability >= 0)
    		Bear.breeding_probability = breeding_probability;
    }
    
    /**
     * setter voor max_litter_size
     * @param max_litter_size
     */
    public static void setMaxLitterSize(int max_litter_size)
    {
    	if (max_litter_size >= 1)
    		Bear.max_litter_size = max_litter_size;
    }  
    
    /**
     * default settings
     */
    public static void setDefault()
    {
    	breeding_age = 12;
    	max_age = 300;
    	breeding_probability = 0.35;
    	max_litter_size = 2;
    }
    
    /**
     * Getter om breeding_age op te halen
     */
    protected int getBreedingAge()
    {
    	return breeding_age;
    }
    
    /**
     * Getter om MAX_LITTER_SIZE op te halen
     * @return MAX_LITTER_SIZE maximum litter
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
