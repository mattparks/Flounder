package com.flounder.processing;

import java.util.*;

/**
 * Holds requests in a simple que.
 */
public class Queue<T> {
	private List<T> requestQueue;

	/**
	 * Creates a new queue.
	 */
	public Queue() {
		requestQueue = new ArrayList<>();
	}

	/**
	 * Adds a new object to queue.
	 *
	 * @param request The object to add.
	 */
	public synchronized void addRequest(T request) {
		requestQueue.add(request);
	}

	/**
	 * Gets the next item in queue and then removes it from this list.
	 *
	 * @return The next item in queue and then removes it from this list.
	 */
	public synchronized T acceptNextRequest() {
		return requestQueue.remove(0);
	}

	/**
	 * Gets if there are any items left in queue.
	 *
	 * @return Returns true if there are any items left in queue.
	 */
	public synchronized boolean hasRequests() {
		return !requestQueue.isEmpty();
	}

	/**
	 * Gets the number of objects in queue.
	 *
	 * @return The number of objects in queue.
	 */
	public synchronized int count() {
		return requestQueue.size();
	}

	/**
	 * Clears the request queue.
	 */
	public synchronized void clear() {
		requestQueue.clear();
	}
}
