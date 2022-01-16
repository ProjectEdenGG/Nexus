package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DisabledPlacement;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat;
import lombok.Getter;
import org.bukkit.Material;

import java.util.List;

public class Chair extends Dyeable implements Seat {
	@Getter
	private final DyedPart dyedPart;

	public Chair(String name, int modelData, DyedPart dyedPart) {
		this(name, modelData, dyedPart, Hitbox.single(Material.BARRIER));
	}

	public Chair(String name, int modelData, DyedPart dyedPart, List<Hitbox> hitboxes) {
		super(name, modelData, dyedPart.getDefaultColor());
		this.hitboxes = hitboxes;
		this.dyedPart = dyedPart;
		this.disabledPlacements = List.of(DisabledPlacement.WALL, DisabledPlacement.CEILING);
	}
}
