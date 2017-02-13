package mbeb.opengldefault.animation;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.assimp.AIAnimation;

/**
 * a list of keyframes for an AnimatedMesh
 */
public class Animation {
	
	private static final float keyFrameMergeTolerance = 0.01f;

	private List<KeyFrame> keyFrames;
	private String name;
	private Bone skeleton;
	
	private double duration;
	
	/**
	 * create a new animations with the settings
	 * (not the keyframe data) from an AIAnimation
	 * @param aianim to copy from
	 * @return a new Animation
	 */
	public static Animation copySettingsFromAI(AIAnimation aianim) {
		Animation anim = new Animation();
		
		anim.setDuration(aianim.mDuration());
		anim.setName(aianim.mName().dataString());
		
		return anim;
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
	
	public void mergeKeyFrame(KeyFrame newOne) {
		for (KeyFrame k: getKeyFrames()) {
			if (Math.abs(k.getTimeStamp() - newOne.getTimeStamp()) <= keyFrameMergeTolerance) {
				k.mergeWith(newOne);
				return;
			}
		}
		keyFrames.add(newOne);
	}
	
	/**
	 * get the keyFrames before and after the provided timestamp. At maximum
	 * one of them could be null (when sampled before or after all other keframes)
	 * 
	 * @param timeStamp the animations time to sample from
	 * @return {before, after}
	 */
	public KeyFrame[] getBeforeAndAfter(double timeStamp) {
		//todo binary search for performance
		
		KeyFrame before = null;
		KeyFrame after = null;
		
		for (KeyFrame currentFrame : getKeyFrames()) {
			after = currentFrame;
			if (currentFrame.getTimeStamp() >= timeStamp) {
				return new KeyFrame[]{before, after};
			}
			before = currentFrame;
		}
		if (before == after) {
			after = null;
		}
		return new KeyFrame[]{before, after};
	}
}
