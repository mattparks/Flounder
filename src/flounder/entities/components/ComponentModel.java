package flounder.entities.components;

import flounder.entities.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;
import flounder.physics.bounding.*;
import flounder.resources.*;
import flounder.textures.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.*;

/**
 * Creates a model with a texture that can be rendered into the world.
 */
public class ComponentModel extends IComponentEntity implements IComponentEditor, IComponentScale, IComponentCollider {
	private float scale;
	private ModelObject model;
	private Matrix4f modelMatrix;

	private Collider collider;

	private TextureObject texture;
	private int textureIndex;

	private Colour colourOffset;

	private MyFile editorPathModel;
	private MyFile editorPathTexture;

	private boolean wasLoaded;

	/**
	 * Creates a new ComponentModel.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentModel(Entity entity) {
		this(entity, 1.0f, null, TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "undefined.png")).create(), 0);
	}

	/**
	 * Creates a new ComponentModel.
	 *
	 * @param entity The entity this component is attached to.
	 * @param scale The scale of the entity.
	 * @param model The model that will be attached to this entity.
	 * @param texture The diffuse texture for the entity.
	 * @param textureIndex What texture index this entity should renderObjects from (0 default).
	 */
	public ComponentModel(Entity entity, float scale, ModelObject model, TextureObject texture, int textureIndex) {
		super(entity);

		this.scale = scale;
		this.model = model;
		this.modelMatrix = new Matrix4f();

		this.collider = null;

		this.texture = texture;
		this.textureIndex = textureIndex;

		this.colourOffset = new Colour();

		this.wasLoaded = false;
	}

	@Override
	public void update() {
		if (model != null && model.isLoaded() != wasLoaded) {
			getEntity().setMoved();
			wasLoaded = model.isLoaded();
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

		FlounderBounding.addShapeRender(collider);
	}

	public ModelObject getModel() {
		return model;
	}

	public void setModel(ModelObject model) {
		if (this.model != model) {
			this.model = model;
			getEntity().setMoved();
		}
	}

	/**
	 * Gets the entitys model matrix.
	 *
	 * @return The entitys model matrix.
	 */
	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}

	public TextureObject getTexture() {
		return texture;
	}

	public void setTexture(TextureObject texture) {
		if (this.texture != texture) {
			this.texture = texture;
		}
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

	@Override
	public void addToPanel(JPanel panel) {
		// Load Model.
		JButton loadModel = new JButton("Select Model");
		loadModel.addActionListener((ActionEvent ae) -> {
			JFileChooser fileChooser = new JFileChooser();
			File workingDirectory = new File(System.getProperty("user.dir"));
			fileChooser.setCurrentDirectory(workingDirectory);
			int returnValue = fileChooser.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String selectedFile = fileChooser.getSelectedFile().getAbsolutePath().replace("\\", "/");
				this.editorPathModel = new MyFile(selectedFile.split("/"));
			}
		});
		panel.add(loadModel);

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
			this.scale = (float) reading / 25.0f;
		});
		scaleSlider.setMajorTickSpacing(25);
		scaleSlider.setMinorTickSpacing(10);
		scaleSlider.setPaintTicks(true);
		scaleSlider.setPaintLabels(true);
		panel.add(scaleSlider);
	}

	@Override
	public void editorUpdate() {
		if (editorPathModel != null && (model == null || !model.getName().equals(editorPathModel.getName()))) {
			if (editorPathModel.getPath().contains(".obj")) {
				this.model = ModelFactory.newBuilder().setFile(new MyFile(editorPathModel)).create();
			}

			editorPathModel = null;
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
		String modelName = "model"; // entityName
		String textureName = "diffuse"; // entityName

		if (model != null) {
			try {
				File file = new File("entities/" + entityName + "/" + modelName + ".obj");

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
				FlounderLogger.exception(e);
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
				FlounderLogger.exception(e);
			}
		}

		String saveScale = scale + "f";
		String saveModel = (model != null) ? ("ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, \"" + entityName + "\", \"" + modelName + ".obj\")).create()") : null;
		String saveTexture = (texture != null) ? ("TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, \"" + entityName + "\", \"" + textureName + ".png\")).setNumberOfRows(" + texture.getNumberOfRows() + ").create()") : null;
		String saveTextureIndex = 1 + "";

		return new Pair<>(
				new String[]{"private static final ModelObject MODEL = " + saveModel, "private static final TextureObject TEXTURE = " + saveTexture}, // Static variables
				new String[]{saveScale, "MODEL", "TEXTURE", saveTextureIndex} // Class constructor
		);
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
	public Collider getCollider() {
		return collider;
	}

	@Override
	public void dispose() {
	}
}
