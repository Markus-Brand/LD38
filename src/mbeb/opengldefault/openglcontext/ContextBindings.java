package mbeb.opengldefault.openglcontext;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import mbeb.opengldefault.gl.texture.Texture;

/**
 * Static storage for all objects currently bound to the context.
 *
 * @author Erik, Potti
 * @version 1.0
 */
public class ContextBindings {

	//SLOT FOR SHADER

	//SLOT FOR VAO

	//SLOT FOR FB

	//MAP FOR BUFFERS

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

	//METHODS FOR SHADER

	//METHODS FOR VAO

	//METHODS FOR FB

	//METHODS FOR BUFFERS

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
			textureUnit = boundTextureUnits.size() + 1;
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

}
