package data.interfaces;

import data.Item;
import data.Person;

/**
 * Anything that is passively usable WITH SOMETHING ELSE. This does
 * not explicitly include usable by itself.
 * 
 * TODO include way
 * Namely: {@link Item}, {@link Person}.
 * 
 * @author Satia
 */
public interface PassivelyUsable extends Identifiable {
	// Does not require anything.
}