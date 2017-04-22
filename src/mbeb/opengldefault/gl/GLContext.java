package mbeb.opengldefault.gl;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.joml.Vector2f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.game.Game;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.logging.Log;
import mbeb.opengldefault.logging.LogMode;

public class GLContext {

	/**
	 * Class Name Tag
	 */
	private static final String TAG = "GLContext";

	/**
	 * Console option constant to switch to console logging
	 */
	private static final String OPTION_LOG_CONSOLE = "-c";
	/**
	 * Console option constant to switch to file logging
	 */
	private static final String OPTION_LOG_FILE = "-f";
	/**
	 * Console option constant to turn off logging
	 */
	private static final String OPTION_LOG_NONE = "-n";

	/**
	 * The created window Object
	 */
	private static long window;

	/**
	 * Primary monitors video mode
	 */
	private static GLFWVidMode vidmode;

	/**
	 * The actual framebuffer width
	 */
	private static int framebufferWidth;

	/**
	 * The actual framebuffer height
	 */
	private static int framebufferHeight;

	/*
	 * Game Object
	 */
	private static Game game;

	/**
	 * Constructor of the Main class It initializes a new Window, then starts
	 * the main loop and cleans when the window is closed
	 */
	public static void startGame(Game game, String[] args) {
		GLContext.game = game;
		init(args);
		loop();
		clean();
	}

	/**
	 * Init the OpenGL context
	 *
	 * @param args
	 *            The command line arguments
	 */
	private static void init(String[] args) {
		evaluateCommandLineArguments(args);
		initOpenGL();

		createWindow("Test window", true, getVideoModeWidth(), getVideoModeHeight());
		GL.createCapabilities();
		GLErrors.checkForError(TAG, "createCapabilities");

		printOpenGLInformation();
		game.init();
	}

	/**
	 * Print OpenGL version and supported extensions
	 */
	private static void printOpenGLInformation() {
		Log.log(TAG, "OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
		GLErrors.checkForError(TAG, "glGetString");
		Log.log(TAG, "Extensions supported:");
		int num = GL11.glGetInteger(GL30.GL_NUM_EXTENSIONS);
		GLErrors.checkForError(TAG, "glGetInteger");

		for (int i = 0; i < num; i++) {
			String extension = GL30.glGetStringi(GL11.GL_EXTENSIONS, i);
			GLErrors.checkForError(TAG, "glGetString");
			Log.log(TAG, extension);
		}
	}

	/**
	 * game loop
	 */
	private static void loop() {
		double lastTime = glfwGetTime();
		while(!glfwWindowShouldClose(window)) {

			glfwSwapBuffers(window); // swap the color buffers
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();

			double thisTime = glfwGetTime();
			double deltaTime = thisTime - lastTime;
			lastTime = thisTime;

			//Log.log(TAG, (int) (1 / deltaTime) + "fps");
			game.update(deltaTime);
			game.render();
		}
	}

	/**
	 * cleaning after closing the window
	 */
	private static void clean() {
		game.clear();
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		Log.closeLogFile();
		File resFile = new File("res");
		if (!resFile.exists()) {
			return;
		}
		try {
			Files.walk(resFile.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		} catch(IOException ex) {
			Log.error(TAG, "Unable to delete old res-directory", ex);
		}
	}

	/**
	 * creates the GLFW Window
	 *
	 * @param title
	 *            windows title
	 * @param fullscreen
	 *            is the window fullscreen?
	 * @param width
	 *            window framebufferWidth
	 * @param height
	 *            window framebufferHeight
	 */
	private static void createWindow(String title, boolean fullscreen, int width, int height) {
		// Create the window
		if (fullscreen) {
			glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
			window = glfwCreateWindow(width, height, title, glfwGetPrimaryMonitor(), NULL);
		} else {
			window = glfwCreateWindow(width, height, title, NULL, NULL);
		}

		if (window == NULL) {
			Log.error(TAG, "Failed to create the GLFW window", new RuntimeException());
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

		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);

		glfwSetCursorPosCallback(window, (window, xPos, yPos) -> Mouse.setPos(xPos, yPos));

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
		if (!fullscreen) {
			glfwSetWindowPos(window, vidmode.width() - width, vidmode.height() - height);
		}

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);

		GLFWWindowSizeCallbackI resizeHandler = (long l, int i, int i1) -> {
			int[] widthBuffer = new int[1];
			int[] heightBuffer = new int[1];
			glfwGetFramebufferSize(window, widthBuffer, heightBuffer);
			framebufferHeight = heightBuffer[0];
			framebufferWidth = widthBuffer[0];
		};
		glfwSetWindowSizeCallback(window, resizeHandler);
		resizeHandler.invoke(0, 0, 0);

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
		//OS X only supports forward compatible contexts if version > 3.2
		if (System.getProperty("os.name").toLowerCase().contains("os x")) {
			glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		}
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
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
		LogMode mode = LogMode.CONSOLE;
		for (int i = 1; i < args.length; i++) {
			String arg = args[i];
			switch(arg) {
				case OPTION_LOG_CONSOLE:
					mode = LogMode.CONSOLE;
					break;
				case OPTION_LOG_FILE:
					mode = LogMode.LOGFILE;
					break;
				case OPTION_LOG_NONE:
					mode = LogMode.NONE;
					break;
				default:
					Log.log(TAG, "Unknown argument: " + arg);
			}
		}
		Log.initDebug(mode);
	}

	public static GLFWVidMode getVidmode() {
		return vidmode;
	}

	public static Vector2f getNDC(Vector2f point) {
		return new Vector2f(getNDCX(point.x), getNDCY(point.y));
	}

	public static float getNDCY(float y) {
		return -(2 * y / getVideoModeHeight() - 1);
	}

	public static float getNDCX(float x) {
		return 2 * x / getVideoModeWidth() - 1;
	}

	public static int getVideoModeWidth() {
		return vidmode.width();
	}

	public static int getVideoModeHeight() {
		return vidmode.height();
	}

	public static int getFramebufferWidth() {
		return framebufferWidth;
	}

	public static int getFramebufferHeight() {
		return framebufferHeight;
	}

	public static float getAspectRatio() {
		return getFramebufferWidth() / (float) getFramebufferHeight();
	}

	public static void close() {
		glfwSetWindowShouldClose(window, true);
	}

	public static void showCursor() {
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}

	public static void hideCursor() {
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}
}
