package flounder.entities.components;

import flounder.animation.*;
import flounder.collada.*;
import flounder.collada.animation.*;
import flounder.entities.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.physics.bounding.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.*;

/**
 * Creates a animation used to set animation properties.
 */
public class ComponentAnimation extends IComponentEntity implements IComponentCollider, IComponentScale, IComponentRender, IComponentEditor {
	private float scale;
	private ModelAnimated model;
	private Matrix4f modelMatrix;

	private Collider collider;

	private TextureObject texture;
	private int textureIndex;

	private Colour colourOffset;

	private Animator animator;

	private MyFile editorPathCollada;
	private MyFile editorPathTexture;

	private boolean wasLoaded;

	/**
	 * Creates a new ComponentAnimation.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentAnimation(Entity entity) {
		this(entity, 1.0f, (ModelAnimated) null, null, 1);
	}

	/**
	 * Creates a new ComponentAnimation.
	 *
	 * @param entity The entity this component is attached to.
	 * @param scale The scale of the entity.
	 * @param file The animated model file to load from.
	 * @param texture The diffuse texture for the entity.
	 * @param textureIndex What texture index this entity should renderObjects from (0 default).
	 */
	public ComponentAnimation(Entity entity, float scale, MyFile file, TextureObject texture, int textureIndex) {
		super(entity);

		ModelAnimated modelAnimated = FlounderCollada.get().loadCollada(file);

		AnimationData animationData = FlounderCollada.get().loadAnimation(file);
		Animation animation = FlounderAnimation.get().loadAnimation(animationData);

		this.scale = scale;
		this.model = modelAnimated;
		this.modelMatrix = new Matrix4f();

		this.collider = null;

		this.texture = texture;
		this.textureIndex = textureIndex;

		this.colourOffset = new Colour();

		this.wasLoaded = false;

		if (model != null) {
			model.getHeadJoint().calculateInverseBindTransform(new Matrix4f());
			this.animator = new Animator(model.getHeadJoint());
		}

		doAnimation(animation);
	}

	/**
	 * Creates a new ComponentAnimation.
	 *
	 * @param entity The entity this component is attached to.
	 * @param scale The scale of the entity.
	 * @param model The animated model to use when animating and rendering.
	 * @param texture The diffuse texture for the entity.
	 * @param textureIndex What texture index this entity should renderObjects from (0 default).
	 */
	public ComponentAnimation(Entity entity, float scale, ModelAnimated model, TextureObject texture, int textureIndex) {
		super(entity);

		this.scale = scale;
		this.model = model;
		this.modelMatrix = new Matrix4f();

		this.collider = null;

		this.texture = texture;
		this.textureIndex = textureIndex;

		this.colourOffset = new Colour();

		if (model != null) {
			model.getHeadJoint().calculateInverseBindTransform(new Matrix4f());
			this.animator = new Animator(model.getHeadJoint());
		}
	}

	@Override
	public void update() {
		if (model != null && model.isLoaded() != wasLoaded) {
			getEntity().setMoved();
			wasLoaded = model.isLoaded();
		}

		if (animator != null) {
			animator.update();
		}

		if (getEntity().hasMoved()) {
			Matrix4f.transformationMatrix(super.getEntity().getPosition(), super.getEntity().getRotation(), scale, modelMatrix);

			if (model != null && model.getCollider() != null) {
				if (collider == null || !model.getCollider().getClass().isInstance(collider)) {
					collider = model.getCollider().clone();
				}

				model.getCollider().update(super.getEntity().getPosition(), super.getEntity().getRotation(), scale, collider);
			}
		}

		FlounderBounding.get().addShapeRender(collider);
	}

	/**
	 * Instructs this entity to carry out a given animation.
	 *
	 * @param animation The animation to be carried out.
	 */
	public void doAnimation(Animation animation) {
		animator.doAnimation(animation);
	}

	/**
	 * Gets the entitys model matrix.
	 *
	 * @return The entitys model matrix.
	 */
	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}

	/**
	 * Gets the animated model for this entity.
	 *
	 * @return The animated model for this entity.
	 */
	public ModelAnimated getModel() {
		return model;
	}

	public void setModel(ModelAnimated model) {
		if (this.model != model) {
			this.model = model;
			this.model.getHeadJoint().calculateInverseBindTransform(new Matrix4f());
			this.animator = new Animator(this.model.getHeadJoint());
			getEntity().setMoved();
		}
	}

	/**
	 * Gets an array of the model-space transforms of all the joints (with the current animation pose applied) in the entity.
	 * The joints are ordered in the array based on their joint index.
	 * The position of each joint's transform in the array is equal to the joint's index.
	 *
	 * @return The array of model-space transforms of the joints in the current animation pose.
	 */
	public Matrix4f[] getJointTransforms() {
		Matrix4f[] jointMatrices = new Matrix4f[model.getSkeletonData().getJointCount()];
		addJointsToArray(model.getHeadJoint(), jointMatrices);
		return jointMatrices;
	}

	/**
	 * This adds the current model-space transform of a joint (and all of its descendants) into an array of transforms.
	 * The joint's transform is added into the array at the position equal to the joint's index.
	 *
	 * @param headJoint The head joint to add children to.
	 * @param jointMatrices The matrices transformation to add with.
	 */
	private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
		if (headJoint.getIndex() < jointMatrices.length) {
			jointMatrices[headJoint.getIndex()] = headJoint.getAnimatedTransform();
		}

		for (Joint childJoint : headJoint.getChildren()) {
			addJointsToArray(childJoint, jointMatrices);
		}
	}

	/**
	 * Gets the diffuse texture for this entity.
	 *
	 * @return The diffuse texture for this entity.
	 */
	public TextureObject getTexture() {
		return texture;
	}

	public void setTexture(TextureObject texture) {
		this.texture = texture;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
	}

	/**
	 * Gets the textures coordinate offset that is used in rendering the model.
	 *
	 * @return The coordinate offset used in rendering.
	 */
	public Vector2f getTextureOffset() {
		int column = textureIndex % texture.getNumberOfRows();
		int row = textureIndex / texture.getNumberOfRows();
		return new Vector2f((float) row / (float) texture.getNumberOfRows(), (float) column / (float) texture.getNumberOfRows());
	}

	public Colour getColourOffset() {
		return colourOffset;
	}

	public void setColourOffset(Colour colourOffset) {
		this.colourOffset.set(colourOffset);
	}

	public Animator getAnimator() {
		return animator;
	}

	public void setAnimator(Animator animator) {
		this.animator = animator;
	}

	@Override
	public Collider getCollider() {
		return collider;
	}

	@Override
	public float getScale() {
		return scale;
	}

	/**
	 * Sets the scale for this model.
	 *
	 * @param scale The new scale.
	 */
	public void setScale(float scale) {
		this.scale = scale;
		getEntity().setMoved();
	}

	@Override
	public void render(ShaderObject shader, Single<Integer> vaoLength) {
		if (vaoLength.getSingle() == -1) {
			return;
		}

		if (model != null && model.isLoaded()) {
			FlounderOpenGL.get().bindVAO(model.getVaoID(), 0, 1, 2, 3, 4, 5);
			shader.getUniformBool("animated").loadBoolean(true);

			if (modelMatrix != null) {
				shader.getUniformMat4("modelMatrix").loadMat4(modelMatrix);
			}

			// Just stop if you are trying to apply a sway to a animated object, rethink life.
			shader.getUniformFloat("swayHeight").loadFloat(0.0f);
			vaoLength.setSingle(model.getVaoLength());

			// Loads joint transforms.
			Matrix4f[] jointMatrices = getJointTransforms();

			for (int i = 0; i < jointMatrices.length; i++) {
				shader.getUniformMat4("jointTransforms[" + i + "]").loadMat4(jointMatrices[i]);
			}
		}

		if (texture != null && texture.isLoaded()) {
			shader.getUniformFloat("atlasRows").loadFloat(texture.getNumberOfRows());
			shader.getUniformVec2("atlasOffset").loadVec2(getTextureOffset());
			shader.getUniformVec3("colourOffset").loadVec3(colourOffset);
			FlounderOpenGL.get().cullBackFaces(!texture.hasAlpha());
			FlounderOpenGL.get().bindTexture(texture, 0);
		}
	}

	@Override
	public void renderClear(ShaderObject shader) {
		shader.getUniformBool("animated").loadBoolean(false);
		shader.getUniformFloat("swayHeight").loadFloat(0.0f);
		shader.getUniformFloat("atlasRows").loadFloat(1);
		shader.getUniformVec2("atlasOffset").loadVec2(0.0f, 0.0f);
		shader.getUniformVec3("colourOffset").loadVec3(0.0f, 0.0f, 0.0f);
	}

	@Override
	public void addToPanel(JPanel panel) {
		// Load Collada.
		JButton loadCollada = new JButton("Select Collada");
		loadCollada.addActionListener((ActionEvent ae) -> {
			JFileChooser fileChooser = new JFileChooser();
			File workingDirectory = new File(System.getProperty("user.dir"));
			fileChooser.setCurrentDirectory(workingDirectory);
			int returnValue = fileChooser.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String selectedFile = fileChooser.getSelectedFile().getAbsolutePath().replace("\\", "/");
				this.editorPathCollada = new MyFile(selectedFile.split("/"));
			}
		});
		panel.add(loadCollada);

		// Load Texture.
		JButton loadTexture = new JButton("Select Texture");
		loadTexture.addActionListener((ActionEvent ae) -> {
			JFileChooser fileChooser = new JFileChooser();
			File workingDirectory = new File(System.getProperty("user.dir"));
			fileChooser.setCurrentDirectory(workingDirectory);
			int returnValue = fileChooser.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String selectedFile = fileChooser.getSelectedFile().getAbsolutePath().replace("\\", "/");
				this.editorPathTexture = new MyFile(selectedFile.split("/"));
			}
		});
		panel.add(loadTexture);

		// Scale Slider.
		//	panel.add(new JLabel("Scale Slider: "));
		JSlider scaleSlider = new JSlider(JSlider.HORIZONTAL, 0, 150, (int) (scale * 25.0f));
		scaleSlider.setToolTipText("Model Scale");
		scaleSlider.addChangeListener((ChangeEvent e) -> {
			JSlider source = (JSlider) e.getSource();
			int reading = source.getValue();
			this.scale = reading / 25.0f;
		});
		scaleSlider.setMajorTickSpacing(25);
		scaleSlider.setMinorTickSpacing(10);
		scaleSlider.setPaintTicks(true);
		scaleSlider.setPaintLabels(true);
		panel.add(scaleSlider);
	}

	@Override
	public void editorUpdate() {
		if (editorPathCollada != null/*  && (model == null|| !model.getFile().equals(editorPathCollada.getPath()))*/) {
			if (editorPathCollada.getPath().contains(".dae")) {
				ModelAnimated modelAnimated = FlounderCollada.get().loadCollada(new MyFile(editorPathCollada));
				AnimationData animationData = FlounderCollada.get().loadAnimation(new MyFile(editorPathCollada));
				Animation animation = FlounderAnimation.get().loadAnimation(animationData);
				setModel(modelAnimated);
				doAnimation(animation);
			}

			editorPathCollada = null;
		}

		if (editorPathTexture != null && (texture == null || !texture.getFile().getPath().equals(editorPathTexture.getPath()))) {
			if (editorPathTexture.getPath().contains(".png")) {
				this.texture = TextureFactory.newBuilder().setFile(new MyFile(editorPathTexture)).create();
			}

			editorPathTexture = null;
		}
	}

	@Override
	public Pair<String[], String[]> getSaveValues(String entityName) {
		String modelName = "collada"; // entityName
		String textureName = "diffuse"; // entityName

		if (model != null) {
			try {
				File file = new File("entities/" + entityName + "/" + modelName + ".dae");

				if (file.exists()) {
					file.delete();
				}

				file.createNewFile();

				InputStream input = model.getFile().getInputStream();
				OutputStream output = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int bytesRead;

				while ((bytesRead = input.read(buf)) > 0) {
					output.write(buf, 0, bytesRead);
				}

				input.close();
				output.close();
			} catch (IOException e) {
				FlounderLogger.get().exception(e);
			}
		}

		if (texture != null) {
			try {
				File file = new File("entities/" + entityName + "/" + textureName + ".png");

				if (file.exists()) {
					file.delete();
				}

				file.createNewFile();

				InputStream input = texture.getFile().getInputStream();
				OutputStream output = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int bytesRead;

				while ((bytesRead = input.read(buf)) > 0) {
					output.write(buf, 0, bytesRead);
				}

				input.close();
				output.close();
			} catch (IOException e) {
				FlounderLogger.get().exception(e);
			}
		}

		String saveScale = scale + "f";
		String saveModel = (model != null) ? ("new MyFile(FlounderEntities.ENTITIES_FOLDER, \"" + entityName + "\", \"" + modelName + ".dae\")") : null;
		String saveTexture = (texture != null) ? ("TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, \"" + entityName + "\", \"" + textureName + ".png\")).setNumberOfRows(" + texture.getNumberOfRows() + ").create()") : null;
		String saveTextureIndex = 1 + "";

		return new Pair<>(
				new String[]{"private static final MyFile COLLADA = " + saveModel, "private static final TextureObject TEXTURE = " + saveTexture}, // Static variables
				new String[]{saveScale, "COLLADA", "TEXTURE", saveTextureIndex} // Class constructor
		);
	}

	@Override
	public void dispose() {
	}
}
