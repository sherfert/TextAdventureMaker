package data.interfaces;

import data.InventoryItem;
import data.Item;
import data.Person;

/**
 * Anything that is actively or passively usable WITH SOMETHING ELSE. This does
 * not explicitly include usable by itself.
 * 
 * Namely: {@link Item}, {@link InventoryItem}, {@link Person}.
 * 
 * @author Satia
 */
public interface UsableOrPassivelyUsable extends Identifiable {
	// Does not require anthing.
}