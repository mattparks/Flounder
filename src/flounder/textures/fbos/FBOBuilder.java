package flounder.textures.fbos;

public class FBOBuilder {
	private DepthBufferType depthBufferType;
	private boolean useColourBuffer;
	private boolean linearFiltering;
	private boolean clampEdge;
	private boolean alphaChannel;
	private boolean antialiased;
	private int samples;
	private int width;
	private int height;
	private boolean fitToScreen;

	protected FBOBuilder(int width, int height) {
		this.depthBufferType = DepthBufferType.NONE;
		this.useColourBuffer = true;
		this.linearFiltering = true;
		this.clampEdge = true;
		this.alphaChannel = false;
		this.antialiased = false;
		this.samples = 1;
		this.width = width;
		this.height = height;
		this.fitToScreen = false;
	}

	/**
	 * @return Returns a newly created FBO off of the builders parameters.
	 */
	public FBO create() {
		return new FBO(width, height, fitToScreen, depthBufferType, useColourBuffer, linearFiltering, clampEdge, alphaChannel, antialiased, samples);
	}

	public FBOBuilder depthBuffer(DepthBufferType type) {
		depthBufferType = type;
		return this;
	}

	public FBOBuilder noColourBuffer() {
		useColourBuffer = false;
		return this;
	}

	public FBOBuilder nearestFiltering() {
		linearFiltering = false;
		return this;
	}

	public FBOBuilder repeatTexture() {
		clampEdge = false;
		return this;
	}

	public FBOBuilder withAlphaChannel(boolean alpha) {
		alphaChannel = alpha;
		return this;
	}

	public FBOBuilder antialias(int samples) {
		antialiased = true;
		this.samples = samples;
		return this;
	}

	public FBOBuilder fitToScreen() {
		fitToScreen = true;
		return this;
	}

	public enum DepthBufferType {
		RENDER_BUFFER, TEXTURE, NONE
	}
}
