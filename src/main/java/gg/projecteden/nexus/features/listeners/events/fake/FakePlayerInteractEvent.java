package gg.projecteden.nexus.features.listeners.events.fake;

import com.gmail.nossr50.events.fake.FakeEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Extension of {@link PlayerInteractEvent} used to test if a plugin like WorldGuard or LWC will block the event.
 */
public class FakePlayerInteractEvent extends PlayerInteractEvent implements FakeEvent {
	public FakePlayerInteractEvent(Player player, Action action, ItemStack itemInHand, Block clickedBlock, BlockFace blockFace) {
		super(player, action, itemInHand, clickedBlock, blockFace);
	}

}
