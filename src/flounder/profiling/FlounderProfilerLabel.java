package flounder.profiling;

import javax.swing.*;

/**
 * Profiler label to ensure compat
 */
class FlounderProfilerLabel<T> {
	private final JLabel jLabel;
	private String contentLabel;
	private T value;

	protected FlounderProfilerLabel(final String contentLabel, final T value) {
		this.contentLabel = contentLabel;
		this.value = value;

		jLabel = new JLabel(contentLabel + " = " + value);
	}

	protected T getValue() {
		return value;
	}

	protected void setValue(T value) {
		this.value = value;
		jLabel.setText(contentLabel + " = " + value);
	}

	protected String getContentLabel() {
		return contentLabel;
	}

	protected void setContentLabel(final String contentLabel) {
		this.contentLabel = contentLabel;
	}

	protected JLabel getDisplayObject() {
		return jLabel;
	}
}
