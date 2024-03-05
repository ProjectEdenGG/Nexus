package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class DecorationModifyEvent extends DecorationInteractEvent {
	private final ItemStack tool;

	public DecorationModifyEvent(Player player, Block block, Decoration decoration, ItemStack tool) {
		super(player, block, decoration, InteractType.RIGHT_CLICK);
		this.tool = tool;
	}

}
