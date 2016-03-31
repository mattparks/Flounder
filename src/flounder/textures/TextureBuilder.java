package flounder.textures;

import flounder.maths.*;
import flounder.processing.*;
import flounder.processing.glProcessing.*;
import flounder.resources.*;

import java.lang.ref.*;
import java.util.*;

public class TextureBuilder {
	private static Map<String, SoftReference<Texture>> loaded = new HashMap<>();

	private boolean m_clampEdges;
	private boolean m_clampToBorder;
	private boolean m_mipmap;
	private boolean m_anisotropic;
	private boolean m_nearest;
	private Colour m_borderColour;
	private MyFile m_file;

	protected TextureBuilder(final MyFile textureFile) {
		m_clampEdges = false;
		m_clampToBorder = false;
		m_mipmap = true;
		m_anisotropic = true;
		m_nearest = false;
		m_borderColour = new Colour(0, 0, 0, 0);
		m_file = textureFile;
	}

	public TextureBuilder clampEdges() {
		m_clampEdges = true;
		m_clampToBorder = false;
		return this;
	}

	public TextureBuilder clampToBorder(final Colour colour) {
		m_clampEdges = false;
		m_clampToBorder = true;
		m_borderColour = colour;
		return this;
	}

	public TextureBuilder nearestFiltering() {
		m_nearest = true;
		return noMipmap();
	}

	public TextureBuilder noMipmap() {
		m_mipmap = true;
		m_anisotropic = false;
		return this;
	}

	public TextureBuilder noFiltering() {
		m_anisotropic = false;
		return this;
	}

	/**
	 * Creates a new texture, carries out the CPU loading, and loads to OpenGL.
	 *
	 * @return The texture that has been created.
	 */
	public Texture create() {
		SoftReference<Texture> ref = loaded.get(m_file.getPath());
		Texture data = ref == null ? null : ref.get();

		if (data == null) {
			System.out.println(m_file.getPath() + " is being loaded into builder memory!");
			loaded.remove(m_file.getPath());
			data = new Texture();
			TextureLoadRequest request = new TextureLoadRequest(data, this);
			request.doResourceRequest();
			request.executeGlRequest();
			loaded.put(m_file.getPath(), new SoftReference<>(data));
		}

		return data;
	}

	/**
	 * Creates a new texture and sends it to be loaded by the loader thread.
	 *
	 * @return The texture.
	 */
	public Texture createInBackground() {
		SoftReference<Texture> ref = loaded.get(m_file.getPath());
		Texture data = ref == null ? null : ref.get();

		if (data == null) {
			loaded.remove(m_file.getPath());
			data = new Texture();
			RequestProcessor.sendRequest(new TextureLoadRequest(data, this));
			loaded.put(m_file.getPath(), new SoftReference<>(data));
		}

		return data;
	}

	/**
	 * Creates a new texture, carries out the CPU loading, and sends to the main thread for GL loading.
	 *
	 * @return The texture.
	 */
	public Texture createInSecondThread() {
		SoftReference<Texture> ref = loaded.get(m_file.getPath());
		Texture data = ref == null ? null : ref.get();

		if (data == null) {
			loaded.remove(m_file.getPath());
			data = new Texture();
			TextureLoadRequest request = new TextureLoadRequest(data, this);
			request.doResourceRequest();
			GlRequestProcessor.sendRequest(request);
			loaded.put(m_file.getPath(), new SoftReference<>(data));
		}

		return data;
	}

	public boolean isClampEdges() {
		return m_clampEdges;
	}

	public boolean isClampToBorder() {
		return m_clampToBorder;
	}

	public boolean isMipmap() {
		return m_mipmap;
	}

	public boolean isAnisotropic() {
		return m_anisotropic;
	}

	public boolean isNearest() {
		return m_nearest;
	}

	public Colour getBorderColour() {
		return m_borderColour;
	}

	public MyFile getFile() {
		return m_file;
	}
}
