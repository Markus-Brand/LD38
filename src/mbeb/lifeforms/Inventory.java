package mbeb.lifeforms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
	private HashMap<LootType, Integer> lootMap;

	private List<Sword> swords;

	private int selectedSword;

	public Inventory() {
		lootMap = new HashMap<>();
		swords = new ArrayList<>();
		selectedSword = 0;
	}

	public boolean canCraft(Recipe recipe) {
		for (Map.Entry<LootType, Integer> entry : recipe.getNeededItems().entrySet()) {
			if (!lootMap.containsKey(entry.getKey()) || lootMap.get(entry.getKey()) < entry.getValue()) {
				return false;
			}
		}
		return true;
	}

	public void craft(Recipe recipe) {
		if (!canCraft(recipe)) {
			return;
		}
		for (Map.Entry<LootType, Integer> entry : recipe.getNeededItems().entrySet()) {
			lootMap.put(entry.getKey(), lootMap.get(entry.getKey()) - entry.getValue());
		}
		swords.add(recipe.getResultingSword());
	}

	public void addLoot(LootType type, int amount) {
		if (lootMap.containsKey(type)) {
			lootMap.put(type, lootMap.get(type) + amount);
		} else {
			lootMap.put(type, amount);
		}
	}

	public void addSword(Sword sword) {
		swords.add(sword);
	}

	public Sword getSelectedSword() {
		return swords.get(selectedSword);
	}

	public void switchSword() {
		selectedSword++;
		selectedSword %= swords.size();
	}
}
