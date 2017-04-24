package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.*;

import java.awt.*;

import org.joml.*;
import org.lwjgl.glfw.*;

import mbeb.ld38.*;
import mbeb.ld38.overworld.*;
import mbeb.lifeforms.*;
import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.controls.*;
import mbeb.opengldefault.gl.*;
import mbeb.opengldefault.gl.shader.*;
import mbeb.opengldefault.gl.texture.*;
import mbeb.opengldefault.gui.*;
import mbeb.opengldefault.gui.elements.*;
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

	private Scene overworldScene;
	private EntityWorld world;
	private OverWorld overworld;

	private ShaderProgram waterShader;

	private float totalTimePassed;

	private Skybox skybox;

	private Camera topDownViewCamera;

	private ShaderProgram defaultShader;

	private final Player player;
	private final IHeightSource playerHeight;

	private final SharedData shared;

	private TextGUI text;
	private TextGUIElement infoBox;

	MonsterEntity goblinEntity;

	private final Vector3f anvilPos = new Vector3f(9.27f, 0.993f, -4.43f);

	@Option(category = "Game")
	@ButtonOption
	public static boolean showBBs = true;

	public OverworldGameState(final SharedData shared) {
		this.shared = shared;
		player = new Player(100, null, null);
		playerHeight = new HeightFromHeightMap(Texture.loadBufferedImage("overworldHeight.png"), new Rectangle(new Vector2f(-16), new Vector2f(32)), 2f, 1f);
	}

	@Override
	public void init() {

		world = new EntityWorld();

		topDownViewCamera = new PerspectiveCamera();

		skybox = new Skybox("beachbox/beach", "png");
		overworldScene = new Scene(topDownViewCamera, skybox);

		final IRenderable water = new ObjectLoader().loadFromFile("overworld/water.obj");

		waterShader = new ShaderProgram("water.frag", "planet.vert");
		waterShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		overworldScene.getLightManager().addShader(waterShader);
		//overworldScene.getSceneGraph().setShader(waterShader);

		defaultShader = new ShaderProgram("basic.frag", "basic.vert");
		defaultShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		overworldScene.getLightManager().addShader(defaultShader);
		overworldScene.getSceneGraph().setShader(defaultShader);

		final SceneObject waterObject = new SceneObject(water, new BoneTransformation(new Vector3f(), new Quaternionf(), new Vector3f(100)));
		waterObject.setShader(waterShader);

		final ShaderProgram animationShader = new ShaderProgram("boneAnimation.vert", "basic.frag");
		animationShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		overworldScene.getLightManager().addShader(animationShader);

		overworld = new OverWorld();
		overworldScene.getSceneGraph().addSubObject(overworld.getSceneObject());
		overworld.getSceneObject().addSubObject(waterObject);

		player.setAnimationShader(animationShader);

		shared.playerEntity = player.spawnNew(new Vector3f(0, 10, 1), 0, overworld.getSceneObject(), shared.healthBarGUI);
		world.add(shared.playerEntity);

		shared.playerEntity.setHeightSource(playerHeight);

		world.add(topDownViewCamera).addBehaviour(0, new TopDownViewBehaviour(shared.playerEntity, 7, 2, 2)).setPosition(new Vector3f(3, 4, 5));

		final DirectionalLight sun = new DirectionalLight(Color.WHITE, new Vector3f(0.2f, -1, 0).normalize());
		overworldScene.getLightManager().addLight(sun);

		final Goblin goblin = new Goblin(shared.playerEntity, animationShader);
		goblinEntity = goblin.spawnNew(new Vector3f(10, 3, 0), 0, overworld.getSceneObject(), shared.healthBarGUI);
		world.add(goblinEntity);

		shared.playerEntity.addTarsched(goblinEntity);

		goblinEntity.showHealthBar(topDownViewCamera);

		goblinEntity.addTarsched(shared.playerEntity);

		shared.playerEntity.showHealthBar(null);

		text = new TextGUI(new Font("Arial", 0, 64));
		infoBox = text.addText(" ", new Vector2f());
		infoBox.setPositionRelativeToScreen(0.01f, 0.01f);
		infoBox.setColor(Color.WHITE);

		final Chest chest = new Chest(animationShader, shared.playerEntity);
		final ChestEntity chestEntity = chest.spawnNew(new Vector3f(-5, 3, -5), 0, overworld.getSceneObject(), ce -> System.out.println("OPEJ"));
		world.add(chestEntity);
	}

	@Override
	public void update(final double deltaTime) {

		if (new Vector2f(anvilPos.x, anvilPos.z).distance(new Vector2f(shared.playerEntity.getPosition().x, shared.playerEntity.getPosition().z)) < 1.3f) {
			infoBox.setText("Press C for crafting");
		} else {
			infoBox.setText(" ");
		}

		totalTimePassed += deltaTime;
		overworldScene.update(deltaTime);
		world.update(deltaTime);

		if (goblinEntity.isDead()) {
			world.remove(goblinEntity);
		}
		shared.healthBarGUI.update(deltaTime);
		text.update(deltaTime);
	}

	@Override
	public void render() {
		waterShader.use();
		skybox.getTexture().bind();
		waterShader.setUniform("skybox", skybox.getTexture());
		waterShader.setUniform("time", totalTimePassed);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		overworldScene.render(showBBs);

		shared.healthBarGUI.render();
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
		shared.playerEntity.addTo(overworld.getSceneObject(), overworldScene.getLightManager());
		shared.playerEntity.setHeightSource(playerHeight);
		shared.playerEntity.setPosition(port);
		if (leftForDungeon) {
			leftForDungeon = false;
		}
		overworldScene.getLightManager().rewriteUBO();
		GLContext.hideCursor();
	}

}
