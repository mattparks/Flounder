package flounder.processing;

import flounder.engine.*;

/**
 * Class that is responsible for processing resource requests in a separate thread.
 */
public class RequestProcessor extends Thread implements IModule {
	private RequestQueue requestQueue;
	private boolean running;

	/**
	 * Creates a new class for processing resource requests in a separate thread.
	 */
	public RequestProcessor() {
		requestQueue = new RequestQueue();
		running = true;
	}

	@Override
	public void init() {
		start();
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {
		FlounderEngine.getProfiler().add("Processor", "Requests", requestQueue.count());
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
					FlounderEngine.getLogger().log("Request was interrupted.");
					FlounderEngine.getLogger().exception(e);
				}
			}
		}
	}

	@Override
	public void dispose() {
		running = false;
		indicateNewRequests();
	}
}
