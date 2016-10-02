package flounder.processing;

import flounder.logger.*;
import flounder.profiling.*;

/**
 * Class that is responsible for processing resource requests in a separate thread.
 */
public class RequestProcessor extends Thread {
	private RequestQueue requestQueue;
	private boolean running;

	/**
	 * Creates a new class for processing resource requests in a separate thread.
	 */
	public RequestProcessor() {
	}

	public void init() {
		this.requestQueue = new RequestQueue();
		this.running = true;
		start();
	}

	public void update() {
	}

	public void profile() {
		FlounderProfiler.add("Processor", "Requests", requestQueue.count());
	}

	/**
	 * Adds a new request to the queue.
	 *
	 * @param request The resource request to add.
	 */
	public void addRequestToQueue(ResourceRequest request) {
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

	@Override
	public synchronized void run() {
		while (running || requestQueue.hasRequests()) {
			if (requestQueue.hasRequests()) {
				requestQueue.acceptNextRequest().doResourceRequest();
			} else {
				try {
					wait();
				} catch (InterruptedException e) {
					FlounderLogger.log("Request was interrupted.");
					FlounderLogger.exception(e);
				}
			}
		}
	}

	public void dispose() {
		running = false;
		indicateNewRequests();
		//	requestQueue.clear();
	}
}
