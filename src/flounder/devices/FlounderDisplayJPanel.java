package flounder.devices;

import flounder.events.*;
import flounder.framework.*;
import flounder.platform.*;
import flounder.tasks.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.nio.*;

/**
 * A module used for rendering a invisible display into a JPanel.
 */
public class FlounderDisplayJPanel extends Module {
	private JPanel panel;
	private BufferedImage image;
	private ByteBuffer buffer;

	/**
	 * Creates a new JPanel renderer.
	 */
	public FlounderDisplayJPanel() {
		super(FlounderPlatform.class, FlounderDisplay.class, FlounderEvents.class, FlounderTasks.class);
	}

	@Module.Instance
	public static FlounderDisplayJPanel get() {
		return (FlounderDisplayJPanel) Framework.get().getInstance(FlounderDisplayJPanel.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
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
	public JPanel createPanel() {
		this.panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				// Sets to paint with a new Graphics.
				super.paintComponent(g);

				// Draws the BufferedImage.
				g.drawImage(image, 0, 0, null);

				// Draws the current engine FPS.
				g.drawString("FPS: " + (1.0f / Framework.get().getDeltaRender()), 10, 15);
				g.drawString("UPS: " + (1.0f / Framework.get().getDelta()), 10, 30);
			}
		};
		this.panel.setSize(FlounderDisplay.get().getWidth(), FlounderDisplay.get().getHeight());
		return this.panel;
	}

	@Handler.Function(Handler.FLAG_UPDATE_ALWAYS)
	public void update() {
		// Updates only if the JPanel exists.
		if (panel == null) {
			return;
		}

		// Updates the invisible displays size.
		if ((panel.getHeight() != 0 && panel.getWidth() != 0) && (FlounderDisplay.get().getWidth() != panel.getWidth() || FlounderDisplay.get().getHeight() != panel.getHeight())) {
			FlounderDisplay.get().setWindowSize(panel.getWidth(), panel.getHeight());
		}

		// Copies the image from the invisible to a BufferedImage.
		image = FlounderDisplay.get().getImage(image, buffer);

		// Forces a JFrame redraw event.
		SwingUtilities.getWindowAncestor(panel).repaint();
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}
}
