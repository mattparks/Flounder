package editors.editor;

import flounder.guis.*;
import flounder.maths.*;

public class EditorGuis extends GuiMaster {
	private static final Colour COLOUR_PRIMARY = new Colour(1.0f, 0.0f, 0.0f);

	public EditorGuis() {
		super();
	}

	@Override
	public void init() {

	}

	@Override
	public void update() {

	}

	@Override
	public void profile() {

	}

	@Override
	public boolean isGamePaused() {
		return false;
	}

	@Override
	public float getBlurFactor() {
		return 0.0f;
	}

	@Override
	public Colour getPrimaryColour() {
		return COLOUR_PRIMARY;
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isActive() {
		return true;
	}
}
