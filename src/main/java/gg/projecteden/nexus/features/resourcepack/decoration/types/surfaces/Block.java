package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Material;

public class Block extends FloorThing {
	public Block(String name, CustomMaterial material, RotationType rotationType) {
		super(name, material, Hitbox.single(Material.BARRIER));
		this.rotationType = rotationType;
	}
}
