package flounder.helpers;

import java.util.*;

/**
 * A helper for various generic sorting algorithms for java arrays. Value types passed must be {@code comparable}!
 * <p>
 * Watch some of these algorithms at work here: @see <a href="https://www.youtube.com/watch?v=ZZuD6iUe3Pc">'Visualization and Comparison of Sorting Algorithms'</a>
 * </p>
 */
public class ArraySorting {
	/**
	 * First divides a large array into two smaller sub-arrays: the low elements and the high elements. Then recursively sort the sub-arrays.
	 *
	 * @param list The list to be sorted.
	 * @param <T> The list type to be sorted.
	 *
	 * @return Returns a sorted list.
	 */
	public static <T extends Comparable<T>> List<T> quickSort(List<T> list) {
		if (!list.isEmpty()) {
			T pivot = list.get(0); // This pivot can change to get faster results.

			List<T> less = new LinkedList<>();
			List<T> pivotList = new LinkedList<>();
			List<T> more = new LinkedList<>();

			for (T i : list) {
				if (i.compareTo(pivot) < 0) {
					less.add(i);
				} else if (i.compareTo(pivot) > 0) {
					more.add(i);
				} else {
					pivotList.add(i);
				}
			}

			less = quickSort(less);
			more = quickSort(more);
			less.addAll(pivotList);
			less.addAll(more);
			return less;
		}

		return list;
	}

	/**
	 * The first pass, 5-sorting, performs insertion sort on separate subarrays. The next pass, 3-sorting, performs insertion sort on the subarrays. The last pass, 1-sorting, is an ordinary insertion sort of the entire array.
	 *
	 * @param list The list to be sorted.
	 * @param <T> The list type to be sorted.
	 *
	 * @return Returns a sorted list.
	 */
	public static <T extends Comparable<T>> List<T> shellSort(List<T> list) {
		int increment = 4;

		while (increment > 0) {
			for (int outer = increment; outer < list.size(); outer++) {
				int inner = outer;
				T temp = list.get(outer);

				while (inner > increment - 1 && list.get(inner - increment).compareTo(temp) >= 0) {
					list.set(inner, list.get(inner - increment));
					inner -= increment;
				}

				list.set(inner, temp);
			}

			increment = (increment - 1) / 3;
		}

		return list;
	}

	/**
	 * Divides the unsorted list into sublists, each containing 1 element, then repeatedly merges sublists to produce new sorted sublists until there is only 1 sublist remaining.
	 *
	 * @param list The list to be sorted.
	 * @param <T> The list type to be sorted.
	 *
	 * @return Returns a sorted list.
	 */
	public static <T extends Comparable<? super T>> List<T> mergeSort(List<T> list) {
		if (list.size() <= 1) return list;

		int middle = list.size() / 2;
		List<T> left = list.subList(0, middle);
		List<T> right = list.subList(middle, list.size());

		right = mergeSort(right);
		left = mergeSort(left);
		List<T> result = new ArrayList<>();
		Iterator<T> it1 = left.iterator();
		Iterator<T> it2 = right.iterator();

		T x = it1.next();
		T y = it2.next();

		while (true) {
			if (x.compareTo(y) <= 0) {
				result.add(x);

				if (it1.hasNext()) {
					x = it1.next();
				} else {
					result.add(y);

					while (it2.hasNext()) {
						result.add(it2.next());
					}

					break;
				}
			} else {
				result.add(y);

				if (it2.hasNext()) {
					y = it2.next();
				} else {
					result.add(x);

					while (it1.hasNext()) {
						result.add(it1.next());
					}

					break;
				}
			}
		}

		return result;
	}

	/**
	 * Prepares the list by first turning it into a max heap. Then repeatedly swaps the first value of the list with the last value, decreasing the range of values considered in the heap operation by one, and sifting the new first value into its position in the heap. It repeats until the range of values is one value in length.
	 *
	 * @param list The list to be sorted.
	 * @param <T> The list type to be sorted.
	 *
	 * @return Returns a sorted list.
	 */
	public static <T extends Comparable<T>> List<T> heapSort(List<T> list) {
		int count = list.size();
		int start = count / 2 - 1;
		int end = count - 1;

		while (start >= 0) {
			siftDown(list, start, count - 1);
			start -= 1;
		}

		while (end > 0) {
			T temp = list.get(end);
			list.set(end, list.get(0));
			list.set(0, temp);
			end = end - 1;
			siftDown(list, 0, end);
		}

		return list;
	}

	private static <T extends Comparable<T>> void siftDown(List<T> list, int start, int end) {
		int root = start;

		while (root * 2 + 1 <= end) {
			int child = root * 2 + 1;
			int swap = root;

			if (list.get(swap).compareTo(list.get(child)) < 0) {
				swap = child;
			}

			if (child + 1 <= end && list.get(swap).compareTo(list.get(child + 1)) < 0) {
				swap = child + 1;
			}

			if (swap != root) {
				T temp = list.get(root);
				list.set(root, list.get(swap));
				list.set(swap, temp);
				root = swap;
			} else {
				return;
			}
		}
	}

	/**
	 * The gap starts out as the length of the list being sorted divided by the shrink factor, and the list is sorted with that value as the gap. Then the gap is divided by the shrink factor again, the list is sorted with this new gap, and the process repeats until the gap is 1. At this point, comb sort continues using a gap of 1 until the list is fully sorted.
	 *
	 * @param list The list to be sorted.
	 * @param <T> The list type to be sorted.
	 *
	 * @return Returns a sorted list.
	 */
	public static <T extends Comparable<T>> List<T> combSort(List<T> list) {
		int gap = list.size();
		boolean swapped = true;

		while (gap > 1 || swapped) {
			if (gap > 1) {
				gap = (int) (gap / 1.3);
			}

			swapped = false;
			for (int i = 0; i + gap < list.size(); i++) {
				if (list.get(i).compareTo(list.get(i + gap)) > 0) {
					T temp = list.get(i);
					list.set(i, list.get(i + gap));
					list.set(i + gap, temp);
					swapped = true;
				}
			}
		}

		return list;
	}

	/**
	 * Iterates consuming one input element each repetition, and growing a sorted output list. Each iteration, insertion sort removes one element from the input data, finds the location it belongs within the sorted list, and inserts it there. It repeats until no input elements remain.
	 *
	 * @param list The list to be sorted.
	 * @param <T> The list type to be sorted.
	 *
	 * @return Returns a sorted list.
	 */
	public static <T extends Comparable<T>> List<T> insertionSort(List<T> list) {
		for (int i = 1; i < list.size(); i++) {
			T toInsert = list.get(i);
			int j = i - 1;

			while (j >= 0 && (list.get(j).compareTo(toInsert) > 0)) {
				list.set(j + 1, list.get(j));
				j--;
			}

			list.set(j + 1, toInsert);
		}

		return list;
	}

	/**
	 * Divides the input list into two parts: the sublist of items already sorted, which is built up from left to right at the front of the list, and the sublist of items remaining to be sorted that occupy the rest of the list.
	 *
	 * @param list The list to be sorted.
	 * @param <T> The list type to be sorted.
	 *
	 * @return Returns a sorted list.
	 */
	public static <T extends Comparable<T>> List<T> selectionSort(List<T> list) {
		for (int x = 0; x < list.size(); x++) {
			int minimum = x;

			for (int y = x; y < list.size(); y++) {
				if (list.get(minimum).compareTo(list.get(y)) > 0) {
					minimum = y;
				}
			}

			T temp = list.get(minimum);
			list.set(minimum, list.get(x));
			list.set(x, temp);
		}

		return list;
	}

	/**
	 * Unlike bubble sort orders the array in both directions. Hence every iteration of the algorithm consists of two phases. In the first one the lightest bubble ascends to the end of the array, in the second phase the heaviest bubble descends to the beginning of the array.
	 *
	 * @param list The list to be sorted.
	 * @param <T> The list type to be sorted.
	 *
	 * @return Returns a sorted list.
	 */
	public static <T extends Comparable<T>> List<T> cocktailSort(List<T> list) {
		boolean swapped;

		do {
			swapped = false;

			for (int i = 0; i <= list.size() - 2; i++) {
				if (list.get(i).compareTo(list.get(i + 1)) > 0) {
					T temp = list.get(i);
					list.set(i, list.get(i + 1));
					list.set(i + 1, temp);
					swapped = true;
				}
			}

			if (!swapped) {
				break;
			}

			swapped = false;

			for (int i = list.size() - 2; i >= 0; i--) {
				if (list.get(i).compareTo(list.get(i + 1)) > 0) {
					T temp = list.get(i);
					list.set(i, list.get(i + 1));
					list.set(i + 1, temp);
					swapped = true;
				}
			}
		} while (swapped);

		return list;
	}

	/**
	 * Finds the position of a target value within a sorted array.
	 *
	 * @param list The list to be searched.
	 * @param value The value to be found.
	 * @param <T> The list type to be searched.
	 *
	 * @return The index the value was found in.
	 */
	public static <T extends Comparable<T>> int binarySearchForValue(List<T> list, T value) {
		int lowIndex = 0;
		int highIndex = list.size() - 1;

		while (lowIndex <= highIndex) {
			int middleIndex = (highIndex + lowIndex) / 2;

			if (list.get(middleIndex).compareTo(value) < 0) {
				lowIndex = middleIndex + 1;
			} else if (list.get(middleIndex).compareTo(value) > 0) {
				highIndex = middleIndex - 1;
			} else {
				return middleIndex;
			}
		}

		return -1;
	}

	/**
	 * Always finds the first place where two adjacent elements are in the wrong order, and swaps them. It takes advantage of the fact that performing a swap can introduce a new out-of-order adjacent pair only next to the two swapped elements. It does not assume that elements forward of the current position are sorted.
	 *
	 * @param list The list to be sorted.
	 * @param <T> The list type to be sorted.
	 *
	 * @return Returns a sorted list.
	 */
	public static <T extends Comparable<T>> List<T> gnomeSort(List<T> list) {
		int i = 1;
		int j = 2;

		while (i < list.size()) {
			if (list.get(i - 1).compareTo(list.get(i)) <= 0) {
				i = j;
				j++;
			} else {
				T tmp = list.get(i - 1);
				list.set(i - 1, list.get(i));
				list.set(i--, tmp);
				i = (i == 0) ? j++ : i;
			}
		}

		return list;
	}

	/**
	 * Repeatedly steps through the list to be sorted, compares each pair of adjacent items and swaps them if they are in the wrong order. The pass through the list is repeated until no swaps are needed.
	 *
	 * @param list The list to be sorted.
	 * @param <T> The list type to be sorted.
	 *
	 * @return Returns a sorted list.
	 */
	public static <T extends Comparable<T>> List<T> bubbleSort(List<T> list) {
		for (int i = list.size() - 1; i > 1; i--) {
			for (int j = 0; j < i; j++) {
				if (list.get(j).compareTo(list.get(j + 1)) > 0) {
					T temp = list.get(j);
					list.set(j, list.get(j + 1));
					list.set(j + 1, temp);
				}
			}
		}

		return list;
	}
}
