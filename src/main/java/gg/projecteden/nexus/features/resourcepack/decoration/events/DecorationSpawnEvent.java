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
	boolean skull;

	public DecorationSpawnEvent(Player player, Decoration decoration, ItemStack itemStack) {
		this(player, decoration, itemStack, false);
	}

	public DecorationSpawnEvent(Player player, Decoration decoration, ItemStack itemStack, boolean isSkull) {
		super(player, decoration);
		this.itemStack = itemStack;
		this.skull = isSkull;
	}
}
