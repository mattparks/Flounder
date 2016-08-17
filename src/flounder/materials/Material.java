package flounder.materials;


import flounder.maths.*;
import flounder.textures.*;

public class Material {
	public String name;

	public float specularCoefficient = 100.0f;
	public Colour ambientColour = new Colour(0.2f, 0.2f, 0.2f);
	public Colour diffuseColour = new Colour(0.3f, 1.0f, 1.0f);
	public Colour specularColour = new Colour(1.0f, 1.0f, 1.0f);

	public Texture texture;
	public Texture normalMap;

	@Override
	public String toString() {
		return name + "[ " + "SpecularCoefficient=(" + specularCoefficient + "), AmbientColour=" + ambientColour.toString() + ", DiffuseColour=" + diffuseColour.toString() + ", SpecularColour=" + specularColour.toString() + " ]";
	}
}
