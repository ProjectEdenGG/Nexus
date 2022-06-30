package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
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

@Data
public class DecorationInteractData {
	private Player player;
	private Decoration decoration;
	private Block block;
	private BlockFace blockFace;
	private ItemStack tool;

	@Builder
	public DecorationInteractData(Player player, Decoration decoration, Block block, BlockFace blockFace, ItemStack tool) {
		this.player = player;
		this.decoration = decoration;
		this.block = block;
		this.blockFace = blockFace;
		this.tool = tool;

		if (this.decoration == null) {
			ItemFrame itemFrame = DecorationUtils.getItemFrame(block, 4, player);
			if (itemFrame != null) {
				final DecorationType type = DecorationType.of(itemFrame.getItem());
				if (type != null)
					this.decoration = new Decoration(type.getConfig(), itemFrame);
			}
		}
	}

	public void interact() {
		debug(player, "interact");
		decoration.interact(player, block);
	}

	public void destroy() {
		debug(player, "destroy");
		decoration.destroy(player);
	}

	public void place() {
		debug(player, "place");
		decoration.getConfig().place(player, block, blockFace, tool);
	}

	boolean validate() {
		return decoration != null;
	}

	public @NonNull Location getLocation() {
		if (decoration.getItemFrame() != null)
			return decoration.getItemFrame().getLocation();
		if (block != null)
			return block.getLocation();

		return player.getLocation();
	}

	public boolean playerCanEdit() {
		return PlayerUtils.canEdit(player, getLocation());
	}
}
