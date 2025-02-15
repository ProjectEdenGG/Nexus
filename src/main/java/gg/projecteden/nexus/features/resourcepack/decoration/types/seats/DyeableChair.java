package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Seat;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class DyeableChair extends Dyeable implements Seat, Colorable {
	private final ColorableType colorableType;
	private final Double sitHeight;
	private final boolean backless;

	public DyeableChair(boolean multiblock, boolean backless, String name, ItemModelType itemModelType, ColorableType colorableType) {
		this(multiblock, backless, name, itemModelType, colorableType, null);
	}

	public DyeableChair(boolean multiblock, boolean backless, String name, ItemModelType itemModelType, ColorableType colorableType, Double sitHeight) {
		this(multiblock, backless, name, itemModelType, colorableType, HitboxSingle._1x1_BARRIER, sitHeight);
	}

	public DyeableChair(boolean multiblock, boolean backless, String name, ItemModelType itemModelType, ColorableType colorableType, CustomHitbox hitbox, Double sitHeight) {
		this(multiblock, backless, name, itemModelType, colorableType, null, hitbox, sitHeight);
	}

	public DyeableChair(boolean multiblock, boolean backless, String name, ItemModelType itemModelType, ColorableType colorableType, String hexOverride, CustomHitbox hitbox, Double sitHeight) {
		super(multiblock, name, itemModelType, colorableType, hexOverride, hitbox);
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
