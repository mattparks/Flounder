package flounder.shaders;

import flounder.factory.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.resources.*;

import java.io.*;
import java.lang.ref.*;
import java.util.*;

/**
 * A class that represents a factory for loading shaders.
 */
public class ShaderFactory extends Factory {
	private static final ShaderFactory INSTANCE = new ShaderFactory();

	private ShaderFactory() {
		super("shader");
	}

	/**
	 * Gets a new builder to be used to create information for build a object from.
	 *
	 * @return A new factory builder.
	 */
	public static ShaderBuilder newBuilder() {
		return new ShaderBuilder(INSTANCE);
	}

	@Override
	public ShaderObject newObject() {
		return new ShaderObject();
	}

	@Override
	public void loadData(FactoryObject object, FactoryBuilder builder, String name) {
		ShaderBuilder b = (ShaderBuilder) builder;
		ShaderObject o = (ShaderObject) object;

		List<Pair<String, String>> constantValues = new ArrayList<>();
		List<String> layoutLocations = new ArrayList<>();
		List<String> layoutBindings = new ArrayList<>();
		List<Pair<Uniform.Uniforms, String>> shaderUniforms = new ArrayList<>();

		for (ShaderType type : b.getTypes()) {
			if (type.getShaderFile() != null) {
				MyFile file = type.getShaderFile();

				try {
					BufferedReader reader = file.getReader();
					String line;

					while ((line = reader.readLine()) != null) {
						type.getShaderBuilder().append(INSTANCE.processShaderLine(line.trim(), constantValues, layoutLocations, layoutBindings, shaderUniforms, type.getShaderType()));
						type.getShaderBuilder().append("\n");
					}
				} catch (Exception e) {
					FlounderLogger.get().error("Could not read file " + file.getName());
					FlounderLogger.get().exception(e);
					System.exit(-1);
				}
			} else if (type.getShaderString() != null) {
				String string = type.getShaderString();

				for (String line : string.split("\n")) {
					type.getShaderBuilder().append(INSTANCE.processShaderLine(line.trim(), constantValues, layoutLocations, layoutBindings, shaderUniforms, type.getShaderType()));
					type.getShaderBuilder().append("\n");
				}
			}
		}

		o.loadData(constantValues, layoutLocations, layoutBindings, shaderUniforms, name);
	}

	private StringBuilder processShaderLine(String line, List<Pair<String, String>> constantValues, List<String> layoutLocations, List<String> layoutBindings, List<Pair<Uniform.Uniforms, String>> shaderUniforms, int shaderType) {
		if (line.contains("#version")) {
			return new StringBuilder(FlounderShaders.get().getVersion());
		} else if (line.contains("#include")) {
			String included = line.replaceAll("\\s+", "").replaceAll("\"", "");
			included = included.substring("#include".length(), included.length());
			MyFile includeFile = new MyFile(FlounderShaders.SHADERS_LOC, included);
			StringBuilder includeSource = new StringBuilder();

			try {
				BufferedReader reader = includeFile.getReader();
				String includeLine;

				while ((includeLine = reader.readLine()) != null) {
					includeSource.append(processShaderLine(includeLine.trim(), constantValues, layoutLocations, layoutBindings, shaderUniforms, shaderType));
					includeSource.append("\n");
				}
			} catch (Exception e) {
				FlounderLogger.get().error("Could not read file " + includeFile.getName());
				FlounderLogger.get().exception(e);
				System.exit(-1);
			}

			return includeSource;
		} else if (line.replaceAll("\\s+", "").startsWith("layout") && shaderType != -1) {
			if (line.contains("location")) {
				layoutLocations.add(line);
				return new StringBuilder().append(line.substring(StringUtils.findCharPos(line, ')') + 1, line.length()));
			} else if (line.contains("binding")) {
				layoutBindings.add(line);
				return new StringBuilder().append(line.substring(StringUtils.findCharPos(line, ')') + 1, line.length()));
			}
		}

		if (line.startsWith("const")) {
			String uniformVarName = line.substring("const".length() + 1, line.length() - 1);
			uniformVarName = uniformVarName.substring(StringUtils.findCharPos(uniformVarName, ' '), uniformVarName.length()).trim();
			constantValues.add(new Pair<>(uniformVarName.split("=")[0].trim(), uniformVarName.split("=")[1].trim()));
		}

		if (line.startsWith("uniform")) {
			String uniformVarName = line.substring("uniform".length() + 1, line.length() - 1);
			String uniform = uniformVarName.split(" ")[0].toUpperCase();
			String name = uniformVarName.split(" ")[1];

			// Array Uniforms.
			if (name.contains("[") && name.contains("]")) {
				String nameArray = name.substring(0, StringUtils.findCharPos(name, '[')).trim();
				String arraySize = name.substring(StringUtils.findCharPos(name, '[') + 1, name.length() - 1).trim();
				int size = 0;

				if (ByteWork.isInteger(arraySize)) {
					size = Integer.parseInt(arraySize);
				} else {
					for (Pair<String, String> pair : constantValues) {
						if (pair.getFirst().equals(arraySize)) {
							size = Integer.parseInt(pair.getSecond());
							break;
						}
					}
				}

				for (int i = 0; i < size; i++) {
					shaderUniforms.add(new Pair<>(Uniform.Uniforms.valueOf(uniform), nameArray + "[" + i + "]"));
				}
			} else {
				// Normal Uniforms.
				shaderUniforms.add(new Pair<>(Uniform.Uniforms.valueOf(uniform), name));
			}
		}

		return new StringBuilder().append(line);
	}

	@Override
	protected void create(FactoryObject object, FactoryBuilder builder) {
		ShaderBuilder b = (ShaderBuilder) builder;
		ShaderObject o = (ShaderObject) object;
		FlounderShaders.get().loadShader(b, o);
	}

	@Override
	public Map<String, SoftReference<FactoryObject>> getLoaded() {
		return FlounderShaders.get().getLoaded();
	}
}
