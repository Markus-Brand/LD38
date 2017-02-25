package mbeb.opengldefault.light;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

import java.nio.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.rendering.shader.*;

import org.lwjgl.*;

public class LightTypeManager<L extends Light> {
	private static final String TAG = "LightTypeManager";

	private static int BYTES_PER_BLOCK = 16;
	private final int UBOBaseID;
	private final int lightBlockSize;
	private final String shaderLightTypeParameterName;

	private int lightCapacity;
	private final ArrayList<L> lights;
	private final int UBO;

	public LightTypeManager(final int initialCapacity, final int UBOBaseID, final int lightBlockSize, final String shaderLightTypeParameterName) {
		this.UBOBaseID = UBOBaseID;
		this.lightCapacity = initialCapacity;
		this.lights = new ArrayList<L>();
		this.lightBlockSize = lightBlockSize;
		this.shaderLightTypeParameterName = shaderLightTypeParameterName;

		this.UBO = glGenBuffers();
		resizeBuffer();
	}

	/**
	 * adjusts capacity of UBO and keeps data in it
	 */
	private void resizeBuffer() {
		glBindBuffer(GL_UNIFORM_BUFFER, UBO);
		GLErrors.checkForError(TAG, "glBindBuffer");

		glBufferData(GL_UNIFORM_BUFFER, getBufferSize() + 16, GL_STATIC_DRAW);
		GLErrors.checkForError(TAG, "glBufferData");

		glBindBufferBase(GL_UNIFORM_BUFFER, UBOBaseID, UBO);
		GLErrors.checkForError(TAG, "glBindBufferBase");

		saveBufferSizes();
		bufferData();
		//updateShaders();

		glBindBuffer(GL_UNIFORM_BUFFER, 0);
	}

	/**
	 * calculates number of Bytes which are needed to store the light data capacity
	 *
	 * @return
	 */
	private int getBufferSize() {
		int blockCount = 0;

		blockCount += lightCapacity * lightBlockSize * BYTES_PER_BLOCK;

		return blockCount;
	}

	/**
	 * saves the 3 buffer sizes in the beginning of the UBO
	 */
	private void saveBufferSizes() {
		glBindBuffer(GL_UNIFORM_BUFFER, UBO);
		GLErrors.checkForError(TAG, "glBindBuffer");

		final IntBuffer sizeBuffer = BufferUtils.createIntBuffer(4);

		sizeBuffer.put(lights.size());
		sizeBuffer.flip();

		glBufferSubData(GL_UNIFORM_BUFFER, 0, sizeBuffer);
		GLErrors.checkForError(TAG, "glBufferSubData");
	}

	private void bufferData() {
		final FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(getBufferSize());

		lights.forEach((final L light) -> {
			dataBuffer.put(light.getData());
		});
		dataBuffer.flip();
		glBufferSubData(GL_UNIFORM_BUFFER, 16, dataBuffer);
		GLErrors.checkForError(TAG, "glBufferSubData");
	}

	public void updateShader(final List<Shader> shaders) {
		for (final Shader shader : shaders) {
			updateShader(shader);
		}
	}

	public void updateShader(final Shader shader) {
		shader.updateParameter(shaderLightTypeParameterName, lightCapacity);
	}

	public void addLight(final L light) {
		if (lightCapacity <= lights.size()) {
			lights.add(light);
			lightCapacity *= 2;
			resizeBuffer();
		} else {
			final int offset = getTotalBufferOffset(lights.size());
			lights.add(light);
			updateSingleLightData(light, offset);
		}
		saveBufferSizes();
		light.setClean();
	}

	private int getTotalBufferOffset(final int lightIndex) {
		return (lightIndex * lightBlockSize * 4) * 4;
	}

	/**
	 * updates the UBO data on position offset to light's data
	 *
	 * @param light
	 *            the light that will be saved
	 * @param offset
	 *            the position that the light will have afterwards
	 */
	private void updateSingleLightData(final L light, final int offset) {
		glBindBuffer(GL_UNIFORM_BUFFER, UBO);
		GLErrors.checkForError(TAG, "glBindBuffer");
		final FloatBuffer lightBuffer = BufferUtils.createFloatBuffer(light.getBlockSize() * 4);
		lightBuffer.put(light.getData());
		lightBuffer.flip();
		glBufferSubData(GL_UNIFORM_BUFFER, offset + 16, lightBuffer);
		GLErrors.checkForError(TAG, "glBufferSubData");

		glBindBuffer(GL_UNIFORM_BUFFER, 0);
	}

	public void update(final double deltaTime) {
		removeDeletedLightsFromList();
		updateDirtyLights();
	}

	private void updateLightData(final L light, final int lightIndex) {
		final int offset = getTotalBufferOffset(lightIndex);
		updateSingleLightData(light, offset);
		light.setClean();
	}

	/**
	 * update data of changed Light objects
	 */
	private void updateDirtyLights() {
		final AtomicInteger lightIndex = new AtomicInteger(0);
		lights.forEach((final L light) -> {
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
					saveBufferSizes();
					break;
				}
				final L swap = lights.get(lights.size() - 1);
				lights.remove(lights.size() - 1);
				saveBufferSizes();
				lights.set(i, swap);
				swap.setDirty();
				i--;
			}
		}
	}
}
