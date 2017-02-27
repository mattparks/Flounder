package flounder.entities.components;

import flounder.entities.*;

/**
 * Base class for all components that can be attached to engine.entities. (Have a constructor that takes in '(Entity entity, EntityTemplate template)' for the entity loader).
 */
public abstract class IComponentEntity {
	private Entity entity;
	private int id;

	/**
	 * Creates a component attached to a specific entity.
	 *
	 * @param entity The entity this component is attached to.
	 * @param id The id identifying the type of component. This should be unique to the subclass, but not unique to the object.
	 */
	public IComponentEntity(Entity entity, int id) {
		this.entity = entity;
		this.id = id;

		if (entity != null) {
			entity.addComponent(this);
		}
	}

	/**
	 * Gets the id of this component.
	 *
	 * @return The id of this component.
	 */
	public int getId() {
		return id;
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
