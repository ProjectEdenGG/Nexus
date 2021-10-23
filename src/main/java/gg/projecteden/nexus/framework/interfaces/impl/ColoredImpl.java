package gg.projecteden.nexus.framework.interfaces.impl;

import gg.projecteden.nexus.framework.interfaces.Colored;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

@AllArgsConstructor
public class ColoredImpl implements Colored {
	@Getter @Accessors(fluent = true)
	private final int value;

	@Override
	public @NotNull Color getColor() {
		return new Color(value);
	}
}
