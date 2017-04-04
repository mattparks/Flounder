package flounder.physics;

import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.bounding.*;
import flounder.space.*;

/**
 * A simple class that represents a physical shape.
 */
public abstract class Collider {
	/**
	 * Tests whether a shape is intersecting this shape.
	 *
	 * @param other The other shape being tested for intersection
	 *
	 * @return Data about the calculated shape intersection.
	 */
	public abstract IntersectData intersects(Collider other) throws IllegalArgumentException;

	/**
	 * Tests whether a ray is intersecting this shape.
	 *
	 * @param other The other ray being tested for intersection
	 *
	 * @return Data about the calculated ray intersection.
	 */
	public abstract IntersectData intersects(Ray other) throws IllegalArgumentException;

	/**
	 * Tests whether another this shape completely contains the other.
	 *
	 * @param other The shape being tested for containment
	 *
	 * @return True if {@code other} is contained by this shape, false otherwise.
	 */
	public abstract boolean contains(Collider other);

	/**
	 * Gets if a point is contained in this shape.
	 *
	 * @param point The point to check if it is contained.
	 *
	 * @return If the point is contained in this shape.
	 */
	public abstract boolean contains(Vector3f point);

	/**
	 * Gets if the shape is partially in the view frustum.
	 *
	 * @param frustum The view frustum.
	 *
	 * @return If the shape is partially in the view frustum.
	 */
	public abstract boolean inFrustum(Frustum frustum);

	/**
	 * Gets the (optional) model to be used in the {@link BoundingRenderer}.
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
