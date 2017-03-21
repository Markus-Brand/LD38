package mbeb.opengldefault.scene.entities;

import mbeb.opengldefault.camera.ICamera;
import mbeb.opengldefault.light.DirectionalLight;
import mbeb.opengldefault.light.Light;
import mbeb.opengldefault.light.PointLight;
import mbeb.opengldefault.light.SpotLight;
import mbeb.opengldefault.scene.SceneObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * A Collection of entities that can interact with each other
 */
public class EntityWorld {

	private Collection<IEntity> entities;

	public EntityWorld() {
		entities = new ArrayList<>();
	}

	public IEntity add(IEntity newEntity) {
		entities.add(newEntity);
		return newEntity;
	}

	public IEntity add(SceneObject objectToWrap) {
		return add(new SceneEntity(objectToWrap));
	}

	public IEntity add(ICamera objectToWrap) {
		return add(new CameraEntity(objectToWrap));
	}

	public IEntity add(Light objectToWrap) {
		if (objectToWrap instanceof DirectionalLight) {
			return add(new DirectionalLightEntity((DirectionalLight) objectToWrap));
		} else if (objectToWrap instanceof PointLight) {
			return add(new PointLightEntity((PointLight)objectToWrap));
		} else if (objectToWrap instanceof SpotLight) {
			return add(new SpotLightEntity((SpotLight)objectToWrap));
		}
		return null;
	}

	public void forEachEntity(Consumer<IEntity> action) {
		entities.forEach(action);
	}

	public void update(double deltaTime) {
		forEachEntity(entity -> entity.update(deltaTime));
	}
}
