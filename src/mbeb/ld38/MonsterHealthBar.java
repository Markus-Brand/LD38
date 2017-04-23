package mbeb.ld38;

import mbeb.lifeforms.MonsterEntity;
import org.joml.Vector3f;

/**
 * a health bar that displas the health of a monsterEntity
 */
public class MonsterHealthBar {

	private MonsterEntity monster;
	private HealthBar bar;

	float lastHealth;

	public MonsterHealthBar(MonsterEntity entity) {
		this.monster = entity;
		this.bar = new HealthBar((int)entity.getHealthpoints());
		lastHealth = entity.getHealthpoints();
	}

	public void render() {
		bar.render();
	}

	public void update(double deltaTime) {
		float currentHealth = monster.getHealthpoints();
		bar.setHealth((int)currentHealth);

		float dist = lastHealth - currentHealth;
		dist *= (float) deltaTime;
		lastHealth -= dist;

		bar.setSecondHealth((int)lastHealth);
		bar.update(deltaTime);
	}

	/**
	 * @param positionOnScreen screen space coordinates
	 */
	public void setPosition(Vector3f positionOnScreen) {
		bar.setPosition((positionOnScreen.x + 1) / 2, (positionOnScreen.y + 1) / 2);
	}
}
