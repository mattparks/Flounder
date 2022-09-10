package com.flounder.entities;

import com.flounder.animation.*;
import com.flounder.events.*;
import com.flounder.framework.*;
import com.flounder.helpers.*;
import com.flounder.logger.*;
import com.flounder.models.*;
import com.flounder.physics.bounding.*;
import com.flounder.resources.*;
import com.flounder.space.*;
import com.flounder.tasks.*;
import com.flounder.textures.*;

import java.io.*;
import java.util.*;

/**
 * A class that manages game entities.
 */
public class FlounderEntities extends com.flounder.framework.Module {
	public static final MyFile ENTITIES_FOLDER = new MyFile(MyFile.RES_FOLDER, "entities");

	private ISpatialStructure<Entity> entityStructure;

	/**
	 * Creates a new game manager for entities.
	 */
	public FlounderEntities() {
		super(FlounderEvents.class, FlounderTasks.class, FlounderBounding.class, FlounderAnimation.class, FlounderModels.class, FlounderTextures.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.entityStructure = new StructureBasic<>();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		if (entityStructure != null) {
			Iterator<Entity> iterator = entityStructure.getAll(null).iterator();

			while (iterator.hasNext()) {
				Entity entity = iterator.next();

				if (entity != null && !entity.isRemoved()) {
					entity.update();
				} else {
					iterator.remove();
					entityStructure.remove(entity);
				}
			}
		}
	}

	/**
	 * Saves the entity components to a .entity file.
	 *
	 * @param packageLocation The package to have for the entity file.
	 * @param editorComponents The entity editor components to save.
	 * @param name The nave for the entity and file.
	 */
	public void save(String packageLocation, List<IComponentEditor> editorComponents, String name) {
		try {
			String className = "Instance" + name.substring(0, 1).toUpperCase() + name.substring(1);

			// Creates the save folder
			File saveFolder = new File("entities");

			if (!saveFolder.exists()) {
				saveFolder.mkdir();
			}

			File saveNameFolder = new File("entities/" + name);

			if (!saveNameFolder.exists()) {
				saveNameFolder.mkdir();
			}

			// The save file and the writers.
			File saveFile = new File(saveFolder.getPath() + "/" + className + ".java");
			saveFile.createNewFile();
			FileWriter fileWriter = new FileWriter(saveFile);
			FileWriterHelper fileWriterHelper = new FileWriterHelper(fileWriter);

			// Package path data.
			fileWriterHelper.writeSegmentData("package " + packageLocation + ";\n", true);

			fileWriterHelper.writeSegmentData("import com.flounder.entities.*;", true);
			fileWriterHelper.writeSegmentData("import com.flounder.lights.*;", true);
			fileWriterHelper.writeSegmentData("import com.flounder.maths.*;", true);
			fileWriterHelper.writeSegmentData("import com.flounder.maths.vectors.*;", true);
			fileWriterHelper.writeSegmentData("import com.flounder.models.*;", true);
			fileWriterHelper.writeSegmentData("import com.flounder.resources.*;", true);
			fileWriterHelper.writeSegmentData("import com.flounder.space.*;", true);
			fileWriterHelper.writeSegmentData("import com.flounder.textures.*;\n", true);

			// Date and save info.
			String savedDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "." + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "." + Calendar.getInstance().get(Calendar.YEAR) + " - " + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
			FlounderLogger.get().log("Entity " + name + " is being saved at: " + savedDate);
			fileWriterHelper.addComment("Automatically generated entity source", "Date generated: " + savedDate, "Created by: " + System.getProperty("user.name"));

			// Entity instance class.
			fileWriterHelper.beginNewSegment("public class " + className + " extends Entity");
			{
				// Writes static data to the save.
				for (IComponentEditor editorComponent : editorComponents) {
					Pair<String[], String[]> saveValues = editorComponent.getSaveValues(name);

					if (saveValues != null) {
						for (String s : saveValues.getFirst()) {
							fileWriterHelper.writeSegmentData(s + ";", true);
						}
					}
				}

				// Add some spacing before constructor.
				if (!editorComponents.isEmpty()) {
					fileWriterHelper.writeSegmentData("\n", true);
				}

				// Entity instance constructor.
				fileWriterHelper.beginNewSegment("public " + className + "(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation)");
				{
					fileWriterHelper.writeSegmentData("super(structure, position, rotation);", true);
					// Writes the component constructors.
					for (IComponentEditor editorComponent : editorComponents) {
						Pair<String[], String[]> saveValues = editorComponent.getSaveValues(name);

						if (saveValues != null) {
							StringBuilder parameterData = new StringBuilder();

							for (String s : saveValues.getSecond()) {
								parameterData.append(s);
								parameterData.append(", ");
							}

							parameterData = new StringBuilder(parameterData.toString().replaceAll(", $", ""));

							if (parameterData.length() == 0) {
								fileWriterHelper.writeSegmentData("new " + editorComponent.getClass().getName() + "(this);", true);
							} else {
								fileWriterHelper.writeSegmentData("new " + editorComponent.getClass().getName() + "(this, " + parameterData + ");", true);
							}
						}
					}
				}
				fileWriterHelper.endSegment(true);
			}
			fileWriterHelper.endSegment(false);

			// Closes the file for writing.
			fileWriter.close();
		} catch (IOException e) {
			FlounderLogger.get().error("File saver for entity " + name + " did not execute successfully!");
			FlounderLogger.get().exception(e);
		}
	}

	/**
	 * Gets a list of entities.
	 *
	 * @return A list of entities.
	 */
	public ISpatialStructure<Entity> getEntities() {
		return this.entityStructure;
	}

	/**
	 * Clears the world of all entities.
	 */
	public void clear() {
		this.entityStructure.foreach(Entity::forceRemove);
		this.entityStructure.clear();
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		if (entityStructure != null) {
			entityStructure.clear();
			entityStructure = null;
		}
	}

	@com.flounder.framework.Module.Instance
	public static FlounderEntities get() {
		return (FlounderEntities) Framework.get().getModule(FlounderEntities.class);
	}
}
