package flounder.post;

import flounder.devices.*;
import flounder.engine.*;
import flounder.loaders.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.fbos.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Represents a post effect shader and on application saves the result into a FBO.
 */
public abstract class PostFilter {
	public static final MyFile POST_LOC = new MyFile("flounder/post/filters");
	public static final MyFile VERTEX_LOCATION = new MyFile(POST_LOC, "defaultVertex.glsl");

	private static final float[] POSITIONS = {0, 0, 0, 1, 1, 0, 1, 1};
	private static final int VAO = Loader.createInterleavedVAO(POSITIONS, 2);

	public final ShaderProgram shader;
	public final FBO fbo;

	public PostFilter(final String filterName, final MyFile fragmentShader) {
		this(new ShaderProgram(filterName, VERTEX_LOCATION, fragmentShader), FBO.newFBO(ManagerDevices.getDisplay().getWidth(), ManagerDevices.getDisplay().getHeight()).fitToScreen().create());
	}

	public PostFilter(final ShaderProgram shader, final FBO fbo) {
		this.shader = shader;
		this.fbo = fbo;
	}

	/**
	 * Stores any shader uniforms into the filters shader program.
	 *
	 * @param uniforms The uniforms to store in the shader program.
	 */
	public void storeUniforms(final Uniform... uniforms) {
		shader.storeAllUniformLocations(uniforms);
	}

	/**
	 * Renders the filter to its FBO.
	 *
	 * @param textures A list of textures in indexed order to be bound for the shader program.
	 */
	public void applyFilter(final int... textures) {
		fbo.bindFrameBuffer();
		OpenglUtils.prepareNewRenderParse(1.0f, 1.0f, 1.0f);
		shader.start();
		storeValues();
		OpenglUtils.antialias(false);
		OpenglUtils.disableDepthTesting();
		OpenglUtils.cullBackFaces(true);
		OpenglUtils.bindVAO(VAO, 0);

		for (int i = 0; i < textures.length; i++) {
			OpenglUtils.bindTextureToBank(textures[i], i);
		}

		glDrawArrays(GL_TRIANGLE_STRIP, 0, POSITIONS.length); // Render post filter.

		OpenglUtils.unbindVAO(0);
		shader.stop();
		OpenglUtils.disableBlending();
		OpenglUtils.enableDepthTesting();
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
		shader.dispose();
	}
}
