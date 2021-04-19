package me.pugabyte.nexus.framework.interfaces;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public interface ColoredAndNamed extends Named, Colored {
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
	 * Returns a component corresponding with this object. Uses the object's name and {@link #getTextColor()}.
	 * @return an adventure text component
	 */
	@Override
	default @NotNull TextComponent getComponent() {
		return Component.text(getName(), getTextColor());
	}
}
