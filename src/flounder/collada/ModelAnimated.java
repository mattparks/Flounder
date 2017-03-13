package flounder.collada;

import flounder.animation.*;
import flounder.collada.geometry.*;
import flounder.collada.joints.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.resources.*;

public class ModelAnimated {
	private final MeshData meshData;

	private final JointsData jointsData;
	private final Joint headJoint;

	private MyFile file;

	private AABB aabb;

	private int vaoID;
	private int vaoLength;

	public ModelAnimated(MeshData meshData, JointsData jointsData, MyFile file) {
		this.meshData = meshData;

		this.file = file;

		float furthest = meshData.getFurthestPoint();
		this.aabb = new AABB(new Vector3f(-furthest, -furthest, -furthest), new Vector3f(furthest, furthest, furthest));

		this.jointsData = jointsData;
		this.headJoint = createJoints(jointsData.getHeadJoint());

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

	public JointsData getJointsData() {
		return jointsData;
	}

	public Joint getHeadJoint() {
		return headJoint;
	}

	public MyFile getFile() {
		return file;
	}

	public AABB getAABB() {
		return aabb;
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
