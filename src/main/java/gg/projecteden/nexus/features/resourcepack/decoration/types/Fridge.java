package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;

public class Fridge extends DyeableFloorThing {
	@Getter
	private final FridgeSize size;

	public Fridge(String name, CustomMaterial material, FridgeSize size) {
		super(name, material, ColorableType.DYE, "FFFFFF");
		this.size = size;
		this.hitboxes = size.getHitboxes();
	}

	@AllArgsConstructor
	public enum FridgeSize {
		MINI(
			Hitbox.single(Material.BARRIER)),
		STANDARD(List.of(
			Hitbox.origin(Material.BARRIER),
			Hitbox.offset(Material.BARRIER, BlockFace.UP, 1))),
		TALL(List.of(
			Hitbox.origin(Material.BARRIER),
			Hitbox.offset(Material.BARRIER, BlockFace.UP, 1),
			Hitbox.offset(Material.BARRIER, BlockFace.UP, 2))),
		;

		@Getter
		final List<Hitbox> hitboxes;
	}
}
