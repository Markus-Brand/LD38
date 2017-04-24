package mbeb.opengldefault.gui.elements.buttons;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map.Entry;

import org.joml.Vector2f;

import mbeb.ld38.recipe.Recipe;
import mbeb.lifeforms.Inventory;
import mbeb.lifeforms.LootType;
import mbeb.lifeforms.Sword;
import mbeb.opengldefault.gui.AtlasGUI;
import mbeb.opengldefault.gui.TextGUI;
import mbeb.opengldefault.gui.elements.AtlasGUIElement;
import mbeb.opengldefault.gui.elements.TextGUIElement;
import mbeb.opengldefault.shapes.Rectangle;

public class CraftingButton extends Button {

	private Recipe recipe;

	private Inventory inventory;

	private HashMap<LootType, TextGUIElement> buttonTexts;

	private AtlasGUIElement buttonAtlas;

	private TextGUIElement title, info;

	private boolean dirty;

	public CraftingButton(Rectangle bounding, Recipe recipe, Inventory inventory) {
		super(bounding, false);
		this.inventory = inventory;
		this.recipe = recipe;
		buttonTexts = new HashMap<>();
		dirty = false;
	}

	/**
	 * Show the guiElements of this button in the guis
	 *
	 * @param atlasGUI
	 * @param textGUI
	 */
	public void show(AtlasGUI atlasGUI, TextGUI textGUI) {
		if (buttonTexts.size() > 0) {
			return;
		}
		buttonAtlas = atlasGUI.addAtlasGUIElement(14, bounding.getPosition(), bounding.getSize());
		buttonAtlas.setColor(Color.LIGHT_GRAY);
		buttonAtlas.setColor(Color.BLACK, 0);

		float spacePerLine = 0.2f;
		float height = 0.5f;

		Sword sword = recipe.getResultingSword();

		title = textGUI.addText(sword.getName(), new Vector2f());
		title.setColor(Color.BLACK);
		title.setPositionRelativeTo(bounding, 0.5f, 0.90f);

		info =
				textGUI.addText("Damage:" + (int) sword.getDamage() + " | " + "Range:" + sword.getRange() + "m | "
						+ "Speed:"
						+ (int) (10 / sword.getStrokeTime()),
						new Vector2f(), 0.03f);
		info.setColor(Color.BLACK);
		info.setPositionRelativeTo(bounding, 0.5f, 0.70f);

		for (Entry<LootType, Integer> entry : recipe.getNeededItems().entrySet()) {
			int numNeeded = entry.getValue();
			Integer integerNumInInventory = inventory.getLootMap().get(entry.getKey());
			int numInInventory = integerNumInInventory == null ? 0 : integerNumInInventory;

			String text = entry.getKey() + ": " + numInInventory + "/" + numNeeded;

			TextGUIElement newElement = textGUI.addText(text, new Vector2f());
			newElement.setPositionRelativeTo(bounding, 0.5f, height);
			if (numInInventory >= numNeeded) {
				newElement.setColor(new Color(0, 150, 0));
			} else {
				newElement.setColor(new Color(150, 0, 0));
			}

			buttonTexts.put(entry.getKey(), newElement);
			height -= spacePerLine;
		}

		onButtonChanged();
	}

	public void updateTexts() {
		for (Entry<LootType, Integer> entry : recipe.getNeededItems().entrySet()) {
			int numNeeded = entry.getValue();
			Integer integerNumInInventory = inventory.getLootMap().get(entry.getKey());
			int numInInventory = integerNumInInventory == null ? 0 : integerNumInInventory;

			String text = entry.getKey() + ": " + numInInventory + "/" + numNeeded;

			buttonTexts.get(entry.getKey()).setText(text);

			if (numInInventory >= numNeeded) {
				buttonTexts.get(entry.getKey()).setColor(new Color(0, 150, 0));
			} else {
				buttonTexts.get(entry.getKey()).setColor(new Color(150, 0, 0));
			}
		}
		dirty = false;
	}

	@Override
	public void gotFocus() {
		if (inventory.canCraft(recipe)) {
			buttonAtlas.setColor(new Color(120, 255, 120));
		} else {
			buttonAtlas.setColor(new Color(255, 120, 120));
		}
	}

	@Override
	public void releasedFocus() {
		buttonAtlas.setColor(Color.LIGHT_GRAY);
	}

	public void hide(AtlasGUI atlasGUI, TextGUI textGUI) {
		textGUI.remove(title);
		textGUI.remove(info);
		for (TextGUIElement lines : buttonTexts.values()) {
			textGUI.remove(lines);
		}
		atlasGUI.remove(buttonAtlas);
		buttonTexts = new HashMap<>();
	}

	@Override
	public void onButtonChanged() {
		if (isPressed) {
			inventory.craft(recipe);
			buttonAtlas.setColor(Color.LIGHT_GRAY);
			dirty = true;
		}
	}

	public boolean isDirty() {
		return dirty;
	}

}
