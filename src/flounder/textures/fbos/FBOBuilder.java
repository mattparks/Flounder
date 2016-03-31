package flounder.textures.fbos;

public class FBOBuilder {
	public enum DepthBufferType {
		RENDER_BUFFER, TEXTURE, NONE
	}

	private DepthBufferType m_depthBufferType;
	private boolean m_useColourBuffer;
	private boolean m_linearFiltering;
	private boolean m_clampEdge;
	private boolean m_alphaChannel;
	private boolean m_antialiased;
	private int m_samples;
	private int m_width;
	private int m_height;
	private boolean m_fitToScreen;

	protected FBOBuilder(final int width, final int height) {
		m_depthBufferType = DepthBufferType.NONE;
		m_useColourBuffer = true;
		m_linearFiltering = true;
		m_clampEdge = true;
		m_alphaChannel = false;
		m_antialiased = false;
		m_samples = 1;
		m_width = width;
		m_height = height;
		m_fitToScreen = false;
	}

	/**
	 * @return Returns a newly created FBO off of the builders parameters.
	 */
	public FBO create() {
		return new FBO(m_width, m_height, m_fitToScreen, m_depthBufferType, m_useColourBuffer, m_linearFiltering, m_clampEdge, m_alphaChannel, m_antialiased, m_samples);
	}

	public FBOBuilder depthBuffer(final DepthBufferType type) {
		m_depthBufferType = type;
		return this;
	}

	public FBOBuilder noColourBuffer() {
		m_useColourBuffer = false;
		return this;
	}

	public FBOBuilder nearestFiltering() {
		m_linearFiltering = false;
		return this;
	}

	public FBOBuilder repeatTexture() {
		m_clampEdge = false;
		return this;
	}

	public FBOBuilder withAlphaChannel(final boolean alpha) {
		m_alphaChannel = alpha;
		return this;
	}

	public FBOBuilder antialias(final int samples) {
		m_antialiased = true;
		m_samples = samples;
		return this;
	}

	public FBOBuilder fitToScreen() {
		m_fitToScreen = true;
		return this;
	}
}
