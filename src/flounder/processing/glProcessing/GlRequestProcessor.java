package flounder.processing.glProcessing;

/**
 * Class that is responsible for flounder.processing OpenGL requests.
 */
public class GlRequestProcessor {
	private static final float MAX_TIME_MILLIS = 8f;

	private static GlRequestQueue requestQueue = new GlRequestQueue();

	/**
	 * Creates a new class for flounder.processing gl requests in a separate thread.
	 */
	public GlRequestProcessor() {
	}

	/**
	 * Sends a new request into queue.
	 *
	 * @param request The request to add.
	 */
	public static void sendRequest(GlRequest request) {
		requestQueue.addRequest(request);
	}

	/**
	 * Deals with in the time slot available.
	 */
	public static void dealWithTopRequests() {
		float remainingTime = MAX_TIME_MILLIS * 1000000;
		long start = System.nanoTime();

		while (requestQueue.hasRequests()) {
			requestQueue.acceptNextRequest().executeGlRequest();
			long end = System.nanoTime();
			long timeTaken = end - start;
			remainingTime -= timeTaken;
			start = end;

			if (remainingTime < 0) {
				break;
			}
		}
	}

	/**
	 * Completes all requests left in queue.
	 */
	public static void completeAllRequests() {
		while (requestQueue.hasRequests()) {
			requestQueue.acceptNextRequest().executeGlRequest();
		}
	}
}
