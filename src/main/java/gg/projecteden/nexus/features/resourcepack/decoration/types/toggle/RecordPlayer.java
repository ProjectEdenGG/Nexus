package gg.projecteden.nexus.features.resourcepack.decoration.types.toggle;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Cyclable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class RecordPlayer extends DyeableFloorThing implements Cyclable {
	@Getter
	private final RecordPlayerType recordPlayerType;

	public RecordPlayer(String name, RecordPlayerType recordPlayerType) {
		super(false, name, recordPlayerType.getItemModelType(), ColorableType.STAIN, HitboxSingle._1x1_BARRIER);
		this.recordPlayerType = recordPlayerType;
	}

	@AllArgsConstructor
	public enum RecordPlayerType {
		OFF(ItemModelType.RECORD_PLAYER_MODERN, ItemModelType.RECORD_PLAYER_MODERN_ON),
		ON(ItemModelType.RECORD_PLAYER_MODERN_ON, ItemModelType.RECORD_PLAYER_MODERN),
		;

		@Getter
		private final ItemModelType itemModelType;
		@Getter
		private final ItemModelType oppositeItemModelType;
	}


	@Override
	public ItemModelType getBaseItemModel() {
		return RecordPlayerType.OFF.getItemModelType();
	}

	@Override
	public ItemModelType getNextItemModel() {
		return recordPlayerType.getOppositeItemModelType();
	}

	@Override
	public ItemModelType getPreviousItemModel() {
		return getNextItemModel();
	}
}
