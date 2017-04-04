package mbeb.opengldefault.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class OptionFieldFinder {
	
	
	public static Set<Field> findStaticFields(Class<? extends Annotation> ann) {
		Set<Field> set = new HashSet<>();
    
		for(Class<?> c : ClassFinder.find("mbeb")){
			for (Field field : c.getDeclaredFields()) {
				if (field.isAnnotationPresent(ann) && java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
	                set.add(field);
	            }
			}
		}
		return set;
	}
}
