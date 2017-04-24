package mbeb.ld38.recipe;

import java.util.HashMap;

import mbeb.lifeforms.LootType;
import mbeb.lifeforms.Sword;

public abstract class Recipe {
	private HashMap<LootType, Integer> neededItems;
	private Sword resultingSword;

	public Recipe(Sword resultingSword) {
		neededItems = new HashMap<>();
		this.resultingSword = resultingSword;
	}

	public void addIngrediant(LootType type, int amount) {
		neededItems.put(type, amount);
	}

	public HashMap<LootType, Integer> getNeededItems() {
		return neededItems;
	}

	public Sword getResultingSword() {
		return resultingSword;
	}
}
