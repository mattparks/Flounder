package flounder.post;

import flounder.fbos.*;
import flounder.helpers.*;
import flounder.loaders.*;
import flounder.resources.*;
import flounder.shaders.*;

import static flounder.platform.Constants.*;

/**
 * Represents a post effect shader and on application saves the result into a FBO.
 */
public abstract class PostFilter {
	public static final MyFile POST_LOC = new MyFile(FlounderShaders.SHADERS_LOC, "filters");
	public static final MyFile VERTEX_LOCATION = new MyFile(POST_LOC, "defaultVertex.glsl");

	private static float[] POSITIONS = {0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f};
	private static int VAO = FlounderLoader.get().createInterleavedVAO(POSITIONS, 2);

	public ShaderObject shader;
	public FBO fbo;

	public PostFilter(String filterName, MyFile fragmentShader) {
		this(ShaderFactory.newBuilder().setName(filterName).addType(new ShaderType(GL_VERTEX_SHADER, VERTEX_LOCATION)).addType(new ShaderType(GL_FRAGMENT_SHADER, fragmentShader)).create(), FBO.newFBO(1.0f).create());
	}

	public PostFilter(ShaderObject shader, FBO fbo) {
		this.shader = shader;
		this.fbo = fbo;
	}

	public PostFilter(String filterName, MyFile fragmentShader, FBO fbo) {
		this(ShaderFactory.newBuilder().setName(filterName).addType(new ShaderType(GL_VERTEX_SHADER, VERTEX_LOCATION)).addType(new ShaderType(GL_FRAGMENT_SHADER, fragmentShader)).create(), fbo);
	}

	public PostFilter(ShaderObject shader) {
		this(shader, FBO.newFBO(1.0f).create());
	}

	/**
	 * Renders the filter to its FBO.
	 *
	 * @param textures A list of textures in indexed order to be bound for the shader program.
	 */
	public void applyFilter(int... textures) {
		if (!shader.isLoaded()) {
			return;
		}

		boolean lastWireframe = FlounderOpenGL.get().isInWireframe();

		fbo.bindFrameBuffer();
		FlounderOpenGL.get().prepareNewRenderParse(1.0f, 1.0f, 1.0f);
		shader.start();
		storeValues();
		FlounderOpenGL.get().antialias(false);
		FlounderOpenGL.get().disableDepthTesting();
		FlounderOpenGL.get().cullBackFaces(true);
		FlounderOpenGL.get().goWireframe(false);
		FlounderOpenGL.get().bindVAO(VAO, 0);

		for (int i = 0; i < textures.length; i++) {
			FlounderOpenGL.get().bindTexture(textures[i], GL_TEXTURE_2D, i);
		}

		FlounderOpenGL.get().renderArrays(GL_TRIANGLE_STRIP, POSITIONS.length); // Render post filter.

		FlounderOpenGL.get().unbindVAO(0);
		FlounderOpenGL.get().goWireframe(lastWireframe);
		shader.stop();
		FlounderOpenGL.get().disableBlending();
		FlounderOpenGL.get().enableDepthTesting();
		fbo.unbindFrameBuffer();
	}

	/**
	 * Can be used to store values into the shader, this is called when the filter is applied and the shader has been already started.
	 */
	public abstract void storeValues();

	/**
	 * Cleans up all of the filter processes and images.
	 */
	public void dispose() {
		fbo.delete();
		shader.delete();
	}
}
