package flounder.toolbox;

/**
 * Implement this in your terrain that needs points to be picked by the {@link MousePicker}.
 */
public interface IPointSearch {
	/**
	 * Gets if the point is in the terrain.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 *
	 * @return If the point is in the terrain.
	 */
	boolean inTerrain(final float x, final float z);

	/**
	 * Gets the height of the terrain.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 *
	 * @return The height of the terrain.
	 */
	float getTerrainHeight(final float x, final float z);
}
