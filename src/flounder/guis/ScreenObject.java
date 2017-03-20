package flounder.guis;

import flounder.maths.vectors.*;

import java.util.*;

public abstract class ScreenObject {
	private ScreenObject parent;
	private List<ScreenObject> children;

	private boolean visible;
	private boolean useAspect;
	private Vector2f position;
	private float rotation;
	private Vector2f dimensions;

	public ScreenObject(ScreenObject parent, boolean useAspect, Vector2f position, float rotation, Vector2f dimensions) {
		this.visible = true;
		this.parent = parent;
		this.children = new ArrayList<>();

		this.useAspect = useAspect;
		this.position = position;
		this.rotation = rotation;
		this.dimensions = dimensions;
	}

	public abstract void update();

	public ScreenObject getParent() {
		return parent;
	}

	public void addChild(ScreenObject child) {
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

	public boolean usesAspect() {
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

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public Vector2f getDimensions() {
		return dimensions;
	}

	public void setDimensions(Vector2f dimensions) {
		this.dimensions.set(dimensions);
	}

	public abstract void delete();
}
