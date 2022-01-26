package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils.debug;

@Builder
@Data
public class HitboxData {
	private Player player;
	private DecorationType decorationType;
	private ItemFrame itemFrame;
	private Block block;
	private BlockFace blockFace;
	private ItemStack tool;


	public void interact() {
		debug(player, "interact");
		decorationType.getDecoration().interact(player, itemFrame, block);
	}

	public void destroy() {
		debug(player, "destroy");
		decorationType.getDecoration().destroy(player, itemFrame);
	}

	public void place() {
		debug(player, "place");
		decorationType.getDecoration().place(player, block, blockFace, tool);
	}

	boolean validateDecoration(ItemStack item) {
		decorationType = decorationType == null ? DecorationType.of(item) : decorationType;
		return decorationType != null;
	}

	boolean validateItemFrame() {
		itemFrame = itemFrame == null ? DecorationUtils.getItemFrame(block, 4, player) : itemFrame;
		return itemFrame != null;
	}

	public @NonNull Location getLocation() {
		if (itemFrame != null)
			return itemFrame.getLocation();
		if (block != null)
			return block.getLocation();

		return player.getLocation();
	}

	public boolean playerCanEdit() {
		return PlayerUtils.canEdit(player, getLocation());
	}
}
