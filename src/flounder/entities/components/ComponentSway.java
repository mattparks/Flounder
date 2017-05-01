package flounder.entities.components;

import flounder.entities.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class ComponentSway extends IComponentEntity implements IComponentRender, IComponentEditor {
	private TextureObject textureSway;

	private MyFile editorPathTexture;

	/**
	 * Creates a new ComponentSway.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentSway(Entity entity) {
		this(entity, null);
	}

	/**
	 * Creates a new ComponentSway.
	 *
	 * @param entity The entity this component is attached to.
	 * @param textureSway
	 */
	public ComponentSway(Entity entity, TextureObject textureSway) {
		super(entity);

		this.textureSway = textureSway;
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
	}

	public TextureObject getTextureSway() {
		return textureSway;
	}

	public void setTextureSway(TextureObject textureSway) {
		this.textureSway = textureSway;
	}

	public float getSwayOffsetX() {
		float wx = (float) Math.sin(getEntity().getPosition().x * 0.6f);
		float wz = (float) Math.sin(getEntity().getPosition().z * 0.5f);
		float sx = Math.abs(wx) + Math.abs(wz);
		float windPower = 0.24f;
		float systemTime = Framework.getTimeSec() * sx;
		return windPower * (float) (Math.sin(0.25 * systemTime) - Math.sin(1.2 * systemTime) + Math.cos(0.5 * systemTime));
	}

	public float getSwayOffsetZ() {
		float wx = (float) Math.sin(getEntity().getPosition().x * 0.6f);
		float wz = (float) Math.sin(getEntity().getPosition().z * 0.5f);
		float sz = Math.abs(wx) - Math.abs(wz);
		float windPower = 0.24f;
		float systemTime = Framework.getTimeSec() * sz;
		return windPower * (float) (Math.cos(0.25 * systemTime) - Math.cos(1.2 * systemTime) + Math.sin(0.5 * systemTime));
	}

	@Override
	public void render(ShaderObject shader, Single<Integer> vaoLength) {
		shader.getUniformBool("swaying").loadBoolean(true);
		shader.getUniformVec2("swayOffset").loadVec2(getSwayOffsetX(), getSwayOffsetZ());

		if (textureSway != null && textureSway.isLoaded()) {
			OpenGlUtils.bindTexture(textureSway, 2);
		}
	}

	@Override
	public void renderClear(ShaderObject shader) {
		shader.getUniformBool("swaying").loadBoolean(false);
	}

	@Override
	public void addToPanel(JPanel panel) {
		// Load Texture.
		JButton loadTexture = new JButton("Select Sway Map");
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
	}

	@Override
	public void editorUpdate() {
		if (editorPathTexture != null && (textureSway == null || !textureSway.getFile().getPath().equals(editorPathTexture.getPath()))) {
			if (editorPathTexture.getPath().contains(".png")) {
				textureSway = TextureFactory.newBuilder().setFile(new MyFile(editorPathTexture)).create();
			}

			editorPathTexture = null;
		}
	}

	@Override
	public Pair<String[], String[]> getSaveValues(String entityName) {
		String swayName = "sway"; // entityName

		if (textureSway != null) {
			try {
				File file = new File("entities/" + entityName + "/" + swayName + ".png");

				if (file.exists()) {
					file.delete();
				}

				file.createNewFile();

				InputStream input = textureSway.getFile().getInputStream();
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

		String saveTexture = (textureSway != null) ? ("TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, \"" + entityName + "\", \"" + swayName + ".png\")).setNumberOfRows(" + textureSway.getNumberOfRows() + ").create()") : null;

		return new Pair<>(
				new String[]{"private static final TextureObject TEXTURE_SWAY = " + saveTexture}, // Static variables
				new String[]{"TEXTURE_SWAY"} // Class constructor
		);
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}
}
