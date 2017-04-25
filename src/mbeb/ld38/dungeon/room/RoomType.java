package mbeb.ld38.dungeon.room;

import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.light.LightManager;
import mbeb.opengldefault.light.PointLight;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.materials.Material;
import mbeb.opengldefault.sound.SoundEnvironment;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RoomType {

	private static RoomType NORMAL_ROOM;
	private static RoomType ENTRANCE_ROOM;
	private static RoomType EXIT_ROOM;
	private static IRenderable CORNER;
	private static IRenderable SEGMENT;

	public static RoomType getNormalRoom() {
		return NORMAL_ROOM;
	}

	public static RoomType getEntranceRoom() {
		return ENTRANCE_ROOM;
	}

	public static RoomType getExitRoom() {
		return EXIT_ROOM;
	}

	public static IRenderable getCORNER() {
		return CORNER;
	}

	public static IRenderable getSEGMENT() {
		return SEGMENT;
	}

	public static void initializeRoomTypes(SoundEnvironment soundEnvironment) {
		Material wallMaterial = new Material("material/stonewall/seam", 1);
		Material doorFrameMaterial = new Material("material/stonewall/purple", 1);
		Material floorMaterial = new Material("material/cobble/moss", 4);
		Material entranceMaterial = new Material("material/carpet", 1);
		Material exitMaterial = new Material("material/chest", 2);

		//meshes
		final ObjectLoader loader = new ObjectLoader();
		IRenderable doorFrame = loader.loadFromFile("dungeon/general/door_frame.obj").withMaterial(doorFrameMaterial);
		IRenderable doorDoor = loader.loadFromFile("dungeon/general/door.obj").withMaterial(wallMaterial);
		IRenderable wall_segment = loader.loadFromFile("dungeon/general/wall_segment.obj").withMaterial(wallMaterial);
		IRenderable room_corner = loader.loadFromFile("dungeon/room/big_corner.obj").withMaterial(wallMaterial);
		IRenderable room_floor = loader.loadFromFile("dungeon/room/floor.obj").withMaterial(floorMaterial);
		CORNER = loader.loadFromFile("dungeon/general/origin_corner.obj").withMaterial(wallMaterial);
		SEGMENT = loader.loadFromFile("dungeon/general/axis_corner.obj").withMaterial(wallMaterial);

		IRenderable entrance = loader.loadFromFile("dungeon/carpet.obj").withMaterial(entranceMaterial);
		IRenderable exit = loader.loadFromFile("ladder.obj").withMaterial(exitMaterial);
		SceneObject exitObject = new SceneObject(exit);
		exitObject.setVisible(false);

		Door door =
				new Door(new SceneObject(doorFrame), new SceneObject(doorDoor), Door.Direction.RIGHT, soundEnvironment);

		SceneObject big_corner =
				new SceneObject(room_corner, new BoneTransformation(new Vector3f(0, 0, 0), new Quaternionf(
						new AxisAngle4f((float) Math.PI / -2, 0, 1, 0))));

		//<editor-fold desc="normal_room">
		NORMAL_ROOM = new RoomType();

		SceneObject base = new SceneObject();
		base.addSubObject(room_floor);
		base.addSubObject(big_corner);
		NORMAL_ROOM.addBaseObject(base);

		addDoor(NORMAL_ROOM, door, wall_segment, soundEnvironment);
		//</editor-fold>

		//<editor-fold desc="entrance_room">
		ENTRANCE_ROOM = new RoomType();

		base = new SceneObject();
		base.addSubObject(room_floor);
		base.addSubObject(big_corner);
		base.addSubObject(entrance);
		ENTRANCE_ROOM.addBaseObject(base);

		//ENTRANCE_ROOM.addLight(new Vector3f(0, 3, 0), Color.WHITE, 20);

		addDoor(ENTRANCE_ROOM, door, wall_segment, soundEnvironment);
		//</editor-fold>

		//<editor-fold desc="exit_room">
		EXIT_ROOM = new RoomType();

		base = new SceneObject();
		base.addSubObject(room_floor);
		base.addSubObject(big_corner);
		EXIT_ROOM.addBaseObject(base);

		EXIT_ROOM.addSlot(
				"exit", new BoneTransformation(null, null)).
				addIf(exitObject, roomParameter -> true);

		addDoor(EXIT_ROOM, door, wall_segment, soundEnvironment);
		//</editor-fold>
	}

	private static void addDoor(RoomType type, Door door, IRenderable wall_segment, SoundEnvironment soundEnvironment) {
		float rect = (float) Math.PI / 2;

		//type.addLight(new Vector3f(0, 1.9f, 0),new Color(12, 255, 0), 20f);

		type.addSlot("right",
				new BoneTransformation(new Vector3f(4, 0, 0), new Quaternionf(new AxisAngle4f(2 * rect, 0, 1, 0)))
				)
				.addIf(new Door(door, Door.Direction.RIGHT, soundEnvironment),
						roomParameter -> roomParameter.get(RoomParameter.Type.RIGHT_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.RIGHT_NEIGHBOUR)
				);
		type.addSlot("left",
				new BoneTransformation(new Vector3f(-4, 0, 0), new Quaternionf(new AxisAngle4f(0 * rect, 0, 1, 0)))
				)
				.addIf(new Door(door, Door.Direction.LEFT, soundEnvironment),
						roomParameter -> roomParameter.get(RoomParameter.Type.LEFT_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.LEFT_NEIGHBOUR)
				);

		type.addSlot("bottom",
				new BoneTransformation(new Vector3f(0, 0, 4), new Quaternionf(new AxisAngle4f(1 * rect, 0, 1, 0)))
				)
				.addIf(new Door(door, Door.Direction.BOTTOM, soundEnvironment),
						roomParameter -> roomParameter.get(RoomParameter.Type.BOTTOM_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.BOTTOM_NEIGHBOUR)
				);
		type.addSlot("top",
				new BoneTransformation(new Vector3f(0, 0, -4), new Quaternionf(new AxisAngle4f(3 * rect, 0, 1, 0)))
				)
				.addIf(new Door(door, Door.Direction.TOP, soundEnvironment),
						roomParameter -> roomParameter.get(RoomParameter.Type.TOP_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.TOP_NEIGHBOUR)
				);
	}

	public class LightPlacement {
		private Vector3f position;
		private Color color;
		private float reach;

		public LightPlacement(Vector3f position, Color color, float reach) {
			this.position = position;
			this.color = color;
			this.reach = reach;
		}

		public Vector3f getPosition() {
			return position;
		}

		public Color getColor() {
			return color;
		}

		public float getReach() {
			return reach;
		}
	}

	private Map<String, Slot> slots;

	private List<SceneObject> baseObjects;

	private List<LightPlacement> lights;

	public RoomType() {
		baseObjects = new LinkedList<>();
		slots = new HashMap<>();
		lights = new LinkedList<>();
	}

	public Room construct(RoomParameter parameters, LightManager manager, Vector3f position,
			SoundEnvironment soundEnvironment) {
		Room result = new Room();
		result.addBaseObjects(this.baseObjects);
		for (Map.Entry<String, Slot> slot : slots.entrySet()) {

			SceneObject add = slot.getValue().getApplicable(parameters);
			if (add != null) {
				if (add instanceof Door) {
					add = new Door((Door) add, soundEnvironment);
				} else {
					add = new SceneObject(add);
				}
				add.setTransformation(slot.getValue().getTransformation());
				result.addSlotObject(slot.getKey(), add);
			}
		}

		for (LightPlacement light : lights) {
			manager.addLight(new PointLight(light.getColor(), position.add(light.getPosition(), new Vector3f()), light
					.getReach()));
		}

		return result;
	}

	public Slot getSlot(String key) {
		return slots.get(key);
	}

	public Slot addSlot(String key, BoneTransformation transform) {
		Slot a = new Slot(transform);
		slots.put(key, a);
		return a;
	}

	public Slot removeSlot(String key) {
		return slots.remove(key);
	}

	public boolean addBaseObject(SceneObject sceneObject) {
		return baseObjects.add(sceneObject);
	}

	public void addLight(Vector3f position, Color color, float reach) {
		this.lights.add(new LightPlacement(position, color, reach));
	}
}
