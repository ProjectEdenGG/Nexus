package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Material;

public class Stump extends Chair {

	public Stump(String name, CustomMaterial material) {
		super(name, material, Type.NONE, Hitbox.single(Material.FLOWER_POT));
	}

}
