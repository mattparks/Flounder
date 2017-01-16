package flounder.entities.template;

import java.util.*;

/**
 * A class that contains data from a section from a component.
 */
public class EntitySectionData {
	public final String name;
	public final String[] lines;

	public EntitySectionData(String name, List<String> lines) {
		this.name = name;
		this.lines = lines.stream().toArray(String[]::new);
	}
}
