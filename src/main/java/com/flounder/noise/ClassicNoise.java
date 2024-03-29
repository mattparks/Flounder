package com.flounder.noise;

/**
 * Classic Perlin noise in 3D.
 */
public class ClassicNoise {
	private static final int GRAD_3[][] = {{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0},
			{1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1},
			{0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}};

	private static final int P[] = {151, 160, 137, 91, 90, 15,
			131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23,
			190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33,
			88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166,
			77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244,
			102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196,
			135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123,
			5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
			223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
			129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228,
			251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107,
			49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254,
			138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};

	// To remove the need for index wrapping, float the permutation table length.
	private static final int PERM[] = {151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140,
			36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94,
			252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68,
			175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220,
			105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187,
			208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217,
			226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17,
			182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43,
			172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228,
			251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192,
			214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93,
			222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180, 151, 160, 137, 91, 90, 15, 131,
			13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6,
			148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149,
			56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83,
			111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161,
			1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100,
			109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207,
			206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70,
			221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112,
			104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249,
			14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254,
			138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};

	private int seed;

	public ClassicNoise(int seed) {
		this.seed = seed;
	}

	public float noise(float x) {
		return noise(x, 0.0f, 0.0f);
	}

	public float noise(float x, float y) {
		return noise(x, y, 0.0f);
	}

	// Classic Perlin noise, 3D version.
	public float noise(float x, float y, float z) {
		// Find unit grid cell containing point.
		int X = fastfloor(x);
		int Y = fastfloor(y);
		int Z = fastfloor(z);

		// Get relative xyz coordinates of point within that cell.
		x = x - X;
		y = y - Y;
		z = z - Z;

		// Wrap the integer cells at 255 (smaller integer period can be introduced here).
		X = X & 255;
		Y = Y & 255;
		Z = Z & 255;

		// Calculate a set of eight hashed gradient indices.
		int gi000 = PERM[X + PERM[Y + PERM[Z]]] % 12;
		int gi001 = PERM[X + PERM[Y + PERM[Z + 1]]] % 12;
		int gi010 = PERM[X + PERM[Y + 1 + PERM[Z]]] % 12;
		int gi011 = PERM[X + PERM[Y + 1 + PERM[Z + 1]]] % 12;
		int gi100 = PERM[X + 1 + PERM[Y + PERM[Z]]] % 12;
		int gi101 = PERM[X + 1 + PERM[Y + PERM[Z + 1]]] % 12;
		int gi110 = PERM[X + 1 + PERM[Y + 1 + PERM[Z]]] % 12;
		int gi111 = PERM[X + 1 + PERM[Y + 1 + PERM[Z + 1]]] % 12;

		// The gradients of each corner are now:
		// g000 = GRAD_3[gi000];
		// g001 = GRAD_3[gi001];
		// g010 = GRAD_3[gi010];
		// g011 = GRAD_3[gi011];
		// g100 = GRAD_3[gi100];
		// g101 = GRAD_3[gi101];
		// g110 = GRAD_3[gi110];
		// g111 = GRAD_3[gi111];

		// Calculate noise contributions from each of the eight corners.
		float n000 = dot(GRAD_3[gi000], x, y, z);
		float n100 = dot(GRAD_3[gi100], x - 1, y, z);
		float n010 = dot(GRAD_3[gi010], x, y - 1, z);
		float n110 = dot(GRAD_3[gi110], x - 1, y - 1, z);
		float n001 = dot(GRAD_3[gi001], x, y, z - 1);
		float n101 = dot(GRAD_3[gi101], x - 1, y, z - 1);
		float n011 = dot(GRAD_3[gi011], x, y - 1, z - 1);
		float n111 = dot(GRAD_3[gi111], x - 1, y - 1, z - 1);

		// Compute the fade curve value for each of x, y, z.
		float u = fade(x);
		float v = fade(y);
		float w = fade(z);

		// Interpolate along x the contributions from each of the corners.
		float nx00 = mix(n000, n100, u);
		float nx01 = mix(n001, n101, u);
		float nx10 = mix(n010, n110, u);
		float nx11 = mix(n011, n111, u);

		// Interpolate the four results along y.
		float nxy0 = mix(nx00, nx10, v);
		float nxy1 = mix(nx01, nx11, v);

		// Interpolate the two last results along z.
		return (float) mix(nxy0, nxy1, w);
	}

	public int getSeed() {
		return seed;
	}

	public void setSeed(int seed) {
		this.seed = seed;
	}

	// This method is a *lot* faster than using (int)Math.floor(x)
	private static int fastfloor(float x) {
		return x > 0 ? (int) x : (int) x - 1;
	}

	private static float dot(int g[], float x, float y, float z) {
		return g[0] * x + g[1] * y + g[2] * z;
	}

	private static float mix(float a, float b, float t) {
		return (1 - t) * a + t * b;
	}

	private static float fade(float t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}
}