package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Seat;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class Chair extends DecorationConfig implements Seat {
	private final Double sitHeight;
	private final boolean backless;

	public Chair(boolean multiblock, boolean backless, String name, CustomMaterial material) {
		this(multiblock, backless, name, material, null);
	}

	public Chair(boolean multiblock, boolean backless, String name, CustomMaterial material, Double sitHeight) {
		this(multiblock, backless, name, material, HitboxSingle._1x1_BARRIER, sitHeight);
	}

	public Chair(boolean multiblock, boolean backless, String name, CustomMaterial material, CustomHitbox hitbox, Double sitHeight) {
		super(multiblock, name, material, hitbox);
		this.disabledPlacements = PlacementType.FLOOR.getDisabledPlacements();
		this.sitHeight = sitHeight;
		this.backless = backless;
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
