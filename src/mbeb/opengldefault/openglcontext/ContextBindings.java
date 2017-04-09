package mbeb.opengldefault.openglcontext;

import java.util.*;

import mbeb.opengldefault.gl.buffer.GLBuffer;
import mbeb.opengldefault.gl.texture.Texture;

/**
 * Static storage for all objects currently bound to the context.
 *
 * @author Erik, Potti
 * @version 1.0
 */
public class ContextBindings {

	private ContextBindings() {
		//should never be instantiated
	}

	//SLOT FOR SHADER

	//SLOT FOR VAO

	//SLOT FOR FB

	//MAP FOR BUFFERS
	/**
	 * A map that saves which buffer is currently bound for each buffer type separately
	 */
	private static Map<GLBuffer.Type, GLBuffer> boundBuffers = new HashMap<>();

	//MAP & QUEUE FOR TEXTURES
	/**
	 * A map of currently bound textures and the texture units they are bound to.
	 * At any point the size of this map is equal to the number texture units in use.
	 */
	private static Map<Texture, Integer> boundTextureUnits = new HashMap<>(48, 1.0f);
	/**
	 * A queue of free texture units.
	 * If a texture is unbound from its current unit, the unit is added to this queue.
	 */
	private static Queue<Integer> freeTextureUnits = new LinkedList<>();
	/**
	 * The number of the currently active texture unit, to prevent unnecessary calls to glActiveTexture.
	 */
	private static Integer activeTextureUnit = null;

	//METHODS FOR SHADER

	//METHODS FOR VAO

	//METHODS FOR FB

	//METHODS FOR BUFFERS

	/**
	 * register, that a given buffer with given type is currently bound
	 * 
	 * @param buffer
	 *            the buffer that is bound
	 */
	public static void bind(GLBuffer buffer) {
		boundBuffers.put(buffer.getType(), buffer);
	}

	/**
	 * check, if a given buffer is bound
	 * 
	 * @param buffer
	 *            the buffer to check
	 * @return whether this buffer is currently bound for its type
	 */
	public static boolean isBound(GLBuffer buffer) {
		return Objects.equals(boundBuffers.get(buffer.getType()), buffer);
	}

	/**
	 * unbind the given Buffer Object if it was bound
	 * 
	 * @param buffer
	 *            the buffer to unbind
	 */
	public static void unbind(GLBuffer buffer) {
		boundBuffers.put(buffer.getType(), null);
	}

	//METHODS FOR TEXTURES

	/**
	 * @param texture
	 *            the texture to check for
	 * @return the texture unit that texture is bound to or null if not bound
	 */
	public static Integer getTextureUnit(Texture texture) {
		return boundTextureUnits.get(texture);
	}

	/**
	 * @param texture
	 *            the texture to check for
	 * @return whether that texture is currently bound
	 */
	public static boolean isBound(Texture texture) {
		return getTextureUnit(texture) != null;
	}

	/**
	 * Finds the number of the next free texture unit and binds the given texture to it.
	 * 
	 * @return the number of the used texture unit
	 */
	public static Integer bind(Texture texture) {
		Integer textureUnit = freeTextureUnits.poll();
		if (textureUnit == null) {
			//allocate next free texture unit
			textureUnit = boundTextureUnits.size();
		}
		boundTextureUnits.put(texture, textureUnit);
		return textureUnit;
	}

	/**
	 * Removes the existing binding of the given texture an marks its texture unit as free.
	 * 
	 * @param texture
	 *            the texture to unbind
	 * @return whether the texture was unbound
	 */
	public static boolean unbind(Texture texture) {
		if (isBound(texture)) {
			Integer textureUnit = boundTextureUnits.remove(texture);
			freeTextureUnits.add(textureUnit);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return the currently active texture unit
	 */
	public static Integer getActiveTextureUnit() {
		return activeTextureUnit;
	}

	/**
	 * @param unit
	 *            the new active texture unit
	 * @return whether the texture unit was changed
	 */
	public static boolean setActiveTextureUnit(Integer unit) {
		boolean same = activeTextureUnit != null && activeTextureUnit.equals(unit);
		activeTextureUnit = unit;
		return !same;
	}
}
