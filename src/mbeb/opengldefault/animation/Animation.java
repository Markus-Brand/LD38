package mbeb.opengldefault.animation;

import java.util.*;

import org.lwjgl.assimp.*;

/**
 * a list of keyframes for an AnimatedMesh
 */
public class Animation {

	private static final float KEY_FRAME_MERGE_TOLERANCE = 0.01f;

	private List<KeyFrame> keyFrames;
	private String name;
	private Bone skeleton;

	private double duration;

	/**
	 * create a new animations with the settings
	 * (not the keyframe data) from an AIAnimation
	 * 
	 * @param aianim
	 *            to copy from
	 * @return a new Animation
	 */
	public static Animation copySettingsFromAI(AIAnimation aianim) {
		Animation anim = new Animation();

		anim.setDuration(aianim.mDuration());
		anim.setName(aianim.mName().dataString());

		return anim;
	}

	public double getDuration() {
		return duration;
	}

	/**
	 * like #getDuration, but with respect to the actual keyFrame data
	 * 
	 * @return
	 */
	public double getKeyFrameDistance() {
		if (getKeyFrames().isEmpty()) {
			return 0;
		}
		return getKeyFrames().get(getKeyFrames().size() - 1).getTimeStamp() - getKeyFrames().get(0).getTimeStamp();
	}

	public void setSkeleton(Bone skeleton) {
		this.skeleton = skeleton;
	}

	public Bone getSkeleton() {
		return skeleton;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<KeyFrame> getKeyFrames() {
		if (keyFrames == null) {
			keyFrames = new ArrayList<>();
		}
		return keyFrames;
	}

	/**
	 * merge a new keyframe into this animation, combining near keyFrames
	 * @param newOne 
	 */
	public void mergeKeyFrame(KeyFrame newOne) {
		for (KeyFrame k : getKeyFrames()) {
			if (Math.abs(k.getTimeStamp() - newOne.getTimeStamp()) <= KEY_FRAME_MERGE_TOLERANCE) {
				k.mergeWith(newOne);
				return;
			}
		}
		keyFrames.add(newOne);
		keyFrames.sort(Comparator.comparingDouble(KeyFrame::getTimeStamp));
	}

	/**
	 * get the keyFrames before and after the provided timestamp. At maximum
	 * one of them could be null (when sampled before or after all other keframes)
	 * 
	 * @param timeStamp
	 *            the animations time to sample from
	 * @return {before, after}
	 */
	public KeyFrame[] getBeforeAndAfter(double timeStamp) {
		//todo maybe binary search for performance

		KeyFrame before = null;
		KeyFrame after;

		for (KeyFrame currentFrame : getKeyFrames()) {
			after = currentFrame;
			if (currentFrame.getTimeStamp() >= timeStamp) {
				return new KeyFrame[] {before, after};
			}
			before = currentFrame;
		}
		return new KeyFrame[] {before, null};
	}
}
