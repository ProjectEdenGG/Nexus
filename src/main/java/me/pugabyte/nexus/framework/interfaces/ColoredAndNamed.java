package me.pugabyte.nexus.framework.interfaces;

import eden.interfaces.Named;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public interface ColoredAndNamed extends Named, Colored, ComponentLike {
	/**
	 * Returns this object's name with a chat color prefixed.
	 */
	default @NotNull String getColoredName() {
		return getChatColor() + getName();
	}

	/**
	 * Returns this object's name with a vanilla chat color per {@link #getVanillaChatColor()} prefixed.
	 */
	default @NotNull String getVanillaColoredName() {
		return getVanillaChatColor() + getName();
	}

	/**
	 * Returns a component corresponding with this object. The default implementation uses the object's name and color.
	 * @return an adventure text component
	 */
	default @NotNull TextComponent asComponent() {
		return Component.text(getName(), this);
	}
}
