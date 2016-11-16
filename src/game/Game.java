package game;

import rendering.Shader;

public class Game {

	private Shader shader;

	public Game() {
		shader = new Shader("Basic.vert", "Basic.frag");
	}

	public void update(double deltaTime) {
		// TODO Auto-generated method stub

	}

}
