package me.pugabyte.nexus.features.resourcepack;

import de.tr7zw.nbtapi.NBTItem;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.PlayerUtils.Dev;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
public class ResourcePack extends Feature implements Listener {

	public static boolean isCustomItem(ItemStack item) {
		return new NBTItem(item).hasKey("CustomModelData");
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (isCustomItem(event.getItemInHand()))
			event.setCancelled(true);
	}

	public static boolean isEnabledFor(Player player) {
		return player.getResourcePackStatus() == Status.SUCCESSFULLY_LOADED || Dev.WAKKA.is(player);
	}
}
