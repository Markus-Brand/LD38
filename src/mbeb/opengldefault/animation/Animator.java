package mbeb.opengldefault.animation;

/**
 * a running animation and logic for sampling
 */
public class Animator {

	private final Animation animation;
	private double currentTime;
	private double fadeInTime;

	private boolean looping;
	
	private double speed;

	public Animator(Animation animation) {
		this(animation, 1);
	}
	public Animator(Animation animation, double speed) {
		this.animation = animation;
		currentTime = 0;
		looping = true;
		speed = 1.0;
	}

	public void setFadeInTime(double fadeInTime) {
		this.fadeInTime = fadeInTime;
	}

	public void setLooping(boolean looping) {
		this.looping = looping;
	}

	public boolean isLooping() {
		return looping;
	}

	public void update(double deltaTime) {
		currentTime += deltaTime * speed;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
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
				return beforeAfter[1].getPose();//todo apply intensity here
			} else if (beforeAfter[1] == null) {
				return beforeAfter[0].getPose();//todo and here
			}
		}

		double lerpFactor = (currentTime - beforeAfter[0].getTimeStamp()) / (beforeAfter[1].getTimeStamp() - beforeAfter[0].getTimeStamp());

		return Pose.lerp(beforeAfter[0].getPose(), beforeAfter[1].getPose(), lerpFactor);
	}

}
