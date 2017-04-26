package mbeb.ld38.recipe;

import mbeb.lifeforms.LootType;
import mbeb.lifeforms.Sword;
import mbeb.lifeforms.SwordType;

public class SteelSwordRecipe extends Recipe {
	public SteelSwordRecipe() {
		super(new Sword(12, 1, 0.7f, LootType.Steel, SwordType.LONG_SWORD));
		addIngrediant(LootType.Steel, 12);
		addIngrediant(LootType.Wood, 2);
	}
}
