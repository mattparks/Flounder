package flounder.models;

import flounder.logger.*;
import flounder.materials.*;
import flounder.processing.*;
import flounder.resources.*;

import java.lang.ref.*;

/**
 * A class capable of setting up a {@link flounder.models.Model}.
 */
public class ModelBuilder {
	private MyFile file;
	private LoadManual loadManual;

	/**
	 * Creates a class to setup a Model.
	 *
	 * @param modelFile The models source file.
	 */
	protected ModelBuilder(MyFile modelFile) {
		this.file = modelFile;
	}

	/**
	 * Creates a class to setup a Model.
	 *
	 * @param loadManual The model's manual loader.
	 */
	protected ModelBuilder(LoadManual loadManual) {
		this.loadManual = loadManual;
	}

	/**
	 * Creates a new model, carries out the CPU loading, and loads to OpenGL.
	 *
	 * @return The model that has been created.
	 */
	public Model create() {
		SoftReference<Model> ref = FlounderModels.getLoaded().get(getPath());
		Model data = ref == null ? null : ref.get();

		if (data == null) {
			FlounderLogger.log(getPath() + " is being loaded into the model builder right now!");
			FlounderModels.getLoaded().remove(getPath());
			data = new Model();
			ModelLoadRequest request = new ModelLoadRequest(data, this, false);
			request.doResourceRequest();
			request.executeGlRequest();
			FlounderModels.getLoaded().put(getPath(), new SoftReference<>(data));
		}

		return data;
	}

	/**
	 * Creates a new model and sends it to be loaded by the loader thread.
	 *
	 * @return The model.
	 */
	public Model createInBackground() {
		SoftReference<Model> ref = FlounderModels.getLoaded().get(getPath());
		Model data = ref == null ? null : ref.get();

		if (data == null) {
			FlounderLogger.log(getPath() + " is being loaded into the model builder in the background!");
			FlounderModels.getLoaded().remove(getPath());
			data = new Model();
			FlounderProcessors.sendRequest(new ModelLoadRequest(data, this, true));
			FlounderModels.getLoaded().put(getPath(), new SoftReference<>(data));
		}

		return data;
	}

	/**
	 * Creates a new model, carries out the CPU loading, and sends to the main thread for GL loading.
	 *
	 * @return The model.
	 */
	public Model createInSecondThread() {
		SoftReference<Model> ref = FlounderModels.getLoaded().get(getPath());
		Model data = ref == null ? null : ref.get();

		if (data == null) {
			FlounderLogger.log(getPath() + " is being loaded into the model builder in separate thread!");
			FlounderModels.getLoaded().remove(getPath());
			data = new Model();
			ModelLoadRequest request = new ModelLoadRequest(data, this, false);
			request.doResourceRequest();
			FlounderProcessors.sendGLRequest(request);
			FlounderModels.getLoaded().put(getPath(), new SoftReference<>(data));
		}

		return data;
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

		/**
		 * Gets the manual model vertices.
		 *
		 * @return The manual model vertices.
		 */
		float[] getVertices();

		/**
		 * Gets the manual model texture.
		 *
		 * @return The manual model texture.
		 */
		float[] getTextureCoords();

		/**
		 * Gets the manual model normals.
		 *
		 * @return The manual model normals.
		 */
		float[] getNormals();

		/**
		 * Gets the manual model tangents.
		 *
		 * @return The manual model tangents.
		 */
		float[] getTangents();

		/**
		 * Gets the manual model indices.
		 *
		 * @return The manual model indices.
		 */
		int[] getIndices();

		/**
		 * Gets the manual model materials.
		 *
		 * @return The manual model materials.
		 */
		Material[] getMaterials();
	}
}
