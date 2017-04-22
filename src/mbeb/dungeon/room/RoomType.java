package mbeb.dungeon.room;

import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.materials.Material;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RoomType {

	private static RoomType NORMAL_ROOM;
	private static RoomType ENTRANCE_ROOM;
	private static RoomType EXIT_ROOM;
	private static RoomType NORMAL_CORRIDOR;

	public static RoomType getNormalRoom() {
		return NORMAL_ROOM;
	}

	public static RoomType getNormalCorridor() {
		return NORMAL_CORRIDOR;
	}

	public static RoomType getEntranceRoom() {
		return ENTRANCE_ROOM;
	}

	public static RoomType getExitRoom() {
		return EXIT_ROOM;
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
		IRenderable room_corner = loader.loadFromFile("dungeon/room/corner.obj").withMaterial(wallMaterial);
		IRenderable corridor_corner = loader.loadFromFile("dungeon/corridor/corner.obj").withMaterial(wallMaterial);
		IRenderable room_floor = loader.loadFromFile("dungeon/room/floor.obj").withMaterial(floorMaterial);
		IRenderable corridor_floor_center = loader.loadFromFile("dungeon/corridor/floor/center.obj").withMaterial(floorMaterial);
		IRenderable corridor_floor_right = loader.loadFromFile("dungeon/corridor/floor/+x.obj").withMaterial(floorMaterial);
		IRenderable corridor_floor_left = loader.loadFromFile("dungeon/corridor/floor/-x.obj").withMaterial(floorMaterial);
		IRenderable corridor_floor_bottom = loader.loadFromFile("dungeon/corridor/floor/+y.obj").withMaterial(floorMaterial);
		IRenderable corridor_floor_top = loader.loadFromFile("dungeon/corridor/floor/-y.obj").withMaterial(floorMaterial);
		IRenderable entrance = loader.loadFromFile("bunny.obj").withMaterial(entranceMaterial);
		IRenderable exit = loader.loadFromFile("player.fbx").withMaterial(exitMaterial);

		SceneObject door = new SceneObject();
		door.addSubObject(doorFrame);
		//door.addSubObject(doorDoor);

		//<editor-fold desc="normal_room">
		NORMAL_ROOM = new RoomType();

		SceneObject base = new SceneObject();
		float rect = (float)Math.PI / 2;
		base.addSubObject(room_floor);
		base.addSubObject(new SceneObject(room_corner, new BoneTransformation(new Vector3f(-4, 0, 4), new Quaternionf(new AxisAngle4f(0, 0, 1, 0)))));
		base.addSubObject(new SceneObject(room_corner, new BoneTransformation(new Vector3f(4, 0, 4), new Quaternionf(new AxisAngle4f(1 * rect, 0, 1, 0)))));
		base.addSubObject(new SceneObject(room_corner, new BoneTransformation(new Vector3f(4, 0, -4), new Quaternionf(new AxisAngle4f(2 * rect, 0, 1, 0)))));
		base.addSubObject(new SceneObject(room_corner, new BoneTransformation(new Vector3f(-4, 0, -4), new Quaternionf(new AxisAngle4f(3 * rect, 0, 1, 0)))));
		NORMAL_ROOM.addBaseObject(base);


		NORMAL_ROOM.addSlot("right",
				new BoneTransformation(new Vector3f(4, 0, 0), new Quaternionf(new AxisAngle4f(2 * rect, 0, 1, 0)))
		)
				.addIf(new SceneObject(door),
						roomParameter -> roomParameter.get(RoomParameter.Type.RIGHT_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.RIGHT_NEIGHBOUR)
				);
		NORMAL_ROOM.addSlot("left",
				new BoneTransformation(new Vector3f(-4, 0, 0), new Quaternionf(new AxisAngle4f(0 * rect, 0, 1, 0)))
		)
				.addIf(new SceneObject(door),
						roomParameter -> roomParameter.get(RoomParameter.Type.LEFT_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.LEFT_NEIGHBOUR)
				);

		NORMAL_ROOM.addSlot("bottom",
				new BoneTransformation(new Vector3f(0, 0, 4), new Quaternionf(new AxisAngle4f(1 * rect, 0, 1, 0)))
		)
				.addIf(new SceneObject(door),
						roomParameter -> roomParameter.get(RoomParameter.Type.BOTTOM_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.BOTTOM_NEIGHBOUR)
				);
		NORMAL_ROOM.addSlot("top",
				new BoneTransformation(new Vector3f(0, 0, -4), new Quaternionf(new AxisAngle4f(3 * rect, 0, 1, 0)))
		)
				.addIf(new SceneObject(door),
						roomParameter -> roomParameter.get(RoomParameter.Type.TOP_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.TOP_NEIGHBOUR)
				);
		//</editor-fold>

		//<editor-fold desc="entrance_room">
		ENTRANCE_ROOM = new RoomType();

		base = new SceneObject();
		base.addSubObject(room_floor);
		base.addSubObject(new SceneObject(room_corner, new BoneTransformation(new Vector3f(-4, 0, 4), new Quaternionf(new AxisAngle4f(0, 0, 1, 0)))));
		base.addSubObject(new SceneObject(room_corner, new BoneTransformation(new Vector3f(4, 0, 4), new Quaternionf(new AxisAngle4f(1 * rect, 0, 1, 0)))));
		base.addSubObject(new SceneObject(room_corner, new BoneTransformation(new Vector3f(4, 0, -4), new Quaternionf(new AxisAngle4f(2 * rect, 0, 1, 0)))));
		base.addSubObject(new SceneObject(room_corner, new BoneTransformation(new Vector3f(-4, 0, -4), new Quaternionf(new AxisAngle4f(3 * rect, 0, 1, 0)))));
		base.addSubObject(entrance);
		ENTRANCE_ROOM.addBaseObject(base);


		ENTRANCE_ROOM.addSlot("right",
				new BoneTransformation(new Vector3f(4, 0, 0), new Quaternionf(new AxisAngle4f(2 * rect, 0, 1, 0)))
		)
				.addIf(new SceneObject(door),
						roomParameter -> roomParameter.get(RoomParameter.Type.RIGHT_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.RIGHT_NEIGHBOUR)
				);
		ENTRANCE_ROOM.addSlot("left",
				new BoneTransformation(new Vector3f(-4, 0, 0), new Quaternionf(new AxisAngle4f(0 * rect, 0, 1, 0)))
		)
				.addIf(new SceneObject(door),
						roomParameter -> roomParameter.get(RoomParameter.Type.LEFT_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.LEFT_NEIGHBOUR)
				);

		ENTRANCE_ROOM.addSlot("bottom",
				new BoneTransformation(new Vector3f(0, 0, 4), new Quaternionf(new AxisAngle4f(1 * rect, 0, 1, 0)))
		)
				.addIf(new SceneObject(door),
						roomParameter -> roomParameter.get(RoomParameter.Type.BOTTOM_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.BOTTOM_NEIGHBOUR)
				);
		ENTRANCE_ROOM.addSlot("top",
				new BoneTransformation(new Vector3f(0, 0, -4), new Quaternionf(new AxisAngle4f(3 * rect, 0, 1, 0)))
		)
				.addIf(new SceneObject(door),
						roomParameter -> roomParameter.get(RoomParameter.Type.TOP_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.TOP_NEIGHBOUR)
				);
		//</editor-fold>

		//<editor-fold desc="exit_room">
		EXIT_ROOM = new RoomType();

		base = new SceneObject();
		base.addSubObject(room_floor);
		base.addSubObject(new SceneObject(room_corner, new BoneTransformation(new Vector3f(-4, 0, 4), new Quaternionf(new AxisAngle4f(0, 0, 1, 0)))));
		base.addSubObject(new SceneObject(room_corner, new BoneTransformation(new Vector3f(4, 0, 4), new Quaternionf(new AxisAngle4f(1 * rect, 0, 1, 0)))));
		base.addSubObject(new SceneObject(room_corner, new BoneTransformation(new Vector3f(4, 0, -4), new Quaternionf(new AxisAngle4f(2 * rect, 0, 1, 0)))));
		base.addSubObject(new SceneObject(room_corner, new BoneTransformation(new Vector3f(-4, 0, -4), new Quaternionf(new AxisAngle4f(3 * rect, 0, 1, 0)))));
		base.addSubObject(exit);
		EXIT_ROOM.addBaseObject(base);


		EXIT_ROOM.addSlot("right",
				new BoneTransformation(new Vector3f(4, 0, 0), new Quaternionf(new AxisAngle4f(2 * rect, 0, 1, 0)))
		)
				.addIf(new SceneObject(door),
						roomParameter -> roomParameter.get(RoomParameter.Type.RIGHT_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.RIGHT_NEIGHBOUR)
				);
		EXIT_ROOM.addSlot("left",
				new BoneTransformation(new Vector3f(-4, 0, 0), new Quaternionf(new AxisAngle4f(0 * rect, 0, 1, 0)))
		)
				.addIf(new SceneObject(door),
						roomParameter -> roomParameter.get(RoomParameter.Type.LEFT_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.LEFT_NEIGHBOUR)
				);

		EXIT_ROOM.addSlot("bottom",
				new BoneTransformation(new Vector3f(0, 0, 4), new Quaternionf(new AxisAngle4f(1 * rect, 0, 1, 0)))
		)
				.addIf(new SceneObject(door),
						roomParameter -> roomParameter.get(RoomParameter.Type.BOTTOM_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.BOTTOM_NEIGHBOUR)
				);
		EXIT_ROOM.addSlot("top",
				new BoneTransformation(new Vector3f(0, 0, -4), new Quaternionf(new AxisAngle4f(3 * rect, 0, 1, 0)))
		)
				.addIf(new SceneObject(door),
						roomParameter -> roomParameter.get(RoomParameter.Type.TOP_NEIGHBOUR)
				)
				.addIf(new SceneObject(wall_segment),
						roomParameter -> !roomParameter.get(RoomParameter.Type.TOP_NEIGHBOUR)
				);
		//</editor-fold>
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
				add = new SceneObject(add);
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
