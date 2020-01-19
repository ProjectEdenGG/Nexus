package me.pugabyte.bncore.features.minigames.lobby;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import de.tr7zw.itemnbtapi.NBTEntity;
import de.tr7zw.itemnbtapi.NBTItem;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.Utils.colorize;

public class Basketball implements Listener {
	@Getter
	static ItemStack basketball = (ItemStack) BNCore.getInstance().getConfig().get("minigames.lobby.basketball.item");
	static Map<UUID, ItemStack> basketballs = new HashMap<>();
	static World world = Minigames.getGameworld();
	static String region = "minigamelobby_basketball";

	public Basketball() {
		BNCore.registerListener(this);
		new Basketball.BasketballJanitor();
	}

	public static ItemStack getBasketball(Player player) {
		if (!basketballs.containsKey(player.getUniqueId())) {
			ItemStack basketball = getBasketball().clone();
			NBTItem nbtItem = new NBTItem(basketball);
			nbtItem.getCompound("SkullOwner").setString("Id", player.getUniqueId().toString());
			basketballs.put(player.getUniqueId(), nbtItem.getItem());
		}

		return basketballs.get(player.getUniqueId());
	}

	public static void giveBasketball(Player player) {
		player.getInventory().addItem(getBasketball(player));
	}

	private static boolean ownsBasketball(Player player, ItemStack item) {
		return new NBTItem(item).asNBTString().contains(player.getUniqueId().toString());
	}

	private static boolean ownsBasketball(Player player, Entity entity) {
		return new NBTEntity(entity).asNBTString().contains(player.getUniqueId().toString());
	}

	private static boolean isBasketball(ItemStack itemStack) {
		if (itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().size() == 0)
			return false;
		return itemStack.getItemMeta().getLore().get(0).contains("Minigame Lobby Basketball");
	}

	private static boolean isBasketball(Entity entity) {
		return entity instanceof Item && new NBTEntity(entity).asNBTString().contains("Minigame Lobby Basketball");
	}

	private static boolean hasBasketball(Player player) {
		return player.getInventory().contains(getBasketball(player));
	}

	private static Collection<Entity> getLobbyEntities() {
		return world.getNearbyEntities(Minigames.getGamelobby(), 200, 200, 200);
	}

	private static void cleanupBasketballs(Player player) {
		if (hasBasketball(player))
			player.getInventory().remove(getBasketball(player));
		else
			getLobbyEntities().forEach(entity -> {
				if (Minigames.getWorldGuardUtils().isInRegion(entity.getLocation(), region))
					if (isBasketball(entity) && ownsBasketball(player, entity))
						entity.remove();
			});
	}

	private class BasketballJanitor {
		public BasketballJanitor() {
			start();
		}

		void start() {
			WorldGuardUtils wgUtils = Minigames.getWorldGuardUtils();
			Utils.repeat(0, 20 * 20, () -> {
				List<Player> players = Bukkit.getOnlinePlayers().stream()
						.filter(player -> player.getWorld() == world)
						.collect(Collectors.toList());

				players.forEach(player -> {
					if (wgUtils.isInRegion(player.getLocation(), region)) {
						if (!hasBasketball(player)) {
							boolean found = false;
							for (Entity entity : getLobbyEntities())
								if (!wgUtils.isInRegion(entity.getLocation(), region))
									entity.remove();
								else {
									found = true;
									break;
								}

							if (!found)
								giveBasketball(player);
						}
					} else {
						cleanupBasketballs(player);
					}
				});
			});
		}
	}

	private class BasketballThrowWatcher {
		private int taskId;
		private int iteration;
		private Player player;
		private Item entity;
		private WorldGuardUtils wgUtils = Minigames.getWorldGuardUtils();

		BasketballThrowWatcher(Player player, Item item) {
			this.player = player;
			this.entity = item;
			start();
		}

		void start() {
			taskId = Utils.repeat(0, 1, () -> {
				++iteration;

				if (!wgUtils.isInRegion(entity.getLocation(), region)) {
					entity.remove();
					giveBasketball(player);
					stop();
				} else if (wgUtils.isInRegion(entity.getLocation(), region + "_hoop")) {
					entity.remove();
					giveBasketball(player);
					player.sendMessage(colorize("&eTouchdown!!"));
					wgUtils.getPlayersInRegion(region).forEach(loopPlayer ->
							loopPlayer.spawnParticle(Particle.LAVA, entity.getLocation(), 50, 2, 2, 2, .01));
					stop();
				} else if (wgUtils.isInRegion(entity.getLocation(), region + "_backboard")) {
					entity.remove();
					giveBasketball(player);
					player.sendMessage(colorize("&eSo close..."));
					stop();
				}

				if (iteration == 60) {
					if (wgUtils.isInRegion(entity.getLocation(), region + "_stuck")) {
						entity.remove();
						giveBasketball(player);
					}
					stop();
				}
			});
		}

		void stop() {
			Utils.cancelTask(taskId);
		}
	}

	@EventHandler
	public void onPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		if (player.getWorld() != world) return;
		if (!isBasketball(event.getItem().getItemStack())) return;

		if (!Minigames.getWorldGuardUtils().isInRegion(player.getLocation(), region))
			event.setCancelled(true);
		else if (!ownsBasketball(player, event.getItem().getItemStack()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (event.getPlayer().getWorld() != world) return;
		if (!isBasketball(event.getItemDrop().getItemStack())) return;

		new BasketballThrowWatcher(event.getPlayer(), event.getItemDrop());
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(region)) return;
		Player player = event.getPlayer();

		if (!hasBasketball(player))
			giveBasketball(player);
	}

	@EventHandler
	public void onRegionLeave(RegionLeftEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(region)) return;
		cleanupBasketballs(event.getPlayer());
	}


}
