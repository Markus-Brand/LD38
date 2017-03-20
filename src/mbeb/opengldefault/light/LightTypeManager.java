package mbeb.opengldefault.light;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

import java.nio.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import mbeb.opengldefault.constants.Constants;
import org.lwjgl.*;

import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.rendering.shader.*;

/**
 * I'm an abstract class blueprint used for classes managing lights of one type (e.g. directional, point or spot lights) and there different parameters (e.g. UBOID, capacity, storage size...).
 * I'm encapsuling all basic functions probably useful for a concrete LightTypeManager.
 *
 * @author Merlin (and Erik and Markus but if something is wrong blame him and only him) :D
 */
public abstract class LightTypeManager {
	/** Class Name Tag */
	private static final String TAG = "LightTypeManager";

	/** stores UBO Name */
	protected String UBOBaseName;
	/** stores UBO identifier */
	protected int UBOBaseID;
	/** number of Blocks needed to store one Object of the managed LightType */
	protected int lightBlockSize;
	/** name of the LightType Parameter for the managed LightType */
	protected String shaderLightTypeParameterName;
	/** maximal number of Lights that can be stored without resizing the UBO */
	protected int lightCapacity;
	/** all managed lights */
	private final ArrayList<Light> lights;
	/** UBO identifier */
	private final int UBO;

	/**
	 * @param shaderLightTypeParameterName
	 * @param lightBlockSize
	 * @param UBOBaseName
	 * @param lightCapacity
	 */
	public LightTypeManager(final String shaderLightTypeParameterName, final int lightBlockSize, final String UBOBaseName, final int lightCapacity) {
		this.shaderLightTypeParameterName = shaderLightTypeParameterName;
		this.lightBlockSize = lightBlockSize;
		this.UBOBaseName = UBOBaseName;
		this.lightCapacity = lightCapacity;
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

		glBufferData(GL_UNIFORM_BUFFER, getBufferSize() + Constants.BLOCK_SIZE, GL_STATIC_DRAW);
		GLErrors.checkForError(TAG, "glBufferData");

		glBindBufferBase(GL_UNIFORM_BUFFER, UBOBaseID, UBO);
		GLErrors.checkForError(TAG, "glBindBufferBase");

		saveBufferSize();
		bufferData();

		glBindBuffer(GL_UNIFORM_BUFFER, 0);
		GLErrors.checkForError(TAG, "glBindBuffer");
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
		glBufferSubData(GL_UNIFORM_BUFFER, Constants.BLOCK_SIZE, dataBuffer);
		GLErrors.checkForError(TAG, "glBufferSubData");
	}

	/**
	 * updates the <i>TYPE</i>_LIGHT_CAPACITY Parameter of the given shader
	 *
	 * @param shader
	 *            that will be updated
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
		glBindBuffer(GL_UNIFORM_BUFFER, UBO);
		GLErrors.checkForError(TAG, "glBindBuffer");
		final FloatBuffer lightBuffer = BufferUtils.createFloatBuffer(lightBlockSize * 4);
		lightBuffer.put(light.getData());
		lightBuffer.flip();

		final int bufferSizeStorageSpace = Constants.BLOCK_SIZE; //one block reserved space for this at the beginning of the UBO
		glBufferSubData(GL_UNIFORM_BUFFER, bufferSizeStorageSpace + offset, lightBuffer);
		GLErrors.checkForError(TAG, "glBufferSubData");

		glBindBuffer(GL_UNIFORM_BUFFER, 0);
		GLErrors.checkForError(TAG, "glBindBuffer");
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
