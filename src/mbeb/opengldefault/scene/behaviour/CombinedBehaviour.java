package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.scene.entities.*;

/**
 * A Behaviour that enables multiple Behaviours to be applied one after another
 * 
 * @author Markus
 */
public class CombinedBehaviour implements IBehaviour {

	IBehaviour[] behaviours;

	public CombinedBehaviour(IBehaviour ... behaviours) {
		this.behaviours = behaviours;
	}

	@Override
	public boolean triggers(IEntity entity) {
		for (IBehaviour iBehaviour : behaviours) {
			if (iBehaviour.triggers(entity)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void update(double deltaTime, IEntity entity) {
		for (IBehaviour iBehaviour : behaviours) {
			iBehaviour.update(deltaTime, entity);
		}
	}

}
