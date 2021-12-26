package gg.projecteden.nexus.features.resourcepack.decoration.common;

import org.bukkit.Material;

public class Chair extends Seat {

	public Chair(String name, int modelData) {
		super(name, modelData, Hitbox.single(Material.BARRIER));
	}
}
