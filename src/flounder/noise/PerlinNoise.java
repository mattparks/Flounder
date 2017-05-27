package flounder.noise;

import flounder.maths.*;

import java.util.*;

/**
 * Computes Perlin Noise for three dimensions.
 * <p>
 * The result is a continuous function that interpolates a smooth path along a series random points. The function is consistent, so given the same parameters, it will always return the same value.
 * <p>
 * Computing noise for one and two dimensions can make use of the 3D problem flounder.space by just setting the un-needed dimensions to a fixed value.
 */
public class PerlinNoise {
	// Constants for setting up the Perlin-1 noise functions
	private static final int B = 0x1000; // 4096
	private static final int BM = 0xff; // 255
	private static final int N = 0x1000; // 4096

	// Default sample size to work with.
	private static final int DEFAULT_SAMPLE_SIZE = 256;

	// Permutation array for the improved noise function.
	private int[] p_imp;

	// P array for flounder.noise 1 noise.
	private int[] p;
	private float[][] g3;
	private float[][] g2;
	private float[] g1;

	// Not too random randomness.
	private int seed;
	private Random random;

	/**
	 * Create a new noise creator with the given seed value for the randomness.
	 *
	 * @param seed The seed value to use.
	 */
	public PerlinNoise(int seed) {
		setSeed(seed);
	}

	/**
	 * gets the seed of the noise generator.
	 *
	 * @return The generators seed.
	 */
	public int getSeed() {
		return seed;
	}

	/**
	 * Sets the seed to the noise generator. Also initializes the perlin noise.
	 *
	 * @param seed The generators seed.
	 */
	public void setSeed(int seed) {
		this.p_imp = new int[DEFAULT_SAMPLE_SIZE << 1];

		this.seed = seed;
		this.random = new Random(seed);

		// Local variables for setting the noise.
		int i, j, k;

		// Calculate the table of psuedo-random coefficients
		for (i = 0; i < DEFAULT_SAMPLE_SIZE; i++) {
			p_imp[i] = i;
		}

		// Generate the psuedo-random permutation table
		while (--i > 0) {
			k = p_imp[i];
			j = (int) (random.nextLong() & DEFAULT_SAMPLE_SIZE);
			p_imp[i] = p_imp[j];
			p_imp[j] = k;
		}

		p = new int[B + B + 2];
		g3 = new float[B + B + 2][3];
		g2 = new float[B + B + 2][2];
		g1 = new float[B + B + 2];

		for (i = 0; i < B; i++) {
			p[i] = i;

			g1[i] = (float) (random.nextInt(B + B + 2) % (B + B) - B) / B;

			for (j = 0; j < 2; j++) {
				g2[i][j] = (float) (random.nextInt(B + B + 2) % (B + B) - B) / B;
			}

			// Normalize 2.
			float s2 = (float) (1 / Math.sqrt(g2[i][0] * g2[i][0] + g2[i][1] * g2[i][1]));
			g2[i][0] *= s2;
			g2[i][1] *= s2;

			for (j = 0; j < 3; j++) {
				g3[i][j] = (float) (random.nextInt(B + B + 2) % (B + B) - B) / B;
			}

			// Normalize 3.
			float s3 = (float) (1 / Math.sqrt(g3[i][0] * g3[i][0] + g3[i][1] * g3[i][1] + g3[i][2] * g3[i][2]));
			g3[i][0] *= s3;
			g3[i][1] *= s3;
			g3[i][2] *= s3;
		}

		while (--i > 0) {
			k = p[i];
			j = random.nextInt(B + B + 2) % B;
			p[i] = p[j];
			p[j] = k;
		}

		for (i = 0; i < B + 2; i++) {
			p[B + i] = p[i];
			g1[B + i] = g1[i];

			for (j = 0; j < 2; j++) {
				g2[B + i][j] = g2[i][j];
			}

			for (j = 0; j < 3; j++) {
				g3[B + i][j] = g3[i][j];
			}
		}
	}

	/**
	 * Create noise in a 1D space using the original noise noise algorithm.
	 *
	 * @param x The X coordinate of the location to sample.
	 *
	 * @return A noisy value at the given position.
	 */
	public float noise(float x) {
		float t = x + N;
		int bx0 = (int) t & BM;
		int bx1 = bx0 + 1 & BM;
		float rx0 = t - (int) t;
		float rx1 = rx0 - 1.0f;

		float sx = sCurve(rx0);

		float u = rx0 * g1[p[bx0]];
		float v = rx1 * g1[p[bx1]];

		return lerp(sx, u, v);
	}

	/**
	 * Create noise in a 2D space using the original noise noise algorithm.
	 *
	 * @param x The X coordinate of the location to sample.
	 * @param y The Y coordinate of the location to sample.
	 *
	 * @return A noisy value at the given position.
	 */
	public float noise(float x, float y) {
		float t = x + N;
		int bx0 = (int) t & BM;
		int bx1 = bx0 + 1 & BM;
		float rx0 = t - (int) t;
		float rx1 = rx0 - 1.0f;

		t = y + N;
		int by0 = (int) t & BM;
		int by1 = by0 + 1 & BM;
		float ry0 = t - (int) t;
		float ry1 = ry0 - 1.0f;

		int i = p[bx0];
		int j = p[bx1];

		int b00 = p[i + by0];
		int b10 = p[j + by0];
		int b01 = p[i + by1];
		int b11 = p[j + by1];

		float sx = sCurve(rx0);
		float sy = sCurve(ry0);

		float[] q = g2[b00];
		float u = rx0 * q[0] + ry0 * q[1];
		q = g2[b10];
		float v = rx1 * q[0] + ry0 * q[1];
		float a = lerp(sx, u, v);

		q = g2[b01];
		u = rx0 * q[0] + ry1 * q[1];
		q = g2[b11];
		v = rx1 * q[0] + ry1 * q[1];
		float b = lerp(sx, u, v);

		return lerp(sy, a, b);
	}

	/**
	 * Create noise in a 3D space using the original noise noise algorithm.
	 *
	 * @param x The X coordinate of the location to sample.
	 * @param y The Y coordinate of the location to sample.
	 * @param z The Z coordinate of the location to sample.
	 *
	 * @return A noisy value at the given position.
	 */
	public float noise(float x, float y, float z) {
		float t = x + N;
		int bx0 = (int) t & BM;
		int bx1 = bx0 + 1 & BM;
		float rx0 = t - (int) t;
		float rx1 = rx0 - 1.0f;

		t = y + N;
		int by0 = (int) t & BM;
		int by1 = by0 + 1 & BM;
		float ry0 = t - (int) t;
		float ry1 = ry0 - 1.0f;

		t = z + N;
		int bz0 = (int) t & BM;
		int bz1 = bz0 + 1 & BM;
		float rz0 = t - (int) t;
		float rz1 = rz0 - 1.0f;

		int i = p[bx0];
		int j = p[bx1];

		int b00 = p[i + by0];
		int b10 = p[j + by0];
		int b01 = p[i + by1];
		int b11 = p[j + by1];

		t = sCurve(rx0);
		float sy = sCurve(ry0);
		float sz = sCurve(rz0);

		float[] q = g3[b00 + bz0];
		float u = rx0 * q[0] + ry0 * q[1] + rz0 * q[2];
		q = g3[b10 + bz0];
		float v = rx1 * q[0] + ry0 * q[1] + rz0 * q[2];
		float a = lerp(t, u, v);

		q = g3[b01 + bz0];
		u = rx0 * q[0] + ry1 * q[1] + rz0 * q[2];
		q = g3[b11 + bz0];
		v = rx1 * q[0] + ry1 * q[1] + rz0 * q[2];
		float b = lerp(t, u, v);

		float c = lerp(sy, a, b);

		q = g3[b00 + bz1];
		u = rx0 * q[0] + ry0 * q[1] + rz1 * q[2];
		q = g3[b10 + bz1];
		v = rx1 * q[0] + ry0 * q[1] + rz1 * q[2];
		a = lerp(t, u, v);

		q = g3[b01 + bz1];
		u = rx0 * q[0] + ry1 * q[1] + rz1 * q[2];
		q = g3[b11 + bz1];
		v = rx1 * q[0] + ry1 * q[1] + rz1 * q[2];
		b = lerp(t, u, v);

		float d = lerp(sy, a, b);

		return lerp(sz, c, d);
	}

	/**
	 * Computes noise function for three dimensions at the point (x,y,z).
	 *
	 * @param x x dimension parameter.
	 * @param y y dimension parameter.
	 * @param z z dimension parameter.
	 *
	 * @return the noise value at the point (x, y, z).
	 */
	public float improvedNoise(float x, float y, float z) {
		// Constraint the point to a unit cube
		int uc_x = (int) Math.floor(x) & 255;
		int uc_y = (int) Math.floor(y) & 255;
		int uc_z = (int) Math.floor(z) & 255;

		// Relative location of the point in the unit cube
		float xo = x - (float) Math.floor(x);
		float yo = y - (float) Math.floor(y);
		float zo = z - (float) Math.floor(z);

		// Fade curves for x, y and z
		float u = fade(xo);
		float v = fade(yo);
		float w = fade(zo);

		// Generate a hash for each coordinate to find out where in the cube it lies
		int a = p_imp[uc_x] + uc_y;
		int aa = p_imp[a] + uc_z;
		int ab = p_imp[a + 1] + uc_z;

		int b = p_imp[uc_x + 1] + uc_y;
		int ba = p_imp[b] + uc_z;
		int bb = p_imp[b + 1] + uc_z;

		// Blend results from the 8 corners based on the noise function
		float c1 = grad(p_imp[aa], xo, yo, zo);
		float c2 = grad(p_imp[ba], xo - 1.0f, yo, zo);
		float c3 = grad(p_imp[ab], xo, yo - 1.0f, zo);
		float c4 = grad(p_imp[bb], xo - 1.0f, yo - 1.0f, zo);
		float c5 = grad(p_imp[aa + 1], xo, yo, zo - 1.0f);
		float c6 = grad(p_imp[ba + 1], xo - 1.0f, yo, zo - 1.0f);
		float c7 = grad(p_imp[ab + 1], xo, yo - 1.0f, zo - 1.0f);
		float c8 = grad(p_imp[bb + 1], xo - 1.0f, yo - 1.0f, zo - 1.0f);

		return lerp(w, lerp(v, lerp(u, c1, c2), lerp(u, c3, c4)), lerp(v, lerp(u, c5, c6), lerp(u, c7, c8)));
	}

	/**
	 * Create a 1D tileable noise function for the given width.
	 *
	 * @param x The X coordinate to generate the noise for.
	 * @param w The width of the tiled block.
	 *
	 * @return The value of the noise at the given coordinate.
	 */
	public float tileableNoise(float x, float w) {
		return (noise(x) * (w - x) + noise(x - w) * x) / w;
	}

	/**
	 * Create a 2D tileable noise function for the given width and height.
	 *
	 * @param x The X coordinate to generate the noise for.
	 * @param y The Y coordinate to generate the noise for.
	 * @param w The width of the tiled block.
	 * @param h The height of the tiled block.
	 *
	 * @return The value of the noise at the given coordinate.
	 */
	public float tileableNoise(float x, float y, float w, float h) {
		return (noise(x, y) * (w - x) * (h - y) + noise(x - w, y) * x * (h - y) + noise(x, y - h) * (w - x) * y + noise(x - w, y - h) * x * y) / (w * h);
	}

	/**
	 * Create a 3D tileable noise function for the given width, height and depth.
	 *
	 * @param x The X coordinate to generate the noise for.
	 * @param y The Y coordinate to generate the noise for.
	 * @param z The Z coordinate to generate the noise for.
	 * @param w The width of the tiled block.
	 * @param h The height of the tiled block.
	 * @param d The depth of the tiled block.
	 *
	 * @return The value of the noise at the given coordinate.
	 */
	public float tileableNoise(float x, float y, float z, float w, float h, float d) {
		return (noise(x, y, z) * (w - x) * (h - y) * (d - z) + noise(x - w, y, z) * x * (h - y) * (d - z) + noise(x, y - h, z) * (w - x) * y * (d - z) + noise(x - w, y - h, z) * x * y * (d - z) + noise(x, y, z - d) * (w - x) * (h - y) * z + noise(x - w, y, z - d) * x * (h - y) * z + noise(x, y - h, z - d) * (w - x) * y * z + noise(x - w, y - h, z - d) * x * y * z) / (w * h * d);
	}

	/**
	 * Create a turbulence function in 1D using the original noise noise function.
	 *
	 * @param x The X coordinate of the location to sample.
	 * @param freq The frequency of the turbulence to create.
	 *
	 * @return The value at the given coordinates.
	 */
	public float turbulence(float x, float freq) {
		float t = 0;

		do {
			t += noise(freq * x) / freq;
			freq *= 0.5f;
		} while (freq >= 1);

		return t;
	}

	/**
	 * Create a turbulence function in 2D using the original noise noise function.
	 *
	 * @param x The X coordinate of the location to sample.
	 * @param y The Y coordinate of the location to sample.
	 * @param freq The frequency of the turbulence to create.
	 *
	 * @return The value at the given coordinates.
	 */
	public float turbulence(float x, float y, float freq) {
		float t = 0.0f;

		do {
			t += noise(freq * x, freq * y) / freq;
			freq *= 0.5f;
		} while (freq >= 1);

		return t;
	}

	/**
	 * Create a turbulence function in 3D using the original noise noise function.
	 *
	 * @param x The X coordinate of the location to sample.
	 * @param y The Y coordinate of the location to sample.
	 * @param z The Z coordinate of the location to sample.
	 * @param freq The frequency of the turbulence to create.
	 *
	 * @return The value at the given coordinates.
	 */
	public float turbulence(float x, float y, float z, float freq) {
		float t = 0.0f;

		do {
			t += noise(freq * x, freq * y, freq * z) / freq;
			freq *= 0.5f;
		} while (freq >= 1);

		return t;
	}

	/**
	 * Create a turbulent noise output based on the core noise function. This uses the noise as a base function and is suitable for creating clouds, marble and explosion effects. For example, a typical marble effect would set the colour to be:
	 * <pre>
	 * sin(point + turbulence(point) * point.x);
	 * </pre>
	 *
	 * @param x The X coordinate of the location to sample.
	 * @param y The Y coordinate of the location to sample.
	 * @param z The Z coordinate of the location to sample.
	 * @param loF The lower location.
	 * @param hiF The upper location.
	 *
	 * @return The value at the given coordinates.
	 */
	public float improvedTurbulence(float x, float y, float z, float loF, float hiF) {
		float p_x = x + 123.456f;
		float p_y = y;
		float p_z = z;
		float t = 0.0f;

		for (float f = loF; f < hiF; f *= 2) {
			t += Math.abs(improvedNoise(p_x, p_y, p_z)) / f;

			p_x *= 2.0f;
			p_y *= 2.0f;
			p_z *= 2.0f;
		}

		return t - 0.3f;
	}

	/**
	 * Create a turbulence function that can be tiled across a surface in 2D.
	 *
	 * @param x The X coordinate of the location to sample.
	 * @param y The Y coordinate of the location to sample.
	 * @param w The width to tile over.
	 * @param h The height to tile over.
	 * @param freq The frequency of the turbulence to create.
	 *
	 * @return The value at the given coordinates.
	 */
	public float tileableTurbulence(float x, float y, float w, float h, float freq) {
		float t = 0.0f;

		do {
			t += tileableNoise(freq * x, freq * y, w * freq, h * freq) / freq;
			freq *= 0.5f;
		} while (freq >= 1.0f);

		return t;
	}

	/**
	 * Create a turbulence function that can be tiled across a surface in 3D.
	 *
	 * @param x The X coordinate of the location to sample.
	 * @param y The Y coordinate of the location to sample.
	 * @param z The Z coordinate of the location to sample.
	 * @param w The width to tile over.
	 * @param h The height to tile over.
	 * @param d The depth to tile over.
	 * @param freq The frequency of the turbulence to create.
	 *
	 * @return The value at the given coordinates.
	 */
	public float tileableTurbulence(float x, float y, float z, float w, float h, float d, float freq) {
		float t = 0.0f;

		do {
			t += tileableNoise(freq * x, freq * y, freq * z, w * freq, h * freq, d * freq) / freq;
			freq *= 0.5f;
		} while (freq >= 1.0f);

		return t;
	}

	/**
	 * Fade curve calculation which is 6t^5 - 15t^4 + 10t^3. This is the new algorithm, where the old one used to be 3t^2 - 2t^3.
	 *
	 * @param t The t parameter to calculate the fade for.
	 *
	 * @return the drop-off amount.
	 */
	private float fade(float t) {
		return t * t * t * (t * (t * 6.0f - 15.0f) + 10.0f);
	}

	/**
	 * Calculate the gradient function based on the hash code.
	 */
	private float grad(int hash, float x, float y, float z) {
		// Convert low 4 bits of hash code into 12 gradient directions
		int h = hash & 15;
		float u = h < 8.0f || h == 12.0f || h == 13.0f ? x : y;
		float v = h < 4.0f || h == 12.0f || h == 13.0f ? y : z;

		return ((h & 1) == 0.0f ? u : -u) + ((h & 2) == 0.0f ? v : -v);
	}

	/**
	 * S-curve function for value distribution for Perlin-1 noise function.
	 */
	private float sCurve(float t) {
		return t * t * (3.0f - 2.0f * t);
	}

	/**
	 * Simple lerp function using floats.
	 */
	private float lerp(float t, float a, float b) {
		return a + t * (b - a);
	}

	/**
	 * Simple bias generator using exponents.
	 */
	private float bias(float a, float b) {
		return (float) Math.pow(a, Math.log(b) / Maths.LOG_HALF);
	}

	/**
	 * Gain generator that caps to the range of [0, 1].
	 */
	private float gain(float a, float b) {
		if (a < 0.001f) {
			return 0;
		} else if (a > 0.999f) {
			return 1.0f;
		}

		float p = (float) Math.log(1.0f - b) / Maths.LOG_HALF;

		if (a < 0.5f) {
			return (float) (Math.pow(2.0f * a, p) / 2.0f);
		} else {
			return 1 - (float) (Math.pow(2.0f * (1.0f - a), p) / 2.0f);
		}
	}
}
