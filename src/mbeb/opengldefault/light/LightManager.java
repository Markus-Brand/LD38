package mbeb.opengldefault.light;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

import java.nio.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import mbeb.opengldefault.rendering.shader.*;

import org.lwjgl.*;

public class LightManager {

	private static int UBOBaseID = 0;
	private static int BYTES_PER_BLOCK = 16;

	private final int directionalLightCapacity, pointLightCapacity, spotLightCapacity;

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

	public void addLight(final DirectionalLight light) {
		directionalLights.add(light);
		resizeBuffer();
	}

	public void addLight(final PointLight light) {
		pointLights.add(light);
		resizeBuffer();
	}

	public void addLight(final SpotLight light) {
		spotLights.add(light);
		resizeBuffer();
	}

	/**
	 * remove Light l and move last Light to the position of l
	 *
	 * @param l
	 */
	private void removeLight(final Light l) {

	}

	public void addShader(final Shader s) {
		shaders.add(s);
		s.addUniformBlockIndex(0, "Lights");
	}

	private void removeShader(final Shader s) {
		shaders.remove(s);
	}

	/**
	 * adjusts capacity of UBO and keeps data in it
	 */
	private void resizeBuffer() {
		glBindBuffer(GL_UNIFORM_BUFFER, UBO);

		glBufferData(GL_UNIFORM_BUFFER, getBufferSize() + 16, GL_STATIC_DRAW);

		glBindBufferBase(GL_UNIFORM_BUFFER, UBOBaseID, UBO);

		bufferSizes();
		bufferData();
		updateShaders();
	}

	private void updateShaders() {
		for (final Shader shader : shaders) {
			shader.updateParameter("DIRECTIONAL_LIGHT_CAPACITY", directionalLightCapacity, false);
			shader.updateParameter("POINT_LIGHT_CAPACITY", pointLightCapacity, false);
			shader.updateParameter("SPOT_LIGHT_CAPACITY", spotLightCapacity, true);
		}
	}

	private void bufferSizes() {
		final IntBuffer sizeBuffer = BufferUtils.createIntBuffer(12);

		sizeBuffer.put(directionalLights.size());
		sizeBuffer.put(pointLights.size());
		sizeBuffer.put(spotLights.size());
		sizeBuffer.flip();

		glBufferSubData(GL_UNIFORM_BUFFER, UBOBaseID, sizeBuffer);
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
	 * checks if Lights were changed since last update and refreshes shaderdata
	 *
	 * @param deltaTime
	 */
	public void update(final float deltaTime) {
		removeDeletedLights();
		updateDirtyLights();
	}

	/**
	 * update data of changed Light objects
	 */
	private void updateDirtyLights() {
		// TODO Auto-generated method stub
		final AtomicInteger lightIndex = new AtomicInteger(0);
		forEachLight((final Light light) -> {
			if (light.isDirty()) {
				updateLightData(light, lightIndex.get());
				light.setClean();
			}
			lightIndex.incrementAndGet();
		});
	}

	private void updateLightData(final Light light, final int lightIndex) {
		// TODO Auto-generated method stub

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

	/**
	 * all Lights that are marked as deleted will be removed here
	 */
	private void removeDeletedLights() {
		// TODO Auto-generated method stub

	}

}
