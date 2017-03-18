package mbeb.opengldefault.animation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import mbeb.opengldefault.logging.Log;
import mbeb.opengldefault.rendering.renderable.IRenderable;

/**
 * A manager wrapping an AnimatedRenderable having convenience functions to control Animations
 */
public class AnimationStateFacade {
	
	private static final String TAG = "AnimationStateFacade";
	
	/** the animatedRenderable, whose state should be controlled */
	final private AnimatedRenderable renderable;
	
	/** all the registered presets */
	private Map<String, AnimatorPreset> presets;
	/** the running instances of presets */
	private Map<String, Animator> runningAnimations;

	public AnimationStateFacade(AnimatedRenderable renderable) {
		this.renderable = renderable;
		presets = new HashMap<>();
		runningAnimations = new HashMap<>();
	}

	public AnimationStateFacade(AnimatedMesh mesh) {
		this(new AnimatedRenderable(mesh));
	}

	/**
	 * @return the renderable to add into the SceneGraph
	 */
	public IRenderable getRenderable() {
		return renderable;
	}
	
//region Animation Preset registration

	/**
	 * accessor for Presets
	 * @param name
	 * @return 
	 */
	private AnimatorPreset getPreset(String name) {
		Log.assertIfFalse(TAG, presets.containsKey(name), "No Preset called \"" + name + "\" found!");
		return presets.get(name);
	}
	
	private void registerAnimation(String name, AnimatorPreset preset) {
		Log.assertIfTrue(TAG, presets.containsKey(name), "A preset with this name is already registered");
		presets.put(name, preset);
	}
	
	public void registerAnimation(String presetName, String animationName) {
		registerAnimation(presetName, new AnimatorPreset(renderable.getAnimatedMesh().getAnimationByName(animationName)));
	}
	
	public void registerAnimation(String presetName, String animationName, double speed) {
		registerAnimation(presetName, new AnimatorPreset(
				renderable.getAnimatedMesh().getAnimationByName(animationName),
				speed));
	}
	
	public void registerAnimation(String presetName, String animationName, double speed, double fadeInTime, double fadeOutTime) {
		registerAnimation(presetName, new AnimatorPreset(
				renderable.getAnimatedMesh().getAnimationByName(animationName),
				speed, fadeInTime, fadeOutTime));
	}
	
	/**
	 * register an "Animation Preset". This is basically an Animation with additional data (see AnimatorPreset-class).
	 * The presetName is the replacement for an actual object reference
	 * @param presetName the name this preset should have (to operate on it later on)
	 * @param animationName the name of the Animation to play
	 * @param speed the speed of the animation
	 * @param fadeInTime how long to fade in the animation (in seconds)
	 * @param fadeOutTime how long to fade out the animation (in seconds)
	 * @param intensity an intensity factor for animations
	 */
	public void registerAnimation(String presetName, String animationName, double speed, double fadeInTime, double fadeOutTime, double intensity) {
		registerAnimation(presetName, new AnimatorPreset(
				renderable.getAnimatedMesh().getAnimationByName(animationName),
				speed, fadeInTime, fadeOutTime, intensity));
	}
	
	/**
	 * copy a formerly registered preset on some other object to myself
	 * @param presetName the name of the preset to copy
	 * @param from the AnimationStateFacade to copy from
	 */
	public void copyPreset(String presetName, AnimationStateFacade from) {
		registerAnimation(presetName, from.getPreset(presetName));
	}
//endregion
	
//region preset parameter changing
	
	/**
	 * change the speed parameter of a given preset
	 * @param presetName
	 * @param speed 
	 */
	public void setSpeed(String presetName, double speed) {
		getPreset(presetName).setSpeed(speed);
	}
		
//endregion
	
	/**
	 * remove all the Animators from the runningAnimations-Map that have ended already
	 */
	private void clearRunningAnimations() {
		Iterator<Map.Entry<String, Animator>> it = runningAnimations.entrySet().iterator();
		while (it.hasNext()) {
			if (it.next().getValue().hasEnded()) {
				it.remove();
			}
		}
	}
	
	/**
	 * @param presetName
	 * @return true exactly when this preset is currently running
	 */
	public boolean isRunning(String presetName) {
		clearRunningAnimations();
		return runningAnimations.containsKey(presetName);
	}
	
	/**
	 * @param presetName
	 * @return true only when this preset is running and hasn't started fading out yet.
	 */
	public boolean isRunningNotFadingOut(String presetName) {
		return isRunning(presetName) && !runningAnimations.get(presetName).isFadingOut();
	}
	
	/**
	 * make sure that an Animation (formerly registered) is currently running. Start it if needed
	 * @param presetName the <code>presetName</code> of the Animation
	 */
	public void ensureRunning(String presetName) {
		if (!isRunning(presetName)) {
			Animator newAnimation = new Animator(getPreset(presetName));
			runningAnimations.put(presetName, newAnimation);
			renderable.playAnimation(newAnimation);
		}
	}
	
	/**
	 * make sure that an Animation (formerly registered) does not run. Stop it if needed (but still fade it out if wanted)
	 * @param presetName the <code>presetName</code> of the Animation
	 * @param hardAbort true to start fadeOut instantly, false to keep it running to the end
	 */
	public void ensureStopped(String presetName, boolean hardAbort) {
		if (isRunningNotFadingOut(presetName)) {
			if (hardAbort) {
				runningAnimations.get(presetName).stop();
			} else {
				runningAnimations.get(presetName).stopAtEnd();
			}
		}
	}
	
	/**
	 * see overloaded function. <code>hardAbort = true</code>
	 * @param presetName
	 * @param running 
	 */
	public void ensureRunning(String presetName, boolean running) {
		ensureRunning(presetName, running, true);
	}
	
	/**
	 * call ensureRunning(String) or ensureStopped(String) based on the condition
	 * @param presetName the <code>presetName</code> of the Animation
	 * @param running whether or not this Animation should be running at the moment
	 * @param hardAbort whether to actually cancel an animation mid-run
	 */
	public void ensureRunning(String presetName, boolean running, boolean hardAbort) {
		if (running) {
			ensureRunning(presetName);
		} else {
			ensureStopped(presetName, hardAbort);
		}
	}
	
}
