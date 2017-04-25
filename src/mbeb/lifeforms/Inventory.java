package mbeb.lifeforms;

import java.util.*;

import mbeb.ld38.recipe.Recipe;

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

	public void looseXPercentofYerShit(float x) {
		float keepingPercentage = 1.0f - x;
		for (Map.Entry<LootType, Integer> entry : lootMap.entrySet()) {
			lootMap.put(entry.getKey(), (int) (lootMap.get(entry.getKey()) * keepingPercentage));
		}
		Random r = new Random();
		LinkedList<Sword> delete = new LinkedList<>();
		int i = 0;
		for (Sword sword : swords) {
			if(r.nextFloat() < x) {
				delete.add(sword);
			}
		}
		for (Sword sword : delete) {
			swords.remove(sword);
		}
		if(swords.isEmpty()) {
			this.addSword(new Sword(10, 1, 1.5f, LootType.Wood, SwordType.LONG_SWORD));
		}
		this.switchSword();
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

	public HashMap<LootType, Integer> getLootMap() {
		return lootMap;
	}
}
