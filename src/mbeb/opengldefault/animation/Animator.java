package mbeb.opengldefault.animation;

/**
 * a running animation and logic for sampling
 */
public class Animator {

	private final Animation animation;
	private double currentTime;
	private double currentTotalTime;
	private double fadeInTime;

	private boolean looping;
	
	private double speed;
	private double intensity;

	public Animator(Animation animation) {
		this(animation, 1, 0);
	}
	public Animator(Animation animation, double speed, double fadeInTime) {
		this.animation = animation;
		currentTime = 0;
		currentTotalTime = 0;
		looping = true;
		setSpeed(speed);
		setFadeInTime(fadeInTime);
		setIntensity(1);
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
		currentTotalTime += deltaTime * speed;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}

	public double getIntensity() {
		return intensity;
	}

	/**
	 * @return the current weight of this animator (for fading / intensity)
	 */
	public double getCurrentStrength() {
		double strength = getIntensity();

		double fadeFactor = currentTotalTime / fadeInTime;
		if (fadeFactor < 1) {
			strength *= fadeFactor * fadeFactor;
		}

		return strength;
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

		double lerpFactor = (currentTime - beforeAfter[0].getTimeStamp()) / (beforeAfter[1].getTimeStamp() - beforeAfter[0].getTimeStamp());

		return Pose.lerp(beforeAfter[0].getPose(), beforeAfter[1].getPose(), lerpFactor);
	}

}
