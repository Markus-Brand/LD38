package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.*;

import java.awt.*;

import org.joml.*;
import org.lwjgl.glfw.*;

import mbeb.ld38.*;
import mbeb.lifeforms.*;
import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.controls.*;
import mbeb.opengldefault.gl.*;
import mbeb.opengldefault.gl.shader.*;
import mbeb.opengldefault.gl.texture.*;
import mbeb.opengldefault.gui.*;
import mbeb.opengldefault.gui.elements.*;
import mbeb.opengldefault.gui.elements.buttons.*;
import mbeb.opengldefault.light.*;
import mbeb.opengldefault.options.*;
import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.*;
import mbeb.opengldefault.shapes.Rectangle;

public class OverworldGameState implements GameState {

	private final Vector3f port = new Vector3f(0.41f, 2.509f, -7.46f);
	private final Vector3f key = new Vector3f(-3.9f, 2.74f, -7.44f);
	private final float threshold = 0.75f;

	private boolean leftForDungeon = false;

	private Scene scene;
	private EntityWorld world;

	private ShaderProgram waterShader;

	private float totalTimePassed;

	private Skybox skybox;

	private Camera topDownViewCamera;

	private ShaderProgram defaultShader;

	private final Player player;
	private final IHeightSource playerHeight;

	private final SharedData shared;

	private TextGUI text;
	private AtlasGUI hud;
	private TextGUIElement infoBox;

	private CraftingHUD craftingHUD;

	private boolean crafting;

	private final Vector3f anvilPos = new Vector3f(9.27f, 0.993f, -4.43f);


	public OverworldGameState(final SharedData shared) {
		this.shared = shared;
		player = new Player(100, null, null);
		playerHeight =
				new HeightFromHeightMap(Texture.loadBufferedImage("overworldHeight.png"), new Rectangle(new Vector2f(
						-16), new Vector2f(32)), 2f, 1f);
	}

	@Override
	public void init() {
		crafting = false;
		world = new EntityWorld();

		topDownViewCamera = new PerspectiveCamera();

		skybox = new Skybox("beachbox/beach", "png");
		scene = new Scene(topDownViewCamera, skybox);
		shared.soundEnvironment = scene.getSoundEnvironment();

		final IRenderable water = new ObjectLoader().loadFromFile("overworld/water.obj");

		waterShader = new ShaderProgram("water.frag", "planet.vert");
		waterShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		scene.getLightManager().addShader(waterShader);
		//scene.getSceneGraph().setShader(waterShader);

		defaultShader = new ShaderProgram("basic.frag", "basic.vert");
		defaultShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		scene.getLightManager().addShader(defaultShader);
		scene.getSceneGraph().setShader(defaultShader);

		final SceneObject waterObject =
				new SceneObject(water, new BoneTransformation(new Vector3f(), new Quaternionf(), new Vector3f(100)));
		waterObject.setShader(waterShader);

		final ShaderProgram animationShader = new ShaderProgram("boneAnimation.vert", "basic.frag");
		animationShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		scene.getLightManager().addShader(animationShader);

		scene.getSceneGraph().addSubObject(waterObject);

		player.setAnimationShader(animationShader);

		shared.playerEntity =
				player.spawnNew(new Vector3f(0, 10, 1), 0, scene.getSceneGraph(), shared.healthBarGUI,
						scene.getSoundEnvironment());
		world.add(shared.playerEntity);

		shared.playerEntity.setHeightSource(playerHeight);

		world.add(topDownViewCamera).addBehaviour(0, new TopDownViewBehaviour(shared.playerEntity, 7, 3, 1))
				.setPosition(port.add(new Vector3f(0.01f), new Vector3f()));

		final DirectionalLight sun = new DirectionalLight(Color.WHITE, new Vector3f(0.2f, -1, 0).normalize());
		scene.getLightManager().addLight(sun);

		shared.playerEntity.showHealthBar(null);

		hud = new AtlasGUI("menu.png", 4, 4);
		text = new TextGUI(new Font("Comic Sans MS", 0, 64));
		infoBox = text.addText(" ", new Vector2f());
		infoBox.setPositionRelativeToScreen(0.01f, 0.01f);
		infoBox.setColor(Color.WHITE);
		shared.playerEntity.getInventory().addLoot(LootType.Wood, 5);

		craftingHUD = new CraftingHUD(hud, text, shared.playerEntity);

		scene.getSoundEnvironment().getListener().asNewEntity().attachTo(topDownViewCamera.asEntity());
	}

	@Override
	public void update(final double deltaTime) {

		if (shared.playerEntity.newLastClearedLevel()) {
			infoBox.setText("Your cleared the dungeon level " + shared.playerEntity.getLastClearedLevel());
		}

		if (new Vector2f(anvilPos.x, anvilPos.z).distance(new Vector2f(shared.playerEntity.getPosition().x,
				shared.playerEntity.getPosition().z)) < 1.3f) {
			infoBox.setText("Press C for crafting at the anvil");
			if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_C)) {
				infoBox.setText(" ");
				crafting = true;
				craftingHUD.show();
				GLContext.showCursor();
			}
		}
		if (crafting) {
			if (KeyBoard.pullKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
				crafting = false;
				craftingHUD.hide();
				infoBox.setText(" ");
				GLContext.hideCursor();
			}
			craftingHUD.update(deltaTime);
		}
		totalTimePassed += deltaTime;
		world.update(deltaTime);

		scene.update(deltaTime);
		shared.healthBarGUI.update(deltaTime);
		text.update(deltaTime);
		hud.update(deltaTime);

	}

	@Override
	public void render() {
		waterShader.use();
		skybox.getTexture().bind();
		waterShader.setUniform("skybox", skybox.getTexture());
		waterShader.setUniform("time", totalTimePassed);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		scene.render(false);

		shared.healthBarGUI.render();
		hud.render();
		text.render();
	}

	@Override
	public GameStateIdentifier getNextState() {
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
			return GameStateIdentifier.INTRO;
		} else {
			if (shared.playerEntity.getPosition().distance(key) < threshold) {
				this.leftForDungeon = true;
				return GameStateIdentifier.DUNGEON;
			} else {
				return null;
			}
		}
	}

	@Override
	public void resetNextGameState() {
		//not needed
	}

	@Override
	public void open() {
		scene.getSceneGraph().addSubObjectFront(shared.overworld.getSceneObject());
		shared.overworld.getSceneObject().setShader(defaultShader);
		shared.overworld.getSceneObject().setTransformation(BoneTransformation.identity());
		shared.playerEntity.addTo(scene.getSceneGraph(), scene.getLightManager());
		shared.playerEntity.setHeightSource(playerHeight);
		shared.playerEntity.setPosition(port);
		if (shared.playerEntity.isJustDied()) {
			infoBox.setText("You died and lost half of your inventory.");
			shared.playerEntity.setJustDied(false);
		}
		if (leftForDungeon) {
			leftForDungeon = false;
		}
		GLContext.hideCursor();
		shared.soundEnvironment.makeCurrent();
		shared.playerEntity.setStoneWalkingSound(false);
		update(0.0001);
		scene.getLightManager().rewriteUBO();
	}

}
