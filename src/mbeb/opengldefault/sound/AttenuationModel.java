package mbeb.opengldefault.sound;

import static org.lwjgl.openal.AL10.AL_INVERSE_DISTANCE;
import static org.lwjgl.openal.AL10.AL_INVERSE_DISTANCE_CLAMPED;
import static org.lwjgl.openal.AL11.*;

/**
 * All the different types of attenuation calculations that OpenAL can perform
 */
public enum AttenuationModel {

	/**
	 * linear falloff until the max_distance
	 */
	LINEAR(AL_LINEAR_DISTANCE),

	/**
	 * linear falloff until the max_distance, with clamp to both maximum and minimum gain
	 * (as specified inside the SoundSource)
	 */
	LINEAR_CLAMPED(AL_LINEAR_DISTANCE_CLAMPED),

	/**
	 * exponential decrease in volume
	 */
	EXPONENT(AL_EXPONENT_DISTANCE),

	/**
	 * exponential decrease in volume, with clamp to both maximum and minimum gain
	 * (as specified inside the SoundSource)
	 */
	EXPONENT_CLAMPED(AL_EXPONENT_DISTANCE_CLAMPED),

	/**
	 * volume decrease via a 1/distance function
	 */
	INVERSE(AL_INVERSE_DISTANCE),

	/**
	 * volume decrease via a 1/distance function, with clamp to both maximum and minimum gain
	 * (as specified inside the SoundSource)
	 */
	INVERSE_CLAMPED(AL_INVERSE_DISTANCE_CLAMPED);

	private int alEnum;

	AttenuationModel(int alEnum) {
		this.alEnum = alEnum;
	}

	/**
	 * @return the OpenAL enum representing this attenuation model
	 */
	public int getALEnum() {
		return alEnum;

	}
}
