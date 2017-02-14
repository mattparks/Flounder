package flounder.physics;

import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.bounding.*;
import flounder.space.*;

/**
 * A simple class that represents a physical shape.
 *
 * @param <T> The type of shape.
 */
public abstract class IBounding<T extends IBounding> {
	/**
	 * Tests whether another this shape completely contains the other.
	 *
	 * @param other The shape being tested for containment
	 *
	 * @return True if {@code other} is contained by this shape, false otherwise.
	 */
	public abstract boolean contains(T other);

	/**
	 * Gets if a point is contained in this shape.
	 *
	 * @param point The point to check if it is contained.
	 *
	 * @return If the point is contained in this shape.
	 */
	public abstract boolean contains(Vector3f point);

	public IntersectData intersects(IBounding bounding) {
		if (bounding instanceof AABB) {
			return intersects((AABB) bounding);
		} else if (bounding instanceof Sphere) {
			return intersects((Sphere) bounding);
		} else if (bounding instanceof Rectangle) {
			return intersects((Rectangle) bounding);
		}

		return null;
	}

	/**
	 * Tests whether a AABB is intersecting this shape.
	 *
	 * @param aabb The other AABB being tested for intersection
	 *
	 * @return Data about the calculated intersection.
	 */
	public abstract IntersectData intersects(AABB aabb) throws IllegalArgumentException;

	/**
	 * Tests whether a Sphere is intersecting this shape.
	 *
	 * @param sphere The other Sphere being tested for intersection
	 *
	 * @return Data about the calculated intersection.
	 */
	public abstract IntersectData intersects(Sphere sphere) throws IllegalArgumentException;

	/**
	 * Tests whether a Rectangle is intersecting this shape.
	 *
	 * @param rectangle The other Rectangle being tested for intersection
	 *
	 * @return Data about the calculated intersection.
	 */
	public abstract IntersectData intersects(Rectangle rectangle) throws IllegalArgumentException;

	/**
	 * Calculates intersection between this shape and a ray.
	 *
	 * @param ray The ray to test if there is an intersection with.
	 *
	 * @return Data about intersection between this shape and a ray.
	 */
	public abstract IntersectData intersects(Ray ray) throws IllegalArgumentException;

	/**
	 * Gets if the shape is partially in the view frustum.
	 *
	 * @param frustum The view frustum.
	 *
	 * @return If the shape is partially in the view frustum.
	 */
	public abstract boolean inFrustum(Frustum frustum);

	/**
	 * Gets the (optinal) model to be used in the {@link BoundingRenderer}.
	 *
	 * @return A model that can be used to render this shape.
	 */
	public abstract ModelObject getRenderModel();

	/**
	 * Gets the centre for the rendered model.
	 *
	 * @param destination The destination for the information.
	 *
	 * @return The centre for the rendered model.
	 */
	public abstract Vector3f getRenderCentre(Vector3f destination);

	/**
	 * Gets the scale for the rendered model.
	 *
	 * @param destination The destination for the information.
	 *
	 * @return The scale for the rendered model.
	 */
	public abstract Vector3f getRenderScale(Vector3f destination);

	/**
	 * Gets the colour for the rendered model.
	 *
	 * @param destination The destination for the information.
	 *
	 * @return The colour for the rendered model.
	 */
	public abstract Colour getRenderColour(Colour destination);
}
