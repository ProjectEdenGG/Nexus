package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import org.bukkit.Material;

public class Chair extends Seat {
	public Chair(String name, int modelData, DyedPart dyedPart) {
		super(name, modelData, dyedPart, Hitbox.single(Material.BARRIER));
	}
}
