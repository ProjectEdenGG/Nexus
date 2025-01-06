package gg.projecteden.nexus.features.legacy.listeners;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.legacy.Legacy;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class LegacyMisc implements Listener {

	@EventHandler
	public void on(PlayerDropItemEvent event) {
		final Player player = event.getPlayer();
		if (WorldGroup.of(player) != WorldGroup.LEGACY)
			return;

		event.setCancelled(true);
		PlayerUtils.send(player, Legacy.PREFIX + "&c&lHey! &7You cannot drop items here");
	}

	private static final List<Material> NO_INTERACT = List.of(
		Material.LOOM,
		Material.ANVIL,
		Material.GRINDSTONE,
		Material.STONECUTTER,
		Material.CRAFTING_TABLE,
		Material.SMITHING_TABLE,
		Material.ENCHANTING_TABLE,
		Material.CARTOGRAPHY_TABLE
	);

	@EventHandler
	public void on(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (WorldGroup.of(player) != WorldGroup.LEGACY)
			return;

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		final Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block))
			return;

		if (!NO_INTERACT.contains(block.getType()))
			return;

		event.setCancelled(true);

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
