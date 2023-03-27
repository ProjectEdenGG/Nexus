package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class Table extends Dyeable implements Colorable {
	boolean multiBlock;

	public Table(String name, CustomMaterial material, CustomHitbox hitbox) {
		this(name, material, hitbox, false);
	}

	public Table(String name, CustomMaterial material, CustomHitbox hitbox, boolean multiBlock) {
		super(name, material, ColorableType.STAIN, hitbox);
		this.multiBlock = multiBlock;
		this.rotationType = RotationType.BOTH;
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);

		if (multiBlock) {
			this.rotationType = RotationType.DEGREE_90;
			this.rotatable = false;
		}
	}

	@Override
	public boolean isMultiBlock() {
		return this.multiBlock;
	}
}
