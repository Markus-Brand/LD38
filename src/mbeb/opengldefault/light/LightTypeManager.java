package mbeb.opengldefault.light;

import static org.lwjgl.opengl.GL15.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import mbeb.opengldefault.constants.Constants;
import mbeb.opengldefault.gl.buffer.GLBufferWriter;
import mbeb.opengldefault.gl.buffer.UniformBuffer;

import mbeb.opengldefault.rendering.shader.*;

/**
 * I'm an abstract class blueprint used for classes managing lights of one type (e.g. directional, point or spot lights) and there different parameters (e.g. UBOID, capacity, storage size...).
 * I'm encapsulating all basic functions probably useful for a concrete LightTypeManager.
 *
 * @author Merlin (and Erik and Markus but if something is wrong blame him and only him) :D
 */
public abstract class LightTypeManager {
	/** Class Name Tag */
	private static final String TAG = "LightTypeManager";

	/** number of Blocks needed to store one Object of the managed LightType */
	protected int lightBlockSize;
	/** name of the LightType Parameter for the managed LightType */
	protected String shaderLightTypeParameterName;
	/** maximal number of Lights that can be stored without resizing the UBO */
	protected int lightCapacity;
	/** all managed lights */
	private final ArrayList<Light> lights;
	/** UBO identifier */
	private final UniformBuffer UBO;

	/**
	 * @param shaderLightTypeParameterName
	 * @param lightBlockSize
	 * @param UBO
	 * @param lightCapacity
	 */
	public LightTypeManager(final String shaderLightTypeParameterName, final int lightBlockSize, final UniformBuffer UBO, final int lightCapacity) {
		this.shaderLightTypeParameterName = shaderLightTypeParameterName;
		this.lightBlockSize = lightBlockSize;
		this.lightCapacity = lightCapacity;
		this.lights = new ArrayList<>();
		this.UBO = UBO;
		resizeBuffer();
	}

	/**
	 * adjusts capacity of UBO and keeps it's data up to date
	 */
	private void resizeBuffer() {
		int bufferSize = getBufferSize() + Constants.BLOCK_SIZE;
		
		UBO.bind();
		
		UBO.bufferData(bufferSize, GL_STATIC_DRAW);
		UBO.bindBufferBase();
		
		GLBufferWriter combinedWriter = UBO.writer(bufferSize);

		saveBufferSize(combinedWriter);
		bufferData(combinedWriter);

		combinedWriter.flush();
		
		UBO.unbind();
	}

	/**
	 * @return number of bytes which are needed to store the light capacity
	 */
	private int getBufferSize() {
		return lightCapacity * lightBlockSize * Constants.BLOCK_SIZE;
	}
	
	/**
	 * stores the buffer size at the beginning of the UBO
	 */
	private void saveBufferSize() {
		saveBufferSize(UBO.writer(Constants.INT_SIZE)).flush();
	}
	
	/**
	 * stores the buffer size at the beginning of the UBO
	 */
	private GLBufferWriter saveBufferSize(GLBufferWriter writer) {
		writer.write(lights.size());
		return writer;
	}

	/**
	 * stores the data for each light in the UBO
	 */
	private void bufferData(GLBufferWriter writer) {
		lights.forEach(writer::write);
	}

	/**
	 * updates the <i>TYPE</i>_LIGHT_CAPACITY Parameter of the given shader
	 *
	 * @param shader
	 *            that will be updated
	 */
	public void updateShader(final ShaderProgram shader) {
		shader.addUniformBlockIndex(UBO);
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
			saveBufferSize();
		}
		light.setClean();
	}

	/**
	 * @param lightIndex
	 *            index of a light in the java side storage
	 * @return offset in the Buffer for the graphics card
	 */
	private int getTotalBufferOffset(final int lightIndex) {
		return lightIndex * lightBlockSize * Constants.BLOCK_SIZE;
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
		final int bufferSizeStorageSpace = Constants.BLOCK_SIZE; //one block reserved space for this at the beginning of the UBO
		UBO.writer(lightBlockSize * Constants.BLOCK_SIZE, bufferSizeStorageSpace + offset)
				.write(light).flush();
	}

	/**
	 * calling this removes lights witch are super markt for deletion
	 * and updates lights witch are super markt as dirty
	 *
	 * @param deltaTime
	 */
	public void update(final double deltaTime) {
		removeDeletedLightsFromList();
		updateDirtyLights();
	}

	/**
	 * updates <i>light</i>'s data in the UBO and cleanses it afterwards
	 *
	 * @param light
	 *            witch data shall be updated
	 * @param lightIndex
	 *            at witch the light is stored in the UBO
	 */
	private void updateLightData(final Light light, final int lightIndex) {
		final int offset = getTotalBufferOffset(lightIndex);
		updateSingleLightData(light, offset);
		light.setClean();
	}

	/**
	 * updates data of all changed ("aka dirty") Light objects
	 */
	private void updateDirtyLights() {
		final AtomicInteger lightIndex = new AtomicInteger(0);
		lights.forEach((final Light light) -> {
			if (light.isDirty()) {
				updateLightData(light, lightIndex.get());
			}
			lightIndex.incrementAndGet();
		});
	}

	/**
	 * all Lights that are marked as deleted will be removed here
	 */
	private void removeDeletedLightsFromList() {
		boolean sizeDecreased = false;
		for (int i = 0; i < lights.size(); i++) {
			if (lights.get(i).shouldBeRemoved()) {
				if (i == lights.size() - 1) {
					lights.remove(i);
					sizeDecreased = true;
					saveBufferSize();
					break;
				}
				final Light swap = lights.get(lights.size() - 1);
				lights.remove(lights.size() - 1);
				lights.set(i, swap);
				swap.setDirty();
				i--;
			}
		}
		if (sizeDecreased) {
			saveBufferSize();
		}
	}
}
