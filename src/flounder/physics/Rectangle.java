package flounder.physics;

import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.util.*;

/**
 * Holds info for a 2d rectangle.
 */
public class Rectangle extends IBounding<Rectangle> {
	private static final MyFile MODEL_FILE = new MyFile(MyFile.RES_FOLDER, "rectangle", "sphere.obj");

	private Vector2f position;
	private float width;
	private float height;

	/**
	 * Creates a new rectangle.
	 *
	 * @param position The rectangles position.
	 * @param width The rectangles width.
	 * @param height The rectangles height.
	 */
	public Rectangle(Vector2f position, float width, float height) {
		this.position = position;
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean contains(Rectangle other) {
		final double x0 = position.getX();
		final double y0 = position.getY();
		final double x = other.position.getX();
		final double y = other.position.getY();
		final double w = other.getWidth();
		final double h = other.getHeight();

		return ((x >= x0) && (y >= y0)
				&& ((x + w) <= (x0 + getWidth()))
				&& ((y + h) <= (y0 + getHeight())));
	}

	@Override
	public IntersectData intersects(AABB aabb) throws IllegalArgumentException {
		throw new NotImplementedException();
	}

	@Override
	public IntersectData intersects(Sphere sphere) throws IllegalArgumentException {
		throw new NotImplementedException();
	}

	@Override
	public IntersectData intersects(Rectangle other) throws IllegalArgumentException {
		return new IntersectData(position.x < other.position.x + other.width && position.x + width > other.position.x && position.y < other.position.y + other.height && position.y + height > other.position.y, 0.0f);
	}

	@Override
	public IntersectData intersects(Ray ray) throws IllegalArgumentException {
		throw new NotImplementedException();
	}

	@Override
	public boolean contains(Vector3f point) {
		return (position.x <= point.x) && (point.x < (position.x + width)) && (position.y <= point.y) && (point.y < (position.y + height));
	}

	/**
	 * Gets if a point is contained in this shape.
	 *
	 * @param point The point to check if it is contained.
	 *
	 * @return If the point is contained in this shape.
	 */
	public boolean contains(Vector2f point) {
		return (position.x <= point.x) && (point.x < (position.x + width)) && (position.y <= point.y) && (point.y < (position.y + height));
	}

	@Override
	public boolean inFrustum(Frustum frustum) {
		throw new NotImplementedException();
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position.set(position);
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	@Override
	public ModelObject getRenderModel() {
		return ModelFactory.newBuilder().setFile(MODEL_FILE).create();
	}

	@Override
	public Vector3f getRenderCentre(Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		return destination.set(position.x, position.y, 0.0f);
	}

	@Override
	public Vector3f getRenderScale(Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		return destination.set(width, height, 0.0f);
	}

	@Override
	public Colour getRenderColour(Colour destination) {
		if (destination == null) {
			destination = new Colour();
		}

		return destination.set(0.0f, 0.0f, 1.0f);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 79 * hash + Objects.hashCode(position);
		hash = 79 * hash + Objects.hashCode(width);
		hash = 79 * hash + Objects.hashCode(height);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		} else if (super.getClass() != object.getClass()) {
			return false;
		}

		Rectangle other = (Rectangle) object;

		if (!Objects.equals(position, other.position)) {
			return false;
		} else if (!Objects.equals(width, other.width)) {
			return false;
		} else if (!Objects.equals(height, other.height)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "Rectangle{" + "position=" + position + ", width=" + width + ", height=" + height + '}';
	}
}
