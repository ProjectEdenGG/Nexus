package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationTagType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.models.trophy.TrophyType;
import lombok.Getter;

import java.util.List;

public class Trophy extends DecorationConfig {
	@Getter
	private final TrophyType trophy;

	public Trophy(TrophyType trophy) {
		this.trophy = trophy;
		this.id = "trophy_" + trophy.name().toLowerCase();
		this.name = trophy.toString();
		this.material = trophy.getMaterial().getMaterial();
		this.modelId = trophy.getMaterial().getModelId();
		this.hitboxes = Hitbox.NONE();
		this.disabledPlacements = PlacementType.FLOOR.getDisabledPlacements();

		DecorationTagType.setLore(List.of("&7" + trophy), this);
	}

}
