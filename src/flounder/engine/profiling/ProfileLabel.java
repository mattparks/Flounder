package flounder.engine.profiling;

import javax.swing.*;

public class ProfileLabel {
	private String label;
	private String value;
	private int xPosition, yPosition;
	private JLabel jLabel;
	private boolean displayed;

	public ProfileLabel(final String label, final String value, final int xPosition, final int yPosition) {
		this.label = label;
		this.value = value;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		jLabel = new JLabel();
		jLabel.setText(label + " = " + value);
		jLabel.setLocation(xPosition, yPosition);
		displayed = false;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(final String label) {
		this.label = label;
		jLabel.setText(label + " = " + value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
		jLabel.setText(label + " = " + value);
	}

	public int getXPosition() {
		return xPosition;
	}

	public void setXPosition(final int xPosition) {
		this.xPosition = xPosition;
		jLabel.setLocation(xPosition, yPosition);
	}

	public int getYPosition() {
		return yPosition;
	}

	public void setYPosition(final int yPosition) {
		this.yPosition = yPosition;
		jLabel.setLocation(xPosition, yPosition);
	}

	public JLabel getJLabel() {
		return jLabel;
	}

	public boolean isDisplayed() {
		return displayed;
	}

	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}
}
