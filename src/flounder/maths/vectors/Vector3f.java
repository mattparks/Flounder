package flounder.maths.vectors;

import flounder.maths.*;
import flounder.maths.matrices.*;

import java.nio.*;

/**
 * Holds a 3-tuple vector.
 */
public class Vector3f {
	public float x, y, z;

	/**
	 * Constructor for Vector3f.
	 */
	public Vector3f() {
		set(0.0f, 0.0f, 0.0f);
	}

	/**
	 * Sets values in the vector.
	 *
	 * @param x The new X value.
	 * @param y The new Y value.
	 * @param z The new Z value.
	 *
	 * @return This.
	 */
	public Vector3f set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Vector3f set(String source) {
		String reduced = source.replace("Vector3f(", "").replace(")", "").trim();
		String[] split = reduced.split("\\|");
		this.x = Float.parseFloat(split[0].substring(2, split[0].length()));
		this.y = Float.parseFloat(split[1].substring(2, split[0].length()));
		this.z = Float.parseFloat(split[2].substring(2, split[0].length()));
		return this;
	}

	/**
	 * Constructor for Vector3f.
	 *
	 * @param source Creates this vector out of a existing one.
	 */
	public Vector3f(Vector3f source) {
		set(source);
	}

	/**
	 * Loads from another Vector3f.
	 *
	 * @param source The source vector.
	 *
	 * @return This.
	 */
	public Vector3f set(Vector3f source) {
		this.x = source.x;
		this.y = source.y;
		this.z = source.z;
		return this;
	}

	/**
	 * Constructor for Vector3f.
	 *
	 * @param source Creates this vector out of a existing one.
	 */
	public Vector3f(Vector4f source) {
		set(source.x, source.y, source.z);
	}

	/**
	 * Constructor for Vector3f.
	 *
	 * @param x Start x.
	 * @param y Start y.
	 * @param z Start z.
	 */
	public Vector3f(float x, float y, float z) {
		set(x, y, z);
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
	public static Vector3f add(Vector3f left, Vector3f right, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		return destination.set(left.x + right.x, left.y + right.y, left.z + right.z);
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
	public static Vector3f subtract(Vector3f left, Vector3f right, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		return destination.set(left.x - right.x, left.y - right.y, left.z - right.z);
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
	public static Vector3f multiply(Vector3f left, Vector3f right, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		return destination.set(left.x * right.x, left.y * right.y, left.z * right.z);
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
	public static Vector3f divide(Vector3f left, Vector3f right, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		return destination.set(left.x / right.x, left.y / right.y, left.z / right.z);
	}

	/**
	 * Calculates the angle between two vectors.
	 *
	 * @param left The left source vector.
	 * @param right The right source vector.
	 *
	 * @return The angle between the two vectors, in radians.
	 */
	public static float angle(Vector3f left, Vector3f right) {
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
	public static float dot(Vector3f left, Vector3f right) {
		return left.x * right.x + left.y * right.y + left.z * right.z;
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
		return x * x + y * y + z * z;
	}

	/**
	 * Scales a vector by a scalar and places the result in the destination vector.
	 *
	 * @param source The source vector.
	 * @param scalar The scalar value.
	 * @param destination The destination vector or null if a new vector is to be created.
	 *
	 * @return The destination vector.
	 */
	public static Vector3f scale(Vector3f source, float scalar, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		return destination.set(source.x * scalar, source.y * scalar, source.z * scalar);
	}

	/**
	 * Takes the cross product of two vectors and places the result in the destination vector.
	 *
	 * @param left The left source vector.
	 * @param right The right source vector.
	 * @param destination The destination vector or null if a new vector is to be created.
	 *
	 * @return The destination vector.
	 */
	public static Vector3f cross(Vector3f left, Vector3f right, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		return destination.set(left.y * right.z - left.z * right.y, right.x * left.z - right.z * left.x, left.x * right.y - left.y * right.x);
	}

	/**
	 * Rotates a vector and places the result in the destination vector.
	 *
	 * @param source The source vector.
	 * @param rotation The rotation amount.
	 * @param destination The destination vector or null if a new vector is to be created.
	 *
	 * @return The destination vector.
	 */
	public static Vector3f rotate(Vector3f source, Vector3f rotation, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		Matrix4f matrix = Matrix4f.transformationMatrix(new Vector3f(0.0f, 0.0f, 0.0f), rotation, new Vector3f(1.0f, 1.0f, 1.0f), null);
		Vector4f direction4 = new Vector4f(source.x, source.y, source.z, 1.0f);
		Matrix4f.transform(matrix, direction4, direction4);
		return destination.set(direction4.x, direction4.y, direction4.z);
	}

	/**
	 * Negates a vector and places the result in the destination vector.
	 *
	 * @param source The source vector.
	 * @param destination The destination vector or null if a new vector is to be created.
	 *
	 * @return The destination vector.
	 */
	public static Vector3f negate(Vector3f source, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		return destination.set(-source.x, -source.y, -source.z);
	}

	/**
	 * Normalizes a vector and places the result in the destination vector.
	 *
	 * @param source The source vector.
	 * @param destination The destination vector or null if a new vector is to be created.
	 *
	 * @return The destination vector.
	 */
	public static Vector3f normalize(Vector3f source, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		float length = source.length();
		return destination.set(source.x / length, source.y / length, source.z / length);
	}

	/**
	 * Gets the height on a point off of a 3d triangle.
	 *
	 * @param p1 Point 1 on the triangle.
	 * @param p2 Point 2 on the triangle.
	 * @param p3 Point 3 on the triangle.
	 * @param pos The XZ position of the object.
	 *
	 * @return Height of the triangle at the position.
	 */
	public static float baryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	/**
	 * Gets the maximum vector size.
	 *
	 * @param a The first vector to get values from.
	 * @param b The second vector to get values from.
	 *
	 * @return The maximum vector.
	 */
	public static Vector3f maxVector(Vector3f a, Vector3f b) {
		return new Vector3f(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));
	}

	/**
	 * Gets the lowest vector size.
	 *
	 * @param a The first vector to get values from.
	 * @param b The second vector to get values from.
	 *
	 * @return The lowest vector.
	 */
	public static Vector3f minVector(Vector3f a, Vector3f b) {
		return new Vector3f(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
	}

	/**
	 * Gets the maximum value in a vector.
	 *
	 * @param vector The value to get the maximum value from.
	 *
	 * @return The maximum value.
	 */
	public static float maxComponent(Vector3f vector) {
		return Maths.maxValue(vector.x, vector.y, vector.z);
	}

	/**
	 * Gets the lowest value in a vector.
	 *
	 * @param vector The value to get the lowest value from.
	 *
	 * @return The lowest value.
	 */
	public static float minComponent(Vector3f vector) {
		return Maths.minValue(vector.x, vector.y, vector.z);
	}

	/**
	 * Gets the distance between two points squared.
	 *
	 * @param point1 The first point.
	 * @param point2 The second point.
	 *
	 * @return The squared distance between the two points.
	 */
	public static float getDistanceSquared(Vector3f point1, Vector3f point2) {
		float dx = point1.x - point2.x;
		float dy = point1.y - point2.y;
		float dz = point1.z - point2.z;
		return dx * dx + dy * dy + dz * dz;
	}

	/**
	 * Gets the total distance between 2 vectors.
	 *
	 * @param point1 The first point.
	 * @param point2 The second point.
	 *
	 * @return The total distance between the points.
	 */
	public static float getDistance(Vector3f point1, Vector3f point2) {
		return (float) Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2) + Math.pow(point2.z - point1.z, 2));
	}

	/**
	 * Gets the vector distance between 2 vectors.
	 *
	 * @param point1 The first point.
	 * @param point2 The second point.
	 *
	 * @return The vector distance between the points.
	 */
	public static Vector3f getVectorDistance(Vector3f point1, Vector3f point2) {
		return new Vector3f((float) Math.pow(point2.x - point1.x, 2), (float) Math.pow(point2.y - point1.y, 2), (float) Math.pow(point2.z - point1.z, 2));
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
	 * @return The vectors z component.
	 */
	public float getZ() {
		return z;
	}

	/**
	 * Translates this vector.
	 *
	 * @param x The translation in x.
	 * @param y the translation in y.
	 * @param z the translation in z.
	 *
	 * @return This.
	 */
	public Vector3f translate(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	/**
	 * Negates this vector.
	 *
	 * @return This.
	 */
	public Vector3f negate() {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}

	/**
	 * Normalises this vector.
	 *
	 * @return This.
	 */
	public Vector3f normalize() {
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
	public Vector3f scale(float scale) {
		x *= scale;
		y *= scale;
		z *= scale;
		return this;
	}

	/**
	 * Loads this vector from a FloatBuffer.
	 *
	 * @param buffer The buffer to load it from, at the current position.
	 *
	 * @return This.
	 */
	public Vector3f load(FloatBuffer buffer) {
		x = buffer.get();
		y = buffer.get();
		z = buffer.get();
		return this;
	}

	/**
	 * Stores this vector in a FloatBuffer.
	 *
	 * @param buffer The buffer to store it in, at the current position.
	 *
	 * @return This.
	 */
	public Vector3f store(FloatBuffer buffer) {
		buffer.put(x);
		buffer.put(y);
		buffer.put(z);
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

		Vector3f other = (Vector3f) object;

		return x == other.x && y == other.y && z == other.z;
	}

	@Override
	public String toString() {
		return "Vector3f(" + "x=" + x + "| y=" + y + "| z=" + z + ")";
	}
}
