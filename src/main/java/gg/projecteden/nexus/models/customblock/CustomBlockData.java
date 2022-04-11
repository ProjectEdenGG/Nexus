package gg.projecteden.nexus.models.customblock;

import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.ICustomBlock;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CustomBlockData {
	UUID placerUUID = null;
	@NonNull CustomBlock customBlock;
	@NonNull Instrument blockInstrument;
	int blockStep;

	public CustomBlockData(UUID uuid, @NotNull CustomBlock customBlock) {
		this.placerUUID = uuid;
		this.customBlock = customBlock;
	}

	public boolean exists() {
		return placerUUID != null;
	}

	public ICustomBlock getCustomBlock() {
		return customBlock.get();
	}

	public Note getBlockNote() {
		return new Note(this.getBlockStep());
	}
}
