package flounder.models;

import flounder.physics.*;

public abstract class ModelLoadManual {
	private String name;

	public ModelLoadManual(String name) {
		this.name = name;
	}

	/**
	 * Gets the manual model name.
	 *
	 * @return The manual model name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the manual model vertices.
	 *
	 * @return The manual model vertices.
	 */
	public abstract float[] getVertices();

	/**
	 * Gets the manual model texture.
	 *
	 * @return The manual model texture.
	 */
	public abstract float[] getTextureCoords();

	/**
	 * Gets the manual model normals.
	 *
	 * @return The manual model normals.
	 */
	public abstract float[] getNormals();

	/**
	 * Gets the manual model tangents.
	 *
	 * @return The manual model tangents.
	 */
	public abstract float[] getTangents();

	/**
	 * Gets the manual model indices.
	 *
	 * @return The manual model indices.
	 */
	public abstract int[] getIndices();

	/**
	 * Gets if the model will render with smooth shading.
	 *
	 * @return If the model uses smooth shading.
	 */
	public abstract boolean isSmoothShading();

	/**
	 * Gets the surrounding AABB (without applied scale).
	 *
	 * @return The surrounding AABB.
	 */
	public abstract AABB getAABB();

	/**
	 * Gets the surrounding Hull (without applied scale).
	 *
	 * @return The surrounding Hull.
	 */
	public abstract QuickHull getHull();

	public ModelData toData() {
		return new ModelData(getVertices(), getTextureCoords(), getNormals(), getTangents(), getIndices(), getAABB(), getHull(), isSmoothShading(), getName());
	}
}
