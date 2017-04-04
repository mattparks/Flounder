package flounder.entities;

import flounder.entities.components.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.space.*;

import java.util.*;

/**
 * A generic object in the game.
 */
public class Entity implements ISpatialObject {
	private ISpatialStructure<Entity> structure;

	private List<IComponentEntity> components;

	private Vector3f position;
	private Vector3f rotation;
	private boolean hasMoved;

	private boolean removed;

	/**
	 * Creates a new Entity with minimum necessary construction.
	 *
	 * @param structure The spatial structure this entity will be contained in.
	 * @param position The location of the entity.
	 * @param rotation The rotation of the entity.
	 */
	public Entity(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		this.structure = structure;

		this.components = new ArrayList<>();

		this.position = position;
		this.rotation = rotation;
		this.hasMoved = true;

		this.removed = false;

		this.structure.add(this);
	}

	/**
	 * Gets a list of all entity components.
	 *
	 * @return All entity components in this entity.
	 */
	public List<IComponentEntity> getComponents() {
		return components;
	}

	/**
	 * Adds a new component to the entity.
	 *
	 * @param component The component to add.
	 */
	public void addComponent(IComponentEntity component) {
		components.add(component);
		setMoved();
	}

	/**
	 * Removes a component to the entity.
	 *
	 * @param component The component to remove.
	 */
	public void removeComponent(IComponentEntity component) {
		component.dispose();
		components.remove(component);
		setMoved();
	}

	/**
	 * Removes a component from this entity by id. If more than one is found, the first component in the list is removed. If none are found, nothing is removed.
	 *
	 * @param id The id of the component. This is typically found with ComponentClass.ID.
	 */
	public void removeComponent(int id) {
		for (IComponentEntity component : components) {
			if (component.getId() == id) {
				component.dispose();
				components.remove(component);
				setMoved();
			}
		}
	}

	/**
	 * Visits every entity with a particular component within a certain range of space.
	 *
	 * @param id The id of the component. This is typically found with ComponentClass.ID. If no particular component is desired, specify -1.
	 * @param range The range of space to be visited.
	 * @param visitor The visitor that will be executed for every entity visited.
	 */
	public void visitInRange(int id, AABB range, IComponentVisitor visitor) {
		for (Entity entity : structure.queryInBounding(range)) {
			if (entity.removed) {
				continue;
			}

			IComponentEntity component = id == -1 ? null : entity.getComponent(id);

			if (component != null || id == -1) {
				visitor.visit(entity, component);
			}
		}
	}

	/**
	 * Finds and returns a component attached to this entity by id. If more than one is found, the first component in the list is returned. If none are found, returns null.
	 *
	 * @param id The id of the component. This is typically found with ComponentClass.ID.
	 *
	 * @return The first component found with the given id, or null if none are found.
	 */
	public IComponentEntity getComponent(int id) {
		for (IComponentEntity component : components) {
			if (component.getId() == id) {
				return component;
			}
		}

		return null;
	}

	/**
	 * Updates all the components attached to this entity.
	 */
	public void update() {
		try {
			components.forEach(IComponentEntity::update);
		} catch (ConcurrentModificationException e) {
			FlounderLogger.exception(e);
		}

		hasMoved = false;
	}

	/**
	 * Moves this entity by a certain amount. If this entity is a colliding entity and it hits another colliding entity when it moves, then this will only verifyMove the entity as far as it can without intersecting a colliding entity.
	 *
	 * @param moveAmount The amount being moved.
	 * @param rotateAmount The amount being rotated.
	 */
	public void move(Vector3f moveAmount, Vector3f rotateAmount) {
		if (moveAmount.isZero() && rotateAmount.isZero()) {
			return;
		}

		for (IComponentEntity component : components) {
			if (IComponentMove.class.isInstance(component)) {
				((IComponentMove) component).verifyMove(this, moveAmount, rotateAmount);
				hasMoved = true;
			}
		}

		if (hasMoved) {
			position.set(
					position.x + moveAmount.x,
					position.y + moveAmount.y,
					position.z + moveAmount.z
			);
			rotation.set(
					Maths.normalizeAngle(rotation.x + rotateAmount.x),
					Maths.normalizeAngle(rotation.y + rotateAmount.y),
					Maths.normalizeAngle(rotation.z + rotateAmount.z)
			);
		}
	}

	/**
	 * Tells the entity components that the entity has moved.
	 */
	public void setMoved() {
		this.hasMoved = true;
	}

	/**
	 * Changes the structure this object is contained in.
	 *
	 * @param structure The new structure too be contained in.
	 */
	public void switchStructure(ISpatialStructure<Entity> structure) {
		structure.remove(this);
		this.structure = structure;
		structure.add(this);
	}

	public ISpatialStructure<Entity> getStructure() {
		return structure;
	}

	/**
	 * Forcibly removes this entity from the spatial structure without triggering remove actions. Use with caution; this function may fail or cause errors if used inappropriately.
	 *
	 * @param structureRemove If the entity will be removed from the structure from this method.
	 */
	public void forceRemove(boolean structureRemove) {
		removed = true;

		if (structureRemove) {
			structure.remove(this);
		}

		for (IComponentEntity component : components) {
			component.dispose();
		}
	}

	/**
	 * Gets whether or not this entity has been removed from the spatial structure.
	 *
	 * @return Whether or not this entity has been removed from the spatial structure.
	 */
	public boolean isRemoved() {
		return removed;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public boolean hasMoved() {
		return hasMoved;
	}

	@Override
	public Collider getCollider() {
		for (IComponentEntity component : components) {
			if (component instanceof IComponentCollider) {
				Collider bounding = ((IComponentCollider) component).getBounding();

				if (bounding != null) {
					return bounding;
				}
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return "Entity{" +
				"position=" + position +
				", rotation=" + rotation +
				", removed=" + removed +
				'}';
	}
}
