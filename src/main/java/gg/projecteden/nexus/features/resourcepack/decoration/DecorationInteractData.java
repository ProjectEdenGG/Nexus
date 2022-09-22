package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils.debug;

@Data
public class DecorationInteractData {
	private static final int MAX_RADIUS = 4; // Since model max size = 3x3x3 blocks, 4 should be enough
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
			ItemFrame itemFrame = DecorationUtils.getItemFrame(block, MAX_RADIUS, player);
			ItemStack item;
			if (itemFrame == null) {
				// Clientside Entities
				ClientSideItemFrame clientSideItemFrame = DecorationUtils.getClientsideItemFrame(block, MAX_RADIUS, player);
				if (clientSideItemFrame == null)
					return;

				final DecorationConfig config = DecorationConfig.of(clientSideItemFrame.content());
				if (config != null) {
					Rotation rotation = Rotation.values()[clientSideItemFrame.rotation()];
					this.decoration = new Decoration(config, null, rotation);
				}
			} else {
				item = itemFrame.getItem();
				if (Nullables.isNullOrAir(item))
					return;

				final DecorationConfig config = DecorationConfig.of(item);
				if (config != null)
					this.decoration = new Decoration(config, itemFrame);
			}
		}
	}

	public boolean interact(InteractType type) {
		debug(player, "interacting...");
		return decoration.interact(player, block, type);
	}

	public boolean destroy() {
		debug(player, "destroying...");
		return decoration.destroy(player);
	}

	public boolean place() {
		debug(player, "placing...");
		boolean placed = decoration.getConfig().place(player, block, blockFace, tool);
		if (!placed) {
			debug(player, "failed to place decoration");
			player.swingMainHand();
		}

		return placed;
	}

	boolean validate() {
		return decoration != null && decoration.getBukkitRotation() != null;
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
