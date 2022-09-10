package com.flounder.processing.resource;

/**
 * Interface for executable resource requests.
 */
@FunctionalInterface
public interface RequestResource {
	/**
	 * Used to send a request to the request processor so it can be queued.
	 */
	void executeRequestResource();
}
