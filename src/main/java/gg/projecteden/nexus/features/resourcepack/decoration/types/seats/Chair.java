package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Material;

import java.util.List;

public class Chair extends Dyeable implements Seat, Colorable {
	private final Colorable.Type type;
	private Double sitHeight;

	public Chair(String name, CustomMaterial material, Colorable.Type type) {
		this(name, material, type, Hitbox.single(Material.BARRIER), null);
	}

	public Chair(String name, CustomMaterial material, Colorable.Type type, Double sitHeight) {
		this(name, material, type, Hitbox.single(Material.BARRIER), sitHeight);
	}

	public Chair(String name, CustomMaterial material, Colorable.Type type, List<Hitbox> hitboxes, Double sitHeight) {
		super(name, material, type);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
		this.hitboxes = hitboxes;
		this.type = type;
		this.sitHeight = sitHeight;
	}

	@Override
	public Type getType() {
		return this.type;
	}

	@Override
	public double getSitHeight() {
		if (sitHeight == null)
			return Seat.super.getSitHeight();

		return sitHeight;
	}
}
