package mbeb.ld38.recipe;

import mbeb.lifeforms.LootType;
import mbeb.lifeforms.Sword;
import mbeb.lifeforms.SwordType;

public class GoldSwordRecipe extends Recipe {
	public GoldSwordRecipe() {
		super(new Sword(20, 1, 0.5f, LootType.Gold, SwordType.LONG_SWORD));
		addIngrediant(LootType.Gold, 30);
		addIngrediant(LootType.Steel, 10);
		addIngrediant(LootType.Wood, 2);
	}
}
