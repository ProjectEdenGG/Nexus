package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Seat;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class Chair extends Dyeable implements Seat, Colorable {
	private final ColorableType colorableType;
	private final Double sitHeight;
	private final boolean backless;

	public Chair(boolean multiblock, boolean backless, String name, CustomMaterial material, ColorableType colorableType) {
		this(multiblock, backless, name, material, colorableType, null);
	}

	public Chair(boolean multiblock, boolean backless, String name, CustomMaterial material, ColorableType colorableType, Double sitHeight) {
		this(multiblock, backless, name, material, colorableType, HitboxSingle._1x1, sitHeight);
	}

	public Chair(boolean multiblock, boolean backless, String name, CustomMaterial material, ColorableType colorableType, CustomHitbox hitbox, Double sitHeight) {
		this(multiblock, backless, name, material, colorableType, null, hitbox, sitHeight);
	}

	public Chair(boolean multiblock, boolean backless, String name, CustomMaterial material, ColorableType colorableType, String hexOverride, CustomHitbox hitbox, Double sitHeight) {
		super(multiblock, name, material, colorableType, hexOverride, hitbox);
		this.disabledPlacements = PlacementType.FLOOR.getDisabledPlacements();
		this.colorableType = colorableType;
		this.sitHeight = sitHeight;
		this.backless = backless;
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

	public boolean isBackless() {
		return backless;
	}
}
