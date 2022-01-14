package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DisabledPlacement;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat;
import lombok.Getter;
import org.bukkit.Material;

import java.util.List;

public class Chair extends Decoration implements Seat {
	@Getter
	private final DyedPart dyedPart;

	public Chair(String name, int modelData, DyedPart dyedPart) {
		this(name, modelData, dyedPart, Hitbox.single(Material.BARRIER));
	}

	public Chair(String name, int modelData, DyedPart dyedPart, List<Hitbox> hitboxes) {
		super(name, modelData, Material.LEATHER_HORSE_ARMOR, hitboxes);
		this.dyedPart = dyedPart;
		this.defaultColor = dyedPart.getDefaultColor();
		this.disabledPlacements = List.of(DisabledPlacement.WALL, DisabledPlacement.CEILING);
	}
}
