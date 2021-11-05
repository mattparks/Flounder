package flounder.collada;

import flounder.collada.animation.*;
import flounder.collada.geometry.*;
import flounder.collada.skeleton.*;
import flounder.collada.skin.*;
import flounder.framework.*;
import flounder.loaders.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.parsing.xml.*;
import flounder.processing.*;
import flounder.resources.*;

import java.lang.ref.*;
import java.util.*;

/**
 * A module used for loading and managing collada models and animations.
 */
public class FlounderCollada extends flounder.framework.Module {
	public static final Matrix4f CORRECTION = Matrix4f.rotate(new Matrix4f(), new Vector3f(1.0f, 0.0f, 0.0f), (float) Math.toRadians(-90.0f), null);
	public static final int MAX_WEIGHTS = 3;

	private Map<String, SoftReference<ModelAnimated>> loaded;

	/**
	 * Creates a new collada loader class.
	 */
	public FlounderCollada() {
		super(FlounderProcessors.class, FlounderLoader.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		loaded = new HashMap<>();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
	}

	/**
	 * Loads a collada file into a model object.
	 *
	 * @param file The collada file to be loaded.
	 *
	 * @return The loaded model.
	 */
	public ModelAnimated loadCollada(MyFile file) {
		XmlNode node = XmlParser.loadXmlFile(file);

		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), FlounderCollada.MAX_WEIGHTS);
		SkinningData skinningData = skinLoader.extractSkinData();

		SkeletonLoader skeletonLoader = new SkeletonLoader(node.getChild("library_visual_scenes"), skinningData.getJointOrder());
		SkeletonData skeletonData = skeletonLoader.extractBoneData();

		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.getVerticesSkinData());
		MeshData meshData = g.extractModelData();

		return new ModelAnimated(meshData, skeletonData, file);
	}

	/**
	 * Loads a collada file into a animation object.
	 *
	 * @param file The collada file to be loaded.
	 *
	 * @return The loaded animation.
	 */
	public AnimationData loadAnimation(MyFile file) {
		XmlNode node = XmlParser.loadXmlFile(file);

		AnimationLoader animationLoader = new AnimationLoader(node.getChild("library_animations"), node.getChild("library_visual_scenes"));
		AnimationData animationData = animationLoader.extractAnimation();

		return animationData;
	}

	public void loadModelToOpenGL(ModelAnimated model) {
		model.setVaoID(FlounderLoader.get().createVAO());
		model.setVaoLength(model.getMeshData().getIndices() != null ? model.getMeshData().getIndices().length : (model.getMeshData().getVertices().length / 3));
		FlounderLoader.get().createIndicesVBO(model.getVaoID(), model.getMeshData().getIndices());
		FlounderLoader.get().storeDataInVBO(model.getVaoID(), model.getMeshData().getVertices(), 0, 3);
		FlounderLoader.get().storeDataInVBO(model.getVaoID(), model.getMeshData().getTextures(), 1, 2);
		FlounderLoader.get().storeDataInVBO(model.getVaoID(), model.getMeshData().getNormals(), 2, 3);
		FlounderLoader.get().storeDataInVBO(model.getVaoID(), model.getMeshData().getTangents(), 3, 3);
		FlounderLoader.get().storeDataInVBO(model.getVaoID(), model.getMeshData().getJointIds(), 4, 3);
		FlounderLoader.get().storeDataInVBO(model.getVaoID(), model.getMeshData().getVertexWeights(), 5, 3);
	}


	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		loaded.keySet().forEach(key -> loaded.get(key).get().delete());
		loaded.clear();
	}

	@flounder.framework.Module.Instance
	public static FlounderCollada get() {
		return (FlounderCollada) Framework.get().getModule(FlounderCollada.class);
	}
}
