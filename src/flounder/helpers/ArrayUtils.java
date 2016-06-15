package flounder.helpers;

import java.util.*;

public class ArrayUtils {
	/**
	 * Copies the contents from an array list to an array.
	 *
	 * @param list The array list to copy from.
	 * @param array The array to copy to (keep same size as list).
	 *
	 * @return The copied array.
	 */
	public static float[] copyToArray(List<Float> list, float[] array) {
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}

		return array;
	}
}
