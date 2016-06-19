package flounder.maths;

import flounder.maths.vectors.*;

import java.nio.*;

/**
 * Holds a RGBA colour.
 */
public class Colour {
	public float r, g, b, a;

	/**
	 * Constructor for Colour.
	 */
	public Colour() {
		set(0.0f, 0.0f, 0.0f, 1.0f);
	}

	/**
	 * Sets values in the colour.
	 *
	 * @param r The new R value.
	 * @param g The new G value.
	 * @param b The new B value.
	 * @param a The new A value.
	 *
	 * @return This.
	 */
	public Colour set(float r, float g, float b, float a) {
		return set(r, g, b, a, false);
	}

	/**
	 * Sets values in the colour.
	 *
	 * @param r The new R value.
	 * @param g The new G value.
	 * @param b The new B value.
	 * @param a The new A value.
	 * @param convert Converts the colours from 0-255 to 0-1.
	 *
	 * @return This.
	 */
	public Colour set(float r, float g, float b, float a, boolean convert) {
		if (convert) {
			this.r = r / 255.0f;
			this.g = g / 255.0f;
			this.b = b / 255.0f;
			this.a = a / 255.0f;
		} else {
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}

		return this;
	}

	/**
	 * Constructor for Colour.
	 *
	 * @param source Creates this colour out of a existing one.
	 */
	public Colour(Colour source) {
		set(source);
	}

	/**
	 * Sets values in the colour.
	 *
	 * @param source The source colour.
	 *
	 * @return This.
	 */
	public Colour set(Colour source) {
		return set(source.r, source.g, source.b, source.a, false);
	}

	/**
	 * Constructor for Colour.
	 *
	 * @param r The new R value.
	 * @param g The new G value.
	 * @param b The new B value.
	 */
	public Colour(float r, float g, float b) {
		set(r, g, b, 1.0f);
	}

	/**
	 * Constructor for Colour.
	 *
	 * @param r The new R value.
	 * @param g The new G value.
	 * @param b The new B value.
	 * @param a The new A value.
	 */
	public Colour(float r, float g, float b, float a) {
		set(r, g, b, a);
	}

	/**
	 * Constructor for Colour.
	 *
	 * @param r The new R value.
	 * @param g The new G value.
	 * @param b The new B value.
	 * @param convert Converts the colours from 0-255 to 0-1.
	 */
	public Colour(float r, float g, float b, boolean convert) {
		set(r, g, b, convert);
	}

	/**
	 * Sets values in the colour.
	 *
	 * @param r The new R value.
	 * @param g The new G value.
	 * @param b The new B value.
	 * @param convert Converts the colours from 0-255 to 0-1.
	 *
	 * @return This.
	 */
	public Colour set(float r, float g, float b, boolean convert) {
		return set(r, g, b, 1.0f, convert);
	}

	/**
	 * Constructor for Colour.
	 *
	 * @param r The new R value.
	 * @param g The new G value.
	 * @param b The new B value.
	 * @param a The new A value.
	 * @param convert Converts the colours from 0-255 to 0-1.
	 */
	public Colour(float r, float g, float b, float a, boolean convert) {
		set(r, g, b, a, convert);
	}

	/**
	 * Adds two colours together and places the result in the destination colour.
	 *
	 * @param left The left source colour.
	 * @param right The right source colour.
	 * @param destination The destination colour or null if a new colour is to be created.
	 *
	 * @return The destination colour.
	 */
	public static Colour add(Colour left, Colour right, Colour destination) {
		if (destination == null) {
			destination = new Colour();
		}

		return destination.set(left.r + right.r, left.g + right.g, left.b + right.b, left.a + right.a);
	}

	/**
	 * Subtracts two colours together and places the result in the destination colour.
	 *
	 * @param left The left source colour.
	 * @param right The right source colour.
	 * @param destination The destination colour or null if a new colour is to be created.
	 *
	 * @return The destination colour.
	 */
	public static Colour subtract(Colour left, Colour right, Colour destination) {
		if (destination == null) {
			destination = new Colour();
		}

		return destination.set(left.r - right.r, left.g - right.g, left.b - right.b, left.a - right.a);
	}

	/**
	 * Multiplies two colours together and places the result in the destination colour.
	 *
	 * @param left The left source colour.
	 * @param right The right source colour.
	 * @param destination The destination colour or null if a new colour is to be created.
	 *
	 * @return The destination colour.
	 */
	public static Colour multiply(Colour left, Colour right, Colour destination) {
		if (destination == null) {
			destination = new Colour();
		}

		return destination.set(left.r * right.r, left.g * right.g, left.b * right.b, left.a * right.a);
	}

	/**
	 * Divides two colours together and places the result in the destination colour.
	 *
	 * @param left The left source colour.
	 * @param right The right source colour.
	 * @param destination The destination colour or null if a new colour is to be created.
	 *
	 * @return The destination colour.
	 */
	public static Colour divide(Colour left, Colour right, Colour destination) {
		if (destination == null) {
			destination = new Colour();
		}

		return destination.set(left.r / right.r, left.g / right.g, left.b / right.b, left.a / right.a);
	}

	/**
	 * Interpolates between two colours and places the result in the destination colour.
	 *
	 * @param left The left source colour.
	 * @param right The right source colour.
	 * @param blend The blend factor.
	 * @param destination The destination colour or null if a new colour is to be created.
	 *
	 * @return The destination colour.
	 */
	public static Colour interpolate(Colour left, Colour right, float blend, Colour destination) {
		if (destination == null) {
			destination = new Colour();
		}

		float leftWeight = 1 - blend;
		float r = leftWeight * left.r + blend * right.r;
		float g = leftWeight * left.g + blend * right.g;
		float b = leftWeight * left.b + blend * right.b;
		return destination.set(r, g, b, 1.0f, false);
	}

	/**
	 * Gets a colour representing the unit value of this colour.
	 *
	 * @return The unit colour.
	 */
	public Colour getUnit() {
		Colour colour = new Colour(this);
		colour.scale(1.0f / length());
		return colour;
	}

	/**
	 * Scales the colour by a scalar.
	 *
	 * @param scalar The scaling value.
	 *
	 * @return this.
	 */
	public Colour scale(float scalar) {
		r *= scalar;
		g *= scalar;
		b *= scalar;
		return this;
	}

	/**
	 * @return The length of the colour.
	 */
	public float length() {
		return (float) Math.sqrt(lengthSquared());
	}

	/**
	 * @return The length squared of the colour.
	 */
	public float lengthSquared() {
		return (float) (Math.pow(r, 2) + Math.pow(g, 2) + Math.pow(b, 2) + Math.pow(a, 2));
	}

	/**
	 * Sets values in the colour.
	 *
	 * @param r The new R value.
	 * @param g The new G value.
	 * @param b The new B value.
	 *
	 * @return This.
	 */
	public Colour set(float r, float g, float b) {
		return set(r, g, b, 1.0f, false);
	}

	/**
	 * @return The colours red component.
	 */
	public float getR() {
		return r;
	}

	/**
	 * @return The colours green component.
	 */
	public float getG() {
		return g;
	}

	/**
	 * @return The colours blue component.
	 */
	public float getB() {
		return b;
	}

	/**
	 * @return The colours alpha component.
	 */
	public float getA() {
		return a;
	}

	/**
	 * Creates a colour equivalent but in vector form and places the result in the destination vector.
	 *
	 * @param destination The destination vector or null if a new vector is to be created.
	 *
	 * @return The destination vector.
	 */
	public Vector3f toVector(Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		return destination.set(r, g, b);
	}

	/**
	 * Loads this colour from a FloatBuffer.
	 *
	 * @param buffer The buffer to load it from, at the current position.
	 *
	 * @return This.
	 */
	public Colour load(FloatBuffer buffer) {
		r = buffer.get();
		g = buffer.get();
		b = buffer.get();
		a = buffer.get();
		return this;
	}

	/**
	 * Stores this colour in a FloatBuffer.
	 *
	 * @param buffer The buffer to store it in, at the current position.
	 *
	 * @return This.
	 */
	public Colour store(FloatBuffer buffer) {
		buffer.clear();
		buffer.put(r);
		buffer.put(g);
		buffer.put(b);
		buffer.put(a);
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

		Colour other = (Colour) object;

		return r == other.r && g == other.g && b == other.b && a == other.a;
	}

	@Override
	public String toString() {
		return "Colour{" + r + ", " + g + ", " + b + "}";
	}
}
