package editors.editor;

import flounder.framework.*;
import flounder.logger.*;

public class EditorsManager extends Module {
	private IEditorType editorType;

	public EditorsManager() {
		super(FlounderLogger.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.editorType = null;
	}

	@Handler.Function(Handler.FLAG_UPDATE_POST)
	public void update() {
		// Gets a new editor, if available.
		IEditorType newManager = (IEditorType) getExtension(editorType, IEditorType.class, true);

		// If there is a editor, disable the old one and start to use the new one.
		if (newManager != null) {
			if (editorType != null) {
				editorType.dispose();
				editorType.setInitialized(false);
			}

			if (!newManager.isInitialized()) {
				newManager.init();
				newManager.setInitialized(true);
			}

			editorType = newManager;
		}

		// Runs updates for the editor.
		if (editorType != null) {
			editorType.update();
		}
	}

	/**
	 * Gets the current editor extension.
	 *
	 * @return The current editor.
	 */
	public IEditorType getEditorType() {
		return this.editorType;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		// Disposes the editor with the module.
		if (editorType != null) {
			editorType.dispose();
			editorType.setInitialized(false);
		}
	}

	@Module.Instance
	public static EditorsManager get() {
		return (EditorsManager) Framework.getInstance(EditorsManager.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Kosmos Editor";
	}
}
