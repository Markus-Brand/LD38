package mbeb.dungeon.room;


import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.behaviour.IBehaviour;
import mbeb.opengldefault.scene.entities.IEntity;
import mbeb.opengldefault.scene.entities.SceneEntity;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;

public class Door extends SceneObject{

	private static final float DOOR_SPEED = 0.5f;

	private class Behaviour implements IBehaviour {

		private float progress = 0.0f;
		private float speed = 0.0f;
		private Vector3f a, b;
		private SceneEntity entity;
		private BoneTransformation transformation;
		private boolean running = false;

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
	private boolean open = false;

	public Door(SceneObject frame, SceneObject door) {
		this.frame = frame;
		this.addSubObject(frame);
		this.door = door;
		this.addSubObject(door);
		SceneEntity e = new SceneEntity(door);
		movement = new Behaviour(new Vector3f(0,0,0), new Vector3f(0,-2,0), e);
		e.addBehaviour(1, movement);
	}

	public Door(Door door) {
		this(new SceneObject(door.frame), new SceneObject(door.door));
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
}
