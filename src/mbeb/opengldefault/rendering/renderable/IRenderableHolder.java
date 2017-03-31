package mbeb.opengldefault.rendering.renderable;

/**
 * An Object that contains an IRenderable and is willing to share it
 */
public interface IRenderableHolder {
	IRenderable getRenderable();
}
