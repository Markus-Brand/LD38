package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.scene.entities.EntityWorld;
import mbeb.opengldefault.scene.entities.IEntity;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

/**
 * Calculate forces to all entities in the specified world
 */
public class GravitationBehaviour implements IBehaviour {

	/** the collection of entities to attract */
	private EntityWorld world;
	/** how many percent of speed to loose each second */
	private float friction;
	/** a multiplier on the attraction force */
	private float forceStrength;

	/** the current speed for each entity that uses this behaviour */
	private Map<IEntity, Vector3f> speeds = new HashMap<>();

	/**
	 * let an entity be attracted to all entities inside a world
	 * @param world the collection of entities that attract
	 * @param friction how strong to loose speed over time
	 * @param forceStrength a multiplier on the force
	 */
	public GravitationBehaviour(EntityWorld world, float friction, float forceStrength) {
		this.world = world;
		this.friction = friction;
		this.forceStrength = forceStrength;
	}

	@Override
	public void update(double deltaTime, IEntity entity) {
		float deltaTimef = (float) deltaTime;
		Vector3f currentPosition = entity.getPosition();

		//accumulate all the forces together
		Vector3f force = new Vector3f();
		world.forEachEntity((IEntity other) -> {
			if (other != entity) {
				Vector3f localForce = other.getPosition().sub(currentPosition, new Vector3f());
				float distance = localForce.length();
				if (distance != 0) {
					//attenuate the force based on the distance
					localForce.normalize();
					localForce.div(Math.max(distance * distance, 1));
					force.add(localForce);
				}
			}
		});
		//apply modifier
		force.mul(forceStrength);

		//calculate the new speed: old speed applied to friction and the force added
		Vector3f speed = getSpeed(entity).mul(1 - (friction * deltaTimef)).add(force.mul(deltaTimef));

		//apply current entity speed to its location / direction
		entity.setDirection(new Vector3f(speed).normalize());
		entity.setPosition(currentPosition.add(speed.mul(deltaTimef, new Vector3f())));

		speeds.put(entity, speed);
	}

	private Vector3f getSpeed(IEntity entity) {
		return speeds.computeIfAbsent(entity, e -> new Vector3f());
	}
}
