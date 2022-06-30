package gg.projecteden.nexus.features.legacy.listeners;

import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static gg.projecteden.nexus.features.legacy.Legacy.PREFIX;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class Misc implements Listener {

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (WorldGroup.of(event.getPlayer()) != WorldGroup.LEGACY)
			return;

		if (!(event.getRightClicked() instanceof ItemFrame itemFrame))
			return;

		final ItemStack item = itemFrame.getItem();
		if (isNullOrAir(item))
			return;

		if (item.getType() != Material.WRITTEN_BOOK && item.getType() != Material.WRITABLE_BOOK)
			return;

		event.getPlayer().openBook(item);
	}

	@EventHandler
	public void on(PlayerDropItemEvent event) {
		final Player player = event.getPlayer();
		if (WorldGroup.of(player) != WorldGroup.LEGACY)
			return;

		event.setCancelled(true);
		PlayerUtils.send(player, PREFIX + "&c&lHey! &7You cannot drop items here");
	}

	private static final List<Material> NO_INTERACT = List.of(
		Material.CRAFTING_TABLE,
		Material.STONECUTTER,
		Material.LOOM,
		Material.ANVIL,
		Material.SMITHING_TABLE,
		Material.CARTOGRAPHY_TABLE,
		Material.GRINDSTONE
	);

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		final Block block = event.getClickedBlock();
		if (isNullOrAir(block))
			return;

		if (NO_INTERACT.contains(block.getType()))
			return;

		event.setCancelled(true);

		final Player player = event.getPlayer();
		if (!new CooldownService().check(player, "legacy_no_interact", TickTime.SECOND.x(3)))
			return;

		PlayerUtils.send(player, "&c&lHey! &7You cannot interact with that in the legacy world");
	}

	@EventHandler
	public void on(FurnaceBurnEvent event) {
		if (WorldGroup.of(event.getBlock()) != WorldGroup.LEGACY)
			return;

		event.setCancelled(true);
	}

}
