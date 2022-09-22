package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox.LightHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;

@MultiBlock
public class LongChair extends Chair implements Seat, Colorable {
	private final static List<Hitbox> hitboxes = List.of(Hitbox.origin(Material.BARRIER), Hitbox.offset(Material.BARRIER, BlockFace.SOUTH));

	public LongChair(String name, CustomMaterial material, ColorableType colorableType, LightHitbox lightHitbox, Double sitHeight) {
		super(name, material, colorableType, light(lightHitbox), sitHeight);
	}

	private static List<Hitbox> light(LightHitbox lightHitbox) {
		return List.of(Hitbox.origin(lightHitbox), Hitbox.offset(lightHitbox, BlockFace.SOUTH));
	}
}
