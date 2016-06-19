package flounder.helpers;

import java.util.*;

/**
 * Utility's built for helping arrays.
 */
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

	/**
	 * Gets the total amount of secondary values in a hashmap.
	 *
	 * @param hashMap The hashmap.
	 * @param <T> Primary value type.
	 * @param <Y> Secondary List value type.
	 *
	 * @return Count of all secondary items.
	 */
	public static <T extends Object, Y extends Object> int totalSecondaryCount(Map<T, List<Y>> hashMap) {
		int count = 0;

		for (List<Y> list : hashMap.values()) {
			count += list.size();
		}

		return count;
	}
}
