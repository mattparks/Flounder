package flounder.models.loaderE;

import flounder.factory.*;
import flounder.processing.*;

public class ModelObject extends FactoryObject {
	private int vaoID;
	private int vaoLength;

	public ModelObject() {
		super();
	}

	public void loadData(int vaoID, int vaoLength) {
		this.vaoID = vaoID;
		this.vaoLength = vaoLength;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVaoLength() {
		return vaoLength;
	}

	/**
	 * Deletes the model from OpenGL memory.
	 */
	public void delete() {
		FlounderProcessors.sendRequest(new ModelDeleteRequest(vaoID));
	}
}
