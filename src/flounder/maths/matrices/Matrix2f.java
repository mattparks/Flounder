package flounder.maths.matrices;

import flounder.maths.vectors.*;

import java.nio.*;

/**
 * Holds a 2x2 matrix.
 */
public class Matrix2f {
	public float m00, m01;
	public float m10, m11;

	/**
	 * Constructor for Matrix2f. The matrix is initialised to the identity.
	 */
	public Matrix2f() {
		setIdentity(this);
	}

	/**
	 * Set the source matrix to be the identity matrix.
	 *
	 * @param source The matrix to set to the identity.
	 *
	 * @return The source matrix.
	 */
	public static Matrix2f setIdentity(Matrix2f source) {
		source.m00 = 1.0f;
		source.m01 = 0.0f;
		source.m10 = 0.0f;
		source.m11 = 1.0f;
		return source;
	}

	/**
	 * Constructor for Matrix2f.
	 *
	 * @param source Creates this matrix out of a existing one.
	 */
	public Matrix2f(Matrix2f source) {
		set(source);
	}

	/**
	 * Loads from another Vector2f.
	 *
	 * @param source The source vector.
	 *
	 * @return This.
	 */
	public Matrix2f set(Matrix2f source) {
		this.m00 = source.m00;
		this.m01 = source.m01;
		this.m10 = source.m10;
		this.m11 = source.m11;
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
	public static Matrix2f add(Matrix2f left, Matrix2f right, Matrix2f destination) {
		if (destination == null) {
			destination = new Matrix2f();
		}

		destination.m00 = left.m00 + right.m00;
		destination.m01 = left.m01 + right.m01;
		destination.m10 = left.m10 + right.m10;
		destination.m11 = left.m11 + right.m11;
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
	public static Matrix2f subtract(Matrix2f left, Matrix2f right, Matrix2f destination) {
		if (destination == null) {
			destination = new Matrix2f();
		}

		destination.m00 = left.m00 - right.m00;
		destination.m01 = left.m01 - right.m01;
		destination.m10 = left.m10 - right.m10;
		destination.m11 = left.m11 - right.m11;
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
	public static Matrix2f multiply(Matrix2f left, Matrix2f right, Matrix2f destination) {
		if (destination == null) {
			destination = new Matrix2f();
		}

		float m00 = left.m00 * right.m00 + left.m10 * right.m01;
		float m01 = left.m01 * right.m00 + left.m11 * right.m01;
		float m10 = left.m00 * right.m10 + left.m10 * right.m11;
		float m11 = left.m01 * right.m10 + left.m11 * right.m11;

		destination.m00 = m00;
		destination.m01 = m01;
		destination.m10 = m10;
		destination.m11 = m11;
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
	public static Vector2f transform(Matrix2f left, Vector2f right, Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		float x = left.m00 * right.x + left.m10 * right.y;
		float y = left.m01 * right.x + left.m11 * right.y;

		destination.x = x;
		destination.y = y;
		return destination;
	}

	/**
	 * Turns a 2x2 matrix into an array.
	 *
	 * @param matrix The matrix to turn into an array.
	 *
	 * @return A 4 float array.
	 */
	public static float[] toArray(Matrix2f matrix) {
		float[] result = new float[16];
		result[0] = matrix.m00;
		result[1] = matrix.m01;
		result[2] = matrix.m10;
		result[3] = matrix.m11;
		return result;
	}

	/**
	 * Sets this matrix to be the identity matrix.
	 *
	 * @return this.
	 */
	public Matrix2f setIdentity() {
		return setIdentity(this);
	}

	/**
	 * Inverts this matrix.
	 *
	 * @return this.
	 */
	public Matrix2f invert() {
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
	public static Matrix2f invert(Matrix2f source, Matrix2f destination) {
		float determinant = source.determinant();

		if (determinant != 0.0f) {
			if (destination == null) {
				destination = new Matrix2f();
			}

			float determinant_inv = 1f / determinant;
			float t00 = source.m11 * determinant_inv;
			float t01 = -source.m01 * determinant_inv;
			float t11 = source.m00 * determinant_inv;
			float t10 = -source.m10 * determinant_inv;

			destination.m00 = t00;
			destination.m01 = t01;
			destination.m10 = t10;
			destination.m11 = t11;
			return destination;
		} else {
			return null;
		}
	}

	/**
	 * @return The determinant of the matrix.
	 */
	public float determinant() {
		return m00 * m11 - m01 * m10;
	}

	/**
	 * Negates this matrix.
	 *
	 * @return this.
	 */
	public Matrix2f negate() {
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
	public static Matrix2f negate(Matrix2f source, Matrix2f destination) {
		if (destination == null) {
			destination = new Matrix2f();
		}

		destination.m00 = -source.m00;
		destination.m01 = -source.m01;
		destination.m10 = -source.m10;
		destination.m11 = -source.m11;
		return destination;
	}

	/**
	 * Transposes this matrix
	 *
	 * @return this.
	 */
	public Matrix2f transpose() {
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
	public static Matrix2f transpose(Matrix2f source, Matrix2f destination) {
		if (destination == null) {
			destination = new Matrix2f();
		}

		float m01 = source.m10;
		float m10 = source.m01;

		destination.m01 = m01;
		destination.m10 = m10;
		return destination;
	}

	/**
	 * Sets this matrix to 0.
	 *
	 * @return this.
	 */
	public Matrix2f setZero() {
		return setZero(this);
	}

	/**
	 * Sets the source matrix to 0.
	 *
	 * @param source The matrix to be set to 0.
	 *
	 * @return The matrix set to zero.
	 */
	public static Matrix2f setZero(Matrix2f source) {
		source.m00 = 0.0f;
		source.m01 = 0.0f;
		source.m10 = 0.0f;
		source.m11 = 0.0f;
		return source;
	}

	/**
	 * Loads this from a float buffer. The buffer stores the matrix in column major (OpenGL) order.
	 *
	 * @param buffer The float buffer to read from.
	 *
	 * @return this.
	 */
	public Matrix2f load(FloatBuffer buffer) {
		m00 = buffer.get();
		m01 = buffer.get();
		m10 = buffer.get();
		m11 = buffer.get();
		return this;
	}

	/**
	 * Loads this from a float buffer. The buffer stores the matrix in row major (mathematical) order.
	 *
	 * @param buffer The float buffer to read from.
	 *
	 * @return this.
	 */
	public Matrix2f loadTranspose(FloatBuffer buffer) {
		m00 = buffer.get();
		m10 = buffer.get();
		m01 = buffer.get();
		m11 = buffer.get();
		return this;
	}

	/**
	 * Stores this matrix in a float buffer. The matrix is stored in column major (OpenGL) order.
	 *
	 * @param buffer The buffer to store this matrix in.
	 *
	 * @return this.
	 */
	public Matrix2f store(FloatBuffer buffer) {
		buffer.put(m00);
		buffer.put(m01);
		buffer.put(m10);
		buffer.put(m11);
		return this;
	}

	/**
	 * Stores this matrix in a float buffer. The matrix is stored in row major (mathematical) order.
	 *
	 * @param buffer The buffer to store this matrix in.
	 *
	 * @return this.
	 */
	public Matrix2f storeTranspose(FloatBuffer buffer) {
		buffer.put(m00);
		buffer.put(m10);
		buffer.put(m01);
		buffer.put(m11);
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

		Matrix2f other = (Matrix2f) object;

		return this.m00 == other.m00 && this.m01 == other.m01 && this.m10 == other.m10 && this.m11 == other.m11;
	}

	@Override
	public String toString() {
		return "Matrix3f{" + m00 + ", " + m10 + "\n" + m01 + ", " + m11 + "}";
	}
}
