package gg.projecteden.nexus.framework.interfaces.impl;

import gg.projecteden.nexus.framework.interfaces.Colored;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@AllArgsConstructor
public class ColoredImpl implements Colored {
	@Getter
	private final int value;

	@Override
	public @NotNull Color getColor() {
		return new Color(value);
	}
}
