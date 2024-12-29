package gg.projecteden.nexus.features.vanish;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.chat.Chat.Broadcast.BroadcastBuilder;
import gg.projecteden.nexus.features.listeners.Tab.Presence;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.vanish.events.VanishToggleEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.vanish.VanishUser;
import gg.projecteden.nexus.models.vanish.VanishUser.Setting;
import gg.projecteden.nexus.models.vanish.VanishUserService;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.parchment.event.sound.SoundEvent;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockReceiveGameEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public class VanishListener implements Listener {
	private static final VanishUserService service = new VanishUserService();

	public VanishListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(VanishToggleEvent event) {
		final Player player = event.getPlayer();
		if (System.currentTimeMillis() - player.getLastLogin() < 500)
			return;

		Tasks.wait(1, () -> {
			if (!player.isOnline())
				return;

			final String presence = "&f" + Presence.of(player).getCharacter() + " ";
			final Supplier<BroadcastBuilder> broadcast = () -> Broadcast.staffIngame().hideFromConsole(true);

			if (event.getUser().isVanished()) {
				service.edit(player, user -> user.setLastVanish(LocalDateTime.now()));
				broadcast.get().include(player).message(presence + "&7You vanished").send();
				broadcast.get().exclude(player).message(presence + "&e" + event.getUser().getNickname() + " &7vanished").send();
			} else {
				service.edit(player, user -> user.setLastUnvanish(LocalDateTime.now()));
				broadcast.get().include(player).message(presence + "&7You unvanished").send();
				broadcast.get().exclude(player).message(presence + "&e" + event.getUser().getNickname() + " &7unvanished").send();
			}
		});
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		if (Rank.of(player).isSeniorStaff())
			Vanish.vanish(player);
		else
			service.edit(player, VanishUser::unvanish);

		Vanish.refreshAll();
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		service.edit(event.getPlayer(), VanishUser::unvanish);
	}

	@EventHandler
	public void on(PlayerChangedWorldEvent event) {
		Vanish.refreshAll();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void on(PaperServerListPingEvent event) {
		event.getPlayerSample().removeIf(profile -> profile.getId() != null && new VanishUserService().get(profile.getId()).isVanished());
		event.setNumPlayers(OnlinePlayers.where(player -> !Vanish.isVanished(player)).count());
	}

	@EventHandler
	public void on(SoundEvent event) {
		if (!(event.getException() instanceof Player player))
			return;

		if (!Vanish.isVanished(player))
			return;

		event.setCancelled(true);
	}

	private static void handle(Cancellable event, Player player, String action) {
		final VanishUser user = service.get(player);

		if (!user.isVanished())
			return;

		if (user.getSetting(Setting.INTERACT))
			return;

		user.notifyDisabled(Setting.INTERACT, action);
		event.setCancelled(true);
	}

	@EventHandler
	public void on(BlockPlaceEvent event) {
		handle(event, event.getPlayer(), "Building");
	}

	@EventHandler
	public void on(BlockBreakEvent event) {
		handle(event, event.getPlayer(), "Building");
	}

	@EventHandler
	public void on(EntityPickupItemEvent event) {
		if (event.getEntity() instanceof Player player)
			handle(event, player, "Picking up items");
	}

	@EventHandler
	public void on(PlayerPickupArrowEvent event) {
		handle(event, event.getPlayer(), "Picking up items");
	}

	@EventHandler
	public void on(PlayerDropItemEvent event) {
		handle(event, event.getPlayer(), "Dropping items");
	}

	private static final MaterialTag CHESTS = MaterialTag.CHESTS.append(MaterialTag.SHULKER_BOXES);

	@EventHandler
	public void on(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final VanishUser user = service.get(player);

		if (!user.isVanished())
			return;

		if (user.getSetting(Setting.INTERACT))
			return;

		if (event.getAction() == Action.LEFT_CLICK_AIR)
			return;

		final Block block = event.getClickedBlock();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null) {
			if (block.getState() instanceof InventoryHolder inventoryHolder) {
				if (block.getType() == Material.ENDER_CHEST) {
					player.openInventory(player.getEnderChest());
					event.setCancelled(true);
					return;
				}

				if (CHESTS.isTagged(block)) {
					new VanishInventory(block.getType(), inventoryHolder.getInventory()).open(player);
					event.setCancelled(true);
					user.notifyDisabled(Setting.INTERACT, "Editing chests");
					return;
				}
			}
		}

		event.setCancelled(true);
		user.notifyDisabled(Setting.INTERACT, "Interacting");
	}

	@AllArgsConstructor
	private static class VanishInventory extends InventoryProvider {
		private Material type;
		private Inventory original;

		@Override
		protected int getRows(Integer page) {
			return original.getSize() / 9;
		}

		@Override
		public String getTitle() {
			return "View only " + StringUtils.camelCase(type);
		}

		@Override
		public void init() {
			int index = 0;
			for (ItemStack content : original.getContents())
				contents.set(index++, ClickableItem.empty(content));
		}
	}

	@EventHandler
	public void on(BlockReceiveGameEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		final VanishUser user = service.get(player);
		if (!user.isVanished())
			return;

		if (user.getSetting(Setting.INTERACT))
			return;

		event.setCancelled(true);
	}

}
