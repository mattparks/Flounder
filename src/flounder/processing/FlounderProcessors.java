package flounder.processing;

import flounder.engine.*;
import flounder.processing.glProcessing.*;

/**
 * Manages the all engine request processors.
 */
public class FlounderProcessors implements IModule {
	private RequestProcessor requestProcessor;
	private GlRequestProcessor glRequestProcessor;

	/**
	 * Creates all engine request processors.
	 */
	public FlounderProcessors() {
		requestProcessor = new RequestProcessor();
		glRequestProcessor = new GlRequestProcessor();
	}

	@Override
	public void init() {
		requestProcessor.init();
	}

	@Override
	public void update() {
		requestProcessor.update();
		glRequestProcessor.update();
	}

	@Override
	public void profile() {
		requestProcessor.profile();
		glRequestProcessor.profile();
	}

	/**
	 * Sends a new resource request to queue.
	 *
	 * @param request The resource request to add.
	 */
	public void sendRequest(ResourceRequest request) {
		requestProcessor.addRequestToQueue(request);
	}

	/**
	 * Sends a new request into queue.
	 *
	 * @param request The request to add.
	 */
	public void sendGLRequest(GlRequest request) {
		glRequestProcessor.addRequestToQueue(request);
	}

	@Override
	public void dispose() {
		requestProcessor.dispose();
		glRequestProcessor.dispose();
	}
}
