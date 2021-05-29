package me.pugabyte.nexus.framework.interfaces;

import eden.interfaces.Nicknamed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public interface IsColoredAndNicknamed extends IsColoredAndNamed, Nicknamed {
	/**
	 * Returns this object's nickname with a chat color prefixed.
	 */
	default @NotNull String getColoredName() {
		return colored().getChatColor() + getNickname();
	}

	/**
	 * Returns this object's nickname with a vanilla chat color per {@link Colored#getVanillaChatColor()} prefixed.
	 */
	default @NotNull String getVanillaColoredName() {
		return colored().getVanillaChatColor() + getNickname();
	}

	/**
	 * Returns a component corresponding with this object. This implementation uses the object's nickname and color.
	 * @return an adventure text component
	 */
	@Override
	default @NotNull TextComponent asComponent() {
		return Component.text(getNickname(), colored());
	}
}
