package flounder.models.animation;

import flounder.resources.*;

public class Animation {
	// TODO: Add data!

	private String name;
	private boolean loaded;

	/**
	 * Creates a new OpenGL model object.
	 */
	protected Animation() {
		this.loaded = false;
	}

	/**
	 * Creates a new Model Builder.
	 *
	 * @param file The model file to be loaded.
	 *
	 * @return A new Model Builder.
	 */
	public static AnimationBuilder newAnimation(MyFile file) {
		return new AnimationBuilder(file);
	}

	/**
	 * Creates a new Model Builder.
	 *
	 * @param loadManual The model's manual loader.
	 *
	 * @return A new Model Builder.
	 */
	public static AnimationBuilder newAnimation(AnimationBuilder.LoadManual loadManual) {
		return new AnimationBuilder(loadManual);
	}

	/**
	 * Creates a new empty Model.
	 *
	 * @return A new empty Model.
	 */
	public static Animation getEmptyAnimation() {
		return new Animation();
	}

	protected void loadData(AnimationData data) {
		data.createRaw(this);
		data.destroy();
	}

	protected void loadData() {
		// TODO: Load data!
		this.loaded = true;
	}

	public void update() {
		// TODO: Update!
	}

	/**
	 * Gets animation name this was stored in.
	 *
	 * @return The animation name.
	 */
	public String getFile() {
		return name;
	}

	/**
	 * Sets the name this animation was loaded from.
	 *
	 * @param name The name this animation was loaded from.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets if the animation is loaded.
	 *
	 * @return If the animation is loaded.
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Deletes the animation from memory.
	 */
	public void delete() {
		loaded = false;
	}
}
