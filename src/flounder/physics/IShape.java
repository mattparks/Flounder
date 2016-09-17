package flounder.physics;

import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.space.*;

/**
 * A simple class that represents a physical shape.
 *
 * @param <T> The type of shape.
 */
public interface IShape<T extends IShape> {
	/**
	 * Tests whether another this shape completely contains the other.
	 *
	 * @param other The shape being tested for containment
	 *
	 * @return True if {@code other} is contained by this shape, false otherwise.
	 */
	boolean contains(T other);

	/**
	 * Tests whether another shape is intersecting this shape.
	 *
	 * @param other The other shape being tested for intersection
	 *
	 * @return Data about the calculated intersection.
	 */
	IntersectData intersects(T other) throws IllegalArgumentException;

	/**
	 * Calculates intersection between this shape and a ray.
	 *
	 * @param ray The ray to test if there is an intersection with.
	 *
	 * @return If there is a intersection between this shape and a ray.
	 */
	boolean intersectsRay(Ray ray);

	/**
	 * Gets if a point is contained in this shape.
	 *
	 * @param point The point to check if it is contained.
	 *
	 * @return If the point is contained in this shape.
	 */
	boolean contains(Vector3f point);

	/**
	 * Gets if the shape is partially in the view frustum.
	 *
	 * @param frustum The view frustum.
	 *
	 * @return If the shape is partially in the view frustum.
	 */
	boolean inFrustum(Frustum frustum);

	/**
	 * Gets the (optinal) model to be used in the {@link flounder.physics.renderer.ShapesRenderer}.
	 *
	 * @return A model that can be used to render this shape.
	 */
	Model getRenderModel();

	Vector3f getRenderCentre();

	Vector3f getRenderScale();

	@Override
	int hashCode();

	@Override
	boolean equals(Object object);

	@Override
	String toString();
}
