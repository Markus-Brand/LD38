package mbeb.opengldefault.animation;

/**
 * a running animation and logic for sampling
 */
public class Animator {

	private final Animation animation;
	private double currentTime;

	private boolean looping;

	public Animator(Animation animation) {
		this.animation = animation;
		currentTime = 0;
		looping = true;
	}

	public void setLooping(boolean looping) {
		this.looping = looping;
	}

	public boolean isLooping() {
		return looping;
	}

	public void update(double deltaTime) {
		currentTime += deltaTime * 0.4f;
	}

	public Animation getAnimation() {
		return animation;
	}

	/**
	 * calculate the current pose of the animation
	 *
	 * @return
	 */
	public Pose getCurrentPose() {

		KeyFrame[] beforeAfter = animation.getBeforeAndAfter(currentTime);

		//edge-case handling
		if (looping) {
			if (beforeAfter[0] == null) {
				currentTime += animation.getKeyFrameDistance();
				return getCurrentPose();
			}
			if (beforeAfter[1] == null) {
				currentTime -= animation.getKeyFrameDistance();
				return getCurrentPose();
			}
		} else {
			if (beforeAfter[0] == null) {
				return beforeAfter[1].getPose();
			} else if (beforeAfter[1] == null) {
				return beforeAfter[0].getPose();
			}
		}

		double lerpFactor = (currentTime - beforeAfter[0].getTimeStamp())
				/ (beforeAfter[1].getTimeStamp() - beforeAfter[0].getTimeStamp());

		return Pose.lerp(beforeAfter[0].getPose(), beforeAfter[1].getPose(), lerpFactor);
	}

}
