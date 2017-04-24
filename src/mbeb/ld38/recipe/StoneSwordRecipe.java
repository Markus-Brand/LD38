package mbeb.ld38.recipe;

import mbeb.lifeforms.LootType;
import mbeb.lifeforms.Sword;
import mbeb.lifeforms.SwordType;

public class StoneSwordRecipe extends Recipe {
	public StoneSwordRecipe() {
		super(new Sword(8, 0.75f, 1.1f, LootType.Stone, SwordType.LONG_SWORD));
		addIngrediant(LootType.Stone, 8);
		addIngrediant(LootType.Wood, 2);
	}
}
