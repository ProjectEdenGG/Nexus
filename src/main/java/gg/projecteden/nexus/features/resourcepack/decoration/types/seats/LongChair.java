package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox.LightHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;

public class LongChair extends Chair implements Seat, Colorable {
	private final static List<Hitbox> hitboxes = List.of(Hitbox.origin(Material.BARRIER), Hitbox.offset(Material.BARRIER, BlockFace.SOUTH));

	public LongChair(String name, CustomMaterial material, ColorableType colorableType) {
		super(name, material, colorableType, hitboxes, null);
		this.rotationType = RotationType.NONE; // TODO: rotatable light hitboxes
	}

	public LongChair(String name, CustomMaterial material, ColorableType colorableType, Double sitHeight) {
		super(name, material, colorableType, hitboxes, sitHeight);
		this.rotationType = RotationType.NONE;
	}

	public LongChair(String name, CustomMaterial material, ColorableType colorableType, LightHitbox lightHitbox, Double sitHeight) {
		super(name, material, colorableType, light(lightHitbox), sitHeight);
		this.rotationType = RotationType.NONE;
	}

	private static List<Hitbox> light(LightHitbox lightHitbox) {
		return List.of(Hitbox.origin(lightHitbox), Hitbox.offset(lightHitbox, BlockFace.SOUTH));
	}
}
