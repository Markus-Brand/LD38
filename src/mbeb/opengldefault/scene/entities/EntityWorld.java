package mbeb.opengldefault.scene.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * A Collection of entities that can interact with each other
 */
public class EntityWorld {
	
	/**
	 * all the entities in this world
	 */
	private Collection<IEntity> entities;

	public EntityWorld() {
		entities = new ArrayList<>();
	}
	
	/**
	 * add a new entity to this world
	 * @param newEntity the object that can be represented as Entity
	 * @return the actual IEntity that got added
	 */
	public IEntity add(IEntityConvertable newEntity) {
		IEntity entity = newEntity.asEntity();
		entities.add(entity);
		return entity;
	}
	
	/**
	 * iterate through all the entities of this world
	 * @param action the action to perform for each entity
	 */
	public void forEachEntity(Consumer<IEntity> action) {
		entities.forEach(action);
	}
	
	/**
	 * update this world and all entities in it
	 * @param deltaTime
	 */
	public void update(double deltaTime) {
		forEachEntity(entity -> entity.update(deltaTime));
	}

	/**
	 * @return the number of entities in this world
	 */
	public int entityCount() {
		return entities.size();
	}
}
