package mbeb.opengldefault.animation;

/**
 * a running animation and logic for sampling
 */
public class Animator {

	private final AnimatorPreset preset;
	private double currentTime;
	private double totalRunningTime;
	private Double stopTimestamp;

	public Animator(AnimatorPreset preset) {
		this.preset = preset;
		currentTime = 0.001;
		totalRunningTime = 0.001;
	}

	public void update(double deltaTime) {
		currentTime += deltaTime * preset.getSpeed();
		totalRunningTime += deltaTime * preset.getSpeed();
	}

	public AnimatorPreset getPreset() {
		return preset;
	}

	public Animation getAnimation() {
		return getPreset().getAnimation();
	}

	/**
	 * init the fade-out phase
	 */
	public void stop() {
		if (isFadingOut()) {
			return;
		}
		stopTimestamp = totalRunningTime;
	}
	
	/**
	 * start fading out to the end of the animation
	 */
	public void stopAtEnd() {
		if (isFadingOut()) {
			return;
		}
		//set the stopTimestamp so that fading out stops exactly on a loop restart
		double loopSize = getAnimation().getKeyFrameDistance();
		double endStamp = totalRunningTime + preset.getActualFadeOutTime();
		endStamp = (((int)(endStamp / loopSize)) + 1) * loopSize;
		endStamp -= preset.getActualFadeOutTime();
		stopTimestamp = endStamp;
	}

	public boolean isFadingOut() {
		return stopTimestamp != null;
	}

	/**
	 *
	 * @return true whether this animator is at its end
	 */
	public boolean hasEnded() {
		if (stopTimestamp != null && stopTimestamp + preset.getActualFadeOutTime() < totalRunningTime) {
			return true; //finished fading out
		}
		return false;
	}

	/**
	 * @return the current weight of this animator (for fading / intensity)
	 */
	public double getCurrentStrength() {
		double strength = preset.getIntensity();

		double fadeInFactor = totalRunningTime / preset.getActualFadeInTime();
		if (fadeInFactor < 1) {
			strength *= fadeInFactor;
		}

		if (stopTimestamp != null && stopTimestamp <= totalRunningTime) {
			double fadeOutFactor = 1 - (totalRunningTime - stopTimestamp) / preset.getActualFadeOutTime();
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
		KeyFrame[] beforeAfter = getAnimation().getBeforeAndAfter(currentTime);

		//edge-case handling
		if (preset.isLooping()) {
			if (beforeAfter[0] == null) {
				currentTime += getAnimation().getKeyFrameDistance();
				return getCurrentPose();
			}
			if (beforeAfter[1] == null) {
				currentTime -= getAnimation().getKeyFrameDistance();
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
