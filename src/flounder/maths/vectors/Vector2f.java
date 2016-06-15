package flounder.maths.vectors;

import flounder.maths.*;

import java.nio.*;

/**
 * Holds a 2-tuple vector.
 */
public class Vector2f {
	public float x;
	public float y;

	/**
	 * Constructor for Vector2f.
	 */
	public Vector2f() {
		set(0.0f, 0.0f);
	}

	/**
	 * Sets values in the vector.
	 *
	 * @param x The new X value.
	 * @param y The new Y value.
	 *
	 * @return This.
	 */
	public Vector2f set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	/**
	 * Constructor for Vector2f.
	 *
	 * @param source Creates this vector out of a existing one.
	 */
	public Vector2f(Vector2f source) {
		set(source);
	}

	/**
	 * Loads from another Vector2f.
	 *
	 * @param source The source vector.
	 *
	 * @return This.
	 */
	public Vector2f set(Vector2f source) {
		this.x = source.x;
		this.y = source.y;
		return this;
	}

	/**
	 * Constructor for Vector2f.
	 *
	 * @param x Start x.
	 * @param y Start y.
	 */
	public Vector2f(float x, float y) {
		set(x, y);
	}

	/**
	 * Adds two vectors together and places the result in the destination vector.
	 *
	 * @param left The left source vector.
	 * @param right The right source vector.
	 * @param destination The destination vector or null if a new vector is to be created.
	 *
	 * @return The destination vector.
	 */
	public static Vector2f add(Vector2f left, Vector2f right, Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		return destination.set(left.x + right.x, left.y + right.y);
	}

	/**
	 * Subtracts two vectors from each other and places the result in the destination vector.
	 *
	 * @param left The left source vector.
	 * @param right The right source vector.
	 * @param destination The destination vector or null if a new vector is to be created.
	 *
	 * @return The destination vector.
	 */
	public static Vector2f subtract(Vector2f left, Vector2f right, Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		return destination.set(left.x - right.x, left.y - right.y);
	}

	/**
	 * Multiplies two vectors from each other and places the result in the destination vector.
	 *
	 * @param left The left source vector.
	 * @param right The right source vector.
	 * @param destination The destination vector or null if a new vector is to be created.
	 *
	 * @return The destination vector.
	 */
	public static Vector2f multiply(Vector2f left, Vector2f right, Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		return destination.set(left.x * right.x, left.y * right.y);
	}

	/**
	 * Divides two vectors from each other and places the result in the destination vector.
	 *
	 * @param left The left source vector.
	 * @param right The right source vector.
	 * @param destination The destination vector or null if a new vector is to be created.
	 *
	 * @return The destination vector.
	 */
	public static Vector2f divide(Vector2f left, Vector2f right, Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		return destination.set(left.x / right.x, left.y / right.y);
	}

	/**
	 * Calculates the angle between two vectors.
	 *
	 * @param left The left source vector.
	 * @param right The right source vector.
	 *
	 * @return The angle between the two vectors, in radians.
	 */
	public static float angle(Vector2f left, Vector2f right) {
		float dls = dot(left, right) / (left.length() * right.length());

		if (dls < -1f) {
			dls = -1f;
		} else if (dls > 1.0f) {
			dls = 1.0f;
		}

		return (float) Math.acos(dls);
	}

	/**
	 * Calculates the dot product of the two vectors.
	 *
	 * @param left The left source vector.
	 * @param right The right source vector.
	 *
	 * @return Left dot right.
	 */
	public static float dot(Vector2f left, Vector2f right) {
		return left.x * right.x + left.y * right.y;
	}

	/**
	 * @return The length of the vector.
	 */
	public float length() {
		return (float) Math.sqrt(lengthSquared());
	}

	/**
	 * @return The length squared of the vector.
	 */
	public float lengthSquared() {
		return (float) (Math.pow(x, 2) + Math.pow(y, 2));
	}

	/**
	 * Rotates a vector and places the result in the destination vector.
	 *
	 * @param source The source vector.
	 * @param angle The angle to rotate by <b>in degrees</b>.
	 * @param destination The destination vector or null if a new vector is to be created.
	 *
	 * @return The destination vector.
	 */
	public static Vector2f rotate(Vector2f source, float angle, Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		double theta = Math.toRadians(angle);
		return destination.set(
				(float) (source.x * Math.cos(theta) - source.y * Math.sin(theta)),
				(float) (source.x * Math.sin(theta) + source.y * Math.cos(theta))
		);
	}

	/**
	 * Rotates a vector around a point and places the result in the destination vector.
	 *
	 * @param source The source vector.
	 * @param angle The angle to rotate by <b>in degrees</b>.
	 * @param rotationAxis The point to rotate the vector around.
	 * @param destination The destination vector or null if a new vector is to be created.
	 *
	 * @return The destination vector.
	 */
	public static Vector2f rotate(Vector2f source, float angle, Vector2f rotationAxis, Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		double theta = Math.toRadians(angle);
		return destination.set(
				(float) (((source.x - rotationAxis.x) * Math.cos(theta)) - ((source.y - rotationAxis.y) * Math.sin(theta) + rotationAxis.x)),
				(float) (((source.x - rotationAxis.x) * Math.sin(theta)) + ((source.y - rotationAxis.y) * Math.cos(theta) + rotationAxis.y))
		);
	}

	/**
	 * Negates a vector and places the result in the destination vector.
	 *
	 * @param source The source vector.
	 * @param destination The destination vector or null if a new vector is to be created.
	 *
	 * @return The destination vector.
	 */
	public static Vector2f negate(Vector2f source, Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		return destination.set(-source.x, -source.y);
	}

	/**
	 * Normalizes a vector and places the result in the destination vector.
	 *
	 * @param source The source vector.
	 * @param destination The destination vector or null if a new vector is to be created.
	 *
	 * @return The destination vector.
	 */
	public static Vector2f normalize(Vector2f source, Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		float length = source.length();
		return destination.set(source.x / length, source.y / length);
	}

	/**
	 * Gets the maximum vector size.
	 *
	 * @param a The first vector to get values from.
	 * @param b The second vector to get values from.
	 *
	 * @return The maximum vector.
	 */
	public static Vector2f maxVector(Vector2f a, Vector2f b) {
		return new Vector2f(Math.max(a.x, b.x), Math.max(a.y, b.y));
	}

	/**
	 * Gets the lowest vector size.
	 *
	 * @param a The first vector to get values from.
	 * @param b The second vector to get values from.
	 *
	 * @return The lowest vector.
	 */
	public static Vector2f minVector(Vector2f a, Vector2f b) {
		return new Vector2f(Math.min(a.x, b.x), Math.min(a.y, b.y));
	}

	/**
	 * Gets the maximum value in a vector.
	 *
	 * @param vector The value to get the maximum value from.
	 *
	 * @return The maximum value.
	 */
	public static float maxComponent(Vector2f vector) {
		return Maths.maxValue(vector.x, vector.y);
	}

	/**
	 * Gets the lowest value in a vector.
	 *
	 * @param vector The value to get the lowest value from.
	 *
	 * @return The lowest value.
	 */
	public static float minComponent(Vector2f vector) {
		return Maths.minValue(vector.x, vector.y);
	}

	/**
	 * Gets the distance between two points squared.
	 *
	 * @param point1 The first point.
	 * @param point2 The second point.
	 *
	 * @return The squared distance between the two points.
	 */
	public static float getDistanceSquared(Vector2f point1, Vector2f point2) {
		float dx = point1.x - point2.x;
		float dy = point1.y - point2.y;
		return dx * dx + dy * dy;
	}

	/**
	 * Gets the total distance between 2 vectors.
	 *
	 * @param point1 The first point.
	 * @param point2 The second point.
	 *
	 * @return The total distance between the points.
	 */
	public static float getDistance(Vector2f point1, Vector2f point2) {
		return (float) Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2));
	}

	/**
	 * Gets the vector distance between 2 vectors.
	 *
	 * @param point1 The first point.
	 * @param point2 The second point.
	 *
	 * @return The vector distance between the points.
	 */
	public static Vector2f getVectorDistance(Vector2f point1, Vector2f point2) {
		return new Vector2f((float) Math.pow(point2.x - point1.x, 2), (float) Math.pow(point2.y - point1.y, 2));
	}

	/**
	 * @return The vectors x component.
	 */
	public float getX() {
		return x;
	}

	/**
	 * @return The vectors y component.
	 */
	public float getY() {
		return y;
	}

	/**
	 * Translates this vector.
	 *
	 * @param x The translation in x.
	 * @param y the translation in y.
	 *
	 * @return This.
	 */
	public Vector2f translate(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	/**
	 * Negates this vector.
	 *
	 * @return This.
	 */
	public Vector2f negate() {
		x = -x;
		y = -y;
		return this;
	}

	/**
	 * Normalises this vector.
	 *
	 * @return This.
	 */
	public Vector2f normalize() {
		float length = length();

		if (length != 0.0f) {
			float l = 1.0f / length;
			return scale(l);
		} else {
			throw new IllegalStateException("Zero length vector");
		}
	}

	/**
	 * Scales this vector.
	 *
	 * @param scale The scale factor.
	 *
	 * @return this.
	 */
	public Vector2f scale(float scale) {
		x *= scale;
		y *= scale;
		return this;
	}

	/**
	 * Loads this vector from a FloatBuffer.
	 *
	 * @param buffer The buffer to load it from, at the current position.
	 *
	 * @return This.
	 */
	public Vector2f load(FloatBuffer buffer) {
		x = buffer.get();
		y = buffer.get();
		return this;
	}

	/**
	 * Stores this vector in a FloatBuffer.
	 *
	 * @param buffer The buffer to store it in, at the current position.
	 *
	 * @return This.
	 */
	public Vector2f store(FloatBuffer buffer) {
		buffer.put(x);
		buffer.put(y);
		return this;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (object == null) {
			return false;
		}

		if (getClass() != object.getClass()) {
			return false;
		}

		Vector2f other = (Vector2f) object;

		return x == other.x && y == other.y;
	}

	@Override
	public String toString() {
		return "Vector2f{" + "x=" + x + ", y=" + y + "}";
	}
}
