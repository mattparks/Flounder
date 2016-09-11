package flounder.maths;

import flounder.maths.matrices.*;
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
		float scaled = RANDOM.nextFloat() * range;
		float shifted = scaled + min;
		return shifted; // == (rand.nextDouble() * (max-min)) + min;
	}

	/**
	 * Generates a random value from between a range.
	 *
	 * @param min The min value.
	 * @param max The max value.
	 *
	 * @return The randomly selected value within the range.
	 */
	public static double randomInRange(double min, double max) {
		double range = max - min;
		double scaled = RANDOM.nextDouble() * range;
		double shifted = scaled + min;
		return shifted;
	}

	/**
	 * Generates a random unit vector.
	 *
	 * @param destination The destination vector or null if a new vector is to be created.
	 *
	 * @return The destination vector.
	 */
	public static Vector3f generateRandomUnitVector(Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		Random random = new Random();
		float theta = (float) (random.nextFloat() * 2.0f * 3.141592653589793);
		float z = random.nextFloat() * 2.0f - 1.0f;
		float rootOneMinusZSquared = (float) Math.sqrt(1.0f - z * z);
		float x = (float) (rootOneMinusZSquared * Math.cos(theta));
		float y = (float) (rootOneMinusZSquared * Math.sin(theta));
		return destination.set(x, y, z);
	}

	/**
	 * Gets a random point from on a circle.
	 *
	 * @param destination The destination vector or null if a new vector is to be created.
	 * @param normal The circles normal.
	 * @param radius The circles radius.
	 *
	 * @return The destination vector.
	 */
	public static Vector3f randomPointOnCircle(Vector3f destination, Vector3f normal, float radius) {
		if (destination == null) {
			destination = new Vector3f();
		}

		Random random = new Random();

		do {
			Vector3f randomVector = generateRandomUnitVector(null);
			Vector3f.cross(randomVector, normal, destination);
		} while (destination.length() == 0.0f);

		destination.normalize();
		destination.scale(radius);
		float a = random.nextFloat();
		float b = random.nextFloat();

		if (a > b) {
			float temp = a;
			a = b;
			b = temp;
		}

		float randX = (float) (b * Math.cos(6.283185307179586 * (a / b)));
		float randY = (float) (b * Math.sin(6.283185307179586 * (a / b)));
		float distance = new Vector2f(randX, randY).length();
		destination.scale(distance);
		return destination;
	}

	/**
	 * Generates a random unit vector from within a cone.
	 *
	 * @param destination The destination vector or null if a new vector is to be created.
	 * @param coneDirection The cones direction.
	 * @param angle The cones major angle.
	 *
	 * @return The destination vector.
	 */
	public static Vector3f generateRandomUnitVectorWithinCone(Vector3f destination, Vector3f coneDirection, float angle) {
		if (destination == null) {
			destination = new Vector3f();
		}

		float cosAngle = (float) Math.cos(angle);
		Random random = new Random();
		float theta = (float) (random.nextFloat() * 2.0f * 3.141592653589793);
		float z = cosAngle + random.nextFloat() * (1.0f - cosAngle);
		float rootOneMinusZSquared = (float) Math.sqrt(1.0f - z * z);
		float x = (float) (rootOneMinusZSquared * Math.cos(theta));
		float y = (float) (rootOneMinusZSquared * Math.sin(theta));

		Vector4f direction = new Vector4f(x, y, z, 1.0f);

		if ((coneDirection.x != 0.0F) || (coneDirection.y != 0.0F) || ((coneDirection.z != 1.0f) && (coneDirection.z != -1.0f))) {
			Vector3f rotateAxis = Vector3f.cross(coneDirection, new Vector3f(0.0f, 0.0f, 1.0f), null);
			rotateAxis.normalize();
			float rotateAngle = (float) Math.acos(Vector3f.dot(coneDirection, new Vector3f(0.0f, 0.0f, 1.0f)));
			Matrix4f rotationMatrix = new Matrix4f();
			rotationMatrix.setIdentity();
			Matrix4f.rotate(rotationMatrix, rotateAxis, -rotateAngle, rotationMatrix);
			Matrix4f.transform(rotationMatrix, direction, direction);
		} else if (coneDirection.z == -1.0f) {
			direction.z *= -1.0f;
		}

		return destination.set(direction.x, direction.y, direction.z);
	}

	/**
	 * Gets if the pt (point) is in a triangle.
	 *
	 * @param point The point to check.
	 * @param v1 The first triangle vertex.
	 * @param v2 The second triangle vertex
	 * @param v3 The third triangle vertex
	 *
	 * @return If the point is in a triangle.
	 */
	public static boolean pointInTriangle(Vector2f point, Vector2f v1, Vector2f v2, Vector2f v3) {
		boolean b1 = triangleSign(point, v1, v2) < 0.0f;
		boolean b2 = triangleSign(point, v2, v3) < 0.0f;
		boolean b3 = triangleSign(point, v3, v1) < 0.0f;
		return ((b1 == b2) && (b2 == b3));
	}

	private static float triangleSign(Vector2f p1, Vector2f p2, Vector2f p3) {
		return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
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
	 * Gets the maximum vector size.
	 *
	 * @param a The first vector to get values from.
	 * @param b The second vector to get values from.
	 *
	 * @return The maximum vector.
	 */
	public static Vector3f max(Vector3f a, Vector3f b) {
		return new Vector3f(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()));
	}

	/**
	 * Gets the maximum value in a vector.
	 *
	 * @param vector The value to get the maximum value from.
	 *
	 * @return The maximum value.
	 */
	public static float max(Vector3f vector) {
		return Math.max(vector.getX(), Math.max(vector.getY(), vector.getZ()));
	}

	/**
	 * Gets the lowest vector size.
	 *
	 * @param a The first vector to get values from.
	 * @param b The second vector to get values from.
	 *
	 * @return The lowest vector.
	 */
	public static Vector3f min(Vector3f a, Vector3f b) {
		return new Vector3f(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()));
	}

	/**
	 * Gets the lowest value in a vector.
	 *
	 * @param vector The value to get the lowest value from.
	 *
	 * @return The lowest value.
	 */
	public static float min(Vector3f vector) {
		return Math.min(vector.getX(), Math.min(vector.getY(), vector.getZ()));
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
	 * Normalizes a angle into the range of 0-360.
	 *
	 * @param angle The source angle.
	 *
	 * @return The normalized angle.
	 */
	public static double normalizeAngle(double angle) {
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
	 * Rounds a value to a amount of places after the decimal point.
	 *
	 * @param value The value to round.
	 * @param place How many places after the decimal to round to.
	 *
	 * @return The rounded value.
	 */
	public static double roundToPlace(double value, int place) {
		double placeMul = Math.pow(10.0, place);
		return Math.round((value) * placeMul) / placeMul;
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
	 * Used to floor the value if less than the min.
	 *
	 * @param min The minimum value.
	 * @param value The value.
	 *
	 * @return Returns a value with deadband applied.
	 */
	public static double deadband(double min, double value) {
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
	 * Ensures {@code value} is in the range of {@code min} to {@code max}. If {@code value} is greater than {@code max}, this will return {@code max}. If {@code value} is less than {@code min}, this will return {@code min}. Otherwise, {@code value} is returned unchanged.
	 *
	 * @param value The value to clamp.
	 * @param min The smallest value of the result.
	 * @param max The largest value of the result.
	 *
	 * @return {@code value}, clamped between {@code min} and {@code max}.
	 */
	public static double clamp(double value, double min, double max) {
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
	 * Limits the value.
	 *
	 * @param value The value.
	 * @param limit The limit.
	 *
	 * @return A limited value.
	 */
	public static double limit(double value, double limit) {
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

	/**
	 * Generates a single value from a normal distribution, using Box-Muller.
	 * https://en.wikipedia.org/wiki/Box%E2%80%93Muller_transform
	 *
	 * @param standardDeviation The standard deviation of the distribution.
	 * @param mean The mean of the distribution.
	 *
	 * @return A normally distributed value.
	 */
	public static float normallyDistributedSingle(float standardDeviation, float mean) {
		// Intentionally duplicated to avoid IEnumerable overhead.
		double u1 = RANDOM.nextDouble(); // These are uniform(0,1) random doubles.
		double u2 = RANDOM.nextDouble();

		double x1 = Math.sqrt(-2.0 * Math.log(u1));
		double x2 = 2.0 * Math.PI * u2;
		double z1 = x1 * Math.sin(x2); // Random normal(0,1)
		return (float) (z1 * standardDeviation + mean);
	}

	/**
	 * Creates a number between two numbers, logarithmic.
	 *
	 * @param lowerLimit The lower number.
	 * @param upperLimit The upper number.
	 *
	 * @return The final random number.
	 */
	public static double logRandom(double lowerLimit, double upperLimit) {
		double logLower = Math.log(lowerLimit);
		double logUpper = Math.log(upperLimit);

		double raw = RANDOM.nextDouble();
		double result = Math.exp(raw * (logUpper - logLower) + logLower);

		if (result < lowerLimit) {
			result = lowerLimit;
		} else if (result > upperLimit) {
			result = upperLimit;
		}

		return result;
	}
}
