package me.pugabyte.nexus.framework.interfaces;

import org.jetbrains.annotations.NotNull;

public interface HasDescription {
	/**
	 * Gets the description of this object
	 * @return a human-readable string
	 */
	@NotNull String getDescription();
}
