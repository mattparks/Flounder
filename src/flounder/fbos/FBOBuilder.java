package flounder.fbos;

/**
 * A class capable of setting up a {@link flounder.fbos.FBO}.
 */
public class FBOBuilder {
	private DepthBufferType depthBufferType;
	private boolean useColourBuffer;
	private boolean linearFiltering;
	private boolean wrapTextures;
	private boolean clampEdge;
	private boolean alphaChannel;
	private boolean antialiased;
	private int samples;
	private int width;
	private int height;
	private int attachments;
	private boolean fitToScreen;
	private float sizeScalar;

	/**
	 * Creates a class to setup a FBO.
	 *
	 * @param width The FBO's initial width.
	 * @param height The FBO's initial height.
	 */
	protected FBOBuilder(int width, int height) {
		this.depthBufferType = DepthBufferType.NONE;
		this.useColourBuffer = true;
		this.linearFiltering = true;
		this.wrapTextures = true;
		this.clampEdge = true;
		this.alphaChannel = false;
		this.antialiased = false;
		this.samples = 0;
		this.width = width;
		this.height = height;
		this.attachments = 1;
		this.fitToScreen = false;
		this.sizeScalar = 1.0f;
	}

	/**
	 * Creates the FBO off of the builders parameters.
	 *
	 * @return Returns a newly created FBO off of the builders parameters.
	 */
	public FBO create() {
		return new FBO(width, height, attachments, fitToScreen, sizeScalar, depthBufferType, useColourBuffer, linearFiltering, wrapTextures, clampEdge, alphaChannel, antialiased, samples);
	}

	/**
	 * Sets the type of depth buffer to use.
	 *
	 * @param type The depth buffer to use.
	 *
	 * @return this.
	 */
	public FBOBuilder depthBuffer(DepthBufferType type) {
		depthBufferType = type;
		return this;
	}

	/**
	 * Disables the colour buffer.
	 *
	 * @return this.
	 */
	public FBOBuilder noColourBuffer() {
		useColourBuffer = false;
		return this;
	}

	/**
	 * Sets the texture to not use linear filtering.
	 *
	 * @return this.
	 */
	public FBOBuilder nearestFiltering() {
		linearFiltering = false;
		return this;
	}

	/**
	 * Sets if the textures will even be bothered with wrapping.
	 *
	 * @return this.
	 */
	public FBOBuilder disableTextureWrap() {
		this.wrapTextures = false;
		return this;
	}

	/**
	 * Sets the texture to repeat.
	 *
	 * @return this.
	 */
	public FBOBuilder repeatTexture() {
		clampEdge = false;
		return this;
	}

	/**
	 * Enables / disables the alpha channel.
	 *
	 * @param alpha If the alpha channel will be enabled.
	 *
	 * @return this.
	 */
	public FBOBuilder withAlphaChannel(boolean alpha) {
		alphaChannel = alpha;
		return this;
	}

	/**
	 * Sets antialiased to true and adds samples.
	 *
	 * @param samples How many MFAA samples should be used on the FBO. Zero disables multisampling.
	 *
	 * @return this.
	 */
	public FBOBuilder antialias(int samples) {
		antialiased = true;
		this.samples = samples;
		return this;
	}

	/**
	 * Sets the amount of colour attachments to create.
	 *
	 * @param attachments The amount of attachments to create.
	 *
	 * @return this.
	 */
	public FBOBuilder attachments(int attachments) {
		this.attachments = attachments;
		return this;
	}

	/**
	 * Sets if the FBO will be fit to the screen.
	 *
	 * @param sizeScalar A scalar factor between the FBO and the screen, enabled when {@code fitToScreen} is enabled. (1.0f disables scalar).
	 *
	 * @return this.
	 */
	public FBOBuilder fitToScreen(float sizeScalar) {
		fitToScreen = true;
		this.sizeScalar = sizeScalar;
		return this;
	}
}
