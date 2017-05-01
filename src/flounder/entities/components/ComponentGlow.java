package flounder.entities.components;

import flounder.entities.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class ComponentGlow extends IComponentEntity implements IComponentRender, IComponentEditor {
	private TextureObject textureGlow;

	private MyFile editorPathTexture;

	/**
	 * Creates a new ComponentGlow.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentGlow(Entity entity) {
		this(entity, null);
	}

	/**
	 * Creates a new ComponentGlow.
	 *
	 * @param entity The entity this component is attached to.
	 * @param textureGlow
	 */
	public ComponentGlow(Entity entity, TextureObject textureGlow) {
		super(entity);

		this.textureGlow = textureGlow;
	}

	@Override
	public void update() {
	}

	public TextureObject getTextureGlow() {
		return textureGlow;
	}

	public void setTextureGlow(TextureObject textureGlow) {
		this.textureGlow = textureGlow;
	}

	@Override
	public void render(ShaderObject shader, Single<Integer> vaoLength) {
		shader.getUniformBool("useGlowMap").loadBoolean(true);

		if (textureGlow != null && textureGlow.isLoaded()) {
			OpenGlUtils.bindTexture(textureGlow, 1);
		}
	}

	@Override
	public void renderClear(ShaderObject shader) {
		shader.getUniformBool("useGlowMap").loadBoolean(false);
	}

	@Override
	public void addToPanel(JPanel panel) {
		// Load Texture.
		JButton loadTexture = new JButton("Select Glow Map");
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
		if (editorPathTexture != null && (textureGlow == null || !textureGlow.getFile().getPath().equals(editorPathTexture.getPath()))) {
			if (editorPathTexture.getPath().contains(".png")) {
				textureGlow = TextureFactory.newBuilder().setFile(new MyFile(editorPathTexture)).create();
			}

			editorPathTexture = null;
		}
	}

	@Override
	public Pair<String[], String[]> getSaveValues(String entityName) {
		String glowName = "glow"; // entityName

		if (textureGlow != null) {
			try {
				File file = new File("entities/" + entityName + "/" + glowName + ".png");

				if (file.exists()) {
					file.delete();
				}

				file.createNewFile();

				InputStream input = textureGlow.getFile().getInputStream();
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

		String saveTexture = (textureGlow != null) ? ("TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, \"" + entityName + "\", \"" + glowName + ".png\")).setNumberOfRows(" + textureGlow.getNumberOfRows() + ").create()") : null;

		return new Pair<>(
				new String[]{"private static final TextureObject TEXTURE_GLOW = " + saveTexture}, // Static variables
				new String[]{"TEXTURE_GLOW"} // Class constructor
		);
	}

	@Override
	public void dispose() {
	}
}
