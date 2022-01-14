package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DisabledRotation;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import org.bukkit.Material;

public class BlockDecor extends Decoration {
	public BlockDecor(String name, int modelData, Material material) {
		super(name, modelData, material, Hitbox.single(Material.BARRIER));
		this.disabledRotation = DisabledRotation.DEGREE_45;
	}
}
