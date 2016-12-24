package flounder.devices;

import flounder.events.*;
import flounder.framework.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.nio.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A module used for rendering a invisible display into a JPanel.
 */
public class FlounderDisplayJPanel extends IModule {
	private static final FlounderDisplayJPanel instance = new FlounderDisplayJPanel();

	private JPanel panel;
	private BufferedImage image;
	private ByteBuffer buffer;

	/**
	 * Creates a new JPanel renderer.
	 */
	public FlounderDisplayJPanel() {
		super(ModuleUpdate.UPDATE_ALWAYS, FlounderDisplay.class, FlounderEvents.class);
	}

	@Override
	public void init() {
		panel = null;
		image = null;
		buffer = null;
	}

	/**
	 * Creates a new JPanel for the instance to render into.
	 *
	 * @return The new JPanel.
	 */
	public static JPanel createPanel() {
		instance.panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				// Sets to paint with a new Graphics.
				super.paintComponent(g);

				// Draws the BufferedImage.
				g.drawImage(instance.image, 0, 0, null);

				// Draws the current engine FPS.
				g.drawString("FPS: " + (1.0f / FlounderFramework.getDeltaRender()), 10, 15);
			}
		};
		instance.panel.setSize(FlounderDisplay.getWidth(), FlounderDisplay.getHeight());
		return instance.panel;
	}

	@Override
	public void update() {
		// Updates only if the JPanel exists.
		if (panel == null) {
			return;
		}

		// Updates the invisible displays size.
		if ((panel.getHeight() != 0 && panel.getWidth() != 0) && (FlounderDisplay.getWidth() != panel.getWidth() || FlounderDisplay.getHeight() != panel.getHeight())) {
			glfwSetWindowSize(FlounderDisplay.getWindow(), panel.getWidth(), panel.getHeight());
		}

		// Copies the image from the invisible to a BufferedImage.
		image = FlounderDisplay.getImage(image, buffer);

		// Forces a JFrame redraw event.
		SwingUtilities.getWindowAncestor(panel).repaint();
	}

	@Override
	public void profile() {
	}

	@Override
	public IModule getInstance() {
		return instance;
	}

	@Override
	public void dispose() {
	}
}
