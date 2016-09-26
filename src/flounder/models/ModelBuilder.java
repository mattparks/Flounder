package flounder.models;

import flounder.logger.*;
import flounder.materials.*;
import flounder.processing.*;
import flounder.resources.*;

import java.lang.ref.*;
import java.util.*;

/**
 * A class capable of setting up a {@link flounder.models.Model}.
 */
public class ModelBuilder {
	private static Map<String, SoftReference<Model>> loadedModels = new HashMap<>();

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
		SoftReference<Model> ref = loadedModels.get(getPath());
		Model data = ref == null ? null : ref.get();

		if (data == null) {
			FlounderLogger.log(getPath() + " is being loaded into the model builder right now!");
			loadedModels.remove(getPath());
			data = new Model();
			ModelLoadRequest request = new ModelLoadRequest(data, this, false);
			request.doResourceRequest();
			request.executeGlRequest();
			loadedModels.put(getPath(), new SoftReference<>(data));
		}

		return data;
	}

	/**
	 * Creates a new model and sends it to be loaded by the loader thread.
	 *
	 * @return The model.
	 */
	public Model createInBackground() {
		SoftReference<Model> ref = loadedModels.get(getPath());
		Model data = ref == null ? null : ref.get();

		if (data == null) {
			FlounderLogger.log(getPath() + " is being loaded into the model builder in the background!");
			loadedModels.remove(getPath());
			data = new Model();
			FlounderProcessors.sendRequest(new ModelLoadRequest(data, this, true));
			loadedModels.put(getPath(), new SoftReference<>(data));
		}

		return data;
	}

	/**
	 * Creates a new model, carries out the CPU loading, and sends to the main thread for GL loading.
	 *
	 * @return The model.
	 */
	public Model createInSecondThread() {
		SoftReference<Model> ref = loadedModels.get(getPath());
		Model data = ref == null ? null : ref.get();

		if (data == null) {
			FlounderLogger.log(getPath() + " is being loaded into the model builder in separate thread!");
			loadedModels.remove(getPath());
			data = new Model();
			ModelLoadRequest request = new ModelLoadRequest(data, this, false);
			request.doResourceRequest();
			FlounderProcessors.sendGLRequest(request);
			loadedModels.put(getPath(), new SoftReference<>(data));
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

	public interface LoadManual {
		String getModelName();

		float[] getVertices();

		float[] getTextureCoords();

		float[] getNormals();

		float[] getTangents();

		int[] getIndices();

		Material[] getMaterials();
	}
}
