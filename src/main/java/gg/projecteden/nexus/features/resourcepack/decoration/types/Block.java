package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import org.bukkit.Material;

public class Block extends DecorationConfig {
	public Block(String name, int modelData, Material material) {
		super(name, modelData, material, Hitbox.single(Material.BARRIER));
		this.rotationType = RotationType.DEGREE_90;
	}
}
