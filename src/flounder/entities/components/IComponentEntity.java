package flounder.entities.components;

import flounder.entities.*;

/**
 * Base class for all components that can be attached to engine.entities. (Have a constructor that takes in '(Entity entity, EntityTemplate template)' for the entity loader).
 */
public abstract class IComponentEntity {
	private Entity entity;

	/**
	 * Creates a component attached to a specific entity.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public IComponentEntity(Entity entity) {
		this.entity = entity;

		if (entity != null) {
			entity.addComponent(this);
		}
	}

	/**
	 * Gets the entity this is attached to.
	 *
	 * @return The entity this is attached to.
	 */
	public Entity getEntity() {
		return entity;
	}

	/**
	 * Updates this component.
	 */
	public abstract void update();

	/**
	 * Runs when the component is removed from the entity.
	 */
	public abstract void dispose();
}
