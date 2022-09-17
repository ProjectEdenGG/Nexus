package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Material;

public class RotatableBlock extends FloorThing {
	public RotatableBlock(String name, CustomMaterial material) {
		super(name, material, Hitbox.single(Material.BARRIER));
		this.rotationType = RotationType.BOTH;
	}
}
