package mbeb.opengldefault.options;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mbeb.opengldefault.game.GameState;
import mbeb.opengldefault.reflection.OptionFieldFinder;

public class Options {
	
	private static File optionsFile = new File("OpenGL-default/src/mbeb/opengldefault/options/options.txt");
	
	public static void load(){
		Map<String, String> options = getOptionMap();
		for(Field f : OptionFieldFinder.findStaticFields(Option.class)){
			if(!options.containsKey(f.getName())){
				System.out.println("There is no option in the option file for: " + f.getName());
				continue;
			}
			try {
				if(int.class.isAssignableFrom(f.getType())){
					f.set(null, Integer.parseInt(options.get(f.getName())));
				}else{
					System.out.println(f.getName() + " has a type that is not supported: " + f.getType());					
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void save(){
		List<String> optionLines = new ArrayList<>();
		for(Field f : OptionFieldFinder.findStaticFields(Option.class)){
			try {
				optionLines.add(f.getName() + " " + f.get(null));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}			
		}
		try {
			Files.write(optionsFile.toPath(), optionLines, StandardOpenOption.WRITE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	


	private static Map<String, String> getOptionMap(){
		Map<String, String> options = new HashMap<>();
		try {
			if(optionsFile.createNewFile()){
				System.out.println("new file was created");
				save();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			for(String s : Files.readAllLines(optionsFile.toPath())){
				String[] subStrings = s.split("\\s");
				options.put(subStrings[0], subStrings[1]);
			};
		} catch (IOException e) {
			e.printStackTrace();
		}
		return options;
	}
}
