package flounder.particles.loading;

import flounder.engine.*;
import flounder.helpers.*;

import java.io.*;
import java.util.*;

/**
 * Class capable of saving engine.entities to a .particle file.
 */
public class ParticleSaver {
	/**
	 * Saves the particle components to a .particle file.
	 *
	 * @param particle The particle to save.
	 */
	public static void save(ParticleTemplate particle) {
		try {
			// Creates the save folder
			File saveFolder = new File("particles");

			if (!saveFolder.exists()) {
				saveFolder.mkdir();
			}

			// The save file and the writers.
			File saveFile = new File(saveFolder.getPath() + "/" + particle.getName() + ".particle");
			saveFile.createNewFile();
			FileWriter fileWriter = new FileWriter(saveFile);
			FlounderFileWriter entityFileWriter = new FlounderFileWriter(fileWriter);

			// Date and save info.
			String savedDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "." + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "." + Calendar.getInstance().get(Calendar.YEAR) + " - " + Calendar.getInstance().get(Calendar.HOUR) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
			FlounderEngine.getLogger().log("Particle " + particle.getName() + " is being saved at: " + savedDate);
			entityFileWriter.addComment("Date Generated: " + savedDate, "Created By: " + System.getProperty("user.classpath"));

			// Entity General Data.
			entityFileWriter.beginNewSegment("ParticleData");
			{
				entityFileWriter.writeSegmentData("Name: " + particle.getName() + ";", true);
				entityFileWriter.writeSegmentData("Texture: " + (particle.getTexture() == null ? "null" : particle.getTexture().getFile().getPath().substring(1, particle.getTexture().getFile().getPath().length())) + ";", true);
				entityFileWriter.writeSegmentData("NumberOfRows: " + (particle.getTexture() == null ? "1" : particle.getTexture().getNumberOfRows()) + ";", true);
				entityFileWriter.writeSegmentData("LifeLength: " + particle.getLifeLength() + ";", true);
				entityFileWriter.writeSegmentData("Scale: " + particle.getScale() + ";", true);
			}
			entityFileWriter.endSegment(false);

			// Closes the file for writing.
			fileWriter.close();
		} catch (IOException e) {
			FlounderEngine.getLogger().error("File saver for entity " + particle.getName() + " did not execute successfully!");
			FlounderEngine.getLogger().exception(e);
		}
	}
}
