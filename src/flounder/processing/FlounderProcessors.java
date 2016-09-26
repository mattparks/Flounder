package flounder.processing;

import flounder.engine.*;
import flounder.logger.*;
import flounder.processing.glProcessing.*;
import flounder.profiling.*;

/**
 * Manages the all engine request processors.
 */
public class FlounderProcessors extends IModule {
	private static final FlounderProcessors instance = new FlounderProcessors();

	private RequestProcessor requestProcessor;
	private GlRequestProcessor glRequestProcessor;

	private FlounderProcessors() {
		super(FlounderLogger.class, FlounderProfiler.class);
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
	public static void sendRequest(ResourceRequest request) {
		instance.requestProcessor.addRequestToQueue(request);
	}

	/**
	 * Sends a new request into queue.
	 *
	 * @param request The request to add.
	 */
	public static void sendGLRequest(GlRequest request) {
		instance.glRequestProcessor.addRequestToQueue(request);
	}

	@Override
	public void dispose() {
		requestProcessor.dispose();
		glRequestProcessor.dispose();
	}
}
