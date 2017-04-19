package mbeb.opengldefault.options;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate any field that will then be saved in a Options File using the @{link Option}
 * 
 * @author Markus
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Option {
	public String category() default "none";
}
