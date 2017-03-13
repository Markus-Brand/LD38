package mbeb.opengldefault.light;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

import java.nio.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import org.lwjgl.*;

import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.rendering.shader.*;

/**
 * @author Erik + Merlin + Markus :)
 */
public abstract class LightTypeManager {
	/** Class Name Tag */
	private static final String TAG = "LightTypeManager";

	private static int BYTES_PER_BLOCK = 16; // 1 Block = four 32bit floats = 16byte
	protected String UBOBaseName;
	protected int UBOBaseID;
	protected int lightBlockSize;
	protected String shaderLightTypeParameterName;
	protected int lightCapacity;
	private ArrayList<Light> lights;
	private int UBO;

	protected void init() {
		this.UBOBaseID = UBOManager.getUBOID(UBOBaseName);
		this.lights = new ArrayList<>();
		this.UBO = glGenBuffers();
		GLErrors.checkForError(TAG, "glGenBuffers");
		resizeBuffer();
	}

	/**
	 * adjusts capacity of UBO and keeps it's data up to date
	 */
	private void resizeBuffer() {
		glBindBuffer(GL_UNIFORM_BUFFER, UBO);
		GLErrors.checkForError(TAG, "glBindBuffer");

		glBufferData(GL_UNIFORM_BUFFER, getBufferSize() + 16, GL_STATIC_DRAW);
		GLErrors.checkForError(TAG, "glBufferData");

		glBindBufferBase(GL_UNIFORM_BUFFER, UBOBaseID, UBO);
		GLErrors.checkForError(TAG, "glBindBufferBase");

		saveBufferSize();
		bufferData();
		//updateShaders();

		glBindBuffer(GL_UNIFORM_BUFFER, 0);
		GLErrors.checkForError(TAG, "glBindBuffer");
	}

	/**
	 * calculates number of bytes which are needed to store the light data capacity
	 *
	 * @return
	 */
	private int getBufferSize() {
		return lightCapacity * lightBlockSize * BYTES_PER_BLOCK;
	}

	/**
	 * stores the buffer size at the beginning of the UBO
	 */
	private void saveBufferSize() {
		glBindBuffer(GL_UNIFORM_BUFFER, UBO);
		GLErrors.checkForError(TAG, "glBindBuffer");

		final IntBuffer sizeBuffer = BufferUtils.createIntBuffer(4);

		sizeBuffer.put(lights.size());
		sizeBuffer.flip();

		glBufferSubData(GL_UNIFORM_BUFFER, 0, sizeBuffer);
		GLErrors.checkForError(TAG, "glBufferSubData");
	}

	/**
	 * stores the data for each light in the UBO
	 */
	private void bufferData() {
		final FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(getBufferSize());

		lights.forEach((final Light light) -> {
			dataBuffer.put(light.getData());
		});
		dataBuffer.flip();
		glBufferSubData(GL_UNIFORM_BUFFER, 16, dataBuffer);
		GLErrors.checkForError(TAG, "glBufferSubData");
	}

	/**
	 * updates the <i>TYPE</i>_LIGHT_CAPACITY Parameter of the given shader
	 *
	 * @param shader
	 */
	public void updateShader(final Shader shader) {
		shader.addUniformBlockIndex(UBOBaseName);
		shader.updateParameter(shaderLightTypeParameterName, lightCapacity);
	}

	/**
	 * adds <i>light</i> and resizes Buffer if necessary
	 *
	 * @param light
	 *            the Light that will be added
	 */
	public void addLight(final Light light) {
		if (lightCapacity <= lights.size()) {
			lights.add(light);
			lightCapacity *= 2;
			resizeBuffer();
		} else {
			final int offset = getTotalBufferOffset(lights.size());
			lights.add(light);
			updateSingleLightData(light, offset);
		}
		saveBufferSize();
		light.setClean();
	}

	/**
	 * @param lightIndex
	 *            index of a light in the java side storage
	 * @return offset in the Buffer for the graphics card
	 */
	private int getTotalBufferOffset(final int lightIndex) {
		return lightIndex * lightBlockSize * BYTES_PER_BLOCK;
	}

	/**
	 * updates the UBO data on position offset to light's data
	 *
	 * @param light
	 *            the light that will be saved
	 * @param offset
	 *            the position that the light will have afterwards
	 */
	private void updateSingleLightData(final Light light, final int offset) {
		glBindBuffer(GL_UNIFORM_BUFFER, UBO);
		GLErrors.checkForError(TAG, "glBindBuffer");
		final FloatBuffer lightBuffer = BufferUtils.createFloatBuffer(lightBlockSize * 4);
		lightBuffer.put(light.getData());
		lightBuffer.flip();
		glBufferSubData(GL_UNIFORM_BUFFER, offset + 16, lightBuffer);
		GLErrors.checkForError(TAG, "glBufferSubData");

		glBindBuffer(GL_UNIFORM_BUFFER, 0);
		GLErrors.checkForError(TAG, "glBindBuffer");
	}

	public void update(final double deltaTime) {
		removeDeletedLightsFromList();
		updateDirtyLights();
	}

	private void updateLightData(final Light light, final int lightIndex) {
		final int offset = getTotalBufferOffset(lightIndex);
		updateSingleLightData(light, offset);
		light.setClean();
	}

	/**
	 * update data of changed Light objects
	 */
	private void updateDirtyLights() {
		final AtomicInteger lightIndex = new AtomicInteger(0);
		lights.forEach((final Light light) -> {
			if (light.isDirty()) {
				updateLightData(light, lightIndex.get());
				light.setClean();
			}
			lightIndex.incrementAndGet();
		});
	}

	/**
	 * all Lights that are marked as deleted will be removed here
	 */
	private void removeDeletedLightsFromList() {
		for (int i = 0; i < lights.size(); i++) {
			if (lights.get(i).shouldBeRemoved()) {
				if (i == lights.size() - 1) {
					lights.remove(i);
					saveBufferSize();
					break;
				}
				final Light swap = lights.get(lights.size() - 1);
				lights.remove(lights.size() - 1);
				saveBufferSize();
				lights.set(i, swap);
				swap.setDirty();
				i--;
			}
		}
	}
}
