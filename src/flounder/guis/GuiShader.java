package flounder.guis;

import flounder.resources.*;
import flounder.shaders.*;

public class GuiShader extends ShaderProgram {
	private static final MyFile VERTEX_SHADER = new MyFile("flounder/guis", "guiVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile("flounder/guis", "guiFragment.glsl");

	protected GuiShader() {
		super("gui", VERTEX_SHADER, FRAGMENT_SHADER);
	}
}
