package flounder.materials;

import flounder.engine.*;
import flounder.maths.*;
import flounder.resources.*;
import flounder.textures.*;

import java.io.*;
import java.util.*;

/**
 * Class capable of loading MTL files into Materials.
 */
public class FlounderMaterials implements IModule {
	public FlounderMaterials() {
	}

	@Override
	public void init() {

	}

	@Override
	public void update() {

	}

	@Override
	public void profile() {

	}

	/**
	 * Loads a MTL file into a list of Material objects.
	 *
	 * @param file The file to be loaded.
	 *
	 * @return Returns a loaded list of MTLMaterials.
	 */
	public List<Material> loadMTL(MyFile file) {
		BufferedReader reader = null;

		try {
			reader = file.getReader();
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Material> materialData = new ArrayList<>();

		String line;
		String parseMaterialName = "";
		Material parseMaterial = new Material();

		if (reader == null) {
			FlounderEngine.getLogger().error("Error creating reader the MTL: " + file);
		}

		try {
			while ((line = reader.readLine()) != null) {
				String prefix = line.split(" ")[0];

				if (line.startsWith("#")) {
					continue;
				}

				switch (prefix) {
					case "newmtl":
						if (!parseMaterialName.equals("")) {
							materialData.add(parseMaterial);
						}

						parseMaterialName = line.split(" ")[1];
						parseMaterial = new Material();
						parseMaterial.name = parseMaterialName;
						break;
					case "Ns":
						parseMaterial.specularCoefficient = Float.valueOf(line.split(" ")[1]);
						break;
					case "Ka":
						String[] rgbKa = line.split(" ");
						parseMaterial.ambientColour = new Colour(Float.valueOf(rgbKa[1]), Float.valueOf(rgbKa[2]), Float.valueOf(rgbKa[3]));
						break;
					case "Kd":
						String[] rgbKd = line.split(" ");
						parseMaterial.diffuseColour = new Colour(Float.valueOf(rgbKd[1]), Float.valueOf(rgbKd[2]), Float.valueOf(rgbKd[3]));
						break;
					case "Ks":
						String[] rgbKs = line.split(" ");
						parseMaterial.specularColour = new Colour(Float.valueOf(rgbKs[1]), Float.valueOf(rgbKs[2]), Float.valueOf(rgbKs[3]));
						break;
					case "map_Kd":
						if (!line.split(" ")[1].isEmpty()) {
							parseMaterial.texture = Texture.newTexture(new MyFile(MyFile.RES_FOLDER, line.split(" ")[1])).create();
						}
						break;
					case "map_bump":
						if (!line.split(" ")[1].isEmpty()) {
							parseMaterial.normalMap = Texture.newTexture(new MyFile(MyFile.RES_FOLDER, line.split(" ")[1])).create();
						}
						break;
					default:
						FlounderEngine.getLogger().log("[MTL " + file.getName() + "] Unknown Line: " + line);
						break;
				}
			}

			reader.close();
			materialData.add(parseMaterial);
		} catch (IOException | NullPointerException e) {
			FlounderEngine.getLogger().error("Error reading the MTL: " + file);
		}

		return materialData;
	}

	@Override
	public void dispose() {

	}
}
