package flounder.processing;

/**
 * Class that is responsible for flounder.processing resource requests in a separate thread.
 */
public class RequestProcessor extends Thread {
	private static RequestProcessor PROCESSOR = new RequestProcessor();

	private final RequestQueue m_requestQueue;
	private boolean m_running;

	/**
	 * Creates a new class for flounder.processing resource requests in a separate thread.
	 */
	private RequestProcessor() {
		m_requestQueue = new RequestQueue();
		m_running = true;
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
		boolean isPaused = !m_requestQueue.hasRequests();
		m_requestQueue.addRequest(request);

		if (isPaused) {
			indicateNewRequests();
		}
	}

	/**
	 * Runs the request queue.
	 */
	public synchronized void run() {
		while (m_running || m_requestQueue.hasRequests()) {
			if (m_requestQueue.hasRequests()) {
				m_requestQueue.acceptNextRequest().doResourceRequest();
			} else {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Stops the request processor from running.
	 */
	private void kill() {
		m_running = false;
		indicateNewRequests();
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
}
