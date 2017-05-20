package flounder.guis;

import flounder.devices.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;
import flounder.visual.*;

public class OverlayStartup extends ScreenObject {
	private GuiObject background;
	private GuiObject logo;
	private TextObject text;
	private boolean starting;

	public OverlayStartup(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.background = new GuiObject(this, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "eg_background.png")).create(), 1);
		this.background.setInScreenCoords(true);

		this.logo = new GuiObject(this, new Vector2f(0.5f, 0.5f), new Vector2f(0.4f, 0.4f), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "equilibrium_games.png")).create(), 1);
		this.logo.setInScreenCoords(true);

		this.text = new TextObject(this, new Vector2f(0.5f, 0.82f), "Copyright 2014-2017 Equilibrium Games. All rights reserved. This product uses LWJGL and Steamworks4J, along with technologies from The Khronos Group Inc.", 1.32f, FlounderFonts.CAFE_FRANCOISE, 0.6f, GuiAlign.CENTRE);
		this.text.setColour(new Colour(1.0f, 1.0f, 1.0f));

		this.starting = true;

		new java.util.Timer().schedule(
				new java.util.TimerTask() {
					@Override
					public void run() {
						setAlphaDriver(new SlideDriver(1.0f, 0.0f, 1.0f));
					}
				},
				5000
		);
	}

	@Override
	public void updateObject() {
		this.background.getDimensions().x = FlounderDisplay.get().getAspectRatio();
		this.background.setVisible(true);
		this.logo.setVisible(true);
	}

	public boolean isStarting() {
		return starting;
	}

	public void setStarting(boolean starting) {
		this.starting = starting;
	}

	@Override
	public void deleteObject() {
	}
}
