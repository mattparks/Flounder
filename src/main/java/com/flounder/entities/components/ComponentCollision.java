package com.flounder.entities.components;

import com.flounder.entities.*;
import com.flounder.helpers.*;
import com.flounder.maths.vectors.*;
import com.flounder.physics.*;

import javax.swing.*;

/**
 * Component that detects collision between two engine.entities.
 * <p>
 * Note: this component requires that both engine.entities have a ComponentCollider. Should one entity not have a ComponentCollider, then no collisions will be detected, because there is no collider to detect collisions against.
 */
public class ComponentCollision extends IComponentEntity implements IComponentMove, IComponentEditor {
	/**
	 * Creates a new ComponentCollision.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentCollision(Entity entity) {
		super(entity);
	}

	@Override
	public void update() {
	}

	/**
	 * Resolves collisions with any other collision components encountered.
	 *
	 * @param amount The amount attempting to be moved.
	 *
	 * @return A new move vector that will not cause collisions after movement.
	 */
	public Vector3f resolveCollisions(Vector3f amount) {
		// Sets the resulting resolved collisions.
		Vector3f result = new Vector3f(amount);

		// Gets this entities collider.
		Collider collider1 = getEntity().getCollider();

		// Verifies that this entities main collider will work.
		if (collider1 == null) {
			return result;
		}

		AABB aabb1 = null;

		if (collider1 instanceof AABB) {
			aabb1 = (AABB) collider1;
		} else if (collider1 instanceof Sphere) {
			float radius = ((Sphere) collider1).getRadius();
			Vector3f pos = ((Sphere) collider1).getPosition();
			aabb1 = new AABB(new Vector3f(-radius + pos.x, -radius + pos.y, -radius + pos.z), new Vector3f(radius + pos.x, radius + pos.y, radius + pos.z));
		} else {
			return result;
		}

		// Gets a collider that may contain more colliders.
		ComponentCollider componentCollider1 = (ComponentCollider) getEntity().getComponent(ComponentCollider.class);

		// Calculates the range in where there can be collisions.
		final AABB collisionRange = AABB.stretch(aabb1, null, amount);

		// Goes though all entities in the collision range.
		getEntity().visitInRange(ComponentCollision.class, collisionRange, (Entity entity, IComponentEntity component) -> {
			// Ignores the original entity.
			if (entity.equals(getEntity())) {
				return;
			}

			// Gets the checked entities collider.
			Collider collider2 = entity.getCollider();

			// Verifies that the checked entities main collider will work.
			if (collider2 == null) {
				return;
			}

			// Gets a collider that may contain more colliders.
			ComponentCollider componentCollider2 = (ComponentCollider) entity.getComponent(ComponentCollider.class);

			// If the main collider intersects with the other entities general collider.
			if (collider2.intersects(collisionRange).isIntersection()) {
				// If the main colliders are the only ones use them.
				boolean hullLeft = componentCollider1 != null && componentCollider1.getQuickHull() != null && componentCollider1.getQuickHull().isLoaded();
				boolean hullRight = componentCollider1 != null && componentCollider1.getQuickHull() != null && componentCollider1.getQuickHull().isLoaded();

				//Collider colliderLeft = hullLeft ? componentCollider1.getQuickHull() : collider1;
				//Collider colliderRight = hullRight ? componentCollider2.getQuickHull() : collider2;
				//colliderLeft.resolveCollision(colliderRight, result, result);

				//if (hullLeft && hullRight) { // TODO: Make Quick Hull collisions work!
				//	componentCollider1.getQuickHull().resolveCollision(componentCollider2.getQuickHull(), result, result);
				//} else {
				collider1.resolveCollision(collider2, result, result);
				//}
			}
		});

		// The final resulting move amount.
		return result;
	}

	@Override
	public void verifyMove(Entity entity, Vector3f moveAmount, Vector3f rotateAmount) {
		moveAmount.set(resolveCollisions(moveAmount));
		// rotateAmount = rotateAmount; // TODO: Stop some rotations?
	}

	@Override
	public void addToPanel(JPanel panel) {
	}

	@Override
	public void editorUpdate() {
	}

	@Override
	public Pair<String[], String[]> getSaveValues(String entityName) {
		return new Pair<>(
				new String[]{}, // Static variables
				new String[]{} // Class constructor
		);
	}

	@Override
	public void dispose() {
	}
}
