package flounder.maths.rays;

import flounder.maths.vectors.*;

/**
 * Holds a 3 dimensional ray.
 */
public class Ray {
	public Vector3f position;
	public Vector3f direction;
	public Vector3f scaledDirection;
	public float distance;
	public float scalar;

	/**
	 * Creates a new ray.
	 *
	 * @param position The rays position.
	 * @param direction The rays direction.
	 * @param scalar The scale factor of the ray.
	 */
	public Ray(Vector3f position, Vector3f direction, float scalar) {
		this.position = position;
		this.direction = direction;
		this.scalar = scalar;
		this.scaledDirection = Vector3f.scale(direction, scalar, null);
	}

	/**
	 * Adds the scaled direction too the position to create the next ray.
	 */
	public void next() {
		Vector3f.add(position, scaledDirection, position);
		distance += scalar;
	}
}
