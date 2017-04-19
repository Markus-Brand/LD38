package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.gl.GLContext;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.gui.AtlasGUI;
import mbeb.opengldefault.gui.TextGUI;
import mbeb.opengldefault.gui.elements.buttons.BooleanOptionButton;
import mbeb.opengldefault.gui.elements.buttons.AbstractButton;
import mbeb.opengldefault.gui.elements.sliders.FloatOptionSlider;
import mbeb.opengldefault.gui.elements.sliders.IntegerOptionSlider;
import mbeb.opengldefault.gui.elements.sliders.OptionSlider;
import mbeb.opengldefault.gui.elements.sliders.Slider;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.logging.Log;
import mbeb.opengldefault.options.ButtonOption;
import mbeb.opengldefault.options.Options;
import mbeb.opengldefault.options.SliderOption;
import mbeb.opengldefault.shapes.Rectangle;

public class OptionsMenu implements GameState {
	private static final String TAG = "OptionsMenu";

	private HashMap<String, LinkedList<Field>> options;
	private boolean dirty;

	private TextGUI optionsHirarchy;

	private AtlasGUI atlasGUI;

	private LinkedList<AbstractButton> buttons;
	private LinkedList<Slider> sliders;

	public OptionsMenu() {
		options = new HashMap<>();
		Options.load(this);
	}

	@Override
	public void init() {

		optionsHirarchy = new TextGUI(new Font("Arial", 0, 128));
		ShaderProgram guiShader = new ShaderProgram("gui.vert", "gui.frag");
		optionsHirarchy.setShader(guiShader);

		atlasGUI = new AtlasGUI("menu.png", 4, 4);
		atlasGUI.setShader(guiShader);

		buttons = new LinkedList<>();
		sliders = new LinkedList<>();

		dirty = true;
	}

	public void addOption(String category, Field field) {
		if (!options.containsKey(category)) {
			options.put(category, new LinkedList<>());
		}
		options.get(category).add(field);
		dirty = true;
	}

	@Override
	public void update(double deltaTime) {
		for (AbstractButton button : buttons) {
			button.update(deltaTime);
		}
		for (Slider slider : sliders) {
			slider.update(deltaTime);
		}
		optionsHirarchy.update(deltaTime);
		atlasGUI.update(deltaTime);
	}

	@Override
	public void render() {
		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");

		glViewport(0, 0, GLContext.getFramebufferWidth(), GLContext.getFramebufferHeight());
		GLErrors.checkForError(TAG, "glViewport");

		setup();
		atlasGUI.render();
		optionsHirarchy.render();
	}

	@Override
	public void clear() {
		mbeb.opengldefault.options.Options.save();

	}

	@Override
	public GameStateIdentifier getNextState() {
		return KeyBoard.isKeyDown(GLFW.GLFW_KEY_ESCAPE) ? GameStateIdentifier.MAIN_MENU : null;
	}

	@Override
	public void resetNextGameState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void open() {
	}

	private void setup() {
		if (dirty) {
			dirty = false;
			float relativeY = 0.98f;
			for (Map.Entry<String, LinkedList<Field>> category : options.entrySet()) {

				optionsHirarchy.addText(category.getKey(), new Vector2f(), 0.12f)
						.setPositionRelativeToScreen(0.5f, relativeY).setColor(Color.RED);

				relativeY -= 0.1f;
				category.getValue().sort(new Comparator<Field>() {

					@Override
					public int compare(Field o1, Field o2) {
						return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
					}
				});

				for (Field option : category.getValue()) {
					relativeY -= setupOption(relativeY, option);
				}

			}
		}
	}

	private float setupOption(float relativeY, Field option) {
		Object value = null;
		try {
			value = option.get(null);
		} catch(IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		if (option.isAnnotationPresent(ButtonOption.class)) {
			addButton(relativeY, option, (boolean) value);
			return 0.065f;
		} else if (option.isAnnotationPresent(SliderOption.class)) {
			SliderOption sliderOption = option.getAnnotation(SliderOption.class);
			float min = sliderOption.min();
			float max = sliderOption.max();
			float step = sliderOption.step();
			addSlider(relativeY, option, min, max, step, value);
			return 0.1f;
		} else {
			optionsHirarchy.addText(option.getName(), new Vector2f(), 0.08f)
					.setPositionRelativeToScreen(0.5f, relativeY).setColor(Color.DARK_GRAY);
			return 0.065f;
		}
	}

	private void addSlider(float relativeY, Field option, float min, float max, float step, Object value) {
		Rectangle bounding = new Rectangle(new Vector2f(), new Vector2f(1.6f, 0.16f));
		bounding.setPositionRelativeTo(new Rectangle(new Vector2f(-1), new Vector2f(2)), 0.5f, relativeY);
		OptionSlider slider;
		if (int.class.isAssignableFrom(option.getType())) {
			slider = new IntegerOptionSlider(option, (int) value, (int) min, (int) max, step, bounding);
		} else if (float.class.isAssignableFrom(option.getType())) {
			slider = new FloatOptionSlider(option, (float) value, min, max, step, bounding);
		} else {
			Log.error(TAG, "Type " + option.getType() + " of Field " + option.getName()
					+ " is not supported for sliders");
			return;
		}
		slider.show(atlasGUI, optionsHirarchy);
		sliders.add(slider);
	}

	private void addButton(float relativeY, Field option, boolean intialValue) {
		Rectangle bounding = new Rectangle(new Vector2f(), new Vector2f(0.3f, 0.16f));
		bounding.setPositionRelativeTo(new Rectangle(new Vector2f(-1), new Vector2f(2)), 0.5f, relativeY);

		BooleanOptionButton button = new BooleanOptionButton(bounding, option, intialValue);
		button.show(atlasGUI, optionsHirarchy);
		buttons.add(button);
	}
}
