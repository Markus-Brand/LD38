package mbeb.dungeon.room;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.scene.SceneObject;

public class Slot {

	private class Possibility {
		private Function<RoomParameter, Boolean> condition;
		private SceneObject object;

		public Possibility(Function<RoomParameter, Boolean> condition, SceneObject object) {
			this.condition = condition;
			this.object = object;
		}

		public boolean isOk(RoomParameter roomParameter) {
			return condition.apply(roomParameter);
		}

		public Function<RoomParameter, Boolean> getCondition() {
			return condition;
		}

		public SceneObject getObject() {
			return object;
		}
	}

	BoneTransformation transformation;

	private List<Possibility> placements;

	public Slot(BoneTransformation transformation) {
		this.transformation = transformation;
		placements = new LinkedList<>();
	}

	public SceneObject getApplicable(RoomParameter parameters) {
		for (Possibility placement : placements) {
			if(placement.isOk(parameters))
				return placement.getObject();
		}
		return null;
	}

	public Slot addIf(SceneObject so, Function<RoomParameter, Boolean> condition) {
		this.placements.add(new Possibility(condition, so));
		return this;
	}

	public BoneTransformation getTransformation() {
		return transformation;
	}

	public void setTransformation(BoneTransformation transformation) {
		this.transformation = transformation;
	}
}
