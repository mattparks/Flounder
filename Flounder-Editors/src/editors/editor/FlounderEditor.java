package editors.editor;

import editors.entities.*;
import flounder.framework.*;
import flounder.framework.updater.*;
import flounder.lwjgl3.*;
import flounder.resources.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.*;

/**
 * The editors entrance and selection class.
 */
public class FlounderEditor extends TimerTask {
	private JFrame frame;
	private JRadioButton optionEntities;

	private boolean startEntrance;
	private boolean running;

	public static void main(String[] args) {
		Timer timer = new Timer();
		timer.schedule(new FlounderEditor(), 0, 1000);
	}

	private FlounderEditor() {
		frame = new JFrame("Flounder Editor");
		frame.setSize(300, 420);
		frame.setResizable(false);
		running = true;

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				frame.setVisible(false);
				running = false;
			}
		});

		frame.setLayout(new FlowLayout());

		optionEntities = new JRadioButton("Entities");
		JButton buttonSubmit = new JButton("Submit");

		buttonSubmit.addActionListener((ActionEvent actionEvent) -> {
			System.out.println("Starting entrance from button!");
			startEntrance = true;
		});

		ButtonGroup group = new ButtonGroup();
		group.add(optionEntities);

		frame.add(optionEntities);
		frame.add(buttonSubmit);
		frame.pack();

		frame.setSize(frame.getWidth() + 64, frame.getHeight() + 8);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
		frame.setVisible(true);
	}

	@Override
	public void run() {
		if (startEntrance) {
			System.out.println("Starting editor entrance.");
			startEntrance = false;

			if (optionEntities.isSelected()) {
				Framework entrance = new Framework(
						"Flounder Editors", new UpdaterDefault(null), -1, // GLFW::glfwGetTime
						new Extension[]{new ExtensionEntities(), new FrameEntities(), new EditorRenderer(), new EditorCamera(), new EditorPlayer(), new EditorGuis()}
				);
				Framework.get().addOverrides(new PlatformLwjgl(
						1080, 720, "Flounder Editor Entities",
						new MyFile[]{new MyFile(MyFile.RES_FOLDER, "flounder.png")},
						false, true, 0, false, true, false, 2.0f
				));
				frame.setVisible(false);
				entrance.run();
			} else {
				System.err.println("No editor selected!");
			}

			frame.setVisible(true);
			frame.toFront();
		}

		if (!running) {
			System.exit(0);
		}
	}
}
