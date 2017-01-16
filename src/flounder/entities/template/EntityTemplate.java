package flounder.entities.template;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.space.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Class that represents loaded .entity data.
 */
public class EntityTemplate {
	private String entityName;
	private Map<EntityIndividualData, List<EntitySectionData>> componentsData;

	/**
	 * Creates a new template from loaded data.
	 *
	 * @param entityName The name of the loaded entity.
	 * @param componentsData A HashMap of loaded component data to be parsed when attaching the component to the new entity.
	 */
	public EntityTemplate(String entityName, Map<EntityIndividualData, List<EntitySectionData>> componentsData) {
		this.entityName = entityName;
		this.componentsData = componentsData;
	}

	/**
	 * Creates a new entity from the template.
	 *
	 * @param structure The structure to place the entity into.
	 * @param position Initial world position.
	 * @param rotation Initial rotation.
	 *
	 * @return Returns a new entity created from the template.
	 */
	public Entity createEntity(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		Entity instance = new Entity(structure, position, rotation);

		for (EntityIndividualData k : componentsData.keySet()) {
			try {
				Class componentClass = Class.forName(k.classpath);
				Class[] componentTypes = new Class[]{Entity.class, EntityTemplate.class};
				@SuppressWarnings("unchecked") Constructor componentConstructor = componentClass.getConstructor(componentTypes);
				Object[] componentParameters = new Object[]{instance, this};
				componentConstructor.newInstance(componentParameters);
			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
				FlounderLogger.error("While loading " + entityName + "'s components " + k.classpath + " constructor could not be found!");
				FlounderLogger.exception(e);
			}
		}

		return instance;
	}

	/**
	 * Gets the individual data from the requested variable.
	 *
	 * @param component The component to get data for.
	 * @param key The key to get data, the nave for the individual variable.
	 *
	 * @return Returns string of parsable data.
	 */
	public String getValue(IComponentEntity component, String key) {
		for (EntityIndividualData data : componentsData.keySet()) {
			if (data.classpath.equals(component.getClass().getName())) {
				for (Pair<String, String> pair : data.individualData) {
					if (pair.getFirst().equals(key)) {
						return pair.getSecond();
					}
				}
			}
		}

		return null;
	}

	/**
	 * Gets data from a entity component section.
	 *
	 * @param component The component to get data for.
	 * @param sectionName The sections name.
	 *
	 * @return The sections data.
	 */
	public String[] getSectionData(IComponentEntity component, String sectionName) {
		for (EntityIndividualData data : componentsData.keySet()) {
			if (data.classpath.equals(component.getClass().getName())) {
				for (EntitySectionData section : componentsData.get(data)) {
					if (section.name.equals(sectionName)) {
						return section.lines;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Gets the entity templates name.
	 *
	 * @return The entity templates name.
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * Turns a segmented string into a float array.
	 *
	 * @param data The segmented data to parse.
	 *
	 * @return The resulting float array.
	 */
	public static float[] toFloatArray(String[] data) {
		float[] result = new float[data.length];

		for (int i = 0; i < data.length; i++) {
			result[i] = Float.parseFloat(data[i]);
		}

		return result;
	}

	/**
	 * Turns a segmented string into a integer array.
	 *
	 * @param data The segmented data to parse.
	 *
	 * @return The resulting integer array.
	 */
	public static int[] toIntArray(String[] data) {
		int[] result = new int[data.length];

		for (int i = 0; i < data.length; i++) {
			result[i] = Integer.parseInt(data[i]);
		}

		return result;
	}
}
