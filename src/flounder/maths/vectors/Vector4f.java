package flounder.maths.vectors;

import java.nio.*;

/**
 * Holds a 4-tuple vector.
 */
public class Vector4f {
	public float x, y, z, w;

	/**
	 * Constructor for Vector4f.
	 */
	public Vector4f() {
		set(0.0f, 0.0f, 0.0f, 0.0f);
	}

	/**
	 * Sets values in the vector.
	 *
	 * @param x The new X value.
	 * @param y The new Y value.
	 * @param z The new Z value.
	 * @param w The new W value.
	 *
	 * @return This.
	 */
	public Vector4f set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}

	/**
	 * Constructor for Vector4f.
	 *
	 * @param source Creates this vector out of a existing one.
	 */
	public Vector4f(Vector4f source) {
		set(source);
	}

	/**
	 * Loads from another Vector3f.
	 *
	 * @param source The source vector.
	 *
	 * @return This.
	 */
	public Vector4f set(Vector4f source) {
		if (source == null) {
			return this;
		}

		this.x = source.x;
		this.y = source.y;
		this.z = source.z;
		return this;
	}

	/**
	 * Constructor for Vector3f.
	 *
	 * @param x Start x.
	 * @param y Start y.
	 * @param z Start z.
	 * @param w Start w.
	 */
	public Vector4f(float x, float y, float z, float w) {
		set(x, y, z, w);
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
	public static Vector4f add(Vector4f left, Vector4f right, Vector4f destination) {
		if (destination == null) {
			destination = new Vector4f();
		}

		return destination.set(left.x + right.x, left.y + right.y, left.z + right.z, left.w + right.w);
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
	public static Vector4f subtract(Vector4f left, Vector4f right, Vector4f destination) {
		if (destination == null) {
			destination = new Vector4f();
		}

		return destination.set(left.x - right.x, left.y - right.y, left.z - right.z, left.w - right.w);
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
	public static Vector4f multiply(Vector4f left, Vector4f right, Vector4f destination) {
		if (destination == null) {
			destination = new Vector4f();
		}

		return destination.set(left.x * right.x, left.y * right.y, left.z * right.z, left.w * right.w);
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
	public static Vector4f divide(Vector4f left, Vector4f right, Vector4f destination) {
		if (destination == null) {
			destination = new Vector4f();
		}

		return destination.set(left.x / right.x, left.y / right.y, left.z / right.z, left.w / right.w);
	}

	/**
	 * Calculates the angle between two vectors.
	 *
	 * @param left The left source vector.
	 * @param right The right source vector.
	 *
	 * @return The angle between the two vectors, in radians.
	 */
	public static float angle(Vector4f left, Vector4f right) {
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
	public static float dot(Vector4f left, Vector4f right) {
		return left.x * right.x + left.y * right.y + left.z * right.z + left.w * right.w;
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
		return x * x + y * y + z * z + w * w;
	}

	/**
	 * Negates a vector and places the result in the destination vector.
	 *
	 * @param source The source vector.
	 * @param destination The destination vector or null if a new vector is to be created.
	 *
	 * @return The destination vector.
	 */
	public static Vector4f negate(Vector4f source, Vector4f destination) {
		if (destination == null) {
			destination = new Vector4f();
		}

		return destination.set(-source.x, -source.y, -source.z, -source.w);
	}

	/**
	 * Normalizes a vector and places the result in the destination vector.
	 *
	 * @param source The source vector.
	 * @param destination The destination vector or null if a new vector is to be created.
	 *
	 * @return The destination vector.
	 */
	public static Vector4f normalize(Vector4f source, Vector4f destination) {
		if (destination == null) {
			destination = new Vector4f();
		}

		float length = source.length();
		return destination.set(source.x / length, source.y / length, source.z / length, source.w / length);
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
	 * @return The vectors w component.
	 */
	public float getW() {
		return w;
	}

	/**
	 * Translates this vector.
	 *
	 * @param x The translation in x.
	 * @param y the translation in y.
	 * @param z the translation in z.
	 * @param w the translation in w.
	 *
	 * @return This.
	 */
	public Vector4f translate(float x, float y, float z, float w) {
		this.x += x;
		this.y += y;
		this.z += z;
		this.w += w;
		return this;
	}

	/**
	 * Negates this vector.
	 *
	 * @return This.
	 */
	public Vector4f negate() {
		x = -x;
		y = -y;
		z = -z;
		w = -w;
		return this;
	}

	/**
	 * Normalises this vector.
	 *
	 * @return This.
	 */
	public Vector4f normalize() {
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
	 * @return This.
	 */
	public Vector4f scale(float scale) {
		x *= scale;
		y *= scale;
		z *= scale;
		w *= scale;
		return this;
	}

	/**
	 * Loads this vector from a FloatBuffer.
	 *
	 * @param buffer The buffer to load it from, at the current position.
	 *
	 * @return This.
	 */
	public Vector4f load(FloatBuffer buffer) {
		x = buffer.get();
		y = buffer.get();
		z = buffer.get();
		w = buffer.get();
		return this;
	}

	/**
	 * Stores this vector in a FloatBuffer.
	 *
	 * @param buffer The buffer to store it in, at the current position.
	 *
	 * @return This.
	 */
	public Vector4f store(FloatBuffer buffer) {
		buffer.clear();
		buffer.put(x);
		buffer.put(y);
		buffer.put(z);
		buffer.put(w);
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

		Vector4f other = (Vector4f) object;

		return x == other.x && y == other.y && z == other.z && w == other.w;
	}

	@Override
	public String toString() {
		return "Vector3f{" + "x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + "}";
	}
}
