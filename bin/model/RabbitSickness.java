package model;


/**
 * sickness that rabbits can spread to other rabbits
 * @author Ieme, Jermo, Yisong
 * @version 2012.02.06
 */
public interface RabbitSickness 
{

	/**
	 * @return true if infected
	 * @return false if not infected
	 */
	boolean isInfected();
}
