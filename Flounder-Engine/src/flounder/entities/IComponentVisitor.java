package flounder.entities;

/**
 * Defines a function to be called when visiting entities.
 */
public interface IComponentVisitor {
	/**
	 * A function that will be called every time an entity is visited.
	 *
	 * @param entity The entity being visited.
	 * @param component The component of the entity being visited, if relevant, or null otherwise.
	 */
	void visit(Entity entity, IComponentEntity component);
}
