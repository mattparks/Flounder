package com.flounder.processing.opengl;

import com.flounder.processing.*;

/**
 * A extension that is responsible for processing OpenGL requests.
 */
public class ProcessorOpenGL extends Processor {
	private static final float MAX_TIME_MILLIS = 8.0f;

	private Queue<RequestOpenGL> requestQueue;

	/**
	 * Creates a new OpenGL processor.
	 */
	public ProcessorOpenGL() {
		super();
	}

	@Override
	public void init() {
		this.requestQueue = new Queue<>();
	}

	@Override
	public void update() {
		float remainingTime = MAX_TIME_MILLIS * 1000000.0f;
		long start = System.nanoTime();

		while (requestQueue.hasRequests()) {
			requestQueue.acceptNextRequest().executeRequestGL();
			long end = System.nanoTime();
			long timeTaken = end - start;
			remainingTime -= timeTaken;
			start = end;

			if (remainingTime < 0.0f) {
				break;
			}
		}
	}

	@Override
	public void addRequestToQueue(Object request) {
		if (!(request instanceof RequestOpenGL)) {
			return;
		}

		requestQueue.addRequest((RequestOpenGL) request);
	}

	@Override
	public Class getRequestClass() {
		return RequestOpenGL.class;
	}

	/**
	 * Completes all requests left in queue.
	 */
	public void completeAllRequests() {
		while (requestQueue.hasRequests()) {
			requestQueue.acceptNextRequest().executeRequestGL();
		}
	}

	@Override
	public void dispose() {
		completeAllRequests();
		requestQueue.clear();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
