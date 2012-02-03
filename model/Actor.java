package model;
import java.util.List;

import logic.Location;

/**
 * 
 * A class representing shared characteristics of living creature.
 * 
 * @author Ieme, Jermo, Yisong
 * @version 2012.01.30 
 */
public interface Actor 
{
	
	
//	public abstract void act(Field currentField, Field updateField, List newAnimals);
	
    /**
     * Make this actor act - that is: make it do
     * whatever it wants/needs to do.
     * @param newActors A list to receive newly born animals.
     */
	void act(List<Actor> newActors);
    
	
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
	boolean isAlive();
//	
//    /**
//     * Indicate that the animal is no longer alive.
//     * It is removed from the field.
//     */
//	void setDead();
//    
//    /**
//     * Return the actor's location.
//     * @return The actor's location.
//     */
//	Location getLocation();
//    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
	void setLocation(Location newLocation);
    
//    /**
//     * Return the animal's field.
//     * @return The animal's field.
//     */
//	Field getField();	
}
