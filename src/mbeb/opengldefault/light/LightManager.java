package mbeb.opengldefault.light;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

import java.nio.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.rendering.shader.*;

import org.lwjgl.*;

/**
 * @author Erik + Merlin + Markus :)
 */
public class LightManager {

	private static final String TAG = "LightManager";

	private static int UBOBaseID = 0;
	private static int BYTES_PER_BLOCK = 16;

	private int directionalLightCapacity;
	private int pointLightCapacity;
	private int spotLightCapacity;

	private final ArrayList<DirectionalLight> directionalLights;
	private final ArrayList<PointLight> pointLights;
	private final ArrayList<SpotLight> spotLights;
	private final ArrayList<Shader> shaders;

	private final int UBO;

	public LightManager() {
		directionalLightCapacity = 1;
		pointLightCapacity = 8;
		spotLightCapacity = 4;

		directionalLights = new ArrayList<>();
		pointLights = new ArrayList<>();
		spotLights = new ArrayList<>();
		shaders = new ArrayList<>();

		UBO = glGenBuffers();
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
		updateShaders();

		glBindBuffer(GL_UNIFORM_BUFFER, 0);
	}

	/**
	 * calculates number of Bytes which are needed to store the light data capacity
	 *
	 * @return
	 */
	private int getBufferSize() {
		int blockCount = 0;

		blockCount += directionalLightCapacity * DirectionalLight.DATASIZE_IN_BLOCKS;
		blockCount += pointLightCapacity * PointLight.DATASIZE_IN_BLOCKS;
		blockCount += spotLightCapacity * SpotLight.DATASIZE_IN_BLOCKS;

		return blockCount * BYTES_PER_BLOCK;
	}

	/**
	 * saves the 3 buffer sizes in the beginning of the UBO
	 */
	private void saveBufferSizes() {
		final IntBuffer sizeBuffer = BufferUtils.createIntBuffer(12);

		sizeBuffer.put(directionalLights.size());
		sizeBuffer.put(pointLights.size());
		sizeBuffer.put(spotLights.size());
		sizeBuffer.flip();

		glBufferSubData(GL_UNIFORM_BUFFER, 0, sizeBuffer);
		GLErrors.checkForError(TAG, "glBufferSubData");
	}

	private void bufferData() {
		final FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(getBufferSize());

		dataBuffer.position(getDirectionalLightBufferOffset());
		directionalLights.forEach((final DirectionalLight light) -> {
			dataBuffer.put(light.getData());
		});

		dataBuffer.position(getPointLightBufferOffset());
		pointLights.forEach((final PointLight light) -> {
			dataBuffer.put(light.getData());
		});

		dataBuffer.position(getSpotLightBufferOffset());
		spotLights.forEach((final SpotLight light) -> {
			dataBuffer.put(light.getData());
		});

		dataBuffer.flip();
		glBufferSubData(GL_UNIFORM_BUFFER, 16, dataBuffer);
		GLErrors.checkForError(TAG, "glBufferSubData");
	}

	private int getDirectionalLightBufferOffset() {
		return 0;
	}

	private int getPointLightBufferOffset() {
		return getDirectionalLightBufferOffset() + directionalLightCapacity * DirectionalLight.DATASIZE_IN_BLOCKS * 4;
	}

	private int getSpotLightBufferOffset() {
		return getPointLightBufferOffset() + pointLightCapacity * PointLight.DATASIZE_IN_BLOCKS * 4;
	}

	private void updateShaders() {
		for (final Shader shader : shaders) {
			shader.updateParameter("DIRECTIONAL_LIGHT_CAPACITY", directionalLightCapacity, false);
			shader.updateParameter("POINT_LIGHT_CAPACITY", pointLightCapacity, false);
			shader.updateParameter("SPOT_LIGHT_CAPACITY", spotLightCapacity, true);
		}
	}

	public void addShader(final Shader shader) {
		shaders.add(shader);
		shader.addUniformBlockIndex(0, "Lights");

		shader.updateParameter("DIRECTIONAL_LIGHT_CAPACITY", directionalLightCapacity, false);
		shader.updateParameter("POINT_LIGHT_CAPACITY", pointLightCapacity, false);
		shader.updateParameter("SPOT_LIGHT_CAPACITY", spotLightCapacity, true);
	}

	private void removeShader(final Shader shader) {
		shaders.remove(shader);
	}

	public void addLight(final DirectionalLight light) {
		if (directionalLightCapacity <= directionalLights.size()) {
			directionalLights.add(light);
			directionalLightCapacity *= 2;
			resizeBuffer();
		} else {
			final int offset = getTotalDirectionalLightBufferOffset(directionalLights.size());
			directionalLights.add(light);
			updateSingleLightData(light, offset);
		}
		light.setClean();
	}

	private int getTotalDirectionalLightBufferOffset(final int lightIndex) {
		return (getDirectionalLightBufferOffset() + lightIndex * DirectionalLight.DATASIZE_IN_BLOCKS * 4) * 4;
	}

	public void addLight(final PointLight light) {
		if (pointLightCapacity <= pointLights.size()) {
			pointLights.add(light);
			pointLightCapacity *= 2;
			resizeBuffer();
		} else {
			final int offset = getTotalPointLightBufferOffset(pointLights.size());
			pointLights.add(light);
			updateSingleLightData(light, offset);
		}
		light.setClean();
	}

	private int getTotalPointLightBufferOffset(final int lightIndex) {
		return (getPointLightBufferOffset() + lightIndex * PointLight.DATASIZE_IN_BLOCKS * 4) * 4;
	}

	public void addLight(final SpotLight light) {
		if (spotLightCapacity <= spotLights.size()) {
			spotLights.add(light);
			spotLightCapacity *= 2;
			resizeBuffer();
		} else {
			final int offset = getTotalSpotLightBufferOffset(spotLights.size());
			spotLights.add(light);
			updateSingleLightData(light, offset);
		}
		light.setClean();
	}

	private int getTotalSpotLightBufferOffset(final int lightIndex) {
		return (getSpotLightBufferOffset() + lightIndex * SpotLight.DATASIZE_IN_BLOCKS * 4) * 4;
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
		System.out.println(offset);
		glBindBuffer(GL_UNIFORM_BUFFER, UBO);
		GLErrors.checkForError(TAG, "glBindBuffer");
		saveBufferSizes();
		final FloatBuffer lightBuffer = BufferUtils.createFloatBuffer(light.getBlockSize() * 4);
		lightBuffer.put(light.getData());
		lightBuffer.flip();
		glBufferSubData(GL_UNIFORM_BUFFER, offset + 16, lightBuffer);
		GLErrors.checkForError(TAG, "glBufferSubData");

		glBindBuffer(GL_UNIFORM_BUFFER, 0);
	}

	/**
	 * remove light and move last Light to the position of light
	 *
	 * @param light
	 *            the Light that will be removed
	 */
	private void removeLight(final Light light) {

	}

	/**
	 * checks if Lights were changed since last update and refreshes shader data
	 *
	 * @param deltaTime
	 */
	public void update(final double deltaTime) {
		//removeDeletedLights();
		updateDirtyLights();
	}

	/**
	 * all Lights that are marked as deleted will be removed here
	 */
	private void removeDeletedLights() {
		final AtomicInteger lightIndex = new AtomicInteger(0);
		forEachLight((final Light light) -> {
			//count removings + save their indices
			lightIndex.incrementAndGet();
		});

	}

	/**
	 * update data of changed Light objects
	 */
	private void updateDirtyLights() {
		final AtomicInteger lightIndex = new AtomicInteger(0);
		forEachLight((final Light light) -> {
			if (light.isDirty()) {
				updateLightData(light, lightIndex.get());
				light.setClean();
			}
			lightIndex.incrementAndGet();
		});
	}

	private void forEachLight(final Consumer<Light> action) {
		for (final DirectionalLight light : directionalLights) {
			action.accept(light);
		}
		for (final PointLight light : pointLights) {
			action.accept(light);
		}
		for (final SpotLight light : spotLights) {
			action.accept(light);
		}
	}

	private void updateLightData(final Light light, final int lightIndex) {
		System.out.println("teset" + lightIndex);
		final int offset = calculateOffsetFromIndex(lightIndex);
		updateSingleLightData(light, offset);
		light.setClean();
	}

	private int calculateOffsetFromIndex(int lightIndex) {
		int offset = 0;
		Log.assertTrue(TAG, lightIndex >= 0, "lightIndex must be greater than 0, but was " + lightIndex);
		if (lightIndex < directionalLights.size()) {
			offset = getTotalDirectionalLightBufferOffset(lightIndex);
			return offset;
		}
		lightIndex -= directionalLights.size();
		if (lightIndex < pointLights.size()) {
			offset = getTotalPointLightBufferOffset(lightIndex);
			return offset;
		}
		lightIndex -= pointLights.size();
		if (lightIndex < spotLights.size()) {
			offset = getTotalSpotLightBufferOffset(lightIndex);
			return offset;
		}
		Log.error(TAG, "LightIndex out of range: " + lightIndex);
		return 0;
	}
}
