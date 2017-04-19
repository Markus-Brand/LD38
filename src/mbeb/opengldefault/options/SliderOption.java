package mbeb.opengldefault.options;

import java.lang.annotation.ElementType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate fields (currently ints or floats) that will then generate a @{link OptionSlider} in the Options
 *
 * @author Markus
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SliderOption {
	public float min() default 0;

	public float max() default 1;

	public float step() default 0.01f;
}
