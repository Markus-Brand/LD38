package mbeb.lifeforms;

import java.util.HashMap;

public class Recipe {
	private HashMap<LootType, Integer> neededItems;
	private Sword resultingSword;

	public Recipe() {
		neededItems = new HashMap<>();
	}

	public HashMap<LootType, Integer> getNeededItems() {
		return neededItems;
	}

	public Sword getResultingSword() {
		return resultingSword;
	}
}
