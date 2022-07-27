package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Material;

import java.util.List;

public class Chair extends Dyeable implements Seat, Colorable {
	private final Colorable.Type type;

	public Chair(String name, CustomMaterial material, Colorable.Type type) {
		this(name, material, type, Hitbox.single(Material.BARRIER));
	}

	public Chair(String name, CustomMaterial material, Colorable.Type type, List<Hitbox> hitboxes) {
		super(name, material, type);
		this.hitboxes = hitboxes;
		this.type = type;
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
	}

	@Override
	public Type getType() {
		return this.type;
	}

}
