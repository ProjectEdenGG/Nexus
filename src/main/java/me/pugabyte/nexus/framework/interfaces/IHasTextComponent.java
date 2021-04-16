package me.pugabyte.nexus.framework.interfaces;

import net.kyori.adventure.text.TextComponent;

public interface IHasTextComponent {
	/**
	 * Returns a text component that represents this object. May be colored.
	 */
	TextComponent getComponent();
}
