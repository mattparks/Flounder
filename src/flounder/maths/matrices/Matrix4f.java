package flounder.maths.matrices;

import flounder.maths.vectors.*;

import java.nio.*;

/**
 * Holds a 4x4 matrix.
 * <p>
 * http://www.cs.cornell.edu/courses/cs4620/2011fa/lectures/10transformsWeb.pdf
 */
public class Matrix4f {
	public float m00, m01, m02, m03;
	public float m10, m11, m12, m13;
	public float m20, m21, m22, m23;
	public float m30, m31, m32, m33;

	/**
	 * Constructor for Matrix4f. The matrix is initialised to the identity.
	 */
	public Matrix4f() {
		setIdentity(this);
	}

	/**
	 * Set the source matrix to be the identity matrix.
	 *
	 * @param source The matrix to set to the identity.
	 *
	 * @return The source matrix.
	 */
	public static Matrix4f setIdentity(Matrix4f source) {
		source.m00 = 1.0f;
		source.m01 = 0.0f;
		source.m02 = 0.0f;
		source.m03 = 0.0f;
		source.m10 = 0.0f;
		source.m11 = 1.0f;
		source.m12 = 0.0f;
		source.m13 = 0.0f;
		source.m20 = 0.0f;
		source.m21 = 0.0f;
		source.m22 = 1.0f;
		source.m23 = 0.0f;
		source.m30 = 0.0f;
		source.m31 = 0.0f;
		source.m32 = 0.0f;
		source.m33 = 1.0f;
		return source;
	}

	/**
	 * Constructor for Matrix4f.
	 *
	 * @param source Creates this matrix out of a existing one.
	 */
	public Matrix4f(Matrix4f source) {
		set(source);
	}

	/**
	 * Loads from another Vector2f.
	 *
	 * @param source The source vector.
	 *
	 * @return This.
	 */
	public Matrix4f set(Matrix4f source) {
		this.m00 = source.m00;
		this.m01 = source.m01;
		this.m02 = source.m02;
		this.m03 = source.m03;
		this.m10 = source.m10;
		this.m11 = source.m11;
		this.m12 = source.m12;
		this.m13 = source.m13;
		this.m20 = source.m20;
		this.m21 = source.m21;
		this.m22 = source.m22;
		this.m23 = source.m23;
		this.m30 = source.m30;
		this.m31 = source.m31;
		this.m32 = source.m32;
		this.m33 = source.m33;
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
	public static Matrix4f add(Matrix4f left, Matrix4f right, Matrix4f destination) {
		if (destination == null) {
			destination = new Matrix4f();
		}

		destination.m00 = left.m00 + right.m00;
		destination.m01 = left.m01 + right.m01;
		destination.m02 = left.m02 + right.m02;
		destination.m03 = left.m03 + right.m03;
		destination.m10 = left.m10 + right.m10;
		destination.m11 = left.m11 + right.m11;
		destination.m12 = left.m12 + right.m12;
		destination.m13 = left.m13 + right.m13;
		destination.m20 = left.m20 + right.m20;
		destination.m21 = left.m21 + right.m21;
		destination.m22 = left.m22 + right.m22;
		destination.m23 = left.m23 + right.m23;
		destination.m30 = left.m30 + right.m30;
		destination.m31 = left.m31 + right.m31;
		destination.m32 = left.m32 + right.m32;
		destination.m33 = left.m33 + right.m33;
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
	public static Matrix4f subtract(Matrix4f left, Matrix4f right, Matrix4f destination) {
		if (destination == null) {
			destination = new Matrix4f();
		}

		destination.m00 = left.m00 - right.m00;
		destination.m01 = left.m01 - right.m01;
		destination.m02 = left.m02 - right.m02;
		destination.m03 = left.m03 - right.m03;
		destination.m10 = left.m10 - right.m10;
		destination.m11 = left.m11 - right.m11;
		destination.m12 = left.m12 - right.m12;
		destination.m13 = left.m13 - right.m13;
		destination.m20 = left.m20 - right.m20;
		destination.m21 = left.m21 - right.m21;
		destination.m22 = left.m22 - right.m22;
		destination.m23 = left.m23 - right.m23;
		destination.m30 = left.m30 - right.m30;
		destination.m31 = left.m31 - right.m31;
		destination.m32 = left.m32 - right.m32;
		destination.m33 = left.m33 - right.m33;
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
	public static Matrix4f multiply(Matrix4f left, Matrix4f right, Matrix4f destination) {
		if (destination == null) {
			destination = new Matrix4f();
		}

		float m00 = left.m00 * right.m00 + left.m10 * right.m01 + left.m20 * right.m02 + left.m30 * right.m03;
		float m01 = left.m01 * right.m00 + left.m11 * right.m01 + left.m21 * right.m02 + left.m31 * right.m03;
		float m02 = left.m02 * right.m00 + left.m12 * right.m01 + left.m22 * right.m02 + left.m32 * right.m03;
		float m03 = left.m03 * right.m00 + left.m13 * right.m01 + left.m23 * right.m02 + left.m33 * right.m03;
		float m10 = left.m00 * right.m10 + left.m10 * right.m11 + left.m20 * right.m12 + left.m30 * right.m13;
		float m11 = left.m01 * right.m10 + left.m11 * right.m11 + left.m21 * right.m12 + left.m31 * right.m13;
		float m12 = left.m02 * right.m10 + left.m12 * right.m11 + left.m22 * right.m12 + left.m32 * right.m13;
		float m13 = left.m03 * right.m10 + left.m13 * right.m11 + left.m23 * right.m12 + left.m33 * right.m13;
		float m20 = left.m00 * right.m20 + left.m10 * right.m21 + left.m20 * right.m22 + left.m30 * right.m23;
		float m21 = left.m01 * right.m20 + left.m11 * right.m21 + left.m21 * right.m22 + left.m31 * right.m23;
		float m22 = left.m02 * right.m20 + left.m12 * right.m21 + left.m22 * right.m22 + left.m32 * right.m23;
		float m23 = left.m03 * right.m20 + left.m13 * right.m21 + left.m23 * right.m22 + left.m33 * right.m23;
		float m30 = left.m00 * right.m30 + left.m10 * right.m31 + left.m20 * right.m32 + left.m30 * right.m33;
		float m31 = left.m01 * right.m30 + left.m11 * right.m31 + left.m21 * right.m32 + left.m31 * right.m33;
		float m32 = left.m02 * right.m30 + left.m12 * right.m31 + left.m22 * right.m32 + left.m32 * right.m33;
		float m33 = left.m03 * right.m30 + left.m13 * right.m31 + left.m23 * right.m32 + left.m33 * right.m33;

		destination.m00 = m00;
		destination.m01 = m01;
		destination.m02 = m02;
		destination.m03 = m03;
		destination.m10 = m10;
		destination.m11 = m11;
		destination.m12 = m12;
		destination.m13 = m13;
		destination.m20 = m20;
		destination.m21 = m21;
		destination.m22 = m22;
		destination.m23 = m23;
		destination.m30 = m30;
		destination.m31 = m31;
		destination.m32 = m32;
		destination.m33 = m33;
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
	public static Vector4f transform(Matrix4f left, Vector4f right, Vector4f destination) {
		if (destination == null) {
			destination = new Vector4f();
		}

		float x = left.m00 * right.x + left.m10 * right.y + left.m20 * right.z + left.m30 * right.w;
		float y = left.m01 * right.x + left.m11 * right.y + left.m21 * right.z + left.m31 * right.w;
		float z = left.m02 * right.x + left.m12 * right.y + left.m22 * right.z + left.m32 * right.w;
		float w = left.m03 * right.x + left.m13 * right.y + left.m23 * right.z + left.m33 * right.w;

		destination.x = x;
		destination.y = y;
		destination.z = z;
		destination.w = w;
		return destination;
	}

	/**
	 * Creates a new transformation matrix for a object in 2d flounder.space.
	 *
	 * @param translation Translation amount the XY.
	 * @param scale How much to scale the matrix.
	 * @param destination The destination matrix or null if a new matrix is to be created.
	 *
	 * @return Returns the transformation matrix.
	 */
	public static Matrix4f transformationMatrix(Vector2f translation, float scale, Matrix4f destination) {
		return transformationMatrix(translation, new Vector3f(scale, scale, scale), destination);
	}

	/**
	 * Creates a new transformation matrix for a object in 2d flounder.space.
	 *
	 * @param translation Translation amount the XY.
	 * @param scale How much to scale the matrix.
	 * @param destination The destination matrix or null if a new matrix is to be created.
	 *
	 * @return Returns the transformation matrix.
	 */
	public static Matrix4f transformationMatrix(Vector2f translation, Vector3f scale, Matrix4f destination) {
		if (destination == null) {
			destination = new Matrix4f();
		}

		destination.setIdentity();
		Matrix4f.translate(destination, translation, destination);
		Matrix4f.scale(destination, scale, destination);
		return destination;
	}

	/**
	 * Translates a matrix by a vector and places the result in the destination matrix.
	 *
	 * @param left The left source matrix.
	 * @param right The right source vector.
	 * @param destination The destination matrix or null if a new matrix is to be created.
	 *
	 * @return The destination matrix.
	 */
	public static Matrix4f translate(Matrix4f left, Vector2f right, Matrix4f destination) {
		if (destination == null) {
			destination = new Matrix4f();
		}

		destination.m30 += left.m00 * right.x + left.m10 * right.y;
		destination.m31 += left.m01 * right.x + left.m11 * right.y;
		destination.m32 += left.m02 * right.x + left.m12 * right.y;
		destination.m33 += left.m03 * right.x + left.m13 * right.y;
		return destination;
	}

	/**
	 * Scales a matrix by a vector and places the result in the destination matrix.
	 *
	 * @param left The left source matrix.
	 * @param right The right source vector.
	 * @param destination The destination matrix or null if a new matrix is to be created.
	 *
	 * @return The destination matrix.
	 */
	public static Matrix4f scale(Matrix4f left, Vector3f right, Matrix4f destination) {
		if (destination == null) {
			destination = new Matrix4f();
		}

		destination.m00 = left.m00 * right.x;
		destination.m01 = left.m01 * right.x;
		destination.m02 = left.m02 * right.x;
		destination.m03 = left.m03 * right.x;
		destination.m10 = left.m10 * right.y;
		destination.m11 = left.m11 * right.y;
		destination.m12 = left.m12 * right.y;
		destination.m13 = left.m13 * right.y;
		destination.m20 = left.m20 * right.z;
		destination.m21 = left.m21 * right.z;
		destination.m22 = left.m22 * right.z;
		destination.m23 = left.m23 * right.z;
		return destination;
	}

	/**
	 * Sets this matrix to be the identity matrix.
	 *
	 * @return this.
	 */
	public Matrix4f setIdentity() {
		return setIdentity(this);
	}

	/**
	 * Creates a new transformation matrix for a object in 3d flounder.space.
	 *
	 * @param translation Translation amount the XYZ.
	 * @param rotation Rotation amount the XYZ.
	 * @param scale How much to scale the matrix.
	 * @param destination The destination matrix or null if a new matrix is to be created.
	 *
	 * @return Returns the transformation matrix.
	 */
	public static Matrix4f transformationMatrix(Vector3f translation, Vector3f rotation, float scale, Matrix4f destination) {
		return transformationMatrix(translation, rotation, new Vector3f(scale, scale, scale), destination);
	}

	/**
	 * Creates a new transformation matrix for a object in 3d flounder.space.
	 *
	 * @param translation Translation amount the XYZ.
	 * @param rotation Rotation amount the XYZ.
	 * @param scale How much to scale the matrix.
	 * @param destination The destination matrix or null if a new matrix is to be created.
	 *
	 * @return Returns the transformation matrix.
	 */
	public static Matrix4f transformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale, Matrix4f destination) {
		if (destination == null) {
			destination = new Matrix4f();
		}

		destination.setIdentity();
		Matrix4f.translate(destination, translation, destination);
		Vector3f reusableVector = new Vector3f();
		Matrix4f.rotate(destination, reusableVector.set(1.0f, 0.0f, 0.0f), (float) Math.toRadians(rotation.x), destination); // Rotate the X component.
		Matrix4f.rotate(destination, reusableVector.set(0.0f, 1.0f, 0.0f), (float) Math.toRadians(rotation.y), destination); // Rotate the Y component.
		Matrix4f.rotate(destination, reusableVector.set(0.0f, 0.0f, 1.0f), (float) Math.toRadians(rotation.z), destination); // Rotate the Z component.
		Matrix4f.scale(destination, scale, destination);
		return destination;
	}

	/**
	 * Translates a matrix by a vector and places the result in the destination matrix.
	 *
	 * @param left The left source matrix.
	 * @param right The right source vector.
	 * @param destination The destination matrix or null if a new matrix is to be created.
	 *
	 * @return The destination matrix.
	 */
	public static Matrix4f translate(Matrix4f left, Vector3f right, Matrix4f destination) {
		if (destination == null) {
			destination = new Matrix4f();
		}

		destination.m30 += left.m00 * right.x + left.m10 * right.y + left.m20 * right.z;
		destination.m31 += left.m01 * right.x + left.m11 * right.y + left.m21 * right.z;
		destination.m32 += left.m02 * right.x + left.m12 * right.y + left.m22 * right.z;
		destination.m33 += left.m03 * right.x + left.m13 * right.y + left.m23 * right.z;
		return destination;
	}

	/**
	 * Rotates a matrix around the given axis the specified angle and places the result in the destination matrix.
	 *
	 * @param source The source matrix.
	 * @param axis The vector representing the rotation axis. Must be normalized.
	 * @param angle the angle, in radians.
	 * @param destination The destination matrix or null if a new matrix is to be created.
	 *
	 * @return The destination matrix.
	 */
	public static Matrix4f rotate(Matrix4f source, Vector3f axis, float angle, Matrix4f destination) {
		if (destination == null) {
			destination = new Matrix4f();
		}

		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);
		float o = 1.0f - c;
		float xy = axis.x * axis.y;
		float yz = axis.y * axis.z;
		float xz = axis.x * axis.z;
		float xs = axis.x * s;
		float ys = axis.y * s;
		float zs = axis.z * s;

		float f00 = axis.x * axis.x * o + c;
		float f01 = xy * o + zs;
		float f02 = xz * o - ys;
		float f10 = xy * o - zs;
		float f11 = axis.y * axis.y * o + c;
		float f12 = yz * o + xs;
		float f20 = xz * o + ys;
		float f21 = yz * o - xs;
		float f22 = axis.z * axis.z * o + c;

		float t00 = source.m00 * f00 + source.m10 * f01 + source.m20 * f02;
		float t01 = source.m01 * f00 + source.m11 * f01 + source.m21 * f02;
		float t02 = source.m02 * f00 + source.m12 * f01 + source.m22 * f02;
		float t03 = source.m03 * f00 + source.m13 * f01 + source.m23 * f02;
		float t10 = source.m00 * f10 + source.m10 * f11 + source.m20 * f12;
		float t11 = source.m01 * f10 + source.m11 * f11 + source.m21 * f12;
		float t12 = source.m02 * f10 + source.m12 * f11 + source.m22 * f12;
		float t13 = source.m03 * f10 + source.m13 * f11 + source.m23 * f12;
		destination.m20 = source.m00 * f20 + source.m10 * f21 + source.m20 * f22;
		destination.m21 = source.m01 * f20 + source.m11 * f21 + source.m21 * f22;
		destination.m22 = source.m02 * f20 + source.m12 * f21 + source.m22 * f22;
		destination.m23 = source.m03 * f20 + source.m13 * f21 + source.m23 * f22;
		destination.m00 = t00;
		destination.m01 = t01;
		destination.m02 = t02;
		destination.m03 = t03;
		destination.m10 = t10;
		destination.m11 = t11;
		destination.m12 = t12;
		destination.m13 = t13;
		return destination;
	}

	/**
	 * Creates a new perspective matrix, or updates a existing one.
	 *
	 * @param fov The cameras FOV.
	 * @param aspectRatio The cameras aspect ratio.
	 * @param zNear The cameras near plane.
	 * @param zFar The cameras far plane.
	 * @param destination The destination matrix or null if a new matrix is to be created.
	 *
	 * @return The transformation matrix.
	 */
	public static Matrix4f perspectiveMatrix(float fov, float aspectRatio, float zNear, float zFar, Matrix4f destination) {
		if (destination == null) {
			destination = new Matrix4f();
		}

		destination.setIdentity();
		float yScale = (float) (1.0f / Math.tan(Math.toRadians(fov / 2.0f)) * aspectRatio);
		float xScale = yScale / aspectRatio;
		float depth = zFar - zNear;

		destination.m00 = xScale;
		destination.m11 = yScale;
		destination.m22 = -((zFar + zNear) / depth);
		destination.m23 = -1.0f;
		destination.m32 = -(2.0f * zNear * zFar / depth);
		destination.m33 = 0.0f;
		return destination;
	}

	/**
	 * Creates a new orthographic matrix, or updates a existing one.
	 *
	 * @param left The left plane.
	 * @param right The right plane.
	 * @param bottom The bottom plane.
	 * @param top The top plane.
	 * @param near The near plane.
	 * @param far The far plane.
	 * @param destination The destination matrix or null if a new matrix is to be created.
	 *
	 * @return The transformation matrix.
	 */
	public static Matrix4f orthographicMatrix(float left, float right, float bottom, float top, float near, float far, Matrix4f destination) {
		if (destination == null) {
			destination = new Matrix4f();
		}

		float ox = 2.0f / (right - left);
		float oy = 2.0f / (top - bottom);
		float oz = -2.0f / (far - near);

		float tx = -(right + left) / (right - left);
		float ty = -(top + bottom) / (top - bottom);
		float tz = -(far + near) / (far - near);

		destination.setIdentity();
		destination.m00 = ox;
		destination.m11 = oy;
		destination.m22 = oz;
		destination.m03 = tx;
		destination.m13 = ty;
		destination.m23 = tz;
		destination.m33 = 1.0f;
		return destination;
	}

	/**
	 * Turns a 4x4 matrix into an array.
	 *
	 * @param matrix The matrix to turn into an array.
	 *
	 * @return A 16 float array.
	 */
	public static float[] toArray(Matrix4f matrix) {
		float[] result = new float[16];
		result[0] = matrix.m00;
		result[1] = matrix.m01;
		result[2] = matrix.m02;
		result[3] = matrix.m03;
		result[4] = matrix.m10;
		result[5] = matrix.m11;
		result[6] = matrix.m12;
		result[7] = matrix.m13;
		result[8] = matrix.m20;
		result[9] = matrix.m21;
		result[10] = matrix.m22;
		result[11] = matrix.m23;
		result[12] = matrix.m30;
		result[13] = matrix.m31;
		result[14] = matrix.m32;
		result[15] = matrix.m33;
		return result;
	}

	/**
	 * Inverts this matrix.
	 *
	 * @return this.
	 */
	public Matrix4f invert() {
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
	public static Matrix4f invert(Matrix4f source, Matrix4f destination) {
		float determinant = source.determinant();

		if (determinant != 0) {
			if (destination == null) {
				destination = new Matrix4f();
			}

			float determinant_inv = 1f / determinant;

			// First row.
			float t00 = determinant3x3(source.m11, source.m12, source.m13, source.m21, source.m22, source.m23, source.m31, source.m32, source.m33);
			float t01 = -determinant3x3(source.m10, source.m12, source.m13, source.m20, source.m22, source.m23, source.m30, source.m32, source.m33);
			float t02 = determinant3x3(source.m10, source.m11, source.m13, source.m20, source.m21, source.m23, source.m30, source.m31, source.m33);
			float t03 = -determinant3x3(source.m10, source.m11, source.m12, source.m20, source.m21, source.m22, source.m30, source.m31, source.m32);
			// Second row.
			float t10 = -determinant3x3(source.m01, source.m02, source.m03, source.m21, source.m22, source.m23, source.m31, source.m32, source.m33);
			float t11 = determinant3x3(source.m00, source.m02, source.m03, source.m20, source.m22, source.m23, source.m30, source.m32, source.m33);
			float t12 = -determinant3x3(source.m00, source.m01, source.m03, source.m20, source.m21, source.m23, source.m30, source.m31, source.m33);
			float t13 = determinant3x3(source.m00, source.m01, source.m02, source.m20, source.m21, source.m22, source.m30, source.m31, source.m32);
			// Third row.
			float t20 = determinant3x3(source.m01, source.m02, source.m03, source.m11, source.m12, source.m13, source.m31, source.m32, source.m33);
			float t21 = -determinant3x3(source.m00, source.m02, source.m03, source.m10, source.m12, source.m13, source.m30, source.m32, source.m33);
			float t22 = determinant3x3(source.m00, source.m01, source.m03, source.m10, source.m11, source.m13, source.m30, source.m31, source.m33);
			float t23 = -determinant3x3(source.m00, source.m01, source.m02, source.m10, source.m11, source.m12, source.m30, source.m31, source.m32);
			// Fourth row.
			float t30 = -determinant3x3(source.m01, source.m02, source.m03, source.m11, source.m12, source.m13, source.m21, source.m22, source.m23);
			float t31 = determinant3x3(source.m00, source.m02, source.m03, source.m10, source.m12, source.m13, source.m20, source.m22, source.m23);
			float t32 = -determinant3x3(source.m00, source.m01, source.m03, source.m10, source.m11, source.m13, source.m20, source.m21, source.m23);
			float t33 = determinant3x3(source.m00, source.m01, source.m02, source.m10, source.m11, source.m12, source.m20, source.m21, source.m22);

			// Transpose and divide by the determinant.
			destination.m00 = t00 * determinant_inv;
			destination.m11 = t11 * determinant_inv;
			destination.m22 = t22 * determinant_inv;
			destination.m33 = t33 * determinant_inv;
			destination.m01 = t10 * determinant_inv;
			destination.m10 = t01 * determinant_inv;
			destination.m20 = t02 * determinant_inv;
			destination.m02 = t20 * determinant_inv;
			destination.m12 = t21 * determinant_inv;
			destination.m21 = t12 * determinant_inv;
			destination.m03 = t30 * determinant_inv;
			destination.m30 = t03 * determinant_inv;
			destination.m13 = t31 * determinant_inv;
			destination.m31 = t13 * determinant_inv;
			destination.m32 = t23 * determinant_inv;
			destination.m23 = t32 * determinant_inv;
			return destination;
		} else {
			return null;
		}
	}

	/**
	 * Calculates the determinant of a 3x3 matrix.
	 *
	 * @param t00 m00
	 * @param t01 m01
	 * @param t02 m02
	 * @param t10 m10
	 * @param t11 m11
	 * @param t12 m12
	 * @param t20 m20
	 * @param t21 m21
	 * @param t22 m22
	 *
	 * @return The determinant of the 3x3 matrix.
	 */
	private static float determinant3x3(float t00, float t01, float t02, float t10, float t11, float t12, float t20, float t21, float t22) {
		return t00 * (t11 * t22 - t12 * t21) + t01 * (t12 * t20 - t10 * t22) + t02 * (t10 * t21 - t11 * t20);
	}

	/**
	 * @return The determinant of the matrix.
	 */
	public float determinant() {
		float f = m00 * (m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32 - m13 * m22 * m31 - m11 * m23 * m32 - m12 * m21 * m33);
		f -= m01 * (m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32 - m13 * m22 * m30 - m10 * m23 * m32 - m12 * m20 * m33);
		f += m02 * (m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31 - m13 * m21 * m30 - m10 * m23 * m31 - m11 * m20 * m33);
		f -= m03 * (m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31 - m12 * m21 * m30 - m10 * m22 * m31 - m11 * m20 * m32);
		return f;
	}

	/**
	 * Negates this matrix.
	 *
	 * @return this.
	 */
	public Matrix4f negate() {
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
	public static Matrix4f negate(Matrix4f source, Matrix4f destination) {
		if (destination == null) {
			destination = new Matrix4f();
		}

		destination.m00 = -source.m00;
		destination.m01 = -source.m01;
		destination.m02 = -source.m02;
		destination.m03 = -source.m03;
		destination.m10 = -source.m10;
		destination.m11 = -source.m11;
		destination.m12 = -source.m12;
		destination.m13 = -source.m13;
		destination.m20 = -source.m20;
		destination.m21 = -source.m21;
		destination.m22 = -source.m22;
		destination.m23 = -source.m23;
		destination.m30 = -source.m30;
		destination.m31 = -source.m31;
		destination.m32 = -source.m32;
		destination.m33 = -source.m33;
		return destination;
	}

	/**
	 * Transposes this matrix
	 *
	 * @return this.
	 */
	public Matrix4f transpose() {
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
	public static Matrix4f transpose(Matrix4f source, Matrix4f destination) {
		if (destination == null) {
			destination = new Matrix4f();
		}

		float m00 = source.m00;
		float m01 = source.m10;
		float m02 = source.m20;
		float m03 = source.m30;
		float m10 = source.m01;
		float m11 = source.m11;
		float m12 = source.m21;
		float m13 = source.m31;
		float m20 = source.m02;
		float m21 = source.m12;
		float m22 = source.m22;
		float m23 = source.m32;
		float m30 = source.m03;
		float m31 = source.m13;
		float m32 = source.m23;
		float m33 = source.m33;

		destination.m00 = m00;
		destination.m01 = m01;
		destination.m02 = m02;
		destination.m03 = m03;
		destination.m10 = m10;
		destination.m11 = m11;
		destination.m12 = m12;
		destination.m13 = m13;
		destination.m20 = m20;
		destination.m21 = m21;
		destination.m22 = m22;
		destination.m23 = m23;
		destination.m30 = m30;
		destination.m31 = m31;
		destination.m32 = m32;
		destination.m33 = m33;
		return destination;
	}

	/**
	 * Sets this matrix to 0.
	 *
	 * @return this.
	 */
	public Matrix4f setZero() {
		return setZero(this);
	}

	/**
	 * Sets the source matrix to 0.
	 *
	 * @param source The matrix to be set to 0.
	 *
	 * @return The matrix set to zero.
	 */
	public static Matrix4f setZero(Matrix4f source) {
		source.m00 = 0.0f;
		source.m01 = 0.0f;
		source.m02 = 0.0f;
		source.m03 = 0.0f;
		source.m10 = 0.0f;
		source.m11 = 0.0f;
		source.m12 = 0.0f;
		source.m13 = 0.0f;
		source.m20 = 0.0f;
		source.m21 = 0.0f;
		source.m22 = 0.0f;
		source.m23 = 0.0f;
		source.m30 = 0.0f;
		source.m31 = 0.0f;
		source.m32 = 0.0f;
		source.m33 = 0.0f;
		return source;
	}

	/**
	 * Loads this from a float buffer. The buffer stores the matrix in column major (OpenGL) order.
	 *
	 * @param buffer The float buffer to read from.
	 *
	 * @return this.
	 */
	public Matrix4f load(FloatBuffer buffer) {
		m00 = buffer.get();
		m01 = buffer.get();
		m02 = buffer.get();
		m03 = buffer.get();
		m10 = buffer.get();
		m11 = buffer.get();
		m12 = buffer.get();
		m13 = buffer.get();
		m20 = buffer.get();
		m21 = buffer.get();
		m22 = buffer.get();
		m23 = buffer.get();
		m30 = buffer.get();
		m31 = buffer.get();
		m32 = buffer.get();
		m33 = buffer.get();
		return this;
	}

	/**
	 * Loads this from a float buffer. The buffer stores the matrix in row major (mathematical) order.
	 *
	 * @param buffer The float buffer to read from.
	 *
	 * @return this.
	 */
	public Matrix4f loadTranspose(FloatBuffer buffer) {
		m00 = buffer.get();
		m10 = buffer.get();
		m20 = buffer.get();
		m30 = buffer.get();
		m01 = buffer.get();
		m11 = buffer.get();
		m21 = buffer.get();
		m31 = buffer.get();
		m02 = buffer.get();
		m12 = buffer.get();
		m22 = buffer.get();
		m32 = buffer.get();
		m03 = buffer.get();
		m13 = buffer.get();
		m23 = buffer.get();
		m33 = buffer.get();
		return this;
	}

	/**
	 * Stores this matrix in a float buffer. The matrix is stored in column major (OpenGL) order.
	 *
	 * @param buffer The buffer to store this matrix in.
	 *
	 * @return this.
	 */
	public Matrix4f store(FloatBuffer buffer) {
		buffer.put(m00);
		buffer.put(m01);
		buffer.put(m02);
		buffer.put(m03);
		buffer.put(m10);
		buffer.put(m11);
		buffer.put(m12);
		buffer.put(m13);
		buffer.put(m20);
		buffer.put(m21);
		buffer.put(m22);
		buffer.put(m23);
		buffer.put(m30);
		buffer.put(m31);
		buffer.put(m32);
		buffer.put(m33);
		return this;
	}

	/**
	 * Stores this matrix in a float buffer. The matrix is stored in row major (mathematical) order.
	 *
	 * @param buffer The buffer to store this matrix in.
	 *
	 * @return this.
	 */
	public Matrix4f storeTranspose(FloatBuffer buffer) {
		buffer.put(m00);
		buffer.put(m10);
		buffer.put(m20);
		buffer.put(m30);
		buffer.put(m01);
		buffer.put(m11);
		buffer.put(m21);
		buffer.put(m31);
		buffer.put(m02);
		buffer.put(m12);
		buffer.put(m22);
		buffer.put(m32);
		buffer.put(m03);
		buffer.put(m13);
		buffer.put(m23);
		buffer.put(m33);
		return this;
	}

	/**
	 * Stores the rotation portion of this matrix in a float buffer. The matrix is stored in column major (OpenGL) order.
	 *
	 * @param buffer The buffer to store this matrix in.
	 *
	 * @return this.
	 */
	public Matrix4f store3f(FloatBuffer buffer) {
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

		Matrix4f other = (Matrix4f) object;

		return this.m00 == other.m00 && this.m01 == other.m01 && this.m02 == other.m02 && this.m03 == other.m03 && this.m10 == other.m10 && this.m11 == other.m11 && this.m12 == other.m12 && this.m13 == other.m13 && this.m20 == other.m20 && this.m21 == other.m21 && this.m22 == other.m22 && this.m23 == other.m23 && this.m30 == other.m30 && this.m31 == other.m31 && this.m32 == other.m32 && this.m33 == other.m33;
	}

	@Override
	public String toString() {
		return "Matrix4f{" + m00 + ", " + m10 + ", " + m20 + ", " + m30 + "\n" + m01 + ", " + m11 + ", " + m21 + ", " + m31 + "\n" + m02 + ", " + m12 + ", " + m22 + ", " + m32 + "\n" + m03 + ", " + m13 + ", " + m23 + ", " + m33 + "}";
	}
}
