package data.interfaces;

import data.Location;

/**
 * Anything with one (!) location in the game. This is not supposed to be used
 * for the player, although it has a location, since it requires that the item
 * can be somehow interacted with, which is not true for the player.
 * 
 * @author Satia
 */
public interface HasLocation extends UsableOrPassivelyUsable {

	/**
	 * @return the location
	 */
	public Location getLocation();

	/**
	 * Sets a new location for this thing. If the location manages a list of
	 * these things, it should take care of removing it from the old location's
	 * list and add it to the new one.
	 * 
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Location location);
}