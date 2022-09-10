package com.flounder.processing.resource;

import com.flounder.logger.*;
import com.flounder.processing.*;

/**
 * A extension that is responsible for processing resource requests in a separate thread.
 */
public class ProcessorResource extends Processor {
	private Queue<RequestResource> requestQueue;

	private boolean running;
	private Thread thread;

	/**
	 * Creates a new resource processor.
	 */
	public ProcessorResource() {
		super();
	}

	@Override
	public void init() {
		this.requestQueue = new Queue<>();

		this.running = true;

		this.thread = new Thread(this::run);
		thread.setName("resources");
		thread.start();
	}

	@Override
	public void update() {
	}

	@Override
	public void addRequestToQueue(Object request) {
		if (!(request instanceof RequestResource)) {
			return;
		}

		boolean isPaused = !requestQueue.hasRequests();
		requestQueue.addRequest((RequestResource) request);

		if (isPaused) {
			indicateNewRequests();
		}
	}

	@Override
	public Class getRequestClass() {
		return RequestResource.class;
	}

	private synchronized void run() {
		while (running || requestQueue.hasRequests()) {
			if (requestQueue.hasRequests()) {
				requestQueue.acceptNextRequest().executeRequestResource();
			} else {
				try {
					wait();
				} catch (InterruptedException e) {
					FlounderLogger.get().log("Request was interrupted.");
					FlounderLogger.get().exception(e);
				}
			}
		}
	}

	private synchronized void indicateNewRequests() {
		notify();
	}

	@Override
	public void dispose() {
		running = false;
		indicateNewRequests();
		requestQueue.clear();
		thread.interrupt();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
