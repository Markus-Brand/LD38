package mbeb.opengldefault.game;

/**
 * An enum that is used by {@link Game}s to identify the current gameState.
 * Each gameState can link to another {@link GameState} by returning the corresponding {@link GameStateIdentifier} in their {@link GameState#getNextState()} method 
 *
 */
public enum GameStateIdentifier {
	MAIN_MENU,
	BUNNY_GAME,
	BEZIER_FLIGHT,
	EXIT
}
