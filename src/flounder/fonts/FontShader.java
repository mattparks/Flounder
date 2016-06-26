package flounder.fonts;

import flounder.resources.*;
import flounder.shaders.*;

/**
 * A shader used for rendering fonts.
 */
public class FontShader extends ShaderProgram {
	private static final MyFile VERTEX_SHADER = new MyFile("flounder/fonts", "fontVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile("flounder/fonts", "fontFragment.glsl");

	/**
	 * Creates a new font shader.
	 */
	protected FontShader() {
		super("font", VERTEX_SHADER, FRAGMENT_SHADER);
	}
}
