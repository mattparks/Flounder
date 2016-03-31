package flounder.engine.profiling;

/**
 * Can be used to record various timings within the engine.
 */
public class ProfileTimer {
	private int m_invocations;
	private float m_totalTime;
	private float m_startTime;

	/**
	 * Creates a new profiling timer.
	 */
	public ProfileTimer() {
		m_invocations = 0;
		m_totalTime = 0.0f;
		m_startTime = 0.0f;
	}

	/**
	 * Starts a new invocation.
	 */
	public void startInvocation() {
		m_startTime = System.nanoTime();
	}

	/**
	 * Stops the current Invocation.
	 */
	public void stopInvocation() {
		if (m_startTime == 0) {
			System.err.println("Stop Invocation called without matching start invocation!");
			assert (m_startTime != 0); // Stops from running faulty data.
		}

		m_invocations++;
		m_totalTime += System.nanoTime() - m_startTime;
		m_startTime = 0;
	}

	/**
	 * @return Returns the total time taken in ms, and resets the timer.
	 */
	public float reset() {
		float timeMs = (float) ((m_totalTime / 1000000.0) / ((float) m_invocations));
		m_invocations = 0;
		m_totalTime = 0;
		m_startTime = 0;
		return timeMs;
	}
}
