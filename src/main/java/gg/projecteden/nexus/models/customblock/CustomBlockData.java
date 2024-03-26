package gg.projecteden.nexus.models.customblock;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock.CustomBlockType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CustomBlockData {
	private UUID placerUUID = null;
	private int modelId;
	private CustomBlockType type;
	private ExtraBlockData extraData;

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
		return CustomBlock.from(modelId);
	}


}
