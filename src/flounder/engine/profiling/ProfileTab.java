package flounder.engine.profiling;

import java.util.*;

public class ProfileTab {
	private String tabName;
	private List<ProfileLabel> labels;
	private int yLocation;

	public ProfileTab(final String tabName) {
		this.tabName = tabName;
		labels = new ArrayList<>();
		yLocation = 320;
	}

	public <T> void addLabel(final String title, final T value) {
		for (ProfileLabel label : labels) {
			if (label.getLabel().equals(title)) {
				label.setValue(value);
				return;
			}
		}

		ProfileLabel label = new ProfileLabel(title, value, 210, (yLocation += 50));
		labels.add(label);
	}

	//public void removeLabel(final String title) {
	//	for (ProfileLabel label : labels) {
	//		if (label.getLabel().equals(title)) {
	//			labels.remove(label);
	//			return;
	//		}
	//	}
	//}

	public String getTabName() {
		return tabName;
	}

	public List<ProfileLabel> getLabels() {
		return labels;
	}

	public int getyLocation() {
		return yLocation;
	}
}
