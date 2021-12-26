package gg.projecteden.nexus.features.resourcepack.decoration.common;

import org.bukkit.Material;

public class Chair extends Seat {

	public Chair(String name, int modelData) {
		this.name = name;
		this.modelData = modelData;
		this.material = Material.LEATHER_HORSE_ARMOR;
		this.hitboxes = Hitbox.single(Material.BARRIER);
	}
}
