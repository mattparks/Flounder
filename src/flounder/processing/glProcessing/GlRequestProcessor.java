package flounder.processing.glProcessing;

import flounder.profiling.*;

/**
 * Class that is responsible for processing OpenGL requests.
 */
public class GlRequestProcessor {
	private static final float MAX_TIME_MILLIS = 8.0f;

	private GlRequestQueue requestQueue;

	/**
	 * Creates a new class for processing OpenGL requests in a separate thread.
	 */
	public GlRequestProcessor() {
	}

	public void init() {
		this.requestQueue = new GlRequestQueue();
	}

	public void update() {
		dealWithTopRequests();
	}

	public void profile() {
		FlounderProfiler.add("GLProcessor", "Requests", requestQueue.count());
	}

	/**
	 * Deals with in the time slot available.
	 */
	public void dealWithTopRequests() {
		float remainingTime = MAX_TIME_MILLIS * 1000000.0f;
		long start = System.nanoTime();

		while (requestQueue.hasRequests()) {
			requestQueue.acceptNextRequest().executeGlRequest();
			long end = System.nanoTime();
			long timeTaken = end - start;
			remainingTime -= timeTaken;
			start = end;

			if (remainingTime < 0.0f) {
				break;
			}
		}
	}

	/**
	 * Completes all requests left in queue.
	 */
	public void completeAllRequests() {
		while (requestQueue.hasRequests()) {
			requestQueue.acceptNextRequest().executeGlRequest();
		}
	}

	/**
	 * Adds a new request to the queue.
	 *
	 * @param request The resource request to add.
	 */
	public void addRequestToQueue(GlRequest request) {
		requestQueue.addRequest(request);
	}

	public void dispose() {
		completeAllRequests();
		//	requestQueue.clear();
	}
}
