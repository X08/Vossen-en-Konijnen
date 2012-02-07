package model;

import java.util.List;
import java.util.Iterator;


//import java.util.Random;

import logic.Field;
import logic.Location;


/**
 * 
 * A class representing shared characteristics of hunters.
 * A simple model of a hunter
 * Hunters move and shoot.
 * 
 * @author Ieme, Jermo, Yisong
 * @version 2012.01.29
 */
public class Hunter implements Actor
{

//    // Whether the hunter is alive or not.
//    private boolean alive = true;
    // The hunter's field.
    private Field field;
    // The hunter's position in the field.
    private Location location;
    // Determine if the hunter is alive
    private boolean alive;
    
    // Characteristics shared by all hunters (class variables).

    // A shared random number generator to control breeding.
//    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a hunter. 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hunter(Field field, Location location)
    {
        this.field = field;
        this.location = location;
    }

    /**
     * Check whether the hunter is alive or not.
     * @return true if the hunter is still alive.
     */
    public boolean isAlive()
    {
    	return alive;
    }
    
    /**
     * This is what the bear does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newbears A list to return newly born bears.
     */
    
    public void act(List<Actor> newHunters)
    {
    	// Move towards a source of food if found.
        Location newLocation = findAnimal();
        if(newLocation == null) { 
            // No food found - try to move to a free location.
            newLocation = getField().freeAdjacentLocation(getLocation());
        }
        // See if it was possible to move.
        if(newLocation != null) {
            setLocation(newLocation);
        }
        else{
            	// Overcrowding.
            	setDead();
        	}
    }


    /**
     * Look for an animal adjacent to the current location.
     * Only the first live animal is shoot.
     * @return Where an animal is found, or null if it wasn't.
     */
    private Location findAnimal()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rabbit) 
            {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) 
                { 
                    rabbit.setDead();
                    return where;
                }
            }
            else if (animal instanceof Fox)
            {
                Fox fox = (Fox) animal;
                if(fox.isAlive()) 
                { 
                    fox.setDead();
                    return where;
                }
            	
            }
            else if (animal instanceof Bear)
            {
                Bear bear = (Bear) animal;
                if(bear.isAlive()) 
                { 
                    bear.setDead();
                    return where;
                }
            }
            else if (animal instanceof Wolf)
            {
            	Wolf wolf = (Wolf) animal;
                if(wolf.isAlive()) 
                { 
                    wolf.setDead();
                    return where;
                }
        	}
        }
        return null;
    }
    
    /**
     * Indicate that the hunter is no longer alive.
     * It is removed from the field.
     */
    public void setDead()
    {
//        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }
    
    /**
     * Return the hunter's location.
     * @return The hunter's location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the hunter at the new location in the given field.
     * @param newLocation The hunter's new location.
     */
    public void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Return the hunter's field.
     * @return Field the hunter's field.
     */
    public Field getField()
    {
        return field;
    }   
}