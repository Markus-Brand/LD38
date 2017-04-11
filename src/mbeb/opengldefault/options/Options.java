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

import mbeb.opengldefault.reflection.ClassFinder;

public class Options {

	private static File optionsFile = new File("resources/options/options.options");

	public static void load(OptionsMenu menu) {
		Map<String, String> options = getOptionMap();
		for (Field f : findOptions()) {
			Option option = f.getAnnotation(Option.class);
			menu.addOption(option.category(), f);
			if (!options.containsKey(f.getName())) {
				System.out.println("There is no option in the option file for: " + f.getName());
				continue;
			}
			try {
				System.out.println("Pre: " + f.get(null));
				if (int.class.isAssignableFrom(f.getType())) {
					f.set(null, Integer.parseInt(options.get(f.getName())));
				} else if (float.class.isAssignableFrom(f.getType())) {
					f.set(null, Float.parseFloat(options.get(f.getName())));
				} else if (String.class.isAssignableFrom(f.getType())) {
					f.set(null, options.get(f.getName()));
				} else if (boolean.class.isAssignableFrom(f.getType())) {
					f.set(null, Boolean.parseBoolean(options.get(f.getName())));
				} else {
					System.out.println(f.getName() + " has a type that is not supported: " + f.getType());
				}
				System.out.println("Post: " + f.get(null));
			} catch(IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static void save() {
		List<String> optionLines = new ArrayList<>();
		for (Field f : findOptions()) {
			try {
				optionLines.add(f.getName() + " " + f.get(null));
			} catch(IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		optionsFile.delete();
		try {
			optionsFile.getParentFile().mkdirs();
			optionsFile.createNewFile();
		} catch(IOException e1) {
			e1.printStackTrace();
		}
		try {
			Files.write(optionsFile.toPath(), optionLines, StandardOpenOption.WRITE);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

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
