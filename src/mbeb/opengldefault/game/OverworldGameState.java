package mbeb.opengldefault.game;

import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.controls.KeyBoard;

public class OverworldGameState implements GameState {

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(double deltaTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	@Override
	public GameStateIdentifier getNextState() {
		return KeyBoard.isKeyDown(GLFW.GLFW_KEY_ESCAPE) ? GameStateIdentifier.INTRO : null;
	}

	@Override
	public void resetNextGameState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void open() {
		// TODO Auto-generated method stub

	}

}
