package data.interfaces;

import data.Location;

/**
 * Anything with one (!) location in the game.
 * 
 * @author Satia
 */
public interface HasLocation extends UsableOrPassivelyUsable {

	/**
	 * @return the location
	 */
	public Location getLocation();

	/**
	 * Sets a new location for this thing. The thing is removed from the old
	 * location's list and added to the new one.
	 * 
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Location location);
}