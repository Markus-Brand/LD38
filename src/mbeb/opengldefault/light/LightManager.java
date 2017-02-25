package mbeb.opengldefault.light;

import java.util.*;

import mbeb.opengldefault.rendering.shader.*;

/**
 * @author Erik + Merlin + Markus :)
 */
public class LightManager {

	private static final String TAG = "LightManager";

	private static int DirectionalUBOBaseID = 1;
	private static int PointUBOBaseID = 2;
	private static int SpotUBOBaseID = 3;
	//private static int BYTES_PER_BLOCK = 16;

	private final int directionalLightCapacity = 1;
	private final int pointLightCapacity = 8;
	private final int spotLightCapacity = 4;

	private final LightTypeManager<DirectionalLight> directionalLightManager;
	private final LightTypeManager<PointLight> pointLightManager;
	private final LightTypeManager<SpotLight> spotLightManager;

	/*private final ArrayList<DirectionalLight> directionalLights;
	private final ArrayList<PointLight> pointLights;
	private final ArrayList<SpotLight> spotLights;*/
	private final ArrayList<Shader> shaders;

	//private final int UBO;

	/**
	 * creates Lightmanager with 3 LightTypeManagers for each LightType (Directional, Point, Spot) and stores the light
	 * shaders
	 */
	public LightManager() {
		directionalLightManager = new LightTypeManager<DirectionalLight>(directionalLightCapacity, DirectionalUBOBaseID, DirectionalLight.DATASIZE_IN_BLOCKS, "DIRECTIONAL_LIGHT_CAPACITY");
		pointLightManager = new LightTypeManager<PointLight>(pointLightCapacity, PointUBOBaseID, PointLight.DATASIZE_IN_BLOCKS, "POINT_LIGHT_CAPACITY");
		spotLightManager = new LightTypeManager<SpotLight>(spotLightCapacity, SpotUBOBaseID, SpotLight.DATASIZE_IN_BLOCKS, "SPOT_LIGHT_CAPACITY");
		shaders = new ArrayList<>();

		/*UBO = glGenBuffers();
		resizeBuffer();*/
	}

	/**
	 * adjusts capacity of UBO and keeps data in it
	 */
	/*private void resizeBuffer() {
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
	}*/

	/**
	 * calculates number of Bytes which are needed to store the light data capacity
	 *
	 * @return
	 */
	/*private int getBufferSize() {
		int blockCount = 0;

		blockCount += directionalLightCapacity * DirectionalLight.DATASIZE_IN_BLOCKS;
		blockCount += pointLightCapacity * PointLight.DATASIZE_IN_BLOCKS;
		blockCount += spotLightCapacity * SpotLight.DATASIZE_IN_BLOCKS;

		return blockCount * BYTES_PER_BLOCK;
	}*/

	/**
	 * saves the 3 buffer sizes in the beginning of the UBO
	 */
	/*private void saveBufferSizes() {
		final IntBuffer sizeBuffer = BufferUtils.createIntBuffer(12);

		sizeBuffer.put(directionalLights.size());
		sizeBuffer.put(pointLights.size());
		sizeBuffer.put(spotLights.size());
		sizeBuffer.flip();

		glBufferSubData(GL_UNIFORM_BUFFER, 0, sizeBuffer);
		GLErrors.checkForError(TAG, "glBufferSubData");
	}*/

	/*private void bufferData() {
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
	}*/

	/*private int getDirectionalLightBufferOffset() {
		return 0;
	}

	private int getPointLightBufferOffset() {
		return getDirectionalLightBufferOffset() + directionalLightCapacity * DirectionalLight.DATASIZE_IN_BLOCKS * 4;
	}

	private int getSpotLightBufferOffset() {
		return getPointLightBufferOffset() + pointLightCapacity * PointLight.DATASIZE_IN_BLOCKS * 4;
	}*/

	private void updateShaders() {
		directionalLightManager.updateShader(shaders);
		pointLightManager.updateShader(shaders);
		spotLightManager.updateShader(shaders);
		/*for (final Shader shader : shaders) {
			shader.updateParameter("DIRECTIONAL_LIGHT_CAPACITY", directionalLightCapacity, false);
			shader.updateParameter("POINT_LIGHT_CAPACITY", pointLightCapacity, false);
			shader.updateParameter("SPOT_LIGHT_CAPACITY", spotLightCapacity, true);
		}*/
	}

	public void addShader(final Shader shader) {
		shaders.add(shader);
		shader.addUniformBlockIndex(UBOManager.DIRECTIONALLIGHT);
		shader.addUniformBlockIndex(UBOManager.POINTLIGHT);
		shader.addUniformBlockIndex(UBOManager.SPOTLIGHT);

		directionalLightManager.updateShader(shader);
		pointLightManager.updateShader(shader);
		spotLightManager.updateShader(shader);

		/*shader.updateParameter("DIRECTIONAL_LIGHT_CAPACITY", directionalLightCapacity, false);
		shader.updateParameter("POINT_LIGHT_CAPACITY", pointLightCapacity, false);
		shader.updateParameter("SPOT_LIGHT_CAPACITY", spotLightCapacity, true);*/
	}

	private void removeShader(final Shader shader) {
		shaders.remove(shader);
	}

	/*private void addLight(final Light light, final ArrayList<Light> lightList, Integer listCapacity, final int offset) {
		if (listCapacity <= lightList.size()) {
			lightList.add(light);
			listCapacity *= 2;
			resizeBuffer();
		} else {
			//final int offset = getTotalDirectionalLightBufferOffset(directionalLights.size());
			lightList.add(light);
			updateSingleLightData(light, offset);
		}
		light.setClean();
	}*/

	public void addLight(final DirectionalLight light) {
		directionalLightManager.addLight(light);
		updateShaders();//addLight(light, directionalLights, directionalLightCapacity, getTotalDirectionalLightBufferOffset(directionalLights.size()));
	}

	/*private int getTotalDirectionalLightBufferOffset(final int lightIndex) {
		return (getDirectionalLightBufferOffset() + lightIndex * DirectionalLight.DATASIZE_IN_BLOCKS * 4) * 4;
	}*/

	public void addLight(final PointLight light) {
		pointLightManager.addLight(light);
		updateShaders();
		/*if (pointLightCapacity <= pointLights.size()) {
			pointLights.add(light);
			pointLightCapacity *= 2;
			resizeBuffer();
		} else {
			final int offset = getTotalPointLightBufferOffset(pointLights.size());
			pointLights.add(light);
			updateSingleLightData(light, offset);
		}
		light.setClean();*/
	}

	/*private int getTotalPointLightBufferOffset(final int lightIndex) {
		return (getPointLightBufferOffset() + lightIndex * PointLight.DATASIZE_IN_BLOCKS * 4) * 4;
	}*/

	public void addLight(final SpotLight light) {
		spotLightManager.addLight(light);
		updateShaders();
		/*if (spotLightCapacity <= spotLights.size()) {
			spotLights.add(light);
			spotLightCapacity *= 2;
			resizeBuffer();
		} else {
			final int offset = getTotalSpotLightBufferOffset(spotLights.size());
			spotLights.add(light);
			updateSingleLightData(light, offset);
		}
		light.setClean();*/
	}

	/*private int getTotalSpotLightBufferOffset(final int lightIndex) {
		return (getSpotLightBufferOffset() + lightIndex * SpotLight.DATASIZE_IN_BLOCKS * 4) * 4;
	}*/

	/**
	 * updates the UBO data on position offset to light's data
	 *
	 * @param light
	 *            the light that will be saved
	 * @param offset
	 *            the position that the light will have afterwards
	 */
	/*private void updateSingleLightData(final Light light, final int offset) {
		glBindBuffer(GL_UNIFORM_BUFFER, UBO);
		GLErrors.checkForError(TAG, "glBindBuffer");
		saveBufferSizes();
		final FloatBuffer lightBuffer = BufferUtils.createFloatBuffer(light.getBlockSize() * 4);
		lightBuffer.put(light.getData());
		lightBuffer.flip();
		glBufferSubData(GL_UNIFORM_BUFFER, offset + 16, lightBuffer);
		GLErrors.checkForError(TAG, "glBufferSubData");

		glBindBuffer(GL_UNIFORM_BUFFER, 0);
	}*/

	/**
	 * remove light and move last Light to the position of light
	 *
	 * @param light
	 *            the Light that will be removed
	 */
	/*private void removeLight(final Light light) {

	}*/

	/**
	 * checks if Lights were changed since last update and refreshes shader data
	 *
	 * @param deltaTime
	 */
	public void update(final double deltaTime) {
		directionalLightManager.update(deltaTime);
		pointLightManager.update(deltaTime);
		spotLightManager.update(deltaTime);
		//removeDeletedLights();
		//updateDirtyLights();
	}

	/**
	 * all Lights that are marked as deleted will be removed here
	 */
	/*private void removeDeletedLightsFromList(final List<Light> lightList) {
		for (int i = 0; i < lightList.size(); i++) {
			if (lightList.get(i).shouldBeRemoved()) {
				final Light swap = lightList.get(lightList.size() - 1);
				lightList.remove(lightList.size() - 1);
				lightList.set(i, swap);
				swap.setDirty();
				i--;
			}
		}
	}*/

	/**
	 * update data of changed Light objects
	 */
	/*private void updateDirtyLights() {
		final AtomicInteger lightIndex = new AtomicInteger(0);
		forEachLight((final Light light) -> {
			if (light.isDirty()) {
				updateLightData(light, lightIndex.get());
				light.setClean();
			}
			lightIndex.incrementAndGet();
		});
	}*/

	/*private void forEachLight(final Consumer<Light> action) {
		for (final DirectionalLight light : directionalLights) {
			action.accept(light);
		}
		for (final PointLight light : pointLights) {
			action.accept(light);
		}
		for (final SpotLight light : spotLights) {
			action.accept(light);
		}
	}*/

	/*private void updateLightData(final Light light, final int lightIndex) {
		final int offset = calculateOffsetFromIndex(lightIndex);
		updateSingleLightData(light, offset);
		light.setClean();
	}*/

	/*private int calculateOffsetFromIndex(int lightIndex) {
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
	}*/
}
