package flounder.maths.rays;

import flounder.maths.vectors.*;

public class Ray {
	public Vector3f position;
	public Vector3f direction;
	public Vector3f scaledDirection;
	public float distance;
	public float scalar;

	public Ray(Vector3f position, Vector3f direction, float scalar) {
		this.position = position;
		this.direction = direction;
		this.scalar = scalar;
		this.scaledDirection = scale(direction, scalar);
	}

	private Vector3f scale(Vector3f vector, float scalar) {
		Vector3f temp = new Vector3f();
		temp.x = vector.x * scalar;
		temp.y = vector.y * scalar;
		temp.z = vector.z * scalar;
		return temp;
	}

	public void next() {
		Vector3f.add(position, scaledDirection, position);
		distance += scalar;
	}
}
