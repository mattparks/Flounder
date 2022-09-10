package com.flounder.skybox;

import com.flounder.camera.*;
import com.flounder.framework.*;
import com.flounder.lights.*;
import com.flounder.loaders.*;
import com.flounder.maths.*;
import com.flounder.maths.matrices.*;
import com.flounder.maths.vectors.*;
import com.flounder.models.*;
import com.flounder.physics.bounding.*;
import com.flounder.resources.*;
import com.flounder.shaders.*;
import com.flounder.textures.*;

public class FlounderSkybox extends com.flounder.framework.Module {
	public static final MyFile SKYBOX_FOLDER = new MyFile(MyFile.RES_FOLDER, "skybox");

	private static final MyFile MODEL_FILE = new MyFile(MyFile.RES_FOLDER, "models", "skybox_cube.obj"); // skybox_sphere

	private static final float SIZE = 250.0f;

	private ModelObject model;

	private TextureObject cubemap;
	private Vector3f rotation;
	private Matrix4f modelMatrix;

	private Fog fog;
	private float blendFactor;

	public FlounderSkybox() {
		super(FlounderBounding.class, FlounderLoader.class, FlounderTextures.class, FlounderShaders.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.model = ModelFactory.newBuilder().setFile(MODEL_FILE).create();

		this.cubemap = null;
		this.rotation = new Vector3f();
		this.modelMatrix = new Matrix4f();

		this.fog = new Fog(new Colour(), 0.001f, 2.0f, -1.28f, 51.2f);
		this.blendFactor = 1.0f;
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		// Update the skybox transformation.
		if (FlounderCamera.get().getCamera() != null) {
			Matrix4f.transformationMatrix(FlounderCamera.get().getCamera().getPosition(), rotation, SIZE, modelMatrix);
		}
	}

	public ModelObject getModel() {
		return this.model;
	}

	public void setModel(ModelObject model) {
		this.model = model;
	}

	public TextureObject getCubemap() {
		return this.cubemap;
	}

	public void setCubemap(TextureObject cubemap) {
		this.cubemap = cubemap;
	}

	public Vector3f getRotation() {
		return this.rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation.set(rotation);
	}

	public Matrix4f getModelMatrix() {
		return this.modelMatrix;
	}

	public Fog getFog() {
		return this.fog;
	}

	public float getBlendFactor() {
		return this.blendFactor;
	}

	public void setBlendFactor(float blendFactor) {
		this.blendFactor = blendFactor;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		if (cubemap != null) {
			this.cubemap.delete();
		}
	}

	@com.flounder.framework.Module.Instance
	public static FlounderSkybox get() {
		return (FlounderSkybox) Framework.get().getModule(FlounderSkybox.class);
	}
}
