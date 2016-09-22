package flounder.engine;

import flounder.exceptions.*;

import java.io.*;

/**
 * The version info for Flounder.
 */
public class Version {
	public final String version;
	private final int major;
	private final int minor;
	private final int revision;

	/**
	 * Creates a new version info class.
	 *
	 * @param version The current version.
	 */
	public Version(String version) {
		this.version = version;

		try {
			String[] v = version.split("\\.");
			major = v.length < 1 ? 0 : Integer.valueOf(v[0]);
			minor = v.length < 2 ? 0 : Integer.valueOf(v[1]);
			revision = v.length < 3 ? 0 : Integer.valueOf(v[2]);
		} catch (Throwable t) {
			throw new FlounderRuntimeException("Invalid Flounder Version " + version);
		}

		try {
			File versionFile = new File(FlounderEngine.getRoamingFolder().getPath() + "/" + "flounder.txt");

			if (!versionFile.exists()) {
				if (!versionFile.createNewFile()) {
					FlounderEngine.getLogger().error("Failed to create new version file!");
				}
			} else {
				if (!versionFile.delete()) {
					FlounderEngine.getLogger().error("Failed to delete version file!");
				}

				if (!versionFile.createNewFile()) {
					FlounderEngine.getLogger().error("Failed to create new version file!");
				}
			}

			FileOutputStream versionOutput = new FileOutputStream(versionFile, false);
			versionOutput.write(version.getBytes());
			versionOutput.flush();
			versionOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets if the provided version info is higher than the current.
	 *
	 * @param major The provided major version.
	 * @param minor The provided minor version.
	 * @param revision The provided revision.
	 *
	 * @return If the provided is higher than the current.
	 */
	public boolean isHigher(int major, int minor, int revision) {
		return isHigherEqual(major, minor, revision + 1);
	}

	/**
	 * Gets if the provided version info is higher or equal than the current.
	 *
	 * @param major The provided major version.
	 * @param minor The provided minor version.
	 * @param revision The provided revision.
	 *
	 * @return If the provided is higher or equal to the current.
	 */
	public boolean isHigherEqual(int major, int minor, int revision) {
		if (this.major != major) {
			return this.major > major;
		}

		if (this.minor != minor) {
			return this.minor > minor;
		}

		return this.revision >= revision;
	}

	/**
	 * Gets if the provided version info is lower than the current.
	 *
	 * @param major The provided major version.
	 * @param minor The provided minor version.
	 * @param revision The provided revision.
	 *
	 * @return If the provided is lower than the current.
	 */
	public boolean isLower(int major, int minor, int revision) {
		return isLowerEqual(major, minor, revision - 1);
	}

	/**
	 * Gets if the provided version info is lower or equal than the current.
	 *
	 * @param major The provided major version.
	 * @param minor The provided minor version.
	 * @param revision The provided revision.
	 *
	 * @return If the provided is lower or equal to the current.
	 */
	public boolean isLowerEqual(int major, int minor, int revision) {
		if (this.major != major) {
			return this.major < major;
		}

		if (this.minor != minor) {
			return this.minor < minor;
		}

		return this.revision <= revision;
	}

	/**
	 * Gets the current revision version of The Flounder Engine.
	 *
	 * @return The current revision version.
	 */
	public int getRevision() {
		return revision;
	}

	/**
	 * Gets the current major version of The Flounder Engine.
	 *
	 * @return The current major version.
	 */
	public int getMajor() {
		return major;
	}

	/**
	 * Gets the current minor version of The Flounder Engine.
	 *
	 * @return The current minor version.
	 */
	public int getMinor() {
		return minor;
	}
}
