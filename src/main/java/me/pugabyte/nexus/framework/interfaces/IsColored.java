package me.pugabyte.nexus.framework.interfaces;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an object that has a {@link Colored} object.
 */
public interface IsColored {
	/**
	 * Gets the {@link Colored} object associated with this object
	 * @return colored object
	 */
	@NotNull Colored colored();
}
