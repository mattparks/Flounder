package editors.entities;

import editors.editor.*;
import flounder.devices.*;
import flounder.entities.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.standards.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;

public class FrameEntities extends Standard {
	private static final IComponentEntity[] COMPONENT_LIST = new IComponentEntity[]{}; // ComponentsList.LIST; // TODO.

	private static JFrame frame;
	private static JMenuBar menuBar;
	private static JPanel componentPanel;
	private static JPanel mainPanel;
	private static JPanel renderPanel;

	public static JTabbedPane componentsPane;

	public static JComboBox componentDropdown;
	public static JButton componentAdd;
	public static JTextField nameField;
	public static JButton loadButton;
	public static JButton converterButton;
	public static JCheckBox polygonMode;
	public static JCheckBox rotateEntity;
	public static JButton resetButton;
	public static JButton saveButton;

	public static List<IComponentEditor> editorComponents;

	private static List<String> addedTabs = new ArrayList<>();

	public FrameEntities() {
		super(FlounderStandard.class, FlounderDisplayJPanel.class);
	}

	@Override
	public void init() {
		frame = new JFrame();
		frame.setTitle(FlounderDisplay.get().getTitle());
		frame.setSize(FlounderDisplay.get().getWidth(), FlounderDisplay.get().getHeight());
		frame.setLayout(new BorderLayout());
		frame.setResizable(true);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				if (JOptionPane.showConfirmDialog(frame,
						"Are you sure to close this editor?", "Any unsaved work will be lost!",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					Framework.requestClose(false);
				} else {
					frame.setVisible(true);
				}
			}
		});

		editorComponents = new ArrayList<>();

		menuBar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		JMenuItem menuFileHelp = new JMenuItem("Help");
		menuFileHelp.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "Use the bottom bar to select a existing entity, or to change views. The right panel is used to edit entity component, previewed in the top-left display.");
			}
		});
		menuFile.add(menuFileHelp);
		JMenuItem menuFileQuit = new JMenuItem("Quit");
		menuFileQuit.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Framework.requestClose(false);
			}
		});
		menuFile.add(menuFileQuit);
		menuBar.add(menuFile);
		frame.setJMenuBar(menuBar);

		componentPanel = new JPanel();
		componentsPane = new JTabbedPane();
		componentPanel.add(componentsPane);
		frame.add(componentPanel, BorderLayout.EAST);

		mainPanel = new JPanel();
		addComponentsDropdown();
		addComponentsButton();
		entityName();
		addEntityLoad();
		polygonMode();
		rotate();
		reset();
		save();
		frame.add(mainPanel, BorderLayout.SOUTH);

		renderPanel = FlounderDisplayJPanel.get().createPanel();
		frame.add(renderPanel, BorderLayout.CENTER);

		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		frame.toFront();
	}

	private void addComponentsDropdown() {
		// Component Dropdown.
		componentDropdown = new JComboBox();

		for (IComponentEntity component : COMPONENT_LIST) {
			if (component instanceof IComponentEditor) {
				String tabName = IComponentEditor.getTabName((IComponentEditor) component);
				componentDropdown.addItem(tabName);
			}
		}

		mainPanel.add(componentDropdown);
	}

	private void addComponentsButton() {
		componentAdd = new JButton("Add Component");
		componentAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String componentName = (String) componentDropdown.getSelectedItem();
				IComponentEditor editorComponent = null;

				for (IComponentEntity component : COMPONENT_LIST) {
					if (component instanceof IComponentEditor) {
						String tabName = IComponentEditor.getTabName((IComponentEditor) component);

						if (tabName.equals(componentName)) {
							if (((ExtensionEntities) EditorsManager.get().getEditorType()).focusEntity != null && ((ExtensionEntities) EditorsManager.get().getEditorType()).focusEntity.getComponent(component.getClass()) == null) {
								try {
									FlounderLogger.get().log("Adding component: " + componentName);
									Class componentClass = Class.forName(((IComponentEditor) component).getClass().getName());
									Class[] componentTypes = new Class[]{Entity.class};
									@SuppressWarnings("unchecked") Constructor componentConstructor = componentClass.getConstructor(componentTypes);
									Object[] componentParameters = new Object[]{((ExtensionEntities) EditorsManager.get().getEditorType()).focusEntity};
									editorComponent = (IComponentEditor) componentConstructor.newInstance(componentParameters);
								} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException ex) {
									FlounderLogger.get().error("While loading component" + component + "'s constructor could not be found!");
									FlounderLogger.get().exception(ex);
								}
							} else {
								FlounderLogger.get().error("Entity already has instance of " + component);
							}
						}
					}
				}

				FlounderLogger.get().log(editorComponent);

				if (editorComponent != null) {
					editorComponents.add(editorComponent);

					JPanel panel = IComponentEditor.makeTextPanel();
					editorComponent.addToPanel(panel);
					componentAddRemove(panel, editorComponent);
					addSideTab(componentName, panel);
				}
			}
		});
		mainPanel.add(componentAdd);
	}

	private void entityName() {
		nameField = new JTextField("unnamed");
		nameField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				textUpdate();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				textUpdate();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				textUpdate();
			}

			private void textUpdate() {
				((ExtensionEntities) EditorsManager.get().getEditorType()).entityName = nameField.getText();
			}
		});
		mainPanel.add(nameField);
	}

	private void addEntityLoad() {
		loadButton = new JButton("Select .java");
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser fileChooser = new JFileChooser();
				File workingDirectory = new File(System.getProperty("user.dir"));
				fileChooser.setCurrentDirectory(workingDirectory);
				int returnValue = fileChooser.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					String selectedFile = fileChooser.getSelectedFile().getPath().replace("\\", "/");

					String[] filepath = selectedFile.split("/");
					String fileName = filepath[filepath.length - 1];

					FlounderLogger.get().error(fileName);

					//	try {
					//		((ExtensionEntities) EditorsManager.getEditorType()).setEntity((Entity) ClassLoader.getSystemClassLoader().loadClass(selectedFile).newInstance());
					//	} catch (Exception e) {
					//		FlounderLogger.exception(e);
					//	}

				}
			}
		});

		mainPanel.add(loadButton);
	}

	private void polygonMode() {
		polygonMode = new JCheckBox("Polygon Mode");
		polygonMode.setSelected(false);
		polygonMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((ExtensionEntities) EditorsManager.get().getEditorType()).polygonMode = !((ExtensionEntities) EditorsManager.get().getEditorType()).polygonMode;
			}
		});
		mainPanel.add(polygonMode);
	}

	private void rotate() {
		rotateEntity = new JCheckBox("Rotate Entity");
		rotateEntity.setSelected(false);
		rotateEntity.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				((ExtensionEntities) EditorsManager.get().getEditorType()).entityRotate = rotateEntity.isSelected();
			}
		});
		mainPanel.add(rotateEntity);
	}

	public static void addSideTab(String tabName, JPanel panel) {
		FlounderLogger.get().log("Adding side panel: " + tabName);
		addedTabs.add(tabName);
		componentsPane.addTab(tabName, null, panel, "");
	}

	public static void removeSideTab(String tabName, boolean force) {
		FlounderLogger.get().log("Removing side panel: " + tabName);

		if (force) {
			addedTabs.remove(tabName);
		}

		List<Integer> ids = new ArrayList<>();

		for (int i = 0; i < componentsPane.getTabCount(); i++) {
			if (componentsPane.getTitleAt(i).contains(tabName)) {
				ids.add(i);
			}
		}

		Collections.reverse(ids);
		ids.forEach(componentsPane::remove);
	}

	public static void clearSideTab() {
		addedTabs.forEach((tabName) -> FrameEntities.removeSideTab(tabName, false));
		addedTabs.clear();
		componentsPane.removeAll();
		// TODO: Fix clearing not working.
	}

	private void reset() {
		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(frame,
						"Are you sure you want to reset entity settings?", "Any unsaved work will be lost!",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					nameField.setText("unnamed");
					((ExtensionEntities) EditorsManager.get().getEditorType()).loadDefaultEntity();
					clearSideTab();
				}
			}
		});
		mainPanel.add(resetButton);
	}

	public static void componentAddRemove(JPanel panel, IComponentEditor editorComponent) {
		JButton removeButton = new JButton("Remove");
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(frame,
						"Are you sure you want to remove this component.", "Any unsaved component data will be lost!",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					editorComponents.remove(editorComponent);
					removeSideTab(IComponentEditor.getTabName(editorComponent), true);
					((ExtensionEntities) EditorsManager.get().getEditorType()).focusEntity.removeComponent((IComponentEntity) editorComponent);
				}
			}
		});
		panel.add(removeButton);
	}

	@Override
	public void update() {
		editorComponents.forEach(IComponentEditor::editorUpdate);

		for (Pair<String, JPanel> p : IComponentEditor.ADD_SIDE_TAB) {
			addSideTab(p.getFirst(), p.getSecond());
		}

		for (String s : IComponentEditor.REMOVE_SIDE_TAB) {
			removeSideTab(s, true);
		}

		IComponentEditor.ADD_SIDE_TAB.clear();
		IComponentEditor.REMOVE_SIDE_TAB.clear();
	}

	@Override
	public void profile() {
	}

	private void save() {
		saveButton = new JButton("Save Entity");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (((ExtensionEntities) EditorsManager.get().getEditorType()).focusEntity != null) {
					FlounderEntities.get().save("kosmos.entities.instances", editorComponents, ((ExtensionEntities) EditorsManager.get().getEditorType()).entityName);
				}
			}
		});
		mainPanel.add(saveButton);
	}

	@Override
	public void dispose() {
		frame.setVisible(false);
		frame.dispose();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
