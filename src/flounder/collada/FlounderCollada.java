package flounder.collada;

import flounder.animation.*;
import flounder.collada.animation.*;
import flounder.collada.geometry.*;
import flounder.collada.joints.*;
import flounder.collada.skin.*;
import flounder.framework.*;
import flounder.loaders.*;
import flounder.logger.*;
import flounder.parsing.xml.*;
import flounder.processing.*;
import flounder.profiling.*;
import flounder.resources.*;

import java.lang.ref.*;
import java.util.*;

/**
 * A module used for loading and managing collada models and animations.
 */
public class FlounderCollada extends Module {
	private static final FlounderCollada INSTANCE = new FlounderCollada();
	public static final String PROFILE_TAB_NAME = "Collada";

	private Map<String, SoftReference<ModelAnimated>> loaded;

	/**
	 * Creates a new collada loader class.
	 */
	public FlounderCollada() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderLogger.class, FlounderProcessors.class, FlounderLoader.class);
	}

	@Override
	public void init() {
		loaded = new HashMap<>();
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {
		FlounderProfiler.add(PROFILE_TAB_NAME, "Loaded", loaded.size());
	}

	/**
	 * Loads a collada file into a model object.
	 *
	 * @param file The collada file to be loaded.
	 *
	 * @return The loaded model.
	 */
	public static ModelAnimated loadCollada(MyFile file) {
		XmlNode node = XmlParser.loadXmlFile(file);

		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), AnimationSettings.MAX_WEIGHTS);
		SkinningData skinningData = skinLoader.extractSkinData();

		JointsLoader jointsLoader = new JointsLoader(node.getChild("library_visual_scenes"), skinningData.getJointOrder());
		JointsData jointsData = jointsLoader.extractBoneData();

		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.getVerticesSkinData());
		MeshData meshData = g.extractModelData();

		return new ModelAnimated(meshData, jointsData);
	}

	/**
	 * Loads a collada file into a animation object.
	 *
	 * @param file The collada file to be loaded.
	 *
	 * @return The loaded animation.
	 */
	public static AnimationData loadAnimation(MyFile file) {
		XmlNode node = XmlParser.loadXmlFile(file);

		AnimationLoader animationLoader = new AnimationLoader(node.getChild("library_animations"));
		AnimationData animationData = animationLoader.extractAnimation();

		return animationData;
	}

	public static void loadModelToOpenGL(ModelAnimated model) {
		model.setVaoID(FlounderLoader.createVAO());
		model.setVaoLength(model.getMeshData().getIndices() != null ? model.getMeshData().getIndices().length : (model.getMeshData().getVertices().length / 3));
		FlounderLoader.createIndicesVBO(model.getVaoID(), model.getMeshData().getIndices());
		FlounderLoader.storeDataInVBO(model.getVaoID(), model.getMeshData().getVertices(), 0, 3);
		FlounderLoader.storeDataInVBO(model.getVaoID(), model.getMeshData().getTextures(), 1, 2);
		FlounderLoader.storeDataInVBO(model.getVaoID(), model.getMeshData().getNormals(), 2, 3);
		FlounderLoader.storeDataInVBO(model.getVaoID(), model.getMeshData().getTangents(), 3, 3);
		FlounderLoader.storeDataInVBO(model.getVaoID(), model.getMeshData().getJointIds(), 4, 3);
		FlounderLoader.storeDataInVBO(model.getVaoID(), model.getMeshData().getVertexWeights(), 5, 3);
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		loaded.keySet().forEach(key -> loaded.get(key).get().delete());
		loaded.clear();
	}
}
