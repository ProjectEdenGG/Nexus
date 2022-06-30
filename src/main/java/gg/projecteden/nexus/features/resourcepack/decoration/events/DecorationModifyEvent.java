package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class DecorationModifyEvent extends DecorationInteractEvent {
	private final ItemStack tool;

	public DecorationModifyEvent(Player player, Decoration decoration, ItemStack tool) {
		super(player, decoration);
		this.tool = tool;
	}

}
