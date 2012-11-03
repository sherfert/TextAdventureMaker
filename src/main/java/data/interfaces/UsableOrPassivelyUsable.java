package data.interfaces;

import data.InventoryItem;
import data.Item;
import data.Person;

/**
 * Anything that is actively or passivle usable with something else. Namely:
 * {@link Item}, {@link InventoryItem}, {@link Person}.
 * 
 * @author Satia
 */
public interface UsableOrPassivelyUsable extends Identifiable {
	// Does not require anthing.
}