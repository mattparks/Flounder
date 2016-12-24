package flounder.models.animation;

import flounder.logger.*;
import flounder.resources.*;

import java.lang.ref.*;

/**
 * A class capable of setting up a {@link Animation}.
 */
public class AnimationBuilder {
	private MyFile file;
	private LoadManual loadManual;

	/**
	 * Creates a class to setup a Animation.
	 *
	 * @param modelFile The animations source file.
	 */
	protected AnimationBuilder(MyFile modelFile) {
		this.file = modelFile;
	}

	/**
	 * Creates a class to setup a Animation.
	 *
	 * @param loadManual The animation's manual loader.
	 */
	protected AnimationBuilder(LoadManual loadManual) {
		this.loadManual = loadManual;
	}

	/**
	 * Creates a new animation, carries out the CPU loading.
	 *
	 * @return The animation that has been created.
	 */
	public Animation create() {
		SoftReference<Animation> ref = FlounderAnimations.getLoaded().get(getPath());
		Animation animation = ref == null ? null : ref.get();

		if (animation == null) {
			FlounderLogger.log(getPath() + " is being loaded into the animation builder right now!");
			FlounderAnimations.getLoaded().remove(getPath());
			animation = new Animation();
			AnimationData data = FlounderAnimations.loadAnimation(getFile());
			data.createRaw(animation);
			data.destroy();
			FlounderAnimations.getLoaded().put(getPath(), new SoftReference<>(animation));
		}

		return animation;
	}

	private String getPath() {
		if (file != null) {
			return file.getPath();
		} else {
			return loadManual.getModelName();
		}
	}

	/**
	 * Gets the source file.
	 *
	 * @return The source file.
	 */
	public MyFile getFile() {
		return file;
	}

	/**
	 * Gets the optimal manual loader.
	 *
	 * @return The optimal manual loader.
	 */
	public LoadManual getLoadManual() {
		return loadManual;
	}

	/**
	 * A interface used when manually loading model data.
	 */
	public interface LoadManual {
		/**
		 * Gets the manual model name.
		 *
		 * @return The manual model name.
		 */
		String getModelName();

		// TODO: Add the data from Animation!
	}
}