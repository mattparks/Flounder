package flounder.models;

import flounder.factory.*;
import flounder.processing.*;

public class ModelObject extends FactoryObject {
	private ModelData data;

	private String name;
	private boolean loaded;

	private int vaoID;
	private int vaoLength;

	public ModelObject() {
		super();
		this.name = null;
		this.loaded = false;
		this.data = null;
	}

	public void loadData(ModelData data, String name) {
		this.data = data;

		this.name = name;
		this.loaded = true;
	}

	public void loadGL(int vaoID, int vaoLength) {
		this.vaoID = vaoID;
		this.vaoLength = vaoLength;
	}

	public ModelData getData() {
		return data;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVaoLength() {
		return vaoLength;
	}

	@Override
	public String toString() {
		return "ModelObject{" +
				"vaoID=" + vaoID +
				", vaoLength=" + vaoLength +
				'}';
	}

	/**
	 * Deletes the model from OpenGL memory.
	 */
	public void delete() {
		FlounderProcessors.sendRequest(new ModelDeleteRequest(vaoID));
		this.loaded = false;
	}
}
