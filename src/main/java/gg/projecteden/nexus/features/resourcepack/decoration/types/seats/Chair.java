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
	private final ColorableType colorableType;
	private final Double sitHeight;

	public Chair(String name, CustomMaterial material, ColorableType colorableType) {
		this(name, material, colorableType, Hitbox.single(Material.BARRIER), null);
	}

	public Chair(String name, CustomMaterial material, ColorableType colorableType, Double sitHeight) {
		this(name, material, colorableType, Hitbox.single(Material.BARRIER), sitHeight);
	}

	public Chair(String name, CustomMaterial material, ColorableType colorableType, List<Hitbox> hitboxes, Double sitHeight) {
		super(name, material, colorableType);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
		this.hitboxes = hitboxes;
		this.colorableType = colorableType;
		this.sitHeight = sitHeight;
	}

	@Override
	public ColorableType getColorableType() {
		return this.colorableType;
	}

	@Override
	public double getSitHeight() {
		if (sitHeight == null)
			return Seat.super.getSitHeight();

		return sitHeight;
	}
}
