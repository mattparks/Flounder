package flounder.fonts;

import flounder.resources.*;
import flounder.shaders.*;

/**
 * A shader used for rendering fonts.
 */
public class FontShader extends ShaderProgram {
	private static final MyFile VERTEX_SHADER = new MyFile("flounder/fonts", "fontVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile("flounder/fonts", "fontFragment.glsl");

	protected UniformVec3 transform = new UniformVec3("transform");
	protected UniformFloat aspectRatio = new UniformFloat("aspectRatio");
	protected UniformVec4 colour = new UniformVec4("colour");
	protected UniformVec3 borderColour = new UniformVec3("borderColour");
	protected UniformVec2 borderSizes = new UniformVec2("borderSizes");
	protected UniformVec2 edgeData = new UniformVec2("edgeData");

	/**
	 * Creates a new font shader.
	 */
	protected FontShader() {
		super("font", VERTEX_SHADER, FRAGMENT_SHADER);
		super.storeAllUniformLocations(transform, aspectRatio, colour, borderColour, borderSizes, edgeData);
	}
}
