package mbeb.ld38.dungeon.room;

import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.materials.Material;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

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

	public static void initializeRoomTypes() {
		Material wallMaterial = new Material("material/stonewall/seam", 1);
		Material doorFrameMaterial = new Material("material/stonewall/purple", 1);
		Material floorMaterial = new Material("material/cobble/moss", 4);
		Material entranceMaterial = new Material("material/bunny", 4);
		Material exitMaterial = new Material("material/player", 2);

		//meshes
		final ObjectLoader loader = new ObjectLoader();
		IRenderable doorFrame = loader.loadFromFile("dungeon/general/door_frame.obj").withMaterial(doorFrameMaterial);
		IRenderable doorDoor = loader.loadFromFile("dungeon/general/door.obj").withMaterial(wallMaterial);
		IRenderable wall_segment = loader.loadFromFile("dungeon/general/wall_segment.obj").withMaterial(wallMaterial);
		IRenderable room_corner = loader.loadFromFile("dungeon/room/big_corner.obj").withMaterial(wallMaterial);
		IRenderable room_floor = loader.loadFromFile("dungeon/room/floor.obj").withMaterial(floorMaterial);
		CORNER = loader.loadFromFile("dungeon/general/origin_corner.obj").withMaterial(wallMaterial);
		SEGMENT = loader.loadFromFile("dungeon/general/axis_corner.obj").withMaterial(wallMaterial);

		IRenderable entrance = loader.loadFromFile("bunny.obj").withMaterial(entranceMaterial);
		IRenderable exit = loader.loadFromFile("player.fbx").withMaterial(exitMaterial);

		Door door = new Door(new SceneObject(doorFrame), new SceneObject(doorDoor), Door.Direction.RIGHT);

		SceneObject big_corner = new SceneObject(room_corner, new BoneTransformation(new Vector3f(0,0,0), new Quaternionf(new AxisAngle4f((float)Math.PI / -2, 0, 1, 0))));

		//<editor-fold desc="normal_room">
		NORMAL_ROOM = new RoomType();

		SceneObject base = new SceneObject();
		base.addSubObject(room_floor);
		base.addSubObject(big_corner);
		NORMAL_ROOM.addBaseObject(base);


		addDoor(NORMAL_ROOM, door, wall_segment);
		//</editor-fold>

		//<editor-fold desc="entrance_room">
		ENTRANCE_ROOM = new RoomType();

		base = new SceneObject();
		base.addSubObject(room_floor);
		base.addSubObject(big_corner);
		base.addSubObject(entrance);
		ENTRANCE_ROOM.addBaseObject(base);


		addDoor(ENTRANCE_ROOM, door, wall_segment);
		//</editor-fold>

		//<editor-fold desc="exit_room">
		EXIT_ROOM = new RoomType();

		base = new SceneObject();
		base.addSubObject(room_floor);
		base.addSubObject(big_corner);
		base.addSubObject(exit);
		EXIT_ROOM.addBaseObject(base);


		addDoor(EXIT_ROOM, door, wall_segment);
		//</editor-fold>
	}

	private static void addDoor(RoomType type, Door door, IRenderable wall_segment) {
		float rect = (float)Math.PI / 2;
		type.addSlot("right",
				new BoneTransformation(new Vector3f(4, 0, 0), new Quaternionf(new AxisAngle4f(2 * rect, 0, 1, 0)))
		)
				.addIf(new Door(door, Door.Direction.RIGHT),
						roomParameter -> roomParameter.get(RoomParameter.Type.RIGHT_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.RIGHT_NEIGHBOUR)
				);
		type.addSlot("left",
				new BoneTransformation(new Vector3f(-4, 0, 0), new Quaternionf(new AxisAngle4f(0 * rect, 0, 1, 0)))
		)
				.addIf(new Door(door, Door.Direction.LEFT),
						roomParameter -> roomParameter.get(RoomParameter.Type.LEFT_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.LEFT_NEIGHBOUR)
				);

		type.addSlot("bottom",
				new BoneTransformation(new Vector3f(0, 0, 4), new Quaternionf(new AxisAngle4f(1 * rect, 0, 1, 0)))
		)
				.addIf(new Door(door, Door.Direction.BOTTOM),
						roomParameter -> roomParameter.get(RoomParameter.Type.BOTTOM_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.BOTTOM_NEIGHBOUR)
				);
		type.addSlot("top",
				new BoneTransformation(new Vector3f(0, 0, -4), new Quaternionf(new AxisAngle4f(3 * rect, 0, 1, 0)))
		)
				.addIf(new Door(door, Door.Direction.TOP),
						roomParameter -> roomParameter.get(RoomParameter.Type.TOP_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.TOP_NEIGHBOUR)
				);
	}

	private Map<String, Slot> slots;

    private List<SceneObject> baseObjects;

	public RoomType() {
		baseObjects = new LinkedList<>();
		slots = new HashMap<>();
	}

	public Room construct(RoomParameter parameters) {
		Room result = new Room();
		result.addBaseObjects(this.baseObjects);
		for (Slot slot : slots.values()) {
			SceneObject add = slot.getApplicable(parameters);
			if(add != null) {
				if (add instanceof Door) {
					add = new Door((Door) add);
				}else {
					add = new SceneObject(add);
				}
				add.setTransformation(slot.getTransformation());
				result.addSlotObject(add);
			}
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
}
