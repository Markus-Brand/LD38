package mbeb.opengldefault.examples;

import java.nio.FloatBuffer;
import mbeb.opengldefault.camera.FirstPersonCamera;
import mbeb.opengldefault.game.Game;
import mbeb.opengldefault.main.GLErrors;
import mbeb.opengldefault.main.Main;
import mbeb.opengldefault.rendering.Texture;
import mbeb.opengldefault.scene.DataFragment;
import mbeb.opengldefault.scene.ObjectLoader;
import mbeb.opengldefault.scene.TexturedRenderable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

/**
 * Test the application by showing a bunny
 */
public class Bunny {
	private static final String TAG = "Bunny";
	
	public static void main(String[] args) {
		Main.init(args);
		Main main = new Main();
		main.startWith(new Game() {
			
			TexturedRenderable bunny = new TexturedRenderable(
					(new ObjectLoader()).loadFromFile("D:/bunny.obj", 
							new DataFragment[]{DataFragment.POSITION, DataFragment.NORMAL, DataFragment.UV}), 
					new Texture("bunny_2d.png"));

			@Override
			public void render() {
				Vector3f pos = ((FirstPersonCamera)cam).getPosition();
				GL20.glUniform3f(bunny.getShader().getUniform("viewPos"), pos.x, pos.y, pos.z);
				GLErrors.checkForError(TAG, "glUniform3f");
				Matrix4f model = new Matrix4f();
				FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
				GL20.glUniformMatrix4fv(bunny.getShader().getUniform("model"), false, model.get(buffer));
				GLErrors.checkForError(TAG, "glUniformMatrix4fv");
				bunny.render();
			}
		});
	}
}
