package gg.projecteden.nexus.features.resourcepack.decoration.types.toggle;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Toggleable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class RecordPlayer extends DyeableFloorThing implements Toggleable {
	@Getter
	private final RecordPlayerType recordPlayerType;

	public RecordPlayer(String name, RecordPlayerType recordPlayerType) {
		super(false, name, recordPlayerType.getMaterial(), ColorableType.STAIN, HitboxSingle._1x1_BARRIER);
		this.recordPlayerType = recordPlayerType;
	}

	@AllArgsConstructor
	public enum RecordPlayerType {
		OFF(CustomMaterial.RECORD_PLAYER_MODERN, CustomMaterial.RECORD_PLAYER_MODERN_ON),
		ON(CustomMaterial.RECORD_PLAYER_MODERN_ON, CustomMaterial.RECORD_PLAYER_MODERN),
		;

		@Getter
		private final CustomMaterial material;
		@Getter
		private final CustomMaterial oppositeMaterial;
	}


	@Override
	public CustomMaterial getBaseMaterial() {
		return RecordPlayerType.OFF.getMaterial();
	}

	@Override
	public CustomMaterial getToggledMaterial() {
		return recordPlayerType.getOppositeMaterial();
	}
}
