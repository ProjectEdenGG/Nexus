package me.pugabyte.nexus.framework.interfaces;

import org.jetbrains.annotations.NotNull;

public interface Nicknamed extends Named {
	/**
	 * Returns a name that represents this object.
	 * @deprecated For display purposes, use {@link #getNickname()} instead.
	 */
	@Override
	@Deprecated
	@NotNull String getName();

	/**
	 * Returns the nickname or name for this object (presumably a player)
	 */
	@NotNull String getNickname();
}
