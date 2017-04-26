package mbeb.ld38.recipe;

import mbeb.lifeforms.LootType;
import mbeb.lifeforms.Sword;
import mbeb.lifeforms.SwordType;

public class DiamondSwordRecipe extends Recipe {
	public DiamondSwordRecipe() {
		super(new Sword(30, 1.3f, 0.2f, LootType.Diamond, SwordType.LONG_SWORD));
		addIngrediant(LootType.Diamond, 15);
		addIngrediant(LootType.Steel, 10);
		addIngrediant(LootType.Wood, 2);
	}
}
