package mbeb.opengldefault.animation;

/**
 * a running animation and logic for sampling
 */
public class Animator {

	private final Animation animation;
	private double currentTime;
	private double currentTotalTime;
	private Double stopTimestamp;

	private double fadeInTime;
	private double fadeOutTime;

	private boolean looping;
	
	private double speed;
	private double intensity;

	public Animator(Animation animation) {
		this(animation, 1, 0, 0);
	}
	public Animator(Animation animation, double speed, double fadeInTime, double fadeOutTime) {
		this.animation = animation;
		currentTime = 0.001;
		currentTotalTime = 0.001;
		looping = true;
		setSpeed(speed);
		setFadeInTime(fadeInTime);
		this.fadeOutTime = fadeOutTime;
		this.stopTimestamp = null;
		setIntensity(1);
	}

	/**
	 * copy constructor
	 * @param reference
	 */
	public Animator(Animator reference) {
		this.animation = reference.animation;
		this.currentTime = reference.currentTime;
		this.currentTotalTime = reference.currentTotalTime;
		this.stopTimestamp = reference.stopTimestamp;
		this.fadeInTime = reference.fadeInTime;
		this.fadeOutTime = reference.fadeOutTime;
		this.looping = reference.looping;
		this.speed = reference.speed;
		this.intensity = reference.intensity;
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
	 * init the fade-out phase
	 */
	public void stop() {
		stopTimestamp = currentTotalTime;
	}

	/**
	 *
	 * @return true whether this animator is at its end
	 */
	public boolean hasEnded() {
		if (stopTimestamp != null && stopTimestamp + fadeOutTime < currentTotalTime) {
			return true; //finished fading out
		}
		return false;
	}

	/**
	 * @return the current weight of this animator (for fading / intensity)
	 */
	public double getCurrentStrength() {
		double strength = getIntensity();

		double fadeInFactor = currentTotalTime / fadeInTime;
		if (fadeInFactor < 1) {
			strength *= fadeInFactor;
		}

		if (stopTimestamp != null) {
			double fadeOutFactor = 1 - (currentTotalTime - stopTimestamp) / fadeOutTime;
			System.out.println(fadeOutFactor);
			strength *= fadeOutFactor;
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
