package flounder.skybox;

import flounder.camera.*;
import flounder.framework.*;
import flounder.lights.*;
import flounder.loaders.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.bounding.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;

public class FlounderSkybox extends Module {
	private static final FlounderSkybox INSTANCE = new FlounderSkybox();
	public static final String PROFILE_TAB_NAME = "Skybox";

	public static final MyFile SKYBOX_FOLDER = new MyFile(MyFile.RES_FOLDER, "skybox");

	private static final MyFile MODEL_FILE = new MyFile(MyFile.RES_FOLDER, "models", "aabb.obj");

	private static final float SIZE = 250.0f;

	private ModelObject model;

	private TextureObject cubemap;
	private Vector3f rotation;
	private Matrix4f modelMatrix;

	private Fog fog;
	private float blendFactor;

	public FlounderSkybox() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class, FlounderLoader.class, FlounderTextures.class, FlounderShaders.class);
	}

	@Override
	public void init() {
		this.model = ModelFactory.newBuilder().setFile(MODEL_FILE).create();

		this.cubemap = null;
		this.rotation = new Vector3f();
		this.modelMatrix = new Matrix4f();

		this.fog = new Fog(new Colour(), 0.001f, 2.0f, -1.28f, 51.2f);
		this.blendFactor = 1.0f;
	}

	@Override
	public void update() {
		// Update the skybox transformation.
		if (FlounderCamera.getCamera() != null) {
			Matrix4f.transformationMatrix(FlounderCamera.getCamera().getPosition(), rotation, SIZE, modelMatrix);
		}
	}

	@Override
	public void profile() {
	}

	public static ModelObject getModel() {
		return INSTANCE.model;
	}

	public static void setModel(ModelObject model) {
		INSTANCE.model = model;
	}

	public static TextureObject getCubemap() {
		return INSTANCE.cubemap;
	}

	public static void setCubemap(TextureObject cubemap) {
		INSTANCE.cubemap = cubemap;
	}

	public static Vector3f getRotation() {
		return INSTANCE.rotation;
	}

	public static void setRotation(Vector3f rotation) {
		INSTANCE.rotation.set(rotation);
	}

	public static Matrix4f getModelMatrix() {
		return INSTANCE.modelMatrix;
	}

	public static Fog getFog() {
		return INSTANCE.fog;
	}

	public static float getBlendFactor() {
		return INSTANCE.blendFactor;
	}

	public static void setBlendFactor(float blendFactor) {
		INSTANCE.blendFactor = blendFactor;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		if (cubemap != null) {
			this.cubemap.delete();
		}
	}
}
