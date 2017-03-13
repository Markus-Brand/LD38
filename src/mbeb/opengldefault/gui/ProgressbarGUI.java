package mbeb.opengldefault.gui;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL33.*;
import mbeb.opengldefault.rendering.shader.Shader;

/**
 * A Progressbar {@link GUI} that is able to render Progressbars in one drawcall
 * 
 * @author Markus
 */
public class ProgressbarGUI extends GUI {

	private static final int FLOAT_SIZE = 4;
	private static final int VEC4_SIZE = FLOAT_SIZE * 4;
	private static final int MAT4_SIZE = VEC4_SIZE * 4;

	public ProgressbarGUI() {
		super();
		stride = 5 * VEC4_SIZE;
	}

	@Override
	public void setupVAO() {
		super.setupVAO();
		renderable.bind();
		glEnableVertexAttribArray(7);
		glVertexAttribPointer(7, 4, GL_FLOAT, false, stride, 4 * VEC4_SIZE);

		glVertexAttribDivisor(7, 1);
		renderable.unbind();
	}

	@Override
	public void render(Shader shader) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		super.render(shader);
		glDisable(GL_BLEND);
	}

}
