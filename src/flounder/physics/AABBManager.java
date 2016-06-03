package flounder.physics;

import java.util.*;

public class AABBManager {
	private static List<AABB> TO_RENDER_AABB = new ArrayList<>();

	public static void addAABBRender(AABB aabb) {
		TO_RENDER_AABB.add(aabb);
	}

	protected static List<AABB> getRenderAABB() {
		return TO_RENDER_AABB;
	}

	protected static void clear() {
		TO_RENDER_AABB.clear();
	}
}
