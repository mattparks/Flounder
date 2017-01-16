package flounder.entities.template;

import flounder.helpers.*;

import java.util.*;

/**
 * A class that contains individual from a component.
 */
public class EntityIndividualData {
	public final String classpath;
	public final List<Pair<String, String>> individualData;

	public EntityIndividualData(String name, List<Pair<String, String>> individualData) {
		this.classpath = name;
		this.individualData = individualData;
	}
}
