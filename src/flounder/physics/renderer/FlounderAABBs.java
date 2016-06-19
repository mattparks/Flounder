package flounder.physics.renderer;

import flounder.engine.*;
import flounder.physics.*;

import java.util.*;

/**
 * A manager for AABB's that want to be renderer.
 */
public class FlounderAABBs implements IModule {
	private List<AABB> toRenderABBB;
	private int aabbCount;

	/**
	 * Creates a new AABB manager.
	 */
	public FlounderAABBs() {
		toRenderABBB = new ArrayList<>();
		aabbCount = 0;
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
		aabbCount = toRenderABBB.size();
		clear(); // Should have already been rendered.
	}

	@Override
	public void profile() {
		FlounderEngine.getProfiler().add("AABB's", "Renderable", aabbCount);
	}

	/**
	 * Adds the AABB to the render pool. (Run every frame).
	 *
	 * @param aabb The AABB to add.
	 */
	public void addAABBRender(AABB aabb) {
		toRenderABBB.add(aabb);
	}

	/**
	 * Gets a list of the renderable AABB's.
	 *
	 * @return The renderable AABB's.
	 */
	protected List<AABB> getRenderAABB() {
		return toRenderABBB;
	}

	/**
	 * Clears the renderable AABB's.
	 */
	protected void clear() {
		toRenderABBB.clear();
	}

	@Override
	public void dispose() {
		clear();
	}
}
