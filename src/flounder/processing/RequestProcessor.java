package flounder.processing;

import flounder.engine.*;

/**
 * Class that is responsible for flounder.processing resource requests in a separate thread.
 */
public class RequestProcessor extends Thread {
	private static RequestProcessor PROCESSOR = new RequestProcessor();

	private final RequestQueue requestQueue;
	private boolean running;

	/**
	 * Creates a new class for flounder.processing resource requests in a separate thread.
	 */
	private RequestProcessor() {
		requestQueue = new RequestQueue();
		running = true;
		this.start();
	}

	/**
	 * Sends a new resource request to queue.
	 *
	 * @param request The resource request to add.
	 */
	public static void sendRequest(ResourceRequest request) {
		PROCESSOR.addRequestToQueue(request);
	}

	/**
	 * Adds a new request to the queue.
	 *
	 * @param request The resource request to add.
	 */
	private void addRequestToQueue(ResourceRequest request) {
		boolean isPaused = !requestQueue.hasRequests();
		requestQueue.addRequest(request);

		if (isPaused) {
			indicateNewRequests();
		}
	}

	/**
	 * Indicates new requests.
	 */
	private synchronized void indicateNewRequests() {
		notify();
	}

	/**
	 * Cleans up the request processor and destroys the thread.
	 */
	public static void dispose() {
		PROCESSOR.kill();
	}

	/**
	 * Stops the request processor from running.
	 */
	private void kill() {
		running = false;
		indicateNewRequests();
	}

	/**
	 * Runs the request queue.
	 */
	public synchronized void run() {
		while (running || requestQueue.hasRequests()) {
			if (requestQueue.hasRequests()) {
				requestQueue.acceptNextRequest().doResourceRequest();
			} else {
				try {
					wait();
				} catch (InterruptedException e) {
					FlounderLogger.exception(e);
				}
			}
		}
	}
}
