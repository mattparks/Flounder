package com.flounder.maths.vectors;

import com.flounder.maths.matrices.*;

import java.nio.*;

/**
 * A vector like object of the form w + xi + yj + zk, where w, x, y, z are real numbers and i, j, k are imaginary units.
 */
public class Quaternion {
	public float x, y, z, w;

	/**
	 * Constructor for Quaternion.
	 */
	public Quaternion() {
		set(0.0f, 0.0f, 0.0f, 0.0f);
	}

	/**
	 * Sets values in the quaternion.
	 *
	 * @param x The new X value.
	 * @param y The new Y value.
	 * @param z The new Z value.
	 * @param w The new W value.
	 *
	 * @return This.
	 */
	public Quaternion set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}

	/**
	 * Constructor for Quaternion.
	 *
	 * @param source Creates this quaternion out of a existing one.
	 */
	public Quaternion(Quaternion source) {
		set(source);
	}

	/**
	 * Constructor for Quaternion.
	 *
	 * @param source Creates this quaternion out of a matrix one.
	 */
	public Quaternion(Matrix4f source) {
		set(source);
	}

	/**
	 * Loads from another Quaternion.
	 *
	 * @param source The source quaternion.
	 *
	 * @return This.
	 */
	public Quaternion set(Quaternion source) {
		this.x = source.x;
		this.y = source.y;
		this.z = source.z;
		this.w = source.w;
		return this;
	}

	/**
	 * Loads from a Matrix4f.
	 *
	 * @param source The source matrix.
	 *
	 * @return This.
	 */
	public Quaternion set(Matrix4f source) {
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
		this.w = 0.0f;

		float diagonal = source.m00 + source.m11 + source.m22;
		if (diagonal > 0) {
			float w4 = (float) (Math.sqrt(diagonal + 1f) * 2f);
			w = w4 / 4f;
			x = (source.m21 - source.m12) / w4;
			y = (source.m02 - source.m20) / w4;
			z = (source.m10 - source.m01) / w4;
		} else if ((source.m00 > source.m11) && (source.m00 > source.m22)) {
			float x4 = (float) (Math.sqrt(1f + source.m00 - source.m11 - source.m22) * 2f);
			w = (source.m21 - source.m12) / x4;
			x = x4 / 4f;
			y = (source.m01 + source.m10) / x4;
			z = (source.m02 + source.m20) / x4;
		} else if (source.m11 > source.m22) {
			float y4 = (float) (Math.sqrt(1f + source.m11 - source.m00 - source.m22) * 2f);
			w = (source.m02 - source.m20) / y4;
			x = (source.m01 + source.m10) / y4;
			y = y4 / 4f;
			z = (source.m12 + source.m21) / y4;
		} else {
			float z4 = (float) (Math.sqrt(1f + source.m22 - source.m00 - source.m11) * 2f);
			w = (source.m10 - source.m01) / z4;
			x = (source.m02 + source.m20) / z4;
			y = (source.m12 + source.m21) / z4;
			z = z4 / 4f;
		}

		return this;
	}

	/**
	 * Constructor for Quaternion.
	 *
	 * @param x Start x.
	 * @param y Start y.
	 * @param z Start z.
	 * @param w Start w.
	 */
	public Quaternion(float x, float y, float z, float w) {
		set(x, y, z, w);
	}

	/**
	 * Sets the value of this quaternion to the quaternion product of quaternions left and right (this = left * right). Note that this is safe for aliasing (e.g. this can be left or right).
	 *
	 * @param left The left source quaternion.
	 * @param right The right source quaternion.
	 * @param destination The destination quaternion or null if a new quaternion is to be created.
	 *
	 * @return The destination quaternion.
	 */
	public static Quaternion multiply(Quaternion left, Quaternion right, Quaternion destination) {
		if (destination == null) {
			destination = new Quaternion();
		}

		return destination.set(left.x * right.w + left.w * right.x + left.y * right.z - left.z * right.y, left.y * right.w + left.w * right.y + left.z * right.x - left.x * right.z,
				left.z * right.w + left.w * right.z + left.x * right.y - left.y * right.x, left.w * right.w - left.x * right.x - left.y * right.y - left.z * right.z);
	}

	/**
	 * Multiplies quaternion left by the inverse of quaternion right and places the value into this quaternion. The value of both argument quaternions is persevered (this = left * right^-1).
	 *
	 * @param left The left source quaternion.
	 * @param right The right source quaternion.
	 * @param destination The destination quaternion or null if a new quaternion is to be created.
	 *
	 * @return The destination quaternion.
	 */
	public static Quaternion multiplyInverse(Quaternion left, Quaternion right, Quaternion destination) {
		if (destination == null) {
			destination = new Quaternion();
		}

		float n = right.lengthSquared();
		n = (n == 0.0f ? n : 1.0f / n);
		return destination.set((left.x * right.w - left.w * right.x - left.y * right.z + left.z * right.y) * n,
				(left.y * right.w - left.w * right.y - left.z * right.x + left.x * right.z) * n,
				(left.z * right.w - left.w * right.z - left.x * right.y + left.y * right.x) * n,
				(left.w * right.w + left.x * right.x + left.y * right.y + left.z * right.z) * n);
	}

	/**
	 * @return The length squared of the vector.
	 */
	public float lengthSquared() {
		return (float) (Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2) + Math.pow(w, 2));
	}

	/**
	 * Calculates the dot product of the two quaternions.
	 *
	 * @param left The left source quaternion.
	 * @param right The right source quaternion.
	 *
	 * @return Left dot right.
	 */
	public static float dot(Quaternion left, Quaternion right) {
		return left.x * right.x + left.y * right.y + left.z * right.z + left.w * right.w;
	}

	/**
	 * Negates a quaternion and places the result in the destination quaternion.
	 *
	 * @param source The source quaternion.
	 * @param destination The destination quaternion or null if a new quaternion is to be created.
	 *
	 * @return The destination quaternion.
	 */
	public static Quaternion negate(Quaternion source, Quaternion destination) {
		if (destination == null) {
			destination = new Quaternion();
		}

		return destination.set(-source.x, -source.y, -source.z, -source.w);
	}

	/**
	 * Normalizes a quaternion and places the result in the destination quaternion.
	 *
	 * @param source The source quaternion.
	 * @param destination The destination quaternion or null if a new quaternion is to be created.
	 *
	 * @return The destination quaternion.
	 */
	public static Quaternion normalize(Quaternion source, Quaternion destination) {
		if (destination == null) {
			destination = new Quaternion();
		}

		float length = source.length();
		return destination.set(source.x / length, source.y / length, source.z / length, source.w / length);
	}

	/**
	 * @return The length of the vector.
	 */
	public float length() {
		return (float) Math.sqrt(lengthSquared());
	}

	public static Quaternion convertFromAxisAngle(double angle, double x, double y, double z) {
		double angleOverTwo = Math.toRadians(angle) / 2.0D;
		double sinAngleOverTwo = Math.sin(angleOverTwo);
		double newW = Math.cos(angleOverTwo);
		double newX = x * sinAngleOverTwo;
		double newY = y * sinAngleOverTwo;
		double newZ = z * sinAngleOverTwo;
		return new Quaternion((float) newW, (float) newX, (float) newY, (float) newZ);
	}

	public static Quaternion slerp(Quaternion start, Quaternion end, float progression) {
		start.normalize();
		end.normalize();
		final float d = start.x * end.x + start.y * end.y + start.z * end.z + start.w * end.w;
		float absDot = d < 0.0f ? -d : d;
		float scale0 = 1.0f - progression;
		float scale1 = progression;

		if ((1.0f - absDot) > 0.1f) {
			final float angle = (float) Math.acos(absDot);
			final float invSinTheta = 1.0f / (float) Math.sin(angle);
			scale0 = ((float) Math.sin((1.0f - progression) * angle) * invSinTheta);
			scale1 = ((float) Math.sin((progression * angle)) * invSinTheta);
		}

		if (d < 0.0f) {
			scale1 = -scale1;
		}

		float newX = (scale0 * start.x) + (scale1 * end.x);
		float newY = (scale0 * start.y) + (scale1 * end.y);
		float newZ = (scale0 * start.z) + (scale1 * end.z);
		float newW = (scale0 * start.w) + (scale1 * end.w);
		return new Quaternion(newX, newY, newZ, newW);
	}

	public static double dotProductOfQuaternions(Quaternion qA, Quaternion qB) {
		return qA.getW() * qB.getW() + qA.getX() * qB.getX() + qA.getY() * qB.getY() + qA.getZ() * qB.getZ();
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

	public static Matrix4f quaternionToMatrix(double w, double x, double y, double z) {
		double xSquared = x * x;
		double twoXY = 2.0D * x * y;
		double twoXZ = 2.0D * x * z;
		double twoXW = 2.0D * x * w;
		double ySquared = y * y;
		double twoYZ = 2.0D * y * z;
		double twoYW = 2.0D * y * w;
		double twoZW = 2.0D * z * w;
		double zSquared = z * z;
		double wSquared = w * w;

		Matrix4f matrix = new Matrix4f();
		matrix.m00 = ((float) (wSquared + xSquared - ySquared - zSquared));
		matrix.m01 = ((float) (twoXY - twoZW));
		matrix.m02 = ((float) (twoXZ + twoYW));
		matrix.m03 = 0.0F;
		matrix.m10 = ((float) (twoXY + twoZW));
		matrix.m11 = ((float) (wSquared - xSquared + ySquared - zSquared));
		matrix.m12 = ((float) (twoYZ - twoXW));
		matrix.m13 = 0.0F;
		matrix.m20 = ((float) (twoXZ - twoYW));
		matrix.m21 = ((float) (twoYZ + twoXW));
		matrix.m22 = ((float) (wSquared - xSquared - ySquared + zSquared));
		matrix.m23 = 0.0F;
		matrix.m30 = 0.0F;
		matrix.m31 = 0.0F;
		matrix.m32 = 0.0F;
		matrix.m33 = 1.0F;
		return matrix;
	}

	/**
	 * Sets the value of this quaternion to the equivalent rotation of the Axis-Angle argument.
	 *
	 * @param axisAngle The axis-angle: (x,y,z) is the axis and w is the angle.
	 */
	public void setFromAxisAngle(Vector4f axisAngle) {
		x = axisAngle.x;
		y = axisAngle.y;
		z = axisAngle.z;
		float n = (float) Math.sqrt(x * x + y * y + z * z);
		float s = (float) (Math.sin(0.5 * axisAngle.w) / n);
		x *= s;
		y *= s;
		z *= s;
		w = (float) Math.cos(0.5 * axisAngle.w);
	}

	/**
	 * Sets the value of this quaternion using the rotational component of the passed matrix.
	 *
	 * @param matrix The matrix.
	 *
	 * @return The quaternion set from the matrix.
	 */
	public Quaternion setFromMatrix(Matrix4f matrix) {
		return setFromMatrix(matrix, this);
	}

	/**
	 * Sets the value of the source quaternion using the rotational component of the passed matrix.
	 *
	 * @param matrix The source matrix.
	 * @param destination The destination quaternion, or null if a new quaternion is to be created.
	 *
	 * @return The destination quaternion.
	 */
	public static Quaternion setFromMatrix(Matrix4f matrix, Quaternion destination) {
		if (destination == null) {
			destination = new Quaternion();
		}

		return destination.setFromMatrix(matrix.m00, matrix.m01, matrix.m02, matrix.m10, matrix.m11, matrix.m12, matrix.m20, matrix.m21, matrix.m22);
	}

	/**
	 * Private method to perform the matrix-to-quaternion conversion.
	 *
	 * @param m00 m00
	 * @param m01 m01
	 * @param m02 m02
	 * @param m10 m10
	 * @param m11 m11
	 * @param m12 m12
	 * @param m20 m20
	 * @param m21 m21
	 * @param m22 m22
	 *
	 * @return Quaternion set from a matrix.
	 */
	private Quaternion setFromMatrix(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
		float s;
		float tr = m00 + m11 + m22;

		if (tr >= 0.0) {
			s = (float) Math.sqrt(tr + 1.0);
			w = s * 0.5f;
			s = 0.5f / s;
			x = (m21 - m12) * s;
			y = (m02 - m20) * s;
			z = (m10 - m01) * s;
		} else {
			float max = Math.max(Math.max(m00, m11), m22);
			if (max == m00) {
				s = (float) Math.sqrt(m00 - (m11 + m22) + 1.0f);
				x = s * 0.5f;
				s = 0.5f / s;
				y = (m01 + m10) * s;
				z = (m20 + m02) * s;
				w = (m21 - m12) * s;
			} else if (max == m11) {
				s = (float) Math.sqrt(m11 - (m22 + m00) + 1.0f);
				y = s * 0.5f;
				s = 0.5f / s;
				z = (m12 + m21) * s;
				x = (m01 + m10) * s;
				w = (m02 - m20) * s;
			} else {
				s = (float) Math.sqrt(m22 - (m00 + m11) + 1.0f);
				z = s * 0.5f;
				s = 0.5f / s;
				x = (m20 + m02) * s;
				y = (m12 + m21) * s;
				w = (m10 - m01) * s;
			}
		}

		return this;
	}

	/**
	 * Converts the quaternion to a 4x4 matrix representing the exact same
	 * rotation as this quaternion. (The rotation is only contained in the
	 * top-left 3x3 part, but a 4x4 matrix is returned here for convenience
	 * seeing as it will be multiplied with other 4x4 matrices).
	 *
	 * @return The rotation matrix which represents the exact same rotation as this quaternion.
	 */
	public Matrix4f toRotationMatrix() {
		Matrix4f matrix = new Matrix4f();
		final float xy = x * y;
		final float xz = x * z;
		final float xw = x * w;
		final float yz = y * z;
		final float yw = y * w;
		final float zw = z * w;
		final float xSquared = x * x;
		final float ySquared = y * y;
		final float zSquared = z * z;
		matrix.m00 = 1 - 2 * (ySquared + zSquared);
		matrix.m01 = 2 * (xy - zw);
		matrix.m02 = 2 * (xz + yw);
		matrix.m03 = 0;
		matrix.m10 = 2 * (xy + zw);
		matrix.m11 = 1 - 2 * (xSquared + zSquared);
		matrix.m12 = 2 * (yz - xw);
		matrix.m13 = 0;
		matrix.m20 = 2 * (xz - yw);
		matrix.m21 = 2 * (yz + xw);
		matrix.m22 = 1 - 2 * (xSquared + ySquared);
		matrix.m23 = 0;
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		matrix.m33 = 1;
		return matrix;
	}

	/**
	 * Sets the value of this quaternion using the rotational component of the passed matrix.
	 *
	 * @param matrix The source matrix.
	 *
	 * @return The quaternion set from the matrix.
	 */
	public Quaternion setFromMatrix(Matrix3f matrix) {
		return setFromMatrix(matrix, this);
	}

	/**
	 * Sets the value of the source quaternion using the rotational component of the passed matrix.
	 *
	 * @param matrix The source matrix
	 * @param destination The destination quaternion, or null if a new quaternion is to be created
	 *
	 * @return The destination quaternion.
	 */
	public static Quaternion setFromMatrix(Matrix3f matrix, Quaternion destination) {
		if (destination == null) {
			destination = new Quaternion();
		}

		return destination.setFromMatrix(matrix.m00, matrix.m01, matrix.m02, matrix.m10, matrix.m11, matrix.m12, matrix.m20, matrix.m21, matrix.m22);
	}

	/**
	 * Set this quaternion to the multiplication identity.
	 *
	 * @return This
	 */
	public Quaternion setIdentity() {
		return setIdentity(this);
	}

	/**
	 * Set the given quaternion to the multiplication identity.
	 *
	 * @param q The quaternion
	 *
	 * @return q
	 */
	public static Quaternion setIdentity(Quaternion q) {
		q.x = 0;
		q.y = 0;
		q.z = 0;
		q.w = 1;
		return q;
	}

	/**
	 * Negates this quaternion.
	 *
	 * @return This.
	 */
	public Quaternion negate() {
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
	public Quaternion normalize() {
		float length = length();

		if (length != 0.0f) {
			float l = 1.0f / length;
			return scale(l);
		} else {
			throw new IllegalStateException("Zero length vector");
		}
	}

	/**
	 * Scales this quaternion.
	 *
	 * @param scale The scale factor.
	 *
	 * @return This.
	 */
	public Quaternion scale(float scale) {
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
	public Quaternion load(FloatBuffer buffer) {
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
	public Quaternion store(FloatBuffer buffer) {
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

		Quaternion other = (Quaternion) object;

		return x == other.x && y == other.y && z == other.z && w == other.w;
	}

	@Override
	public String toString() {
		return "Quaternion{" + x + ", " + y + ", " + z + ", " + w + "}";
	}
}
