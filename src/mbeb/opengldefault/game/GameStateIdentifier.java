package mbeb.opengldefault.game;

/**
 * A enum that is used by {@link Game}s to identify the current gameState.
 * Each gameState can link to every other {@link GameState} by returning the corresponding {@link GameStateIdentifier} in their {@link GameState#getNextState()} method 
 *
 */
public enum GameStateIdentifier {
	MAIN_MENU, GAME, EXIT
}
