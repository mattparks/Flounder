package flounder.entities;

import flounder.entities.components.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.space.*;

import java.io.*;
import java.util.*;

/**
 * A class that manages game entities.
 */
public class FlounderEntities extends Module {
	private static final FlounderEntities INSTANCE = new FlounderEntities();
	public static final String PROFILE_TAB_NAME = "Entities";

	public static final MyFile ENTITIES_FOLDER = new MyFile(MyFile.RES_FOLDER, "entities");

	private ISpatialStructure<Entity> entityStructure;

	/**
	 * Creates a new game manager for entities.
	 */
	public FlounderEntities() {
		super(ModuleUpdate.UPDATE_POST, PROFILE_TAB_NAME, FlounderLogger.class, FlounderProfiler.class);
	}

	@Override
	public void init() {
		this.entityStructure = new StructureBasic<>();
	}

	@Override
	public void update() {
		if (entityStructure != null) {
			entityStructure.getAll().forEach(Entity::update);
		}
	}

	@Override
	public void profile() {
		FlounderProfiler.add(PROFILE_TAB_NAME, "Count", entityStructure.getSize());
	}

	/**
	 * Clears the world of all entities.
	 */
	public static void clear() {
		INSTANCE.entityStructure.getAll().forEach((Entity entity) -> entity.forceRemove(false));
		INSTANCE.entityStructure.clear();
	}

	/**
	 * Saves the entity components to a .entity file.
	 *
	 * @param packageLocation The package to have for the entity file.
	 * @param editorComponents The entity editor components to save.
	 * @param name The nave for the entity and file.
	 */
	public static void save(String packageLocation, List<IComponentEditor> editorComponents, String name) {
		try {
			String className = "Instance" + name.substring(0, 1).toUpperCase() + name.substring(1);

			// Creates the save folder
			File saveFolder = new File("entities");

			if (!saveFolder.exists()) {
				saveFolder.mkdir();
			}

			saveFolder = new File("entities/" + name);

			if (!saveFolder.exists()) {
				saveFolder.mkdir();
			}

			// The save file and the writers.
			File saveFile = new File(saveFolder.getPath() + "/" + className + ".java");
			saveFile.createNewFile();
			FileWriter fileWriter = new FileWriter(saveFile);
			FileWriterHelper FileWriterHelper = new FileWriterHelper(fileWriter);

			// Date and save info.
			String savedDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "." + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "." + Calendar.getInstance().get(Calendar.YEAR) + " - " + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
			FlounderLogger.log("Entity " + name + " is being saved at: " + savedDate);
			FileWriterHelper.addComment("Automatically generated entity source", "Date generated: " + savedDate, "Created by: " + System.getProperty("user.name"));

			FileWriterHelper.writeSegmentData("package " + packageLocation + ";\n\n", true);

			// Entity instance class.
			FileWriterHelper.beginNewSegment("public class " + className + " extends Entity");
			{
				// Writes static data to the save.
				for (int i = 0; i < editorComponents.size(); i++) {
					for (String s : editorComponents.get(i).getSaveValues(name).getFirst()) {
						FileWriterHelper.writeSegmentData(s + ";", true);
					}
				}

				// Add some spacing before constructor.
				if (!editorComponents.isEmpty()) {
					FileWriterHelper.writeSegmentData("\n", true);
				}

				// Entity instance constructor.
				FileWriterHelper.beginNewSegment("public " + className + "(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation)");
				{
					FileWriterHelper.writeSegmentData("super(structure, position, rotation);", true);
					// Writes the component constructors.
					for (int i = 0; i < editorComponents.size(); i++) {
						String parameterData = "";

						for (String s : editorComponents.get(i).getSaveValues(name).getSecond()) {
							parameterData += s + ", ";
						}

						parameterData = parameterData.replaceAll(", $", "");

						if (parameterData.isEmpty()) {
							FileWriterHelper.writeSegmentData("new " + editorComponents.get(i).getClass().getName() + "(this);", true);
						} else {
							FileWriterHelper.writeSegmentData("new " + editorComponents.get(i).getClass().getName() + "(this, " + parameterData + ");", true);
						}
					}
				}
				FileWriterHelper.endSegment(true);
			}
			FileWriterHelper.endSegment(false);

			// Closes the file for writing.
			fileWriter.close();
		} catch (IOException e) {
			FlounderLogger.error("File saver for entity " + name + " did not execute successfully!");
			FlounderLogger.exception(e);
		}
	}

	/**
	 * Gets a list of entities.
	 *
	 * @return A list of entities.
	 */
	public static ISpatialStructure<Entity> getEntities() {
		return INSTANCE.entityStructure;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		if (entityStructure != null) {
			entityStructure.clear();
			entityStructure = null;
		}
	}
}
