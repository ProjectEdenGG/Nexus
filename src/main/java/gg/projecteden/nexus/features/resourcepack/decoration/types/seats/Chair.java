package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class Chair extends Dyeable implements Seat, Colorable {
	private final ColorableType colorableType;
	private final Double sitHeight;

	public Chair(String name, CustomMaterial material, ColorableType colorableType) {
		this(name, material, colorableType, null);
	}

	public Chair(String name, CustomMaterial material, ColorableType colorableType, Double sitHeight) {
		this(name, material, colorableType, HitboxSingle._1x1, sitHeight);
	}

	public Chair(String name, CustomMaterial material, ColorableType colorableType, CustomHitbox hitbox, Double sitHeight) {
		this(name, material, colorableType, null, hitbox, sitHeight);
	}

	public Chair(String name, CustomMaterial material, ColorableType colorableType, String hexOverride, CustomHitbox hitbox, Double sitHeight) {
		super(name, material, colorableType, hexOverride, hitbox);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
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
