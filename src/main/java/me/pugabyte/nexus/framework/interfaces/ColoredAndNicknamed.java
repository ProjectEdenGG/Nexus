package me.pugabyte.nexus.framework.interfaces;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public interface ColoredAndNicknamed extends ColoredAndNamed, Nicknamed {
	/**
	 * Returns this object's nickname with a chat color prefixed.
	 */
	default @NotNull String getColoredName() {
		return getChatColor() + getNickname();
	}

	/**
	 * Returns a component corresponding with this object. Uses the object's nickname by default. May include colors.
	 * @return an adventure text component
	 */
	@Override
	default @NotNull TextComponent getComponent() {
		return Component.text(getNickname(), getTextColor());
	}
}
