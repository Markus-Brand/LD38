package mbeb.opengldefault.gui.elements.buttons;

import java.util.*;

import org.joml.*;

import mbeb.ld38.recipe.*;
import mbeb.lifeforms.*;
import mbeb.opengldefault.gui.*;
import mbeb.opengldefault.shapes.*;

public class CraftingHUD {
	private final List<CraftingButton> buttons;

	private final AtlasGUI atlasGUI;
	private final TextGUI textGUI;

	public CraftingHUD(final AtlasGUI atlasGUI, final TextGUI textGUI, final PlayerEntity playerEntity) {
		this.atlasGUI = atlasGUI;
		this.textGUI = textGUI;
		buttons = new ArrayList<>();

		addNew(new WoodenSwordRecipe(), playerEntity);
		addNew(new StoneSwordRecipe(), playerEntity);
		addNew(new SteelSwordRecipe(), playerEntity);
		addNew(new GoldSwordRecipe(), playerEntity);
		addNew(new DiamondSwordRecipe(), playerEntity);
	}

	private void addNew(final Recipe recipe, final PlayerEntity playerEntity) {
		buttons.add(new CraftingButton(new Rectangle(new Vector2f(-0.95f + buttons.size() * 0.39f, -0.275f), new Vector2f(0.35f, 0.55f)), recipe, playerEntity));
	}

	public void show() {
		for (final CraftingButton button : buttons) {
			button.show(atlasGUI, textGUI);
		}
	}

	public void hide() {
		for (final CraftingButton button : buttons) {
			button.hide(atlasGUI, textGUI);
		}
	}

	public void update(final double deltaTime) {
		for (final CraftingButton button : buttons) {
			button.update(deltaTime);
			if (button.isDirty()) {
				for (final CraftingButton b : buttons) {
					b.updateTexts();
				}
			}
		}
	}
}
