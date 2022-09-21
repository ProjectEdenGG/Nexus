package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieConfig;
import gg.projecteden.nexus.utils.MathUtils;
import lombok.Getter;

import java.util.List;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;

public class PlayerPlushie extends DecorationConfig {
	@Getter
	private final Pose pose;

	public PlayerPlushie(Pose pose) {
		this.pose = pose;
		this.id = "player_plushie_" + pose.name().toLowerCase();
		this.name = camelCase(pose) + " Player Plushie";
		this.material = PlayerPlushieConfig.MATERIAL;
		this.modelId = pose.getStartingIndex() + 1;
		this.modelIdPredicate = modelId -> MathUtils.isBetween(modelId, pose.getStartingIndex(), pose.getEndingIndex());
		this.hitboxes = Hitbox.NONE();
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
	}
}
