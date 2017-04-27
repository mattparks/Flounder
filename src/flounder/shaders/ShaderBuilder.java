package flounder.shaders;

import flounder.factory.*;

import java.util.*;

/**
 * A builder used to set shader parameters for loading.
 */
public class ShaderBuilder extends FactoryBuilder {
	private String name;
	private List<ShaderType> types;

	public ShaderBuilder(Factory factory) {
		super(factory);
		this.name = null;
		this.types = new ArrayList<>();
	}

	/**
	 * Sets the shaders name.
	 *
	 * @param name The name.
	 *
	 * @return this.
	 */
	public ShaderBuilder setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Adds a new shader type to the load pool.
	 *
	 * @param type The shader type to add.
	 *
	 * @return this.
	 */
	public ShaderBuilder addType(ShaderType type) {
		this.types.add(type);
		return this;
	}

	/**
	 * Gets the shaders name.
	 *
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the shader types to be loaded.
	 *
	 * @return The shader types to load.
	 */
	public List<ShaderType> getTypes() {
		return types;
	}

	@Override
	public ShaderObject create() {
		return (ShaderObject) builderCreate(name);
	}

	@Override
	public String toString() {
		return "ShaderBuilder{" +
				"name='" + name + '\'' +
				", types=" + types +
				'}';
	}
}
