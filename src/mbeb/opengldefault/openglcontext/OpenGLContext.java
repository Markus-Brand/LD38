package mbeb.opengldefault.openglcontext;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import mbeb.opengldefault.controls.*;
import mbeb.opengldefault.game.*;
import mbeb.opengldefault.logging.*;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

public class OpenGLContext {

	/**
	 * Class Name Tag
	 */
	private static final String TAG = "OpenGLContext";

	/**
	 * The created window Object
	 */
	private long window;

	/**
	 * Primary monitors video mode
	 */
	private static GLFWVidMode vidmode;

	/**
	 * The actual framebuffer width
	 */
	private static int width;

	/**
	 * The actual framebuffer height
	 */
	private static int height;

	/*
	 * Game Object
	 */
	private IGame game;

	/**
	 * Constructor of the Main class It initializes a new Window, then starts
	 * the main loop and cleans when the window is closed
	 */
	public OpenGLContext(IGame game, String[] args) {
		this.game = game;
		init(args);
		loop();
		clean();
	}

	/**
	 * Init the OpenGL context
	 *
	 * @param args
	 */
	private void init(String[] args) {
		evaluateCommandLineArguments(args);
		initOpenGL();

		createWindow("Test window", false, getVideoWidth(), getVideoHeight());
		GL.createCapabilities();
		GLErrors.checkForError(TAG, "createCapabilities");
		
		printOpenGLInformation();
		game.init();
	}

	/**
	 * Print OpenGL version and supported extensions
	 */
	private void printOpenGLInformation(){
		Log.log(TAG, "OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
		Log.log(TAG, "Extensions supported:");
		int num = GL11.glGetInteger(GL30.GL_NUM_EXTENSIONS);

		for (int i = 0; i < num; i++) {
			String extension = GL30.glGetStringi(GL11.GL_EXTENSIONS, i);
			Log.log(TAG, extension);
		}
	}

	/**
	 * game loop
	 */
	private void loop() {
		double lastTime = glfwGetTime();
		while (!glfwWindowShouldClose(window)) {

			glfwSwapBuffers(window); // swap the color buffers
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();

			if (KeyBoard.isKeyDown(GLFW_KEY_ESCAPE)) {
				glfwSetWindowShouldClose(window, true); // We will detect this in our rendering loop
			}

			double thisTime = glfwGetTime();
			double deltaTime = thisTime - lastTime;
			lastTime = thisTime;

			//Debug.log((int) (1 / deltaTime) + "fps");
			game.update(deltaTime);
			game.render();
		}
	}

	/**
	 * cleaning after closing the window
	 */
	private void clean() {
		game.clear();
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		Log.closeLogFile();
		try {
			Files.walk(new File("res").toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		} catch (IOException ex) {
			Log.log(TAG, ex.getMessage() + " - unable to delete old res-directory");
		}
	}

	/**
	 * creates the GLFW Window
	 *
	 * @param title windows title
	 * @param fullscreen is the window fullscreen?
	 * @param width window width
	 * @param height window height
	 */
	private void createWindow(String title, boolean fullscreen, int width, int height) {
		// Create the window
		if (fullscreen) {
			window = glfwCreateWindow(width, height, title, glfwGetPrimaryMonitor(), NULL);
		} else {
			window = glfwCreateWindow(width, height, title, NULL, NULL);
		}

		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (action == GLFW_PRESS) {
				KeyBoard.keyDown(key);
			}
			if (action == GLFW_RELEASE) {
				KeyBoard.keyUp(key);
			}
		});

		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

		glfwSetCursorPosCallback(window, (window, xPos, yPos) -> {
			Mouse.setPos(xPos, yPos);
		});

		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			if (action == GLFW_PRESS) {
				Mouse.buttonDown(button);
			}
			if (action == GLFW_RELEASE) {
				Mouse.buttonUp(button);
			}
		});

		glfwSetScrollCallback(window, (window, xOffset, yOffset) -> {

		});

		// Center our window
		glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);

		int[] widthp = new int[1], heightp = new int[1];
		glfwGetFramebufferSize(window, widthp, heightp);
		OpenGLContext.height = heightp[0];
		OpenGLContext.width = widthp[0];

		// Enable v-sync
		//glfwSwapInterval(1);
		// Make the window visible
		glfwShowWindow(window);
	}

	/**
	 * inits OpenGL
	 */
	private static void initOpenGL() {

		Log.log(TAG, "LWJGL Version " + Version.getVersion() + " is working.");

		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Get the resolution of the primary monitor
		vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		setDefaultWindowHints();

	}

	/**
	 * sets window hints
	 */
	private static void setDefaultWindowHints() {
		// Configure default window hints
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_RED_BITS, vidmode.redBits());
		glfwWindowHint(GLFW_GREEN_BITS, vidmode.greenBits());
		glfwWindowHint(GLFW_BLUE_BITS, vidmode.blueBits());
		glfwWindowHint(GLFW_REFRESH_RATE, vidmode.refreshRate());
	}

	/**
	 * Sets Debug Mode
	 *
	 * @param args command line arguments
	 */
	private static void evaluateCommandLineArguments(String[] args) {
		LogMode mode = LogMode.CONSOLE;
		for (int i = 1; i < args.length; i++) {
			String arg = args[i];
			switch (arg) {
				case "-c":
					mode = LogMode.CONSOLE;
					break;
				case "-f":
					mode = LogMode.LOGFILE;
					break;
				case "-n":
					mode = LogMode.NONE;
					break;
			}
		}
		Log.initDebug(mode);
	}

	public static GLFWVidMode getVidmode() {
		return vidmode;
	}

	public static int getVideoWidth() {
		return vidmode.width();
	}

	public static int getVideoHeight() {
		return vidmode.height();
	}

	public static int getWidth() {
		return width;
	}

	public static int getHeight() {
		return height;
	}
}
