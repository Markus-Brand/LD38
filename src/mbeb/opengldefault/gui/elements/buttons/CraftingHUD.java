package mbeb.opengldefault.gui.elements.buttons;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import mbeb.ld38.recipe.DiamondSwordRecipe;
import mbeb.ld38.recipe.GoldSwordRecipe;
import mbeb.ld38.recipe.Recipe;
import mbeb.ld38.recipe.SteelSwordRecipe;
import mbeb.ld38.recipe.StoneSwordRecipe;
import mbeb.ld38.recipe.WoodenSwordRecipe;
import mbeb.lifeforms.Inventory;
import mbeb.opengldefault.gui.AtlasGUI;
import mbeb.opengldefault.gui.TextGUI;
import mbeb.opengldefault.shapes.Rectangle;

public class CraftingHUD {
	private List<CraftingButton> buttons;

	private AtlasGUI atlasGUI;
	private TextGUI textGUI;

	public CraftingHUD(AtlasGUI atlasGUI, TextGUI textGUI, Inventory inventory) {
		this.atlasGUI = atlasGUI;
		this.textGUI = textGUI;
		buttons = new ArrayList<>();

		addNew(new WoodenSwordRecipe(), inventory);
		addNew(new StoneSwordRecipe(), inventory);
		addNew(new SteelSwordRecipe(), inventory);
		addNew(new GoldSwordRecipe(), inventory);
		addNew(new DiamondSwordRecipe(), inventory);
	}

	private void addNew(Recipe recipe, Inventory inventory) {
		buttons.add(new CraftingButton(new Rectangle(new Vector2f(-0.95f + buttons.size() * 0.39f, -0.275f),
				new Vector2f(0.35f, 0.55f)), recipe, inventory));
	}

	public void show() {
		for (CraftingButton button : buttons) {
			button.show(atlasGUI, textGUI);
		}
	}

	public void hide() {
		for (CraftingButton button : buttons) {
			button.hide(atlasGUI, textGUI);
		}
	}

	public void update(double deltaTime) {
		for (CraftingButton button : buttons) {
			button.update(deltaTime);
			if (button.isDirty()) {
				for (CraftingButton b : buttons) {
					b.updateTexts();
				}
			}
		}
	}
}
