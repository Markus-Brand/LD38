package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.scene.entities.Entity;

public abstract class Behaviour {

	public abstract boolean triggers(Entity entity);

	public abstract void update(double deltaTime, Entity entity);
}
