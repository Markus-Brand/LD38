package mbeb.opengldefault.scene.behaviour;

/**
 * A Behaviour that combines a {@link PitchYawMouseBehaviour} and a {@link FlyingKeyboardBehaviour} to Control a Entity
 * with User Input
 *
 * @author Markus
 */
public class PlayerControlBehaviour extends CombinedBehaviour {

	public PlayerControlBehaviour() {
		super(new PitchYawMouseBehaviour(), new FlyingKeyboardBehaviour());
	}

	public PlayerControlBehaviour(float pitch, float yaw, float rotationSpeed, float movementSpeed) {
		super(new PitchYawMouseBehaviour(pitch, yaw, rotationSpeed), new FlyingKeyboardBehaviour(movementSpeed));
	}
}
