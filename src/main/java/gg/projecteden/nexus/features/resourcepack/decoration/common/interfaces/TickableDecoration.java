package gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.Decorations;
import org.bukkit.Location;

public interface TickableDecoration {
	void tick(Location location);

	default boolean shouldTick() {
		return !Decorations.isServerReloading();
	}
}
