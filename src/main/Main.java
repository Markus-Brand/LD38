package main;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import game.Game;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

public class Main {

	/** Class Name Tag */
	private static final String TAG = "Main";

	/** The created window Object */
	private long window;

	/** Primary monitors video mode */
	private static GLFWVidMode vidmode;

	/** Game Object */
	private Game game;

	/**
	 * Constructor of the Main class
	 * It initializes a new Window, then starts the main loop and cleans when the window is closed
	 */
	public Main() {
		init();
		loop();
		clean();
	}

	/**
	 * init main project
	 */
	private void init() {
		createWindow("Test window", false, vidmode.width(), vidmode.height());
		GL.createCapabilities();
		GLErrors.checkForError(TAG, "createCapabilities");
		game = new Game();
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

		}
	}

	/**
	 * cleaning after closing the window
	 */
	private void clean() {
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		Log.closeLogFile();
	}

	/**
	 * creates the GLFW Window
	 *
	 * @param title
	 *            windows title
	 * @param fullscreen
	 *            is the window fullscreen?
	 * @param width
	 *            window width
	 * @param height
	 *            window height
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

		// Center our window
		glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);

		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}

	/**
	 * inits OpenGL
	 */
	private static void initOpenGL() {
		Log.log("LWJGL Version " + Version.getVersion() + " is working.");

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
		glfwWindowHint(GLFW_RED_BITS, vidmode.redBits());
		glfwWindowHint(GLFW_GREEN_BITS, vidmode.greenBits());
		glfwWindowHint(GLFW_BLUE_BITS, vidmode.blueBits());
		glfwWindowHint(GLFW_REFRESH_RATE, vidmode.refreshRate());
	}

	/**
	 * Sets Debug Mode
	 *
	 * @param args
	 *            command line arguments
	 */
	private static void evaluateCommandLineArguments(String[] args) {
		if (args.length < 2) {
			Log.initDebug(LogMode.CONSOLE);
		} else if (args[1].equals("console")) {
			Log.initDebug(LogMode.CONSOLE);
		} else if (args[1].equals("logfile")) {
			Log.initDebug(LogMode.LOGFILE);
		} else {
			Log.initDebug(LogMode.NONE);
		}
	}

	/**
	 * main method
	 *
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		evaluateCommandLineArguments(args);
		initOpenGL();
		new Main();
	}
}
