package flounder.fonts;

import flounder.guis.*;
import flounder.maths.vectors.*;

public class TextObject extends ScreenObject {
	private Text text;

	public TextObject(ScreenObject parent, Vector2f position, Text text) {
		super(parent, position, new Vector2f());
		this.text = text;
	}

	@Override
	public void updateObject() {
		text.getPosition().set(getPosition());
	}

	public Text getText() {
		return text;
	}

	@Override
	public void deleteObject() {
		text.remove();
	}
}
