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

public class ComponentNormals extends IComponentEntity implements IComponentRender, IComponentEditor {
	private TextureObject textureNormals;

	private MyFile editorPathTexture;

	/**
	 * Creates a new ComponentNormals.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentNormals(Entity entity) {
		this(entity, null);
	}

	/**
	 * Creates a new ComponentGlow.
	 *
	 * @param entity The entity this component is attached to.
	 * @param textureNormals The texture to be used as the normal map.
	 */
	public ComponentNormals(Entity entity, TextureObject textureNormals) {
		super(entity);

		this.textureNormals = textureNormals;
	}

	@Override
	public void update() {
	}

	@Override
	public void dispose() {
	}

	public TextureObject getTextureNormals() {
		return textureNormals;
	}

	public void setTextureNormals(TextureObject textureNormals) {
		this.textureNormals = textureNormals;
	}

	@Override
	public void render(ShaderObject shader, Single<Integer> vaoLength) {
		//	shader.getUniformBool("useNormalMap").loadBoolean(true);
		//	if (textureNormals != null && textureNormals.isLoaded()) {
		//		FlounderOpenGL.get().bindTexture(textureNormals, 1);
		//	}
	}

	@Override
	public void renderClear(ShaderObject shader) {
		//	shader.getUniformBool("useNormalMap").loadBoolean(false);
	}

	@Override
	public void addToPanel(JPanel panel) {
		// Load Texture.
		JButton loadTexture = new JButton("Select Normal Map");
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
		if (editorPathTexture != null && (textureNormals == null || !textureNormals.getFile().getPath().equals(editorPathTexture.getPath()))) {
			if (editorPathTexture.getPath().contains(".png")) {
				textureNormals = TextureFactory.newBuilder().setFile(new MyFile(editorPathTexture)).create();
			}

			editorPathTexture = null;
		}
	}

	@Override
	public Pair<String[], String[]> getSaveValues(String entityName) {
		String normalName = "normals"; // entityName

		if (textureNormals != null) {
			try {
				File file = new File("entities/" + entityName + "/" + normalName + ".png");

				if (file.exists()) {
					file.delete();
				}

				file.createNewFile();

				InputStream input = textureNormals.getFile().getInputStream();
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

		String saveTexture = (textureNormals != null) ? ("TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, \"" + entityName + "\", \"" + normalName + ".png\")).setNumberOfRows(" + textureNormals.getNumberOfRows() + ").create()") : null;

		return new Pair<>(
				new String[]{"private static final TextureObject TEXTURE_NORMALS = " + saveTexture}, // Static variables
				new String[]{"TEXTURE_NORMALS"} // Class constructor
		);
	}
}
