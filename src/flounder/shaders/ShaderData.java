package flounder.shaders;

import flounder.engine.*;
import flounder.helpers.*;

import java.lang.reflect.*;
import java.util.*;

public class ShaderData {
	public StringBuilder builderVertex;
	public StringBuilder builderGeometry;
	public StringBuilder builderFragment;

	public List<Pair<String, String>> conatantValues;
	public List<String> layoutLocations;
	public List<String> layoutBindings;
	public List<Pair<Uniforms, String>> shaderUniforms;

	public ShaderData() {
		this.conatantValues = new ArrayList<>();
		this.layoutLocations = new ArrayList<>();
		this.layoutBindings = new ArrayList<>();
		this.shaderUniforms = new ArrayList<>();
	}

	public void destroy() {
		conatantValues.clear();
		layoutLocations.clear();
		layoutBindings.clear();
		shaderUniforms.clear();

		conatantValues = null;
		layoutLocations = null;
		layoutBindings = null;
		shaderUniforms = null;
	}

	public enum Uniforms {
		BOOL(UniformBool.class.getName()), FLOAT(UniformFloat.class.getName()), SAMPLER2D(UniformSampler2D.class.getName()),
		MAT2(UniformMat2.class.getName()), MAT3(UniformMat3.class.getName()), MAT4(UniformMat4.class.getName()),
		VEC2(UniformVec2.class.getName()), VEC3(UniformVec3.class.getName()), VEC4(UniformVec4.class.getName());

		private String uniformClass;

		Uniforms(String uniformClass) {
			this.uniformClass = uniformClass;
		}

		public String getUniformClass() {
			return uniformClass;
		}
	}
}
