package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class DecorationPaintEvent extends DecorationModifyEvent {
	ItemFrame itemFrame;
	Color color;

	public DecorationPaintEvent(Player player, Block block, Decoration decoration, ItemStack tool, ItemFrame itemFrame, Color color) {
		super(player, block, decoration, tool);
		this.itemFrame = itemFrame;
		this.color = color;
	}
}
