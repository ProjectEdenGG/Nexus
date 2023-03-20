package gg.projecteden.nexus.features.vanish;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.chat.Chat.Broadcast.BroadcastBuilder;
import gg.projecteden.nexus.features.listeners.Tab.Presence;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.vanish.events.UnvanishEvent;
import gg.projecteden.nexus.features.vanish.events.VanishEvent;
import gg.projecteden.nexus.features.vanish.events.VanishStateChangedEvent;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.vanish.VanishUser;
import gg.projecteden.nexus.models.vanish.VanishUser.Setting;
import gg.projecteden.nexus.models.vanish.VanishUserService;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
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
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class VanishListener implements Listener {
	private static final VanishUserService service = new VanishUserService();
	private static final NerdService nerdService = new NerdService();

	public VanishListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(VanishEvent event) {
		nerdService.edit(event.getPlayer(), nerd -> nerd.setLastVanish(LocalDateTime.now()));
	}

	@EventHandler
	public void on(UnvanishEvent event) {
		nerdService.edit(event.getPlayer(), nerd -> nerd.setLastUnvanish(LocalDateTime.now()));
	}

	@EventHandler
	public void on(VanishStateChangedEvent event) {
		final UUID uuid = event.getPlayer().getUniqueId();
		Consumer<Function<BroadcastBuilder, BroadcastBuilder>> broadcastSelf = builder ->
			builder.apply(Broadcast.staffIngame().hideFromConsole(true).include(uuid)).send();

		Consumer<Function<BroadcastBuilder, BroadcastBuilder>> broadcastOthers = builder ->
			builder.apply(Broadcast.staffIngame().hideFromConsole(true).exclude(uuid)).send();

		Tasks.wait(1, () -> {
			Player player = Bukkit.getPlayer(uuid);
			if (player == null || !player.isOnline())
				return;

			final String presence = "&f" + Presence.of(player).getCharacter() + " ";

			if (event.getUser().isVanished()) {
				broadcastSelf.accept(builder -> builder.message(presence + "&7You vanished"));
				broadcastOthers.accept(builder -> builder.message(presence + "&e" + event.getUser().getNickname() + " &7vanished"));
			} else {
				broadcastSelf.accept(builder -> builder.message(presence + "&7You unvanished"));
				broadcastOthers.accept(builder -> builder.message(presence + "&e" + event.getUser().getNickname() + " &7unvanished"));
			}
		});
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		if (!Rank.of(player).isSeniorStaff()) {
			service.edit(player, VanishUser::unvanish);
			return;
		}

		Vanish.vanish(player);
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
			return "View only " + type.name().toLowerCase();
		}

		@Override
		public void init() {
			int index = 0;
			for (ItemStack content : original.getContents())
				contents.set(index++, ClickableItem.empty(content));
		}
	}

}
