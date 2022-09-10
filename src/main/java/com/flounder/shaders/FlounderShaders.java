package com.flounder.shaders;

import com.flounder.factory.*;
import com.flounder.framework.*;
import com.flounder.helpers.Pair;
import com.flounder.helpers.StringUtils;
import com.flounder.logger.FlounderLogger;
import com.flounder.maths.matrices.Matrix2f;
import com.flounder.maths.matrices.Matrix3f;
import com.flounder.maths.matrices.Matrix4f;
import com.flounder.maths.vectors.Vector2f;
import com.flounder.maths.vectors.Vector3f;
import com.flounder.maths.vectors.Vector4f;
import com.flounder.platform.FlounderPlatform;
import com.flounder.platform.Platform;
import com.flounder.processing.*;
import com.flounder.resources.*;

import java.lang.ref.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.*;
import java.util.*;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

/**
 * A module used for loading GLSL files into shaders.
 */
public class FlounderShaders extends com.flounder.framework.Module {
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

	public String getVersion() {
		return FlounderPlatform.get().getPlatform().equals(Platform.MACOS) ? "#version 150 core" : "#version 130";
	}

	/**
	 * Loads a shader into memory.
	 *
	 * @param builder The builder to load from.
	 * @param object The object to load to.
	 */
	public void loadShader(ShaderBuilder builder, ShaderObject object) {
		int programID = glCreateProgram();

		for (ShaderType type : builder.getTypes()) {
			int shaderID = glCreateShader(type.getShaderType());
			glShaderSource(shaderID, type.getShaderBuilder());
			glCompileShader(shaderID);

			if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
				FlounderLogger.get().error(glGetShaderInfoLog(shaderID, 500));
				throw new RuntimeException("Could not compile shader " + object.getName() + ", type=" + type.toString());
			}

			type.setShaderProgramID(shaderID);
			glAttachShader(programID, shaderID);
		}

		for (String location : object.getLayoutLocations()) {
			String locationName = location.substring(location.lastIndexOf(" ") + 1, location.length() - 1);
			String type = location.substring(0, location.lastIndexOf(" ") + 1);
			type = type.substring(location.lastIndexOf(")") + 1, type.length()).trim();
			int locationValue = Integer.parseInt(location.substring(StringUtils.findCharPos(location, '=') + 1, StringUtils.findCharPos(location, ')')).replaceAll("\\s+", ""));

			if (type.contains("in")) {
				glBindAttribLocation(programID, locationValue, locationName);
			} else if (type.contains("out")) {
				glBindFragDataLocation(programID, locationValue, locationName);
			} else {
				FlounderLogger.get().error("Could not find location type of: " + type);
			}
		}

		glLinkProgram(programID);

		for (ShaderType type : builder.getTypes()) {
			glDetachShader(programID, type.getShaderProgramID());
			glDeleteShader(type.getShaderProgramID());
			type.setShaderProgramID(-1);
		}

		glUseProgram(programID);

		for (String binding : object.getLayoutBindings()) {
			String bindingName = binding.substring(binding.lastIndexOf(" ") + 1, binding.length() - 1);
			int bindingValue = Integer.parseInt(binding.substring(StringUtils.findCharPos(binding, '=') + 1, StringUtils.findCharPos(binding, ')')).replaceAll("\\s+", ""));
			UniformSampler2D sampler = new UniformSampler2D(bindingName, object);
			sampler.storeUniformLocation(programID);
			sampler.loadTexUnit(bindingValue);
		}

		glUseProgram(0);

		Map<String, Uniform> uniforms = new HashMap<>();

		for (Pair<Uniform.Uniforms, String> pair : object.getShaderUniforms()) {
			String uniformClass = pair.getFirst().getUniformClass();
			Uniform uniformObject = null;

			// Loads the uniform from the class name.
			try {
				Class<?> clazz = Class.forName(uniformClass);
				Constructor<?> ctor = clazz.getConstructor(String.class, ShaderObject.class);
				Object uobject = ctor.newInstance(pair.getSecond(), object);
				uniformObject = (Uniform) uobject;
			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
				FlounderLogger.get().error("Shader could not create the uniform type of " + uniformClass);
				e.printStackTrace();
			}

			// If the uniform was loaded.
			if (uniformObject != null) {
				// Store uniform locations.
				uniformObject.storeUniformLocation(programID);

				// Keeps the uniform variables for later usage.
				uniforms.put(pair.getSecond(), uniformObject);
			}
		}

		object.loadGL(uniforms, programID);
	}

	public int getUniformLocation(int programID, String uniformName) {
		return glGetUniformLocation(programID, uniformName);
	}

	public <T> void storeSimpleData(int location, T data) {
		if (data instanceof Boolean) {
			glUniform1f(location, (Boolean) data ? 1.0f : 0.0f);
		}
		if (data instanceof Float) {
			glUniform1f(location, (Float) data);
		} else if (data instanceof Integer) {
			glUniform1i(location, (Integer) data);
		}
	}

	public <T> void storeMatrixData(int location, FloatBuffer buffer, T data) {
		if (data instanceof Matrix2f) {
			buffer.clear();
			((Matrix2f) data).store(buffer);
			buffer.flip();

			glUniformMatrix2fv(location, false, buffer);
		} else if (data instanceof Matrix3f) {
			buffer.clear();
			((Matrix3f) data).store(buffer);
			buffer.flip();

			glUniformMatrix3fv(location, false, buffer);
		} else if (data instanceof Matrix4f) {
			buffer.clear();
			((Matrix4f) data).store(buffer);
			buffer.flip();

			glUniformMatrix4fv(location, false, buffer);
		}
	}

	public <T> void storeVectorData(int location, T data) {
		if (data instanceof Vector2f) {
			glUniform2f(location, ((Vector2f) data).x, ((Vector2f) data).y);
		} else if (data instanceof Vector3f) {
			glUniform3f(location, ((Vector3f) data).x, ((Vector3f) data).y, ((Vector3f) data).z);
		} else if (data instanceof Vector4f) {
			glUniform4f(location, ((Vector4f) data).x, ((Vector4f) data).y, ((Vector4f) data).z, ((Vector4f) data).w);
		}
	}

	/**
	 * Binds a shader.
	 *
	 * @param shaderID The shader to bind.
	 */
	public void useShader(int shaderID) {
		glUseProgram(shaderID);
	}

	/**
	 * Deletes a shader from memory.
	 *
	 * @param shaderID The shader to delete.
	 */
	public void deleteShader(int shaderID) {
		glUseProgram(0);
		glDeleteProgram(shaderID);
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		loaded.keySet().forEach(key -> ((ShaderObject) loaded.get(key).get()).delete());
		loaded.clear();
	}

	@com.flounder.framework.Module.Instance
	public static FlounderShaders get() {
		return (FlounderShaders) Framework.get().getModule(FlounderShaders.class);
	}
}
