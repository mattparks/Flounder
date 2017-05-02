package flounder.lwjgl3.shaders;

import flounder.framework.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.shaders.*;

import java.lang.reflect.*;
import java.nio.*;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

@Module.ModuleOverride
public class LwjglShaders extends FlounderShaders {
	public LwjglShaders() {
		super();
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {

		super.init();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		super.update();

	}

	@Override
	public void loadShader(ShaderBuilder builder, ShaderObject object) {
		int programID = glCreateProgram();

		for (ShaderType type : builder.getTypes()) {
			int shaderID = glCreateShader(type.getShaderType());
			glShaderSource(shaderID, type.getShaderBuilder());
			glCompileShader(shaderID);

			if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
				FlounderLogger.get().error(glGetShaderInfoLog(shaderID, 500));
				throw new RuntimeException("Could not compile shader " + object.getName() + ", type=" + type);
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
				Object uobject = ctor.newInstance(new Object[]{pair.getSecond(), object});
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

	@Override
	public int getUniformLocation(int programID, String uniformName) {
		return glGetUniformLocation(programID, uniformName);
	}

	@Override
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

	@Override
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

	@Override
	public void useShader(int shaderID) {
		glUseProgram(shaderID);
	}

	@Override
	public void deleteShader(int shaderID) {
		glUseProgram(0);
		glDeleteProgram(shaderID);
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
		super.profile();
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		super.dispose();

	}
}
