package gg.projecteden.nexus.framework.interfaces;

import gg.projecteden.api.interfaces.Named;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public interface IsColoredAndNamed extends Named, IsColored, ComponentLike {
	/**
	 * Returns this object's name with a chat color prefixed.
	 */
	default @NotNull String getColoredName() {
		return colored().getChatColor() + getName();
	}

	/**
	 * Returns this object's name with a vanilla chat color per {@link Colored#getVanillaChatColor()} prefixed.
	 */
	default @NotNull String getVanillaColoredName() {
		return colored().getVanillaChatColor() + getName();
	}

	/**
	 * Returns a component corresponding with this object. The default implementation uses the object's name and color.
	 * @return an adventure text component
	 */
	default @NotNull TextComponent asComponent() {
		return Component.text(getName(), colored().getBukkitColor());
	}
}
