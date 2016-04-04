package flounder.engine.profiling;

import javax.swing.*;

public class ProfileLabel<T> {
	private String label;
	private T value;
	private int xPosition, yPosition;
	private JLabel jLabel;
	private boolean displayed;

	protected ProfileLabel(final String label, final T value, final int xPosition, final int yPosition) {
		this.label = label;
		this.value = value;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		jLabel = new JLabel();
		jLabel.setText(label + " = " + value);
		jLabel.setLocation(xPosition, yPosition);
		displayed = false;
	}

	protected String getLabel() {
		return label;
	}

	protected void setLabel(final String label) {
		this.label = label;
		jLabel.setText(label + " = " + value);
	}

	protected T getValue() {
		return value;
	}

	protected void setValue(final T value) {
		this.value = value;
		jLabel.setText(label + " = " + value);
	}

	protected int getXPosition() {
		return xPosition;
	}

	protected void setXPosition(final int xPosition) {
		this.xPosition = xPosition;
		jLabel.setLocation(xPosition, yPosition);
	}

	protected int getYPosition() {
		return yPosition;
	}

	protected void setYPosition(final int yPosition) {
		this.yPosition = yPosition;
		jLabel.setLocation(xPosition, yPosition);
	}

	protected JLabel getJLabel() {
		return jLabel;
	}

	protected boolean isDisplayed() {
		return displayed;
	}

	protected void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}
}
