package flounder.models;

import flounder.engine.*;
import flounder.resources.*;

import java.lang.ref.*;
import java.util.*;

/**
 * A class capable of setting up a {@link flounder.models.Model}.
 */
public class ModelBuilder {
	private static Map<String, SoftReference<Model>> loadedModels = new HashMap<>();

	private MyFile file;

	/**
	 * Creates a class to setup a Model.
	 *
	 * @param modelFile The models source file.
	 */
	protected ModelBuilder(MyFile modelFile) {
		file = modelFile;
	}

	/**
	 * Creates a new model, carries out the CPU loading, and loads to OpenGL.
	 *
	 * @return The model that has been created.
	 */
	public Model create() {
		SoftReference<Model> ref = loadedModels.get(file.getPath());
		Model data = ref == null ? null : ref.get();

		if (data == null) {
			FlounderEngine.getLogger().log(file.getPath() + " is being loaded into the model builder right now!");
			loadedModels.remove(file.getPath());
			data = new Model();
			ModelLoadRequest request = new ModelLoadRequest(data, this, false);
			request.doResourceRequest();
			request.executeGlRequest();
			loadedModels.put(file.getPath(), new SoftReference<>(data));
		}

		return data;
	}

	/**
	 * Creates a new model and sends it to be loadedModels by the loader thread.
	 *
	 * @return The model.
	 */
	public Model createInBackground() {
		SoftReference<Model> ref = loadedModels.get(file.getPath());
		Model data = ref == null ? null : ref.get();

		if (data == null) {
			FlounderEngine.getLogger().log(file.getPath() + " is being loaded into the model builder in the background!");
			loadedModels.remove(file.getPath());
			data = new Model();
			FlounderEngine.getProcessors().sendRequest(new ModelLoadRequest(data, this, true));
			loadedModels.put(file.getPath(), new SoftReference<>(data));
		}

		return data;
	}

	/**
	 * Creates a new model, carries out the CPU loading, and sends to the main thread for GL loading.
	 *
	 * @return The model.
	 */
	public Model createInSecondThread() {
		SoftReference<Model> ref = loadedModels.get(file.getPath());
		Model data = ref == null ? null : ref.get();

		if (data == null) {
			FlounderEngine.getLogger().log(file.getPath() + " is being loaded into the model builder in separate thread!");
			loadedModels.remove(file.getPath());
			data = new Model();
			ModelLoadRequest request = new ModelLoadRequest(data, this, false);
			request.doResourceRequest();
			FlounderEngine.getProcessors().sendGLRequest(request);
			loadedModels.put(file.getPath(), new SoftReference<>(data));
		}

		return data;
	}

	/**
	 * Gets the source file.
	 *
	 * @return The source file.
	 */
	public MyFile getFile() {
		return file;
	}
}
