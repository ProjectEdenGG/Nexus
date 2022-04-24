package gg.projecteden.nexus.models.customblock;

import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock.CustomBlockType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CustomBlockData {
	UUID placerUUID = null;
	int modelId;
	CustomBlockType type;
	ExtraBlockData extraData;

	public CustomBlockData(UUID uuid, int modelId, CustomBlockType type) {
		switch (type) {
			case NOTE_BLOCK -> extraData = new CustomNoteBlockData();
			case TRIPWIRE -> extraData = new CustomTripwireData();
		}

		this.placerUUID = uuid;
		this.modelId = modelId;
	}

	public boolean exists() {
		return this.placerUUID != null && getCustomBlock() != null;
	}

	public CustomBlock getCustomBlock() {
		return CustomBlock.fromModelId(modelId);
	}


}
