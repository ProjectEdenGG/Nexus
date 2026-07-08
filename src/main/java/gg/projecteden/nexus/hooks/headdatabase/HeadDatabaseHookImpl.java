package gg.projecteden.nexus.hooks.headdatabase;

import lombok.Getter;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.arcaniax.hdb.api.PlayerClickHeadEvent;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class HeadDatabaseHookImpl extends HeadDatabaseHook {

	@Getter
	private final static HeadDatabaseAPI headAPI = new HeadDatabaseAPI();

	public ItemStack getItemHead(String id) {
		return headAPI.getItemHead(id);
	}

	public ItemStack getItemHead(Block block) {
		return headAPI.getItemHead(block);
	}

	public String getItemID(ItemStack itemStack) {
		return headAPI.getItemID(itemStack);
	}

	@EventHandler
	public void onPlayerClickHeadDatabase(PlayerClickHeadEvent event) {
		HeadDatabasePlayerClickHeadEvent hookEvent;
		if (event.isEconomy())
			hookEvent = new HeadDatabasePlayerClickHeadEvent(event.getPlayer(), event.getPrice(), event.getHeadID(), event.getEconomyEnum().name(), event.getHead(), event.getCategoryEnum().name());
		else
			hookEvent = new HeadDatabasePlayerClickHeadEvent(event.getPlayer(), event.getHeadID(), event.getHead(), event.getCategoryEnum().name());

		if (!hookEvent.callEvent())
			event.setCancelled(true);
	}

}
