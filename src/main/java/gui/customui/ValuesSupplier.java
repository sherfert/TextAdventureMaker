package gui.customui;

import java.util.List;

import data.NamedObject;
import exception.DBClosedException;

/**
 * Supplier of a list of values, that can be chosen from.
 * 
 * @author Satia
 *
 * @param <E>
 *            the type
 */
public interface ValuesSupplier<E extends NamedObject> {
	/**
	 * Get the values.
	 * 
	 * @return the values
	 * @throws DBClosedException
	 *             if the DB was closed.
	 */
	public List<E> get() throws DBClosedException;
}