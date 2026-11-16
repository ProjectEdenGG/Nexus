package gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.Decorations;
import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;

public interface TickableDecoration {
	void tick(Location location, ItemFrame itemFrame);

	default boolean shouldTick() {
		return !Decorations.isServerReloading();
	}
}
