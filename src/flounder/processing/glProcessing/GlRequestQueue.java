package flounder.processing.glProcessing;

import java.util.*;

/**
 * Holds OpenGL requests that are in queue.
 */
public class GlRequestQueue {
	private List<GlRequest> requestQueue;

	/**
	 * Creates a new GL request queue.
	 */
	public GlRequestQueue() {
		requestQueue = new ArrayList<>();
	}

	/**
	 * Adds a new GL request to queue.
	 *
	 * @param request The GL request to add.
	 */
	public synchronized void addRequest(GlRequest request) {
		requestQueue.add(request);
	}

	/**
	 * Gets the next item in queue and then removes it from this list.
	 *
	 * @return The next item in queue and then removes it from this list.
	 */
	public synchronized GlRequest acceptNextRequest() {
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
