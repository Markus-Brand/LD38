package mbeb.opengldefault.scene.entities;

/**
 * An Object that can be represented as an entity
 */
public interface IEntityConvertable {
	/**
	 * create a new IEntity that represents this object
	 * @return a new IEntity
	 */
	IEntity asNewEntity();
	
	/**
	 * create an IEntity that represents this object.
	 * Don't necessarily create a new one if an old one is present.
	 * @return an IEntity
	 */
	default IEntity asEntity() {
		return asNewEntity();
	}
}
