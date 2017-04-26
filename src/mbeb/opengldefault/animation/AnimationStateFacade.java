package mbeb.opengldefault.animation;

import java.util.HashMap;
import java.util.Map;
import mbeb.opengldefault.logging.Log;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.IRenderableHolder;
import mbeb.opengldefault.scene.materials.Material;

/**
 * A manager wrapping an AnimatedRenderable having convenience functions to control Animations
 */
public class AnimationStateFacade implements IRenderableHolder {

	private static final String TAG = "AnimationStateFacade";

	/** the animatedRenderable, whose state should be controlled */
	private final AnimatedRenderable renderable;
	/** the material that this thing want to be rendered with */
	private Material material;

	/** all the registered presets */
	private Map<String, AnimatorPreset> presets;
	/** the running instances of presets */
	private Map<String, Animator> runningAnimations;

	/**
	 * create a new AnimationStateFacade around an AnimatedRenderable-instance.
	 *
	 * @param renderable
	 *            the AnimatedRenderable, who's state to control
	 */
	public AnimationStateFacade(AnimatedRenderable renderable, Material material) {
		this.renderable = renderable;
		presets = new HashMap<>();
		runningAnimations = new HashMap<>();
		this.material = material;
	}

	/**
	 * create a new AnimationStateFacade around a new renderable instance from the given mesh
	 *
	 * @param mesh
	 *            the AnimatedMesh to create a new AnimatedRenderable-instance from
	 */
	public AnimationStateFacade(AnimatedMesh mesh, Material material) {
		this(new AnimatedRenderable(mesh), material);
	}

	/**
	 * @return the renderable to add into the SceneGraph
	 */
	@Override
	public IRenderable getRenderable() {
		return material == null ? getAnimatedRenderable() : getAnimatedRenderable().withMaterial(material);
	}

	/**
	 * @return the AnimatedRenderable usedfor animations (without any material)
	 */
	public AnimatedRenderable getAnimatedRenderable() {
		return renderable;
	}

	/**
	 * Getter for the clean set of running animations
	 *
	 * @return
	 */
	private Map<String, Animator> getRunningAnimations() {
		clearRunningAnimations();
		return runningAnimations;
	}

	/**
	 * remove all the Animators from the runningAnimations-Map that have ended already
	 */
	private void clearRunningAnimations() {
		runningAnimations.entrySet().removeIf(entry -> entry.getValue().hasEnded());
	}

	//region Animation Preset registration

	/**
	 * accessor for Presets
	 *
	 * @param name
	 * @return
	 */
	private AnimatorPreset getPreset(String name) {
		Log.assertTrue(TAG, presets.containsKey(name), "No Preset called \"" + name + "\" found!");
		return presets.get(name);
	}

	private void registerAnimation(String name, AnimatorPreset preset) {
		Log.assertFalse(TAG, presets.containsKey(name), "A preset with this name is already registered");
		presets.put(name, preset);
	}

	public void registerAnimation(String presetName, String animationName) {
		registerAnimation(presetName,
				new AnimatorPreset(renderable.getAnimatedMesh().getAnimationByName(animationName)));
	}

	public void registerAnimation(String presetName, String animationName, double speed) {
		registerAnimation(presetName, new AnimatorPreset(
				renderable.getAnimatedMesh().getAnimationByName(animationName),
				speed));
	}

	public void registerAnimation(String presetName, String animationName, double speed, double fadeInTime,
			double fadeOutTime) {
		registerAnimation(presetName, new AnimatorPreset(
				renderable.getAnimatedMesh().getAnimationByName(animationName),
				speed, fadeInTime, fadeOutTime));
	}

	/**
	 * register an "Animation Preset". This is basically an {@link Animation} with additional data (see
	 * {@link AnimatorPreset}).
	 * The presetName is the replacement for an actual object reference
	 *
	 * @param presetName
	 *            the name this preset should have (to operate on it later on)
	 * @param animationName
	 *            the name of the animation to play
	 * @param speed
	 *            the speed of the animation
	 * @param fadeInTime
	 *            how long to fade in the animation (in seconds)
	 * @param fadeOutTime
	 *            how long to fade out the animation (in seconds)
	 * @param intensity
	 *            an intensity factor for animations
	 */
	public void registerAnimation(String presetName, String animationName, double speed, double fadeInTime,
			double fadeOutTime, double intensity) {
		registerAnimation(presetName, new AnimatorPreset(
				renderable.getAnimatedMesh().getAnimationByName(animationName),
				speed, fadeInTime, fadeOutTime, intensity));
	}

	/**
	 * copy a formerly registered preset on some other object to myself
	 *
	 * @param presetName
	 *            the name of the preset to copy
	 * @param reference
	 *            the AnimationStateFacade to copy from
	 */
	public void copyPreset(String presetName, AnimationStateFacade reference) {
		registerAnimation(presetName, reference.getPreset(presetName));
	}

	//endregion

	//region preset parameter changing

	/**
	 * change the speed parameter of a given preset
	 *
	 * @param presetName
	 * @param speed
	 */
	public void setSpeed(String presetName, double speed) {
		getPreset(presetName).setSpeed(speed);
	}

	/**
	 * smoothly slide the speed parameter to the given value
	 *
	 * @param presetName
	 * @param target
	 *            the speed to slide to
	 * @param deltaTime
	 *            time since last call of this method
	 */
	public void slideSpeed(String presetName, double target, double deltaTime, double speed) {
		AnimatorPreset preset = getPreset(presetName);
		preset.setSpeed(slideParameter(preset.getSpeed(), target, deltaTime, speed));
	}

	/**
	 * internal function to slide a parameter to a given target, by the strength calculated from deltaTime
	 *
	 * @param current
	 * @param target
	 * @param deltaTime
	 * @return
	 */
	private double slideParameter(double current, double target, double deltaTime, double speed) {
		double factor = 1 / (deltaTime * speed + 1);
		return target * (1 - factor) + current * factor;
	}

	//endregion

	/**
	 * @param presetName
	 * @return true exactly when this preset is currently running
	 */
	public boolean isRunning(String presetName) {
		return getRunningAnimations().containsKey(presetName);
	}

	/**
	 * @param presetName
	 * @return true only if this preset is running and hasn't started fading out yet.
	 */
	public boolean isRunningNotFadingOut(String presetName) {
		return isRunning(presetName) && !getRunningAnimations().get(presetName).isFadingOut();
	}

	/**
	 * make sure that an Animation (formerly registered) is currently running. Start it if needed
	 *
	 * @param presetName
	 *            the <code>presetName</code> of the Animation
	 */
	public void ensureRunning(String presetName) {
		if (!isRunning(presetName)) {
			Animator newAnimation = new Animator(getPreset(presetName));
			getRunningAnimations().put(presetName, newAnimation);
			renderable.playAnimation(newAnimation);
		}
	}

	/**
	 * make sure that an Animation (formerly registered) does not run. Stop it if needed (but still fade it out if
	 * wanted)
	 *
	 * @param presetName
	 *            the <code>presetName</code> of the Animation
	 * @param hardAbort
	 *            true to start fading out instantly, false to keep it running until the end
	 */
	public void ensureStopped(String presetName, boolean hardAbort) {
		if (isRunningNotFadingOut(presetName)) {
			if (hardAbort) {
				getRunningAnimations().get(presetName).stop();
			} else {
				getRunningAnimations().get(presetName).stopAtEnd();
			}
		}
	}

	/**
	 * see overloaded function. <code>hardAbort = true</code>
	 *
	 * @param presetName
	 * @param running
	 * @see #ensureRunning(String, boolean)
	 */
	public void ensureRunning(String presetName, boolean running) {
		ensureRunning(presetName, running, true);
	}

	/**
	 * call {@link #ensureRunning(String)} or {@link #ensureStopped(String, boolean)} based on the condition
	 *
	 * @param presetName
	 *            the <code>presetName</code> of the Animation
	 * @param running
	 *            whether or not this Animation should be running at the moment
	 * @param hardAbort
	 *            whether to actually cancel an animation mid-run
	 */
	public void ensureRunning(String presetName, boolean running, boolean hardAbort) {
		if (running) {
			ensureRunning(presetName);
		} else {
			ensureStopped(presetName, hardAbort);
		}
	}

	public void setDuration(String presetName, float duration) {
		setSpeed(presetName, getDuration(presetName) / duration);
	}

	public float getDuration(String presetName) {
		return (float) getPreset(presetName).getAnimation().getDuration();
	}

}
