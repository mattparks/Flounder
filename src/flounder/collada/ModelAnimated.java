package flounder.collada;

import flounder.animation.*;
import flounder.collada.geometry.*;
import flounder.collada.skeleton.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.resources.*;

public class ModelAnimated {
	private final MeshData meshData;

	private final SkeletonData skeletonData;
	private final Joint headJoint;

	private MyFile file;

	private Collider collider;
	private QuickHull quickHull;

	private int vaoID;
	private int vaoLength;

	public ModelAnimated(MeshData meshData, SkeletonData skeletonData, MyFile file) {
		this.meshData = meshData;

		this.file = file;

		this.collider = meshData.getAABB();
		this.quickHull = new QuickHull();

		this.quickHull.loadData(meshData.getVertices());

		this.skeletonData = skeletonData;
		this.headJoint = createJoints(skeletonData.getHeadJoint());

		this.vaoID = -1;
		this.vaoLength = -1;

		FlounderCollada.loadModelToOpenGL(this);
	}

	private static Joint createJoints(JointData data) {
		Joint j = new Joint(data.getIndex(), data.getNameId(), data.getBindLocalTransform());

		for (JointData child : data.getChildren()) {
			j.addChild(createJoints(child));
		}

		return j;
	}

	public MeshData getMeshData() {
		return meshData;
	}

	public SkeletonData getSkeletonData() {
		return skeletonData;
	}

	public Joint getHeadJoint() {
		return headJoint;
	}

	public MyFile getFile() {
		return file;
	}

	public Collider getCollider() {
		return collider;
	}

	public QuickHull getQuickHull() {
		return quickHull;
	}

	public int getVaoID() {
		return vaoID;
	}

	public void setVaoID(int vaoID) {
		this.vaoID = vaoID;
	}

	public int getVaoLength() {
		return vaoLength;
	}

	public void setVaoLength(int vaoLength) {
		this.vaoLength = vaoLength;
	}

	public boolean isLoaded() {
		return vaoID != -1 && vaoLength != -1;
	}

	/**
	 * Deletes the animated model from OpenGL memory.
	 */
	public void delete() {
		//	loaded = false;
		//	FlounderProcessors.sendRequest(new ModelDeleteRequest(vaoID));
	}
}
