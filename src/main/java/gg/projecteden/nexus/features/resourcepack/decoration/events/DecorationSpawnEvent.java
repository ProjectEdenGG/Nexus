package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


@Setter
@Getter
public class DecorationSpawnEvent extends DecorationEvent {

	ItemStack itemStack;

	public DecorationSpawnEvent(Player player, Decoration decoration, ItemStack itemStack) {
		super(player, decoration);
		this.itemStack = itemStack;
	}
}
