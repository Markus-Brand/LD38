package mbeb.ld38.recipe;

import mbeb.lifeforms.LootType;
import mbeb.lifeforms.Sword;
import mbeb.lifeforms.SwordType;

public class WoodenSwordRecipe extends Recipe {
	public WoodenSwordRecipe() {
		super(new Sword(4, 0.6f, 0.7f, LootType.Wood, SwordType.LONG_SWORD));
		addIngrediant(LootType.Wood, 5);
	}
}
