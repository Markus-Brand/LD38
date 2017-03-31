package mbeb.opengldefault.game;

import mbeb.opengldefault.rendering.textures.*;
import java.util.ArrayList;
import java.util.Random;

import mbeb.opengldefault.animation.AnimatedMesh;
import mbeb.opengldefault.animation.AnimationStateFacade;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.opengl.GL11.*;

import java.awt.*;

import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.*;
import org.joml.*;

import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.curves.BezierCurve;
import mbeb.opengldefault.curves.BezierCurve.ControlPointInputMode;
import mbeb.opengldefault.light.*;
import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.rendering.textures.*;
import mbeb.opengldefault.scene.*;

import org.lwjgl.glfw.GLFW;

public class BunnyGame extends Game {
	/** Class Name Tag */
	private static final String TAG = "BunnyGame";

	@Override
	public void init() {
		addGameState(GameStateIdentifier.MAIN_MENU, new MainMenu());
		addGameState(GameStateIdentifier.GAME, new BunnyGameState());
	}

	@Override
	public void clear() {
		super.clear();
		TextureCache.clearCache();
	}

}
