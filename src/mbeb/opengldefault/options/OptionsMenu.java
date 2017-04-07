package mbeb.opengldefault.options;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.game.GameState;
import mbeb.opengldefault.game.GameStateIdentifier;
import mbeb.opengldefault.gui.TextGUI;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.openglcontext.OpenGLContext;
import mbeb.opengldefault.rendering.shader.ShaderProgram;

public class OptionsMenu implements GameState{
	private static final String TAG = "OptionsMenu";	
	
	private HashMap<String, LinkedList<Field>> options;
	private boolean dirty;
	
	private TextGUI optionsHirarchy;
	
	public OptionsMenu() {
	}
	
	@Override
	public void init() {
		options = new HashMap<>();
		Options.load(this);
		
		optionsHirarchy = new TextGUI(new Font("Arial", 0, 128));
		ShaderProgram guiShader = new ShaderProgram("gui.vert", "gui.frag");		
		optionsHirarchy.setShader(guiShader);
		
		dirty = true;
	}
	
	public void addOption(String category, Field field){
		if(!options.containsKey(category)){
			options.put(category, new LinkedList<>());
		}
		options.get(category).add(field);
		dirty = true;
	}

	@Override
	public void update(double deltaTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");

		glViewport(0, 0, OpenGLContext.getFramebufferWidth(), OpenGLContext.getFramebufferHeight());
		GLErrors.checkForError(TAG, "glViewport");
		
		setup();
		optionsHirarchy.render();
	}

	@Override
	public void clear() {
		mbeb.opengldefault.options.Options.save();
		
	}

	@Override
	public GameStateIdentifier getNextState() {
		return KeyBoard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)?GameStateIdentifier.MAIN_MENU:null;
	}

	@Override
	public void resetNextGameState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void open() {
	}
	
	private void setup(){
		if(dirty){
			dirty = false;
			float relativeY = 0.98f;
			for(Map.Entry<String, LinkedList<Field>> categories : options.entrySet()){
				optionsHirarchy.addText(categories.getKey(), new Vector2f(), 0.1f).setPositionRelativeToScreen(0.5f, relativeY).setColor(Color.RED);
				relativeY -= 0.07f;
				for(Field option : categories.getValue()){
					optionsHirarchy.addText(option.getName(), new Vector2f(), 0.08f).setPositionRelativeToScreen(0.5f, relativeY).setColor(Color.BLUE);;
					relativeY -= 0.05f;
				}
			}
		}
	}
}