package me.pugabyte.nexus.framework.interfaces;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public interface ColoredAndNamed extends Named, Colored {
	default @NotNull String getColoredName() {
		return getChatColor() + getName();
	}

	@Override
	default @NotNull TextComponent getComponent() {
		return Component.text(getName(), getTextColor());
	}
}
