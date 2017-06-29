package flounder.shaders;

import flounder.factory.*;
import flounder.framework.*;
import flounder.processing.*;
import flounder.resources.*;

import java.lang.ref.*;
import java.nio.*;
import java.util.*;

/**
 * A module used for loading GLSL files into shaders.
 */
public class FlounderShaders extends Module {
	public static final MyFile SHADERS_LOC = new MyFile(MyFile.RES_FOLDER, "shaders");

	private Map<String, SoftReference<FactoryObject>> loaded;

	/**
	 * Creates a new shader loader class.
	 */
	public FlounderShaders() {
		super(FlounderProcessors.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.loaded = new HashMap<>();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
	}

	/**
	 * Gets a list of loaded shaders.
	 *
	 * @return A list of loaded shaders.
	 */
	public Map<String, SoftReference<FactoryObject>> getLoaded() {
		return this.loaded;
	}

	@Module.MethodReplace
	public String getVersion() {
		return "#version 110";
	}

	/**
	 * Loads a shader into memory.
	 *
	 * @param builder The builder to load from.
	 * @param object The object to load to.
	 */
	@Module.MethodReplace
	public void loadShader(ShaderBuilder builder, ShaderObject object) {

	}

	@Module.MethodReplace
	public int getUniformLocation(int programID, String uniformName) {
		return -1;
	}

	@Module.MethodReplace
	public <T> void storeSimpleData(int location, T data) {

	}

	@Module.MethodReplace
	public <T> void storeMatrixData(int location, FloatBuffer buffer, T data) {

	}

	@Module.MethodReplace
	public <T> void storeVectorData(int location, T data) {

	}

	/**
	 * Binds a shader.
	 *
	 * @param shaderID The shader to bind.
	 */
	@Module.MethodReplace
	public void useShader(int shaderID) {
	}

	/**
	 * Deletes a shader from memory.
	 *
	 * @param shaderID The shader to delete.
	 */
	@Module.MethodReplace
	public void deleteShader(int shaderID) {
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		loaded.keySet().forEach(key -> ((ShaderObject) loaded.get(key).get()).delete());
		loaded.clear();
	}

	@Module.Instance
	public static FlounderShaders get() {
		return (FlounderShaders) Framework.get().getInstance(FlounderShaders.class);
	}
}
