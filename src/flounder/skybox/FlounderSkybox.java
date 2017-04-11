package flounder.skybox;

import flounder.camera.*;
import flounder.framework.*;
import flounder.lights.*;
import flounder.loaders.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;

public class FlounderSkybox extends Module {
	private static final FlounderSkybox INSTANCE = new FlounderSkybox();
	public static final String PROFILE_TAB_NAME = "Kosmos Skybox";

	public static final MyFile SKYBOX_FOLDER = new MyFile(MyFile.RES_FOLDER, "skybox");

	private static MyFile[] TEXTURE_FILES = {
			new MyFile(SKYBOX_FOLDER, "starsRight.png"),
			new MyFile(SKYBOX_FOLDER, "starsLeft.png"),
			new MyFile(SKYBOX_FOLDER, "starsTop.png"),
			new MyFile(SKYBOX_FOLDER, "starsBottom.png"),
			new MyFile(SKYBOX_FOLDER, "starsBack.png"),
			new MyFile(SKYBOX_FOLDER, "starsFront.png")
	};

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
		this.cubemap = TextureFactory.newBuilder().setCubemap(TEXTURE_FILES).create();
		this.rotation = new Vector3f();
		this.modelMatrix = new Matrix4f();

		this.fog = new Fog(new Colour(), 0.032f, 2.56f, -1.28f, 51.2f);
		this.blendFactor = 1.0f;
	}

	@Override
	public void update() {
		// Update the skybox transformation.
		if (FlounderCamera.getCamera() != null) {
			Matrix4f.transformationMatrix(FlounderCamera.getCamera().getPosition(), rotation, 1.0f, modelMatrix);
		}
	}

	@Override
	public void profile() {
	}

	public static TextureObject getCubemap() {
		return INSTANCE.cubemap;
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
		this.cubemap.delete();
	}
}
