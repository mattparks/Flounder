package flounder.maths;

import flounder.maths.vectors.*;

import java.util.*;

/**
 * A class that holds many various math functions.
 */
public class Maths {
	public static final float PI = 3.14159265358979323846f;
	public static final float DEGREES_IN_CIRCLE = 360;
	public static final float DEGREES_IN_HALF_CIRCLE = 180;
	public static final float ANG2RAD = PI / DEGREES_IN_HALF_CIRCLE;
	public static final float LOG_HALF = (float) Math.log(0.5f);
	public static final Random RANDOM = new Random();

	/**
	 * Generates a random value from between a range.
	 *
	 * @param min The min value.
	 * @param max The max value.
	 *
	 * @return The randomly selected value within the range.
	 */
	public static float randomInRange(float min, float max) {
		float range = max - min;
		float scaled = (float) RANDOM.nextDouble() * range;
		float shifted = scaled + min;
		return shifted; // == (rand.nextDouble() * (max-min)) + min;
	}

	/**
	 * Gets the maximum value.
	 *
	 * @param fs The values to sort though.
	 *
	 * @return The maximum value.
	 */
	public static float maxValue(float... fs) {
		float max = 0.0f;

		for (float v : fs) {
			if (v > max) {
				max = v;
			}
		}

		return max;
	}

	/**
	 * Gets the minimum value.
	 *
	 * @param fs The values to sort though.
	 *
	 * @return The minimum value.
	 */
	public static float minValue(float... fs) {
		float min = 0.0f;

		for (float v : fs) {
			if (v < min) {
				min = v;
			}
		}

		return min;
	}

	/**
	 * A flooring modulus operator. Works similarly to the % operator, except this rounds towards negative infinity rather than towards 0.
	 * <p>
	 * For example: <br>
	 * -7 % 3 = -2; floorMod(-7, 3) = 2 <br>
	 * -6 % 3 = -0; floorMod(-6, 3) = 0 <br>
	 * -5 % 3 = -2; floorMod(-5, 3) = 1 <br>
	 * -4 % 3 = -1; floorMod(-4, 3) = 2 <br>
	 * -3 % 3 = -0; floorMod(-3, 3) = 0 <br>
	 * -2 % 3 = -2; floorMod(-2, 3) = 1 <br>
	 * -1 % 3 = -1; floorMod(-1, 3) = 2 <br>
	 * 0 % 3 = 0; floorMod(0, 3) = 0 <br>
	 * 1 % 3 = 1; floorMod(1, 3) = 1 <br>
	 * 2 % 3 = 2; floorMod(2, 3) = 2 <br>
	 * 3 % 3 = 0; floorMod(3, 3) = 0 <br>
	 * 4 % 3 = 1; floorMod(4, 3) = 1 <br>
	 *
	 * @param numerator The numerator of the modulus operator.
	 * @param denominator The denominator of the modulus operator.
	 *
	 * @return numerator % denominator, rounded towards negative infinity.
	 */
	public static int floorMod(int numerator, int denominator) {
		if (denominator < 0) {
			throw new IllegalArgumentException("FloorMod does not support negative denominators!");
		}

		if (numerator > 0) {
			return numerator % denominator;
		} else {
			int mod = (-numerator) % denominator;

			if (mod != 0) {
				mod = denominator - mod;
			}

			return mod;
		}
	}

	/**
	 * A flooring modulus operator. Works similarly to the % operator, except this rounds towards negative infinity rather than towards 0.
	 * <p>
	 * For example: <br>
	 * -7 % 3 = -2; floorMod(-7, 3) = 2 <br>
	 * -6 % 3 = -0; floorMod(-6, 3) = 0 <br>
	 * -5 % 3 = -2; floorMod(-5, 3) = 1 <br>
	 * -4 % 3 = -1; floorMod(-4, 3) = 2 <br>
	 * -3 % 3 = -0; floorMod(-3, 3) = 0 <br>
	 * -2 % 3 = -2; floorMod(-2, 3) = 1 <br>
	 * -1 % 3 = -1; floorMod(-1, 3) = 2 <br>
	 * 0 % 3 = 0; floorMod(0, 3) = 0 <br>
	 * 1 % 3 = 1; floorMod(1, 3) = 1 <br>
	 * 2 % 3 = 2; floorMod(2, 3) = 2 <br>
	 * 3 % 3 = 0; floorMod(3, 3) = 0 <br>
	 * 4 % 3 = 1; floorMod(4, 3) = 1 <br>
	 *
	 * @param numerator The numerator of the modulus operator.
	 * @param denominator The denominator of the modulus operator.
	 *
	 * @return numerator % denominator, rounded towards negative infinity.
	 */
	public static double floorMod(double numerator, double denominator) {
		if (denominator < 0.0) {
			throw new IllegalArgumentException("FloorMod does not support negative denominators!");
		}

		if (numerator > 0.0) {
			return numerator % denominator;
		} else {
			double mod = (-numerator) % denominator;

			if (mod != 0.0) {
				mod = denominator - mod;
			}

			return mod;
		}
	}

	/**
	 * Normalizes a angle into the range of 0-360.
	 *
	 * @param angle The source angle.
	 *
	 * @return The normalized angle.
	 */
	public static float normalizeAngle(float angle) {
		if (angle >= 360.0f) {
			return angle - 360.0f;
		} else if (angle < 0) {
			return angle + 360.0f;
		}

		return angle;
	}

	/**
	 * Rounds a value to a amount of places after the decimal point.
	 *
	 * @param value The value to round.
	 * @param place How many places after the decimal to round to.
	 *
	 * @return The rounded value.
	 */
	public static float roundToPlace(float value, int place) {
		float placeMul = (float) (Math.pow(10.0f, place));
		return (float) Math.round((value) * placeMul) / placeMul;
	}

	/**
	 * Used to floor the value if less than the min.
	 *
	 * @param min The minimum value.
	 * @param value The value.
	 *
	 * @return Returns a value with deadband applied.
	 */
	public static float deadband(float min, float value) {
		return Math.abs(value) >= Math.abs(min) ? value : 0.0f;
	}

	/**
	 * Ensures {@code value} is in the range of {@code min} to {@code max}. If {@code value} is greater than {@code max}, this will return {@code max}. If {@code value} is less than {@code min}, this will return {@code min}. Otherwise, {@code value} is returned unchanged.
	 *
	 * @param value The value to clamp.
	 * @param min The smallest value of the result.
	 * @param max The largest value of the result.
	 *
	 * @return {@code value}, clamped between {@code min} and {@code max}.
	 */
	public static float clamp(float value, float min, float max) {
		return (value < min) ? min : (value > max) ? max : value;
	}

	/**
	 * Limits the value.
	 *
	 * @param value The value.
	 * @param limit The limit.
	 *
	 * @return A limited value.
	 */
	public static float limit(float value, float limit) {
		return value > limit ? limit : value;
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
		float det = (p2.getZ() - p3.getZ()) * (p1.getX() - p3.getX()) + (p3.getX() - p2.getX()) * (p1.getZ() - p3.getZ());
		float l1 = ((p2.getZ() - p3.getZ()) * (pos.getX() - p3.getX()) + (p3.getX() - p2.getX()) * (pos.getY() - p3.getZ())) / det;
		float l2 = ((p3.getZ() - p1.getZ()) * (pos.getX() - p3.getX()) + (p1.getX() - p3.getX()) * (pos.getY() - p3.getZ())) / det;
		float l3 = 1.0f - l1 - l2;

		return l1 * p1.getY() + l2 * p2.getY() + l3 * p3.getY();
	}

	/**
	 * Interpolates two values by a blendFactor using cos interpolation.
	 *
	 * @param a The first value.
	 * @param b The second value.
	 * @param blend The blend value.
	 *
	 * @return Returns a interpolated value.
	 */
	public static float cosInterpolate(float a, float b, float blend) {
		double ft = blend * Math.PI;
		float f = (float) ((1.0f - Math.cos(ft)) * 0.5f);
		return a * (1.0f - f) + b * f;
	}
}
