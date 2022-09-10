package com.flounder.maths.matrices;

import com.flounder.maths.vectors.*;

import java.nio.*;

/**
 * Holds a 3x3 matrix.
 */
public class Matrix3f {
	public float m00, m01, m02;
	public float m10, m11, m12;
	public float m20, m21, m22;

	/**
	 * Constructor for Matrix3f. The matrix is initialised to the identity.
	 */
	public Matrix3f() {
		setIdentity(this);
	}

	/**
	 * Set the source matrix to be the identity matrix.
	 *
	 * @param source The matrix to set to the identity.
	 *
	 * @return The source matrix.
	 */
	public static Matrix3f setIdentity(Matrix3f source) {
		source.m00 = 1.0f;
		source.m01 = 0.0f;
		source.m02 = 0.0f;
		source.m10 = 0.0f;
		source.m11 = 1.0f;
		source.m12 = 0.0f;
		source.m20 = 0.0f;
		source.m21 = 0.0f;
		source.m22 = 1.0f;
		return source;
	}

	/**
	 * Constructor for Matrix3f.
	 *
	 * @param source Creates this matrix out of a existing one.
	 */
	public Matrix3f(Matrix3f source) {
		set(source);
	}

	/**
	 * Loads from another Vector2f.
	 *
	 * @param source The source vector.
	 *
	 * @return This.
	 */
	public Matrix3f set(Matrix3f source) {
		this.m00 = source.m00;
		this.m01 = source.m01;
		this.m02 = source.m02;
		this.m10 = source.m10;
		this.m11 = source.m11;
		this.m12 = source.m12;
		this.m20 = source.m20;
		this.m21 = source.m21;
		this.m22 = source.m22;
		return this;
	}

	/**
	 * Adds two matrices together and places the result in the destination matrix.
	 *
	 * @param left The left source matrix.
	 * @param right The right source matrix.
	 * @param destination The destination matrix or null if a new matrix is to be created.
	 *
	 * @return The destination matrix.
	 */
	public static Matrix3f add(Matrix3f left, Matrix3f right, Matrix3f destination) {
		if (destination == null) {
			destination = new Matrix3f();
		}

		destination.m00 = left.m00 + right.m00;
		destination.m01 = left.m01 + right.m01;
		destination.m02 = left.m02 + right.m02;
		destination.m10 = left.m10 + right.m10;
		destination.m11 = left.m11 + right.m11;
		destination.m12 = left.m12 + right.m12;
		destination.m20 = left.m20 + right.m20;
		destination.m21 = left.m21 + right.m21;
		destination.m22 = left.m22 + right.m22;
		return destination;
	}

	/**
	 * Subtracts two matrices together and places the result in the destination matrix.
	 *
	 * @param left The left source matrix.
	 * @param right The right source matrix.
	 * @param destination The destination matrix or null if a new matrix is to be created.
	 *
	 * @return The destination matrix.
	 */
	public static Matrix3f subtract(Matrix3f left, Matrix3f right, Matrix3f destination) {
		if (destination == null) {
			destination = new Matrix3f();
		}

		destination.m00 = left.m00 - right.m00;
		destination.m01 = left.m01 - right.m01;
		destination.m02 = left.m02 - right.m02;
		destination.m10 = left.m10 - right.m10;
		destination.m11 = left.m11 - right.m11;
		destination.m12 = left.m12 - right.m12;
		destination.m20 = left.m20 - right.m20;
		destination.m21 = left.m21 - right.m21;
		destination.m22 = left.m22 - right.m22;
		return destination;
	}

	/**
	 * Multiplies two matrices together and places the result in the destination matrix.
	 *
	 * @param left The left source matrix.
	 * @param right The right source matrix.
	 * @param destination The destination matrix or null if a new matrix is to be created.
	 *
	 * @return The destination matrix.
	 */
	public static Matrix3f multiply(Matrix3f left, Matrix3f right, Matrix3f destination) {
		if (destination == null) {
			destination = new Matrix3f();
		}

		float m00 = left.m00 * right.m00 + left.m10 * right.m01 + left.m20 * right.m02;
		float m01 = left.m01 * right.m00 + left.m11 * right.m01 + left.m21 * right.m02;
		float m02 = left.m02 * right.m00 + left.m12 * right.m01 + left.m22 * right.m02;
		float m10 = left.m00 * right.m10 + left.m10 * right.m11 + left.m20 * right.m12;
		float m11 = left.m01 * right.m10 + left.m11 * right.m11 + left.m21 * right.m12;
		float m12 = left.m02 * right.m10 + left.m12 * right.m11 + left.m22 * right.m12;
		float m20 = left.m00 * right.m20 + left.m10 * right.m21 + left.m20 * right.m22;
		float m21 = left.m01 * right.m20 + left.m11 * right.m21 + left.m21 * right.m22;
		float m22 = left.m02 * right.m20 + left.m12 * right.m21 + left.m22 * right.m22;

		destination.m00 = m00;
		destination.m01 = m01;
		destination.m02 = m02;
		destination.m10 = m10;
		destination.m11 = m11;
		destination.m12 = m12;
		destination.m20 = m20;
		destination.m21 = m21;
		destination.m22 = m22;
		return destination;
	}

	/**
	 * Transforms a matrix by a vector and places the result in the destination matrix.
	 *
	 * @param left The left source matrix.
	 * @param right The right source vector.
	 * @param destination The destination vector or null if a new matrix is to be created.
	 *
	 * @return The destination vector.
	 */
	public static Vector3f transform(Matrix3f left, Vector3f right, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}


		float x = left.m00 * right.x + left.m10 * right.y + left.m20 * right.z;
		float y = left.m01 * right.x + left.m11 * right.y + left.m21 * right.z;
		float z = left.m02 * right.x + left.m12 * right.y + left.m22 * right.z;

		destination.x = x;
		destination.y = y;
		destination.z = z;
		return destination;
	}

	/**
	 * Turns a 3x3 matrix into an array.
	 *
	 * @param matrix The matrix to turn into an array.
	 *
	 * @return A 9 float array.
	 */
	public static float[] toArray(Matrix3f matrix) {
		float[] result = new float[16];
		result[0] = matrix.m00;
		result[1] = matrix.m01;
		result[2] = matrix.m02;
		result[3] = matrix.m10;
		result[4] = matrix.m11;
		result[5] = matrix.m12;
		result[6] = matrix.m20;
		result[7] = matrix.m21;
		result[8] = matrix.m22;
		return result;
	}

	/**
	 * Sets this matrix to be the identity matrix.
	 *
	 * @return this.
	 */
	public Matrix3f setIdentity() {
		return setIdentity(this);
	}

	/**
	 * Inverts this matrix.
	 *
	 * @return this.
	 */
	public Matrix3f invert() {
		return invert(this, this);
	}

	/**
	 * Inverts the source matrix and puts the result in the destination matrix.
	 *
	 * @param source The source matrix to be inverted.
	 * @param destination The destination matrix, or null if a new one is to be created.
	 *
	 * @return The inverted matrix, or null if source can't be reverted.
	 */
	public static Matrix3f invert(Matrix3f source, Matrix3f destination) {
		float determinant = source.determinant();

		if (determinant != 0.0f) {
			if (destination == null) {
				destination = new Matrix3f();
			}

			/*
			 * Does it the ordinary way. inv(A) = 1/det(A) * adj(T), where adj(T) = transpose(Conjugate Matrix) m00 m01 m02 m10 m11 m12 m20 m21 m22
			 */
			float determinant_inv = 1.0f / determinant;

			// Get the conjugate matrix.
			float t00 = source.m11 * source.m22 - source.m12 * source.m21;
			float t01 = -source.m10 * source.m22 + source.m12 * source.m20;
			float t02 = source.m10 * source.m21 - source.m11 * source.m20;
			float t10 = -source.m01 * source.m22 + source.m02 * source.m21;
			float t11 = source.m00 * source.m22 - source.m02 * source.m20;
			float t12 = -source.m00 * source.m21 + source.m01 * source.m20;
			float t20 = source.m01 * source.m12 - source.m02 * source.m11;
			float t21 = -source.m00 * source.m12 + source.m02 * source.m10;
			float t22 = source.m00 * source.m11 - source.m01 * source.m10;

			destination.m00 = t00 * determinant_inv;
			destination.m11 = t11 * determinant_inv;
			destination.m22 = t22 * determinant_inv;
			destination.m01 = t10 * determinant_inv;
			destination.m10 = t01 * determinant_inv;
			destination.m20 = t02 * determinant_inv;
			destination.m02 = t20 * determinant_inv;
			destination.m12 = t21 * determinant_inv;
			destination.m21 = t12 * determinant_inv;
			return destination;
		} else {
			return null;
		}
	}

	/**
	 * @return The determinant of the matrix.
	 */
	public float determinant() {
		return m00 * (m11 * m22 - m12 * m21) + m01 * (m12 * m20 - m10 * m22) + m02 * (m10 * m21 - m11 * m20);
	}

	/**
	 * Negates this matrix.
	 *
	 * @return this.
	 */
	public Matrix3f negate() {
		return negate(this, this);
	}

	/**
	 * Negates the source matrix and places the result in the destination matrix.
	 *
	 * @param source The source matrix.
	 * @param destination The destination matrix or null if a new matrix is to be created.
	 *
	 * @return The negated matrix.
	 */
	public static Matrix3f negate(Matrix3f source, Matrix3f destination) {
		if (destination == null) {
			destination = new Matrix3f();
		}

		destination.m00 = -source.m00;
		destination.m01 = -source.m02;
		destination.m02 = -source.m01;
		destination.m10 = -source.m10;
		destination.m11 = -source.m12;
		destination.m12 = -source.m11;
		destination.m20 = -source.m20;
		destination.m21 = -source.m22;
		destination.m22 = -source.m21;
		return destination;
	}

	/**
	 * Transposes this matrix
	 *
	 * @return this.
	 */
	public Matrix3f transpose() {
		return transpose(this, this);
	}

	/**
	 * Transpose the source matrix and places the result in the destination matrix.
	 *
	 * @param source The source matrix.
	 * @param destination The destination matrix or null if a new matrix is to be created.
	 *
	 * @return The transposed matrix.
	 */
	public static Matrix3f transpose(Matrix3f source, Matrix3f destination) {
		if (destination == null) {
			destination = new Matrix3f();
		}

		float m00 = source.m00;
		float m01 = source.m10;
		float m02 = source.m20;
		float m10 = source.m01;
		float m11 = source.m11;
		float m12 = source.m21;
		float m20 = source.m02;
		float m21 = source.m12;
		float m22 = source.m22;

		destination.m00 = m00;
		destination.m01 = m01;
		destination.m02 = m02;
		destination.m10 = m10;
		destination.m11 = m11;
		destination.m12 = m12;
		destination.m20 = m20;
		destination.m21 = m21;
		destination.m22 = m22;
		return destination;
	}

	/**
	 * Sets this matrix to 0.
	 *
	 * @return this.
	 */
	public Matrix3f setZero() {
		return setZero(this);
	}

	/**
	 * Sets the source matrix to 0.
	 *
	 * @param source The matrix to be set to 0.
	 *
	 * @return The matrix set to zero.
	 */
	public static Matrix3f setZero(Matrix3f source) {
		source.m00 = 0.0f;
		source.m01 = 0.0f;
		source.m02 = 0.0f;
		source.m10 = 0.0f;
		source.m11 = 0.0f;
		source.m12 = 0.0f;
		source.m20 = 0.0f;
		source.m21 = 0.0f;
		source.m22 = 0.0f;
		return source;
	}

	/**
	 * Loads this from a float buffer. The buffer stores the matrix in column major (OpenGL) order.
	 *
	 * @param buffer The float buffer to read from.
	 *
	 * @return this.
	 */
	public Matrix3f load(FloatBuffer buffer) {
		m00 = buffer.get();
		m01 = buffer.get();
		m02 = buffer.get();
		m10 = buffer.get();
		m11 = buffer.get();
		m12 = buffer.get();
		m20 = buffer.get();
		m21 = buffer.get();
		m22 = buffer.get();
		return this;
	}

	/**
	 * Loads this from a float buffer. The buffer stores the matrix in row major (mathematical) order.
	 *
	 * @param buffer The float buffer to read from.
	 *
	 * @return this.
	 */
	public Matrix3f loadTranspose(FloatBuffer buffer) {
		m00 = buffer.get();
		m10 = buffer.get();
		m20 = buffer.get();
		m01 = buffer.get();
		m11 = buffer.get();
		m21 = buffer.get();
		m02 = buffer.get();
		m12 = buffer.get();
		m22 = buffer.get();
		return this;
	}

	/**
	 * Stores this matrix in a float buffer. The matrix is stored in column major (OpenGL) order.
	 *
	 * @param buffer The buffer to store this matrix in.
	 *
	 * @return this.
	 */
	public Matrix3f store(FloatBuffer buffer) {
		buffer.put(m00);
		buffer.put(m01);
		buffer.put(m02);
		buffer.put(m10);
		buffer.put(m11);
		buffer.put(m12);
		buffer.put(m20);
		buffer.put(m21);
		buffer.put(m22);
		return this;
	}

	/**
	 * Stores this matrix in a float buffer. The matrix is stored in row major (mathematical) order.
	 *
	 * @param buffer The buffer to store this matrix in.
	 *
	 * @return this.
	 */
	public Matrix3f storeTranspose(FloatBuffer buffer) {
		buffer.put(m00);
		buffer.put(m10);
		buffer.put(m20);
		buffer.put(m01);
		buffer.put(m11);
		buffer.put(m21);
		buffer.put(m02);
		buffer.put(m12);
		buffer.put(m22);
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

		Matrix3f other = (Matrix3f) object;

		return this.m00 == other.m00 && this.m01 == other.m01 && this.m02 == other.m02 && this.m10 == other.m10 && this.m11 == other.m11 && this.m12 == other.m12 && this.m20 == other.m20 && this.m21 == other.m21 && this.m22 == other.m22;
	}

	@Override
	public String toString() {
		return "Matrix3f{" + m00 + ", " + m10 + ", " + m20 + "\n" + m01 + ", " + m11 + ", " + m21 + "\n" + m02 + ", " + m12 + ", " + m22 + "}";
	}
}
