package data.interfaces;

import data.Item;
import data.Person;
import data.Way;

/**
 * Anything that is passively usable WITH SOMETHING ELSE. This does
 * not explicitly include usable by itself.
 * 
 * Namely: {@link Item}, {@link Person}, {@link Way}
 * 
 * @author Satia
 */
public interface PassivelyUsable extends Identifiable {
	// Does not require anything.
}