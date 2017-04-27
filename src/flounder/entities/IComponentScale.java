package flounder.entities;

/**
 * Defines a function to be called when looking for entity scale.
 */
public interface IComponentScale {
	/**
	 * A method that can be implemented to a component that adds scale to the entity.
	 *
	 * @return The scale.
	 */
	float getScale();
}
