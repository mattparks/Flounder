package flounder.parsing.xml;

import flounder.logger.*;

import java.util.*;

/**
 * Represents a node in an XML file. This contains the name of the node, a map of the attributes and their values, any text data between the start and end tag, and a list of all its children nodes.
 */
public class XmlNode {
	private String name;
	private Map<String, String> attributes;
	private String data;
	private Map<String, List<XmlNode>> childNodes;

	protected XmlNode(String name) {
		this.name = name;
	}

	/**
	 * Gets the name of the XML node.
	 *
	 * @return The name of the XML node.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets any text data contained between the start and end tag of the node.
	 *
	 * @return The text data.
	 */
	public String getData() {
		return data;
	}

	/**
	 * Gets the value of a certain attribute of the node. Returns {@code null} if the attribute doesn't exist.
	 *
	 * @param attribute The name of the attribute.
	 *
	 * @return The value of the attribute.
	 */
	public String getAttribute(String attribute) {
		if (attributes != null) {
			return attributes.get(attribute);
		} else {
			return null;
		}
	}

	/**
	 * Gets a certain child node of this node.
	 *
	 * @param childName The name of the child node.
	 *
	 * @return The child XML node with the given name.
	 */
	public XmlNode getChild(String childName) {
		if (childNodes != null) {
			List<XmlNode> nodes = childNodes.get(childName);

			if (nodes != null && !nodes.isEmpty()) {
				return nodes.get(0);
			}
		}

		FlounderLogger.get().error("Could not find Xml node child: " + childName + " in parent " + name);
		return null;
	}

	/**
	 * Gets a child node with a certain name, and with a given value of a given attribute.
	 * Used to get a specific child when there are multiple child nodes with the same node name.
	 *
	 * @param childName The name of the child node.
	 * @param attribute The attribute whose value is to be checked.
	 * @param value The value that the attribute must have.
	 *
	 * @return The child node which has the correct name and the correct value for the chosen attribute.
	 */
	public XmlNode getChildWithAttribute(String childName, String attribute, String value) {
		List<XmlNode> children = getChildren(childName);

		if (children == null || children.isEmpty()) {
			return null;
		}

		for (XmlNode child : children) {
			String val = child.getAttribute(attribute);

			if (value.equals(val)) {
				return child;
			}
		}

		FlounderLogger.get().error("Could not find Xml node child: " + childName + " in parent " + name + " with attribute " + attribute);
		return null;
	}

	/**
	 * Get the child nodes of this node that have a given name.
	 *
	 * @param name The name of the child nodes.
	 *
	 * @return A list of the child nodes with the given name. If none exist then an empty list is returned.
	 */
	public List<XmlNode> getChildren(String name) {
		if (childNodes != null) {
			List<XmlNode> children = childNodes.get(name);

			if (children != null) {
				return children;
			}
		}

		return new ArrayList<>();
	}

	/**
	 * Adds a new attribute to this node. An attribute has a name and a value.
	 * Attributes are stored in a HashMap which is initialized in here if it was previously null.
	 *
	 * @param attribute The name of the attribute.
	 * @param value The value of the attribute.
	 */
	protected void addAttribute(String attribute, String value) {
		if (attributes == null) {
			attributes = new HashMap<>();
		}

		attributes.put(attribute, value);
	}

	/**
	 * Adds a child node to this node.
	 *
	 * @param child The child node to add.
	 */
	protected void addChild(XmlNode child) {
		if (childNodes == null) {
			childNodes = new HashMap<>();
		}

		List<XmlNode> list = childNodes.computeIfAbsent(child.name, k -> new ArrayList<>());
		list.add(child);
	}

	/**
	 * Sets some data for this node.
	 *
	 * @param data The data for this node (text that is found between the start and end tags of this node).
	 */
	protected void setData(String data) {
		this.data = data;
	}
}
