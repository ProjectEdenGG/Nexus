package gg.projecteden.nexus.framework.interfaces;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an object that can be described
 */
public interface HasDescription {
	/**
	 * Gets the description of this object
	 * @return a human-readable string
	 */
	@NotNull String getDescription();
}
