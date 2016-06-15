package flounder.maths.vectors;

import flounder.maths.matrices.*;

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
	 * Multiplies quaternion left by the inverse of quaternion right and places the value into this quaternion. The value of both argument quaternions is preservered (this = left * right^-1).
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

	public static Quaternion slerp(Quaternion qA, Quaternion qB, double time) {
		double cosHalfTheta = dotProductOfQuaternions(qA, qB);
		if (Math.abs(cosHalfTheta) >= 1.0D) {
			return new Quaternion(qA.getW(), qA.getX(), qA.getY(), qA.getZ());
		}
		double halfTheta = Math.acos(cosHalfTheta);
		double sinHalfTheta = Math.sqrt(1.0D - cosHalfTheta * cosHalfTheta);
		double ratioA = 0.5D;
		double ratioB = 0.5D;
		if (Math.abs((float) sinHalfTheta) >= 0.001D) {
			ratioA = Math.sin((1.0D - time) * halfTheta) / sinHalfTheta;
			ratioB = Math.sin(time * halfTheta) / sinHalfTheta;
		}
		double newW = qA.getW() * ratioA + qB.getW() * ratioB;
		double newX = qA.getX() * ratioA + qB.getX() * ratioB;
		double newY = qA.getY() * ratioA + qB.getY() * ratioB;
		double newZ = qA.getZ() * ratioA + qB.getZ() * ratioB;
		return new Quaternion((float) newW, (float) newX, (float) newY, (float) newZ);
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
		return "Quaternion{" + "x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + "}";
	}
}
