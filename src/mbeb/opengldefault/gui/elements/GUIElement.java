package mbeb.opengldefault.gui.elements;

import java.awt.Color;
import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.gl.buffer.GLBufferWritable;
import mbeb.opengldefault.gl.buffer.GLBufferWriter;
import mbeb.opengldefault.gl.texture.Texture2D;
import mbeb.opengldefault.gui.GUI;
import mbeb.opengldefault.shapes.Rectangle;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * A GUI Element that gets rendered in a {@link GUI}
 *
 * @author Markus
 */
public abstract class GUIElement implements GLBufferWritable {

	/**
	 * the row in the lutTexture that is used for coloring this element
	 */
	private float lutRow;

	/**
	 * the lut texture
	 */
	private Texture2D lut;

	/**
	 * Bounding of this GUIElement
	 */
	private Rectangle bounding;

	/**
	 * Has data changed?
	 */
	private boolean dirty;

	public GUIElement(Vector2f position, Vector2f size, float lutRow, Texture2D lut) {
		bounding = new Rectangle(position, size);
		dirty = true;
		setLut(lut, lutRow);
	}

	public GUIElement(Vector2f position, Vector2f size) {
		this(position, size, 0, null);
	}

	public GUIElement(Vector2f size) {
		this(new Vector2f(), size);
	}

	public GUIElement() {
		this(new Vector2f(1));
	}

	/**
	 * Makes this GUIElement use the input Texture directly
	 */
	public void useTexture() {
		lut = null;
		setDirty();
	}

	/**
	 * Sets the position of the GUI Element relative to the screen
	 *
	 * @param position
	 *            float value tuple, normally in range [0, 1] for x and y position relative to screen
	 * @return this
	 */
	public GUIElement setPositionRelativeToScreen(Vector2f position) {
		return setPositionRelativeToScreen(position.x, position.y);
	}

	/**
	 * Sets the position of the GUI Element relative to the screen
	 *
	 * @param relativeX
	 *            float value normally in range [0, 1] for x position relative to screen.
	 * @param relativeY
	 *            float value normally in range [0, 1] for y position relative to screen.
	 * @return this
	 */
	public GUIElement setPositionRelativeToScreen(float relativeX, float relativeY) {
		return setPositionRelativeTo(new Vector2f(-1), new Vector2f(2), relativeX, relativeY);
	}

	/**
	 * Sets the position of the GUIElement relative to another GUI Element
	 *
	 * @param element
	 *            the parent GUIElement
	 * @param relativeX
	 *            float value normally in range [0, 1] for x position relative to parent.
	 * @param relativeY
	 *            float value normally in range [0, 1] for y position relative to parent.
	 * @return this
	 */
	public GUIElement setPositionRelativeTo(GUIElement element, float relativeX, float relativeY) {
		return setPositionRelativeTo(element.getPosition(), element.getSize(), relativeX, relativeY);
	}

	/**
	 * Sets the position of the GUIElement relative to a BoundingBox
	 *
	 * @param boundingStart
	 *            bounding box start
	 * @param boundingSize
	 *            bounding box size
	 * @param relativeX
	 *            float value normally in range [0, 1] for x position relative to a BoundingBox.
	 * @param relativeY
	 *            float value normally in range [0, 1] for y position relative to a BoundingBox.
	 * @return this
	 */
	public GUIElement setPositionRelativeTo(Vector2f boundingStart, Vector2f boundingSize, float relativeX,
			float relativeY) {
		return setPositionRelativeTo(new Rectangle(boundingStart, boundingSize), relativeX, relativeY);
	}

	/**
	 * Sets the position of the GUIElement relative to a BoundingBox
	 *
	 * @param bounding
	 *            bounding box to fit the GUIElement into
	 * @param relativeX
	 *            float value normally in range [0, 1] for x position relative to a BoundingBox.
	 * @param relativeY
	 *            float value normally in range [0, 1] for y position relative to a BoundingBox.
	 * @return this
	 */
	public GUIElement setPositionRelativeTo(Rectangle bounding, float relativeX, float relativeY) {
		this.bounding.setPositionRelativeTo(bounding, relativeX, relativeY);
		setDirty();
		return this;
	}

	/**
	 * Getter for the position of the GUIElement
	 *
	 * @return the position of the GUIElement
	 */
	public Vector2f getPosition() {
		return bounding.getPosition();
	}

	/**
	 * Setter for the position of the GUIElement
	 *
	 * @param position
	 *            the new position of the GUIElement
	 */
	public void setPosition(Vector2f position) {
		bounding.setPosition(position);
		setDirty();
	}

	/**
	 * Getter for the size of the GUIElement
	 *
	 * @return the size of the GUIElement
	 */
	public Vector2f getSize() {
		return bounding.getSize();
	}

	/**
	 * setter for the size of the GUIElement
	 *
	 * @param size
	 *            the new size of the GUIElement
	 */
	public void setSize(Vector2f size) {
		bounding.setSize(size);
		setDirty();
	}

	/**
	 * Calculates Model Matrix based on the {@link #getPosition()} and {@link #getSize()}
	 *
	 * @return
	 */
	protected Matrix4f getModelMatrix() {
		return new Matrix4f().
				translate(new Vector3f(getPosition().x, getPosition().y, 0)).
				scale(new Vector3f(getSize().x, getSize().y, 1));
	}

	@Override
	public void writeTo(GLBufferWriter writer) {
		writer
			.write(new Vector4f(lut != null ? 1.0f : 0.0f, getLutRow(), 0, 0))
			.write(getModelMatrix());
		setClean();
	}

	/**
	 * Updates the GUIElement
	 *
	 * @param deltaTime
	 */
	public abstract void update(double deltaTime);

	/**
	 * is the GUIElement dirty?
	 *
	 * @return true if dirty
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Sets the dirty parameter to true
	 */
	public void setDirty() {
		this.dirty = true;
	}

	/**
	 * Sets the dirty parameter to false
	 */
	public void setClean() {
		this.dirty = false;
	}

	/**
	 * Tests if the GUIElement contains a given point
	 *
	 * @param point
	 *            input point
	 * @return true if he point is within the GUIElement
	 */
	public boolean contains(Vector2f point) {
		return bounding.contains(point);
	}

	/**
	 * Tests if the GUIElement is selected, meaning that the mouse is hovering over it
	 *
	 * @return true if the GUIElement is selected
	 */
	public boolean selected() {
		return contains(Mouse.getNormalizedDeviceCoordinates());
	}

	/**
	 * Getter for the bounding of the GUIElement
	 *
	 * @return the bounding if the GUIElement
	 */
	public Rectangle getBounding() {
		return bounding;
	}

	/**
	 * Setter for the bounding of the GUIElement
	 *
	 * @param bounding
	 *            the new bounding of the GUIElement
	 */
	public void setBounding(Rectangle bounding) {
		this.bounding = bounding;
		setDirty();
	}

	/**
	 * Returns the number of GUIElements, that this GUIElement writes into the buffer
	 *
	 * @return the number of GUIElements
	 */
	public int getNumElements() {
		return 1;
	}

	/**
	 * Getter for the lut row
	 *
	 * @return the lutRow
	 */
	public float getLutRow() {
		return lutRow;
	}

	/**
	 * Setter for the lut row
	 *
	 * @return the new lutRow
	 */
	public void setLutRow(float lutRow) {
		this.lutRow = lutRow;
		setDirty();
	}

	/**
	 * Makes this GUIElement use a lut and sets the lut and the lutRow
	 *
	 * @param lut
	 *            the lut texture that will be used for this GUIElement
	 * @param lutRow
	 *            the lutRow in the lut that will be used
	 */
	public void setLut(Texture2D lut, float lutRow) {
		this.lut = lut;
		this.lutRow = lutRow;
		setDirty();
	}

	/**
	 * Sets a pixel color in the lut
	 *
	 * @param color
	 *            the Color to set the pixel to
	 * @param xPosition
	 *            the xPos of the pixel
	 */
	public void setColor(Color color, int xPosition) {
		getLut().setPixel(xPosition, (int) (255 * getLutRow()), color);
		setDirty();
	}

	/**
	 * Sets the last pixel in the GUIElements lutRow to a color. This is the color that will be used if the inputTexture
	 * is completely white
	 *
	 * @param color
	 *            the Color to set the pixel to
	 */
	public void setColor(Color color) {
		setColor(color, 255);
	}

	/**
	 * @return the lut Texture
	 */
	public Texture2D getLut() {
		return lut;
	}

	/**
	 * @param lut
	 *            the lut Texture to set
	 */
	public void setLut(Texture2D lut) {
		this.lut = lut;
	}
}
