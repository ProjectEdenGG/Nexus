package gg.projecteden.nexus.models.customblock;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
public class CustomTripwireData extends ExtraBlockData {
	@NonNull
	private BlockFace facing;

	public CustomTripwireData(@NotNull BlockFace facing) {
		this.facing = facing;
	}
}
