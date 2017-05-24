package flounder.guis;

import flounder.devices.*;
import flounder.framework.*;
import flounder.maths.vectors.*;
import flounder.visual.*;

import java.util.*;

/**
 * A representation of a object this is rendered to a screen. This object is contained in a parent and has children.
 * The screen object has a few values that allow for it to be positioned and scaled, along with other variables that are used when rendering.
 * This class can be extended to create a representation for GUI textures, fonts, etc.
 */
public abstract class ScreenObject {
	private ScreenObject parent;
	private List<ScreenObject> children;

	private boolean visible;
	private Vector2f position;
	private Vector2f dimensions;
	private Vector2f meshSize;
	private Vector4f scissor;

	private boolean inScreenCoords;

	private Vector2f screenPosition;
	private Vector2f screenDimensions;
	private Vector2f positionOffsets;

	private ValueDriver rotationDriver;
	private float rotation;

	private ValueDriver alphaDriver;
	private float alpha;

	private ValueDriver scaleDriver;
	private float scale;

	/**
	 * Creates a new screen object.
	 *
	 * @param parent The parent screen object.
	 * @param position The position in relative space (can be changed to screen space be changing {@code #inScreenCoords} to true.)
	 * @param dimensions The dimensions of the object, its width is scaled with the aspect ratio so it remains in proportion to the original values.
	 */
	public ScreenObject(ScreenObject parent, Vector2f position, Vector2f dimensions) {
		if (parent != null) {
			parent.children.add(this);
		}

		this.visible = true;
		this.parent = parent;
		this.children = new ArrayList<>();

		this.position = position;
		this.dimensions = dimensions;
		this.meshSize = new Vector2f();
		this.scissor = new Vector4f(-1.0f, -1.0f, -1.0f, -1.0f);

		this.inScreenCoords = true;

		this.screenPosition = new Vector2f();
		this.screenDimensions = new Vector2f();
		this.positionOffsets = new Vector2f();

		this.rotationDriver = new ConstantDriver(0.0f);
		this.rotation = 0.0f;

		this.alphaDriver = new ConstantDriver(1.0f);
		this.alpha = 1.0f;

		this.scaleDriver = new ConstantDriver(1.0f);
		this.scale = 1.0f;
	}

	/**
	 * Updates this screen object and the extended object.
	 */
	public void update() {
		rotation = rotationDriver.update(Framework.getDelta());
		alpha = alphaDriver.update(Framework.getDelta());
		scale = scaleDriver.update(Framework.getDelta());

		if (isVisible() && getAlpha() != 0.0f) {
			updateObject();
		}

		children.forEach(ScreenObject::update);
	}

	/**
	 * Updates the implementation.
	 */
	public abstract void updateObject();

	/**
	 * Gets the parent object.
	 *
	 * @return The parent object.
	 */
	public ScreenObject getParent() {
		return parent;
	}

	/**
	 * Removes this object from the previous parent and attaches it to another parent.
	 *
	 * @param parent The new parent object.
	 */
	public void setParent(ScreenObject parent) {
		this.parent.removeChild(this);
		parent.children.add(this);
		this.parent = parent;
	}

	/**
	 * Disowns a child from this screen objects children list.
	 *
	 * @param child The child to disown.
	 */
	public void removeChild(ScreenObject child) {
		this.children.remove(child);
	}

	/**
	 * Adds this object and its children to a list.
	 *
	 * @param list The list to add to.
	 *
	 * @return The list that has been added to.
	 */
	public List<ScreenObject> getAll(List<ScreenObject> list) {
		list.add(this);
		children.forEach((child) -> child.getAll(list));
		return list;
	}

	public boolean isVisible() {
		if (parent != null) {
			return visible && parent.isVisible();
		} else {
			return visible;
		}
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position.set(position);
	}

	public Vector2f getDimensions() {
		return dimensions;
	}

	public void setDimensions(Vector2f dimensions) {
		this.dimensions.set(dimensions);
	}

	/**
	 * Gets the original mesh dimensions for the object.
	 *
	 * @return The original mesh dimensions.
	 */
	public Vector2f getMeshSize() {
		return meshSize;
	}

	public void setMeshSize(Vector2f meshSize) {
		this.meshSize = meshSize;
	}

	public Vector4f getScissor() {
		return scissor;
	}

	public void setScissor(Vector4f scissor) {
		this.scissor.set(scissor);
	}

	public void clearScissor() {
		this.scissor.set(-1.0f, -1.0f, -1.0f, -1.0f);
	}

	public Vector2f getPositionOffsets() {
		return positionOffsets;
	}

	public boolean isInScreenCoords() {
		return inScreenCoords;
	}

	public void setInScreenCoords(boolean inScreenCoords) {
		this.inScreenCoords = inScreenCoords;
	}

	public ValueDriver getRotationDriver() {
		return rotationDriver;
	}

	public void setRotationDriver(ValueDriver rotationDriver) {
		this.rotationDriver = rotationDriver;
	}

	public float getRotation() {
		return rotation;
	}

	public ValueDriver getAlphaDriver() {
		return alphaDriver;
	}

	public void setAlphaDriver(ValueDriver alphaDriver) {
		this.alphaDriver = alphaDriver;
	}

	public float getAlpha() {
		if (parent != null) {
			return alpha * parent.getAlpha();
		} else {
			return alpha;
		}
	}

	public ValueDriver getScaleDriver() {
		return scaleDriver;
	}

	public void setScaleDriver(ValueDriver scaleDriver) {
		this.scaleDriver = scaleDriver;
	}

	public float getScale() {
		return scale;
	}

	/**
	 * Gets the positions relative in screen space.
	 *
	 * @return The screen positions.
	 */
	public Vector2f getScreenPosition() {
		return screenPosition.set((position.x * (inScreenCoords ? FlounderDisplay.get().getAspectRatio() : 1.0f)) + positionOffsets.x, position.y + positionOffsets.y);
	}

	/**
	 * Gets the dimensions relative in screen space.
	 *
	 * @return The screen dimensions.
	 */
	public Vector2f getScreenDimensions() {
		return screenDimensions.set(dimensions.x, dimensions.y).scale(getScale());
	}

	/**
	 * Deletes this screen object and the extended object.
	 */
	public void delete() {
		for (ScreenObject child : children) {
			child.delete();
		}

		this.deleteObject();
	}

	/**
	 * Deletes the implementation.
	 */
	public abstract void deleteObject();
}
