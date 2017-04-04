package mbeb.opengldefault.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jdk.nashorn.internal.runtime.options.Options;
import mbeb.opengldefault.openglcontext.OpenGLContext;
import mbeb.opengldefault.options.Option;
import mbeb.opengldefault.reflection.OptionFieldFinder;

/**
 * Abstract class to characterize a whole game
 */
public abstract class Game {

	/**
	 * Current GameStateIdentifier
	 */
	private GameStateIdentifier currentGameState;
	
	/**
	 * Mapping from the GameStateIdentifier enum to the actual GameState
	 */
	private Map<GameStateIdentifier, GameState> gameStates;

	/**
	 * Adds a GameStateIdentifier -> GameState mapping entry. The first GameState to add will be the startup entry per default
	 *
	 * @param key
	 * @param newGameState
	 */
	protected void addGameState(GameStateIdentifier key, GameState newGameState) {
		newGameState.init();
		if (gameStates == null) {
			gameStates = new HashMap<>();
			currentGameState = key;
			newGameState.open();
		}
		gameStates.put(key, newGameState);
	}

	/**
	 * Init the Game here. The OpenGL context is already created at this Point.
	 */
	public void init(){
		mbeb.opengldefault.options.Options.load();
	}
	
	/**
	 * Entry Point for the update cycle
	 *
	 * @param deltaTime
	 *            time that passed since the last update
	 */
	public void update(double deltaTime) {
		GameState currentState = getCurrentGameState();
		currentState.update(deltaTime);
		if (!currentState.isActive()) {
			currentGameState = currentState.getNextState();
			currentState.resetNextGameState();
			if (currentGameState == GameStateIdentifier.EXIT) {
				OpenGLContext.close();
			} else {
				getCurrentGameState().open();
			}
		}
	}

	/**
	 * Rendering entry point of an update cycle
	 */
	public void render() {
		GameState currentGameState = getCurrentGameState();
		if (currentGameState != null) {
			currentGameState.render();
		}
	}

	/**
	 * Getter for the current GameState
	 *
	 * @return the current GameState
	 */
	private GameState getCurrentGameState() {
		return gameStates.get(currentGameState);
	}

	/**
	 * Clear the Game. The game will close after this method is called.
	 */
	public void clear() {
		mbeb.opengldefault.options.Options.save();
		for (GameState state : gameStates.values()) {
			state.clear();
		}
	}
	
	/**
	 * @return null safe set
	 */
	public static Set<Field> findStaticFields(Class<?> classs, Class<? extends Annotation> ann) {
	    return findStaticFields(classs, new LinkedList<Class<?>>(), ann);
	}
	
	/**
	 * @return null safe set
	 */
	public static Set<Field> findStaticFields(Class<?> classs, List<Class<?>> usedClasses, Class<? extends Annotation> ann) {
	    Set<Field> set = new HashSet<>();
	    usedClasses.add(classs);
	    Class<?> c = classs;
	    if(!c.getName().contains("mbeb.opengldefault")){
	    	System.out.println("Not in the package: " + c);
	    	return set;
	    }
	   // System.out.println(c.getName());
	    while (c != null) {
	        for (Field field : c.getDeclaredFields()) {
				System.out.println(" 0 " + field.getType());
	        	if ( Collection.class.isAssignableFrom( field.getType() ) ){
					System.out.println(" 3 " + field.getType());
	        		  try {
						for(Object obj : (Collection<?>) field.get(c)){
							System.out.println(" 3 " + obj.getClass());
							  findStaticFields(obj.getClass(),  usedClasses, ann);
						  }
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	        	}
	        	if (field.isAnnotationPresent(ann) && java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
	                set.add(field);
	            }
        	    System.out.println("1 " + field.getType());
	        	if(!usedClasses.contains(field.getType())){
	        	    System.out.println("2 " + field.getType());
		        	set.addAll(findStaticFields(field.getType(), usedClasses, ann));       				
    			}
	        }
	        c = c.getSuperclass();
	    }
	    return set;
	}
}
