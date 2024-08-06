package gg.projecteden.nexus.features.events.y2024.pugmas24.items;

import org.bukkit.Location;

public class Pugmas24ItemsManager {

	public Pugmas24ItemsManager() {
		new Pugmas24ItemsListener();
	}

	public static Location getCompassLocation(Location location) {
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();

		return new Location(location.getWorld(), x + 688, y - 8, z + 2965);
	}


}
