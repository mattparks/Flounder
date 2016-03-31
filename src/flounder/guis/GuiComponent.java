package flounder.guis;

import flounder.devices.*;
import flounder.engine.*;
import flounder.fonts.*;
import flounder.maths.vectors.*;

import java.util.*;

/**
 * An component of a GUI. Implementations of this range from a whole GUI frame to a single button. All components can have subtract-components, and the parent component has the responsibility of keeping the subtract-components updated.
 */
public abstract class GuiComponent {
	private Vector2f m_position = new Vector2f();
	private Vector2f m_scale = new Vector2f();

	private Vector2f m_relativePosition = new Vector2f();
	private Vector2f m_relativeScale = new Vector2f();
	private GuiComponent m_parent;

	private boolean m_visible = true;

	private List<GuiComponent> m_childComponents = new ArrayList<>();
	private Map<Text, Vector3f> m_componentTexts = new HashMap<>();

	private List<GuiComponent> m_componentsToRemove = new ArrayList<>();
	private List<GuiComponent> m_componentsToAdd = new ArrayList<>();

	private boolean m_initialized = false;

	/**
	 * Determines whether the component (and all subtract-components) should be visible or not. Non-visible components are not updated.
	 *
	 * @param visible Whether the component should be visible or not.
	 */
	public void show(boolean visible) {
		m_visible = visible;
	}

	/**
	 * Add a subtract-component to this component.
	 *
	 * @param component The subtract=component.
	 * @param relX The x position of the top-left corner of the child component, in relation to this component (0 is the left edge of this component, 1 is the right edge).
	 * @param relY The y position of the top-left corner of the child component, in relation to this component (0 is the top edge of this component, 1 is the bottom edge).
	 * @param relScaleX The x scale of the child component, in relation to the x scale of this component.
	 * @param relScaleY The y scale of the child component, in relation to the y scale of this component.
	 */
	public void addComponent(GuiComponent component, float relX, float relY, float relScaleX, float relScaleY) {
		component.m_relativePosition.set(relX, relY);
		component.m_relativeScale.set(relScaleX, relScaleY);
		component.m_parent = this;
		m_componentsToAdd.add(component);
	}

	/**
	 * Indicates that a child component should be removed from this component and deleted.
	 *
	 * @param component - the child component to be removed.
	 */
	public void removeComponent(GuiComponent component) {
		m_componentsToRemove.add(component);
	}

	/**
	 * Adds some text to the component.
	 *
	 * @param text The text to be added.
	 * @param relX The x position of the left edge of the text, relative to this component's size and position (0 = far left edge, 1 = far right).
	 * @param relY The y position of the top edge of the text, relative to this component's size and position (0 = top edge, 1 = bottom edge).
	 * @param relLineWidth The width of the line of text, relative to the width of the component.
	 */
	public void addText(Text text, float relX, float relY, float relLineWidth) {
		Vector3f relativePosition = new Vector3f(relX, relY, relLineWidth);
		m_componentTexts.put(text, relativePosition);

		if (m_initialized) {
			setTextScreenSpacePosition(text, relativePosition);
		}
	}

	/**
	 * Calculate the screen-space position and screen-space line width of a text based on their relative position and the screen space position of this component.
	 *
	 * @param text The text whose screen-space position needs to be set.
	 * @param relativePosition The position of the text relative to this component's top-left corner. The z component is the line width, specified relative to the width of this component.
	 */
	private void setTextScreenSpacePosition(Text text, Vector3f relativePosition) {
		float x = m_position.x + m_scale.x * relativePosition.x;
		float y = m_position.y + m_scale.y * relativePosition.y;
		float lineWidth = relativePosition.z * m_scale.x;
		text.initialise(x, y, lineWidth);
	}

	/**
	 * @return {@code true} if this component isn't currently hidden.
	 */
	public boolean isShown() {
		return m_visible;
	}

	/**
	 * Removes some text from the component and deletes the text.
	 *
	 * @param text The text currently in the component that needs to be removed.
	 */
	public void deleteText(Text text) {
		m_componentTexts.remove(text);
		text.deleteFromMemory();
	}

	/**
	 * @return The x position of the top-left corner of the component, relative to the parent component.
	 */
	public float getRelativeX() {
		return m_relativePosition.x;
	}

	public void setRelativeX(float x) {
		m_relativePosition.x = x;
		updateScreenSpacePosition();
	}

	public void increaseRelativePosition(float dX, float dY) {
		m_relativePosition.x += dX;
		m_relativePosition.y += dY;
		updateScreenSpacePosition();
	}

	/**
	 * @return The screen-space position of the top-left corner of the component.
	 */
	protected Vector2f getPosition() {
		return m_position;
	}

	/**
	 * @return The screen-space x and y scales of the component.
	 */
	protected Vector2f getScale() {
		return m_scale;
	}

	/**
	 * @return {@code true} if the mouse cursor is currently over this component.
	 */
	protected boolean isMouseOver() {
		if (ManagerDevices.getMouse().isDisplaySelected()) {
			if (ManagerDevices.getMouse().getPositionX() >= m_position.x && ManagerDevices.getMouse().getPositionX() <= m_position.x + m_scale.x) {
				if (ManagerDevices.getMouse().getPositionY() >= m_position.y && ManagerDevices.getMouse().getPositionY() <= m_position.y + m_scale.y) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Updates the component and all of its child-components, as well as filling the list of {@link GuiTexture}s with any textures that this component or any of its subtract-components need to be rendered. It also removes any subtract components that have indicated that they need to be removed.
	 *
	 * @param guiTextures The list of {@link GuiTexture}s to be rendered.
	 * @param texts The map of texts to be rendered this frame.
	 */
	public final void update(List<GuiTexture> guiTextures, Map<FontType, List<Text>> texts) {
		if (!m_visible) {
			return;
		}

		updateSelf();
		updateTexts();
		getGuiTextures(guiTextures);
		addTextsToRenderBatch(texts);
		removeOldComponents();
		m_childComponents.forEach(childComponent -> childComponent.update(guiTextures, texts));
		updateAndAddNewChildren(guiTextures, texts);
	}

	/**
	 * Calculates the screen space position of the component based on their relative position and the screen-space position of their parent.
	 */
	protected void updateScreenSpacePosition() {
		float x = m_parent.m_position.x + m_parent.m_scale.x * m_relativePosition.x;
		float y = m_parent.m_position.y + m_parent.m_scale.y * m_relativePosition.y;
		float width = m_relativeScale.x * m_parent.m_scale.x;
		float height = m_relativeScale.y * m_parent.m_scale.y;
		setScreenSpacePosition(x, y, width, height);

		m_childComponents.forEach(GuiComponent::updateScreenSpacePosition);
		m_componentTexts.keySet().forEach(text -> setTextScreenSpacePosition(text, m_componentTexts.get(text)));
	}

	/**
	 * Sets the screen-space position and dimensions of this component. (0, 0) is the top left of the screen and (1, 1) is the bottom right.
	 *
	 * @param x The x position of the top-left of the component in screen-space.
	 * @param y The y position of the top-left of the component in screen-space.
	 * @param width The width of the component in screen-space.
	 * @param height The height of the component in screen-space.
	 */
	protected void setScreenSpacePosition(float x, float y, float width, float height) {
		m_position.set(x, y);
		m_scale.set(width, height);
		m_initialized = true;
	}

	/**
	 * Updates the functionality of this particular component.
	 */
	protected abstract void updateSelf();

	/**
	 * Adds any of this component's {@link GuiTexture}s.
	 *
	 * @param guiTextures The list of GUI textures that are going to be rendered. This method adds any necessary {@link GuiTexture}s from this component to that list.
	 */
	protected abstract void getGuiTextures(List<GuiTexture> guiTextures);

	/**
	 * Adds the texts from this component into the map of texts for rendering this frame. There is a list of texts for each font being used.
	 *
	 * @param texts All the lists of texts, each associated with the font that all the texts in that list use.
	 */
	private void addTextsToRenderBatch(Map<FontType, List<Text>> texts) {
		for (Text text : m_componentTexts.keySet()) {
			FontType font = text.getFontType();
			List<Text> textBatch = texts.get(font);

			if (textBatch == null) {
				textBatch = new ArrayList<>();
				texts.put(font, textBatch);
			}

			textBatch.add(text);
		}
	}

	/**
	 * Update the component's texts.
	 */
	private void updateTexts() {
		m_componentTexts.keySet().forEach(text -> text.update(FlounderEngine.getDelta()));
	}

	/**
	 * Deletes the component and all subtract-components. Mainly just deletes the texts VAOs from memory.
	 */
	private void delete() {
		m_componentTexts.keySet().forEach(Text::deleteFromMemory);
		m_componentsToRemove.forEach(GuiComponent::delete);
		m_componentsToAdd.forEach(GuiComponent::delete);
		m_childComponents.forEach(GuiComponent::delete);
	}

	private void removeOldComponents() {
		for (GuiComponent component : m_componentsToRemove) {
			m_childComponents.remove(component);
			component.delete();
		}

		m_componentsToRemove.clear();
	}

	private void updateAndAddNewChildren(List<GuiTexture> guiTextures, Map<FontType, List<Text>> texts) {
		for (GuiComponent component : m_componentsToAdd) {
			m_childComponents.add(component);
			component.updateScreenSpacePosition();
			component.update(guiTextures, texts);
		}

		m_componentsToAdd.clear();
	}
}
