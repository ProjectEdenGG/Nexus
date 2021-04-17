package me.pugabyte.nexus.framework.interfaces;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public interface Named {
	/**
	 * Returns a name that represents this object.
	 * @return human-readable string
	 */
	@NotNull String getName();

	/**
	 * Returns a text component that represents this object. May be colored.
	 * @return an adventure text component
	 */
	default @NotNull TextComponent getComponent() {
		return Component.text(getName());
	}
}
