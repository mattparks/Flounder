package flounder.processing;

/**
 * Interface for executable resource requests.
 */
public interface ResourceRequest {
	/**
	 * Used to send a request to the request processor so it can be queued.
	 */
	void doResourceRequest();
}
