package flounder.helpers;

import java.util.*;

/**
 * A helper for helping java arrays.
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
	 * Gets the total amount of secondary values in a map.
	 *
	 * @param hashMap The map.
	 * @param <T> Primary value type.
	 * @param <Y> Secondary List value type.
	 *
	 * @return Count of all secondary items.
	 */
	public static <T, Y> int totalSecondaryCount(Map<T, List<Y>> hashMap) {
		int count = 0;

		for (List<Y> list : hashMap.values()) {
			count += list.size();
		}

		return count;
	}

	/**
	 * Adds a object to an existing array in Java.
	 *
	 * @param array The array to add into.
	 * @param newObject The object to add.
	 * @param <T> The generic array type.
	 *
	 * @return The array with the added value.
	 */
	public static <T> T[] addElement(T[] array, T newObject) {
		array = Arrays.copyOf(array, array.length + 1);
		array[array.length - 1] = newObject;
		return array;
	}

	/**
	 * Adds a object to an existing int array in Java.
	 *
	 * @param array The array to add into.
	 * @param newObject The object to add.
	 *
	 * @return The array with the added value.
	 */
	public static int[] addElement(int[] array, int newObject) {
		array = Arrays.copyOf(array, array.length + 1);
		array[array.length - 1] = newObject;
		return array;
	}

	/**
	 * Removes a object to an existing array in Java.
	 *
	 * @param array The array to remove from.
	 * @param removeObject The object to remove.
	 * @param <T> The generic array type.
	 *
	 * @return The array with the removed value.
	 */
	public static <T> T[] removeElement(T[] array, T removeObject) {
		List<T> result = new LinkedList<>();

		for (T item : array) {
			if (!removeObject.equals(item)) {
				result.add(item);
			}
		}

		return result.toArray(array);
	}
}
