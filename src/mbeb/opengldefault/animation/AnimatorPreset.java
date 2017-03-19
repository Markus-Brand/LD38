package mbeb.opengldefault.animation;

/**
 * An Animation with additional information that doesn't come from the file (like fade in / out times and a playback speed)
 */
public class AnimatorPreset {
	
	private final Animation animation;

	private double fadeInTime;
	private double fadeOutTime;

	private boolean looping;
	
	private double speed;
	private double intensity;

	public AnimatorPreset(Animation animation) {
		this(animation, 1);
	}
	public AnimatorPreset(Animation animation, double speed) {
		this(animation, speed, 0, 0);
	}
	public AnimatorPreset(Animation animation, double speed, double fadeInTime, double fadeOutTime) {
		this(animation, speed, fadeInTime, fadeOutTime, 1);
	}
	public AnimatorPreset(Animation animation, double speed, double fadeInTime, double fadeOutTime, double intensity) {
		this.animation = animation;
		this.speed = speed;
		this.fadeInTime = fadeInTime;
		this.fadeOutTime = fadeOutTime;
		this.intensity = intensity;
		this.looping = true;
	}

	public Animation getAnimation() {
		return animation;
	}

	public double getFadeInTime() {
		return fadeInTime;
	}

	public double getFadeOutTime() {
		return fadeOutTime;
	}
	
	public double getActualFadeInTime() {
		return getFadeInTime() * getSpeed();
	}
	
	public double getActualFadeOutTime() {
		return getFadeOutTime() * getSpeed();
	}

	public boolean isLooping() {
		return looping;
	}

	public double getSpeed() {
		return speed;
	}

	public double getIntensity() {
		return intensity;
	}

	public void setFadeInTime(double fadeInTime) {
		this.fadeInTime = fadeInTime;
	}

	public void setFadeOutTime(double fadeOutTime) {
		this.fadeOutTime = fadeOutTime;
	}

	public void setLooping(boolean looping) {
		this.looping = looping;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}
}
