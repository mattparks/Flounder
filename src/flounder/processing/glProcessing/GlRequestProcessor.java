package flounder.processing.glProcessing;

import flounder.engine.*;

/**
 * Class that is responsible for processing OpenGL requests.
 */
public class GlRequestProcessor implements IModule {
	private static final float MAX_TIME_MILLIS = 8.0f;

	private GlRequestQueue requestQueue;

	/**
	 * Creates a new class for processing OpenGL requests in a separate thread.
	 */
	public GlRequestProcessor() {
		requestQueue = new GlRequestQueue();
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
		dealWithTopRequests();
	}

	@Override
	public void profile() {
		FlounderEngine.getProfiler().add("GLProcessor", "Requests", requestQueue.count());
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

	@Override
	public void dispose() {
		completeAllRequests();
	}
}
