package mbeb.opengldefault.options;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mbeb.opengldefault.game.OptionsMenu;
import mbeb.opengldefault.reflection.ClassFinder;

/**
 * Class containing static methods for loading and saving options
 * 
 * @author Markus
 */
public class Options {

	/**
	 * File to save the options in the form of a mapping from Field names to their serialized values
	 */
	private static File optionsFile = new File("resources/options/options.options");

	/**
	 * Loads all Options and adds them to a OptionsMenu
	 * 
	 * @param menu
	 *            the OptionsMenu that will manage the options
	 */
	public static void load(OptionsMenu menu) {
		Map<String, String> options = getOptionMap();
		for (Field field : findOptions()) {
			Option option = field.getAnnotation(Option.class);
			menu.addOption(option.category(), field);
			if (!options.containsKey(field.getName())) {
				System.out.println("There is no option in the option file for: " + field.getName());
				continue;
			}
			loadField(options.get(field.getName()), field);
		}
	}

	/**
	 * Loads a single field by interpreting the value String based on the Fields type
	 * 
	 * @param value
	 *            the String containing the seralized value of the option
	 * @param field
	 *            the option field
	 */
	private static void loadField(String value, Field field) {
		try {
			System.out.println("Pre: " + field.get(null));
			if (int.class.isAssignableFrom(field.getType())) {
				field.set(null, Integer.parseInt(value));
			} else if (float.class.isAssignableFrom(field.getType())) {
				field.set(null, Float.parseFloat(value));
			} else if (String.class.isAssignableFrom(field.getType())) {
				field.set(null, value);
			} else if (boolean.class.isAssignableFrom(field.getType())) {
				field.set(null, Boolean.parseBoolean(value));
			} else {
				System.out.println(field.getName() + " has a type that is not supported: " + field.getType());
			}
			System.out.println("Post: " + field.get(null));
		} catch(IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves all option @{link Field}s found by @{link {@link #findOptions()} to the options file
	 */
	public static void save() {
		List<String> optionLines = new ArrayList<>();
		for (Field f : findOptions()) {
			try {
				optionLines.add(f.getName() + " " + f.get(null));
			} catch(IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		recreateFile();
		try {
			Files.write(optionsFile.toPath(), optionLines, StandardOpenOption.WRITE);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clears and recreates the option file
	 */
	private static void recreateFile() {
		optionsFile.delete();
		try {
			optionsFile.getParentFile().mkdirs();
			optionsFile.createNewFile();
		} catch(IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Returns a @{link Map} for the name of the option @{link Field}s to their serialized values.
	 * 
	 * @return a @{link Map} for the name of the option @{link Field}s to their serialized values
	 */
	private static Map<String, String> getOptionMap() {
		Map<String, String> options = new HashMap<>();
		try {
			if (optionsFile.createNewFile()) {
				System.out.println("new file was created");
				save();
			}
		} catch(IOException e1) {
			e1.printStackTrace();
		}
		try {
			for (String s : Files.readAllLines(optionsFile.toPath())) {
				String[] subStrings = s.split("\\s", 2);
				options.put(subStrings[0], subStrings[1]);
			};
		} catch(IOException e) {
			e.printStackTrace();
		}
		return options;
	}

	/**
	 * Returns a @{link Set} Set with all @{link Field}s with a Option Annotation in the Project
	 * 
	 * @return a @{link Set} Set with all @{link Field}s with a Option Annotation in the Project
	 */
	private static Set<Field> findOptions() {
		Set<Field> set = new HashSet<>();

		for (Class<?> c : ClassFinder.find("mbeb")) {
			for (Field field : c.getDeclaredFields()) {
				if (field.isAnnotationPresent(Option.class)
						&& java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
					set.add(field);
				}
			}
		}
		return set;
	}
}
