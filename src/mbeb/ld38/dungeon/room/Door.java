package mbeb.ld38.dungeon.room;


import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.behaviour.IBehaviour;
import mbeb.opengldefault.scene.entities.IEntity;
import mbeb.opengldefault.scene.entities.SceneEntity;
import org.joml.Vector3f;

import java.util.EnumMap;

public class Door extends SceneObject{

	public boolean isOpen() {
		return open;
	}

	public enum Direction {
		LEFT(-1,0),
		RIGHT(1,0),
		TOP(0,-1),
		BOTTOM(0,1);

		private static final EnumMap<Direction, Direction> opposites= new EnumMap<>(Direction.class);
		private int x, y;

		static {
			opposites.put(LEFT, RIGHT);
			opposites.put(RIGHT, LEFT);
			opposites.put(TOP, BOTTOM);
			opposites.put(BOTTOM, TOP);
		}

		Direction(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public Direction getOpposite() {
			return opposites.get(this);
		}
	}

	private static final float DOOR_SPEED = 0.5f;

	private class Behaviour implements IBehaviour {

		private float progress = 1.0f;
		private float speed = 0.01f;
		private Vector3f a, b;
		private SceneEntity entity;
		private BoneTransformation transformation;
		private boolean running = true;

		public Behaviour(Vector3f a, Vector3f b, SceneEntity entity) {
			this.a = a;
			this.b = b;
			this.entity = entity;
			transformation = new BoneTransformation(new Vector3f());
			entity.getSceneObject().setTransformation(transformation);
		}

		@Override
		public void update(double deltaTime, IEntity entity) {
			if (running) {
				progress += speed * deltaTime;
				if(progress <= 0.0f){
					progress = 0.0f;
					running = false;
				} else if(progress >= 1.0f){
					progress = 1.0f;
					running = false;
				}
				transformation.setPosition(a.lerp(b, progress, new Vector3f()));
				this.entity.getSceneObject().setTransformation(transformation);
			}
		}
	}

	private SceneObject frame;
	private SceneObject door;
	private Behaviour movement;
	private Direction direction;
	private boolean open = false;

	public Door(SceneObject frame, SceneObject door, Direction direction) {
		this.frame = frame;
		this.addSubObject(frame);
		this.door = door;
		this.addSubObject(door);
		this.direction = direction;
		SceneEntity e = new SceneEntity(door);
		movement = new Behaviour(new Vector3f(0,0,0), new Vector3f(0,-2,0), e);
		e.addBehaviour(1, movement);
	}

	public Door(Door door) {
		this(new SceneObject(door.frame), new SceneObject(door.door), door.direction);
	}

	public Door(Door door, Direction direction) {
		this(new SceneObject(door.frame), new SceneObject(door.door), direction);
	}

	public void open() {
		this.open = true;
		this.movement.speed = DOOR_SPEED;
		this.movement.running = true;
	}

	public void close() {
		this.open = false;
		this.movement.speed = -DOOR_SPEED;
		this.movement.running = true;
	}

	public void forceOpen() {
		this.open();
		this.movement.progress = 1.0f;
	}

	public void forceClose() {
		this.close();
		this.movement.progress = 0.0f;
	}

	@Override
	public void update(double deltaTime) {
		super.update(deltaTime);
		this.movement.entity.update(deltaTime);
	}

	public Direction getDirection() {
		return direction;
	}
}
