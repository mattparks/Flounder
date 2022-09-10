package com.flounder.physics;

import com.flounder.maths.matrices.*;

/**
 * Represents the region of flounder.space in the modeled world that may appear on the screen.
 */
public class Frustum {
	// Each frustum planes.
	public static final int RIGHT = 0;
	public static final int LEFT = 1;
	public static final int BOTTOM = 2;
	public static final int TOP = 3;
	public static final int BACK = 4;
	public static final int FRONT = 5;

	// The values stored in the planes.
	public static final int A = 0;
	public static final int B = 1;
	public static final int C = 2;
	public static final int D = 3;

	private float[][] frustum;

	/**
	 * Creates a new frustum.
	 */
	public Frustum() {
		frustum = new float[6][4];
	}

	/**
	 * Updates a frustum from the view and projection matrix.
	 *
	 * @param projection The projection matrix.
	 * @param viewMatrix The view matrix.
	 */
	public void recalculateFrustum(Matrix4f projection, Matrix4f viewMatrix) {
		float[] proj = Matrix4f.toArray(projection);
		float[] view = Matrix4f.toArray(viewMatrix);
		float[] clip = new float[16];

		clip[0] = view[0] * proj[0] + view[1] * proj[4] + view[2] * proj[8] + view[3] * proj[12];
		clip[1] = view[0] * proj[1] + view[1] * proj[5] + view[2] * proj[9] + view[3] * proj[13];
		clip[2] = view[0] * proj[2] + view[1] * proj[6] + view[2] * proj[10] + view[3] * proj[14];
		clip[3] = view[0] * proj[3] + view[1] * proj[7] + view[2] * proj[11] + view[3] * proj[15];

		clip[4] = view[4] * proj[0] + view[5] * proj[4] + view[6] * proj[8] + view[7] * proj[12];
		clip[5] = view[4] * proj[1] + view[5] * proj[5] + view[6] * proj[9] + view[7] * proj[13];
		clip[6] = view[4] * proj[2] + view[5] * proj[6] + view[6] * proj[10] + view[7] * proj[14];
		clip[7] = view[4] * proj[3] + view[5] * proj[7] + view[6] * proj[11] + view[7] * proj[15];

		clip[8] = view[8] * proj[0] + view[9] * proj[4] + view[10] * proj[8] + view[11] * proj[12];
		clip[9] = view[8] * proj[1] + view[9] * proj[5] + view[10] * proj[9] + view[11] * proj[13];
		clip[10] = view[8] * proj[2] + view[9] * proj[6] + view[10] * proj[10] + view[11] * proj[14];
		clip[11] = view[8] * proj[3] + view[9] * proj[7] + view[10] * proj[11] + view[11] * proj[15];

		clip[12] = view[12] * proj[0] + view[13] * proj[4] + view[14] * proj[8] + view[15] * proj[12];
		clip[13] = view[12] * proj[1] + view[13] * proj[5] + view[14] * proj[9] + view[15] * proj[13];
		clip[14] = view[12] * proj[2] + view[13] * proj[6] + view[14] * proj[10] + view[15] * proj[14];
		clip[15] = view[12] * proj[3] + view[13] * proj[7] + view[14] * proj[11] + view[15] * proj[15];

		// This will extract the LEFT side of the frustum
		frustum[LEFT][A] = clip[3] - clip[0];
		frustum[LEFT][B] = clip[7] - clip[4];
		frustum[LEFT][C] = clip[11] - clip[8];
		frustum[LEFT][D] = clip[15] - clip[12];

		normalizePlane(frustum, LEFT);

		// This will extract the RIGHT side of the frustum
		frustum[RIGHT][A] = clip[3] + clip[0];
		frustum[RIGHT][B] = clip[7] + clip[4];
		frustum[RIGHT][C] = clip[11] + clip[8];
		frustum[RIGHT][D] = clip[15] + clip[12];

		normalizePlane(frustum, RIGHT);

		// This will extract the BOTTOM side of the frustum
		frustum[BOTTOM][A] = clip[3] + clip[1];
		frustum[BOTTOM][B] = clip[7] + clip[5];
		frustum[BOTTOM][C] = clip[11] + clip[9];
		frustum[BOTTOM][D] = clip[15] + clip[13];

		normalizePlane(frustum, BOTTOM);

		// This will extract the TOP side of the frustum
		frustum[TOP][A] = clip[3] - clip[1];
		frustum[TOP][B] = clip[7] - clip[5];
		frustum[TOP][C] = clip[11] - clip[9];
		frustum[TOP][D] = clip[15] - clip[13];

		normalizePlane(frustum, TOP);

		// This will extract the FRONT side of the frustum
		frustum[FRONT][A] = clip[3] - clip[2];
		frustum[FRONT][B] = clip[7] - clip[6];
		frustum[FRONT][C] = clip[11] - clip[10];
		frustum[FRONT][D] = clip[15] - clip[14];

		normalizePlane(frustum, FRONT);

		// This will extract the BACK side of the frustum
		frustum[BACK][A] = clip[3] + clip[2];
		frustum[BACK][B] = clip[7] + clip[6];
		frustum[BACK][C] = clip[11] + clip[10];
		frustum[BACK][D] = clip[15] + clip[14];

		normalizePlane(frustum, BACK);
	}

	private void normalizePlane(float[][] frustum, int side) {
		float magnitude = (float) Math.sqrt(frustum[side][A] * frustum[side][A] + frustum[side][B] * frustum[side][B] + frustum[side][C] * frustum[side][C]);
		frustum[side][A] /= magnitude;
		frustum[side][B] /= magnitude;
		frustum[side][C] /= magnitude;
		frustum[side][D] /= magnitude;
	}

	/**
	 * @return The planes*value array used to represent the frustum.
	 */
	public float[][] getFrustum() {
		return frustum;
	}

	/**
	 * Is the point contained in the frustum?
	 *
	 * @param x The points X coord.
	 * @param y The points Y coord.
	 * @param z The points Z coord.
	 *
	 * @return True if contained, false if outside.
	 */
	public boolean pointInFrustum(float x, float y, float z) {
		for (int i = 0; i < 6; i++) {
			if (frustum[i][0] * x + frustum[i][1] * y + frustum[i][2] * z + frustum[i][3] <= 0.0f) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Is the sphere contained in the frustum?
	 *
	 * @param x The sphere X coord.
	 * @param y The sphere Y coord.
	 * @param z The sphere Z coord.
	 * @param radius The spheres radius.
	 *
	 * @return True if contained, false if outside.
	 */
	public boolean sphereInFrustum(float x, float y, float z, float radius) {
		for (int i = 0; i < 6; i++) {
			if (frustum[i][0] * x + frustum[i][1] * y + frustum[i][2] * z + frustum[i][3] <= -radius) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Is the cube contained partially in the frustum?
	 *
	 * @param x1 The point 1's X coord.
	 * @param y1 The point 1's Y coord.
	 * @param z1 The point 1's Z coord.
	 * @param x2 The point 2's X coord.
	 * @param y2 The point 2's Y coord.
	 * @param z2 The point 2's Z coord.
	 *
	 * @return True if partially contained, false if outside.
	 */
	public boolean cubeInFrustum(float x1, float y1, float z1, float x2, float y2, float z2) {
		for (int i = 0; i < 6; i++) {
			if (frustum[i][0] * x1 + frustum[i][1] * y1 + frustum[i][2] * z1 + frustum[i][3] <= 0.0f &&
					frustum[i][0] * x2 + frustum[i][1] * y1 + frustum[i][2] * z1 + frustum[i][3] <= 0.0f &&
					frustum[i][0] * x1 + frustum[i][1] * y2 + frustum[i][2] * z1 + frustum[i][3] <= 0.0f &&
					frustum[i][0] * x2 + frustum[i][1] * y2 + frustum[i][2] * z1 + frustum[i][3] <= 0.0f &&
					frustum[i][0] * x1 + frustum[i][1] * y1 + frustum[i][2] * z2 + frustum[i][3] <= 0.0f &&
					frustum[i][0] * x2 + frustum[i][1] * y1 + frustum[i][2] * z2 + frustum[i][3] <= 0.0f &&
					frustum[i][0] * x1 + frustum[i][1] * y2 + frustum[i][2] * z2 + frustum[i][3] <= 0.0f &&
					frustum[i][0] * x2 + frustum[i][1] * y2 + frustum[i][2] * z2 + frustum[i][3] <= 0.0f) {
				return false;
			}
		}

		return true;
	}
}