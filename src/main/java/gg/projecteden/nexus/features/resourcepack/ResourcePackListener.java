package gg.projecteden.nexus.features.resourcepack;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Tasks;
import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResourcePackListener implements Listener {
	private static final Map<UUID, Map<Status, LocalDateTime>> statusUpdateTimes = new HashMap<>();

	public ResourcePackListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Tasks.wait(TickTime.SECOND.x(2), () -> {
			ResourcePack.send(player);

			// Try Again if failed
			Tasks.wait(TickTime.SECOND.x(5), () -> {
				if (Status.FAILED_DOWNLOAD == player.getResourcePackStatus())
					ResourcePack.send(player);
			});
		});
	}

	@EventHandler
	public void on(BlockPlaceEvent event) {
		final CustomMaterial customMaterial = CustomMaterial.of(event.getItemInHand());
		if (customMaterial != null && customMaterial.canBePlaced())
			return;

		if (ResourcePack.isCustomItem(event.getItemInHand()))
			event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof ItemFrame)
			return;

		if (ResourcePack.isCustomItem(ItemUtils.getTool(event.getPlayer())))
			event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerFlowerPotManipulateEvent event) {
		if (ResourcePack.isCustomItem(event.getItem()))
			event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		if (!Rank.of(event.getPlayer()).isAdmin())
			return;

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		final ItemStack item = event.getPlayer().getInventory().getItem(EquipmentSlot.HAND);
		if (ModelId.of(item) == 0)
			return;

		if (!(event.getRightClicked() instanceof ArmorStand armorStand))
			return;

		final ItemStack existing = armorStand.getItem(EquipmentSlot.HEAD);
		armorStand.setItem(EquipmentSlot.HEAD, item);
		event.getPlayer().getInventory().setItem(EquipmentSlot.HAND, existing);
	}

	@EventHandler
	public void onResourcePackEvent(PlayerResourcePackStatusEvent event) {
		Nexus.debug("Resource Pack Status Update: " + event.getPlayer().getName() + " = " + event.getStatus());

		final var statuses = statusUpdateTimes.computeIfAbsent(event.getPlayer().getUniqueId(), $ -> new HashMap<>());
		statuses.put(event.getStatus(), LocalDateTime.now());
		if (event.getStatus() == Status.SUCCESSFULLY_LOADED)
			if (statuses.get(Status.SUCCESSFULLY_LOADED).isBefore(statuses.get(Status.ACCEPTED).plus(500, ChronoUnit.MILLIS)))
				Tasks.wait(1, () -> ResourcePack.send(event.getPlayer()));
	}

}
