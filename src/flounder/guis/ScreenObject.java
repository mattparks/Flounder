package flounder.guis;

import flounder.devices.*;
import flounder.framework.*;
import flounder.maths.vectors.*;
import flounder.visual.*;

import java.util.*;

public abstract class ScreenObject {
	private ScreenObject parent;
	private List<ScreenObject> children;

	private boolean visible;
	private boolean useAspect;
	private Vector2f position;
	private Vector2f dimensions;

	private ValueDriver rotationDriver;
	private float rotation;

	private ValueDriver alphaDriver;
	private float alpha;

	private ValueDriver scaleDriver;
	private float scale;

	public ScreenObject(ScreenObject parent, boolean useAspect, Vector2f position, Vector2f dimensions) {
		this.visible = true;
		this.parent = parent;
		this.children = new ArrayList<>();

		this.useAspect = useAspect;
		this.position = position;
		this.dimensions = dimensions;

		this.rotationDriver = new ConstantDriver(0.0f);
		this.rotation = 0.0f;

		this.alphaDriver = new ConstantDriver(1.0f);
		this.alpha = 1.0f;

		this.scaleDriver = new ConstantDriver(1.0f);
		this.scale = 1.0f;
	}

	public void update() {
		rotation = rotationDriver.update(Framework.getDelta());
		alpha = alphaDriver.update(Framework.getDelta());
		scale = scaleDriver.update(Framework.getDelta());
	}

	public abstract void updateObject();

	public ScreenObject getParent() {
		return parent;
	}

	public void addChild(ScreenObject child, boolean visible) {
		child.visible = visible;
		this.children.add(child);
	}

	public void removeChild(ScreenObject child) {
		this.children.remove(child);
	}

	public void getAll(List<ScreenObject> list) {
		list.add(this);
		children.forEach((child) -> child.getAll(list));
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isUseAspect() {
		return useAspect;
	}

	public void setUseAspect(boolean useAspect) {
		this.useAspect = useAspect;
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
		return alpha;
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

	public Vector2f getScreenPosition(Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		return destination.set(position.x * (useAspect ? FlounderDisplay.getAspectRatio() : 1.0f), position.y);
	}

	public Vector2f getScreenDimensions(Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		return destination.set(dimensions.x * (useAspect ? FlounderDisplay.getAspectRatio() : 1.0f), dimensions.y).scale(getScale());
	}

	public abstract void delete();
}
