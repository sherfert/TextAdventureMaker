package persistence;

import data.Item;

public class ItemManager {
	public static void removeItem(Item item) {
		Main.getEntityManager().remove(item);
	}
}