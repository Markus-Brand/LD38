package mbeb.opengldefault.options;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate any field that will then be saved in an Options File using the @{link Option}
 *
 * @author Markus
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Option {
	String category() default "none";
}
