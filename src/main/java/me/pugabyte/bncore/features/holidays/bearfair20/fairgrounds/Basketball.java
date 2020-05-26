package me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BFPoints.BFPointSource;
import me.pugabyte.bncore.features.holidays.bearfair20.BFPoints.BFPointsUser;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.holidays.bearfair20.Fairgrounds;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;
import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class Basketball implements Listener {

	@Getter
	private static ItemStack basketballItem = Fairgrounds.BearFairKit.BASKETBALL.getItem();
	private static Map<UUID, ItemStack> basketballs = new HashMap<>();
	private static World world = BearFair20.world;
	public static String gameRg = BearFair20.BFRg + "_basketball";
	public static String courtRg = gameRg + "_court";
	private static String stuckRg = gameRg + "_stuck_";
	private static String backboardRg = gameRg + "_backboard_";
	private static String hoopRg = gameRg + "_hoop_";
	private BFPointSource SOURCE = BFPointSource.BASKETBALL;

	static {
		janitor();
	}

	public Basketball() {
		BNCore.registerListener(this);
	}

	private static ItemStack getBasketball(Player player) {
		if (!basketballs.containsKey(player.getUniqueId())) {
			ItemStack basketball = getBasketballItem().clone();
			NBTItem nbtItem = new NBTItem(basketball);
			nbtItem.getCompound("SkullOwner").setString("Id", player.getUniqueId().toString());
			basketballs.put(player.getUniqueId(), nbtItem.getItem());
		}

		return basketballs.get(player.getUniqueId());
	}

	private static void giveBasketball(Player player) {
		if (!hasBasketball(player))
			player.getInventory().addItem(getBasketball(player));
	}

	private static void removeBasketball(Player player) {
		PlayerInventory inv = player.getInventory();
		removeBasketball(inv.getContents(), player);
		removeBasketball(inv.getExtraContents(), player);
		removeBasketball(inv.getArmorContents(), player);
		removeBasketball(inv.getStorageContents(), player);
		if (!Utils.isNullOrAir(inv.getHelmet()) && isBasketball(inv.getHelmet()))
			inv.setHelmet(new ItemStack(Material.AIR));
		if (!Utils.isNullOrAir(inv.getItemInOffHand()) && isBasketball(inv.getItemInOffHand()))
			inv.setItemInOffHand(new ItemStack(Material.AIR));

	}

	private static void removeBasketball(ItemStack[] itemStacks, Player player) {
		for (ItemStack item : itemStacks) {
			if (Utils.isNullOrAir(item))
				continue;
			if (isBasketball(item)) {
				player.getInventory().remove(item);
			}
		}
	}

	private static void removeBasketballEntity(Player player) {
		Collection<Entity> entities = WGUtils.getEntitiesInRegion(player.getWorld(), gameRg);
		for (Entity entity : entities) {
			if (entity instanceof Item) {
				Item item = (Item) entity;
				if (!isBasketball(item.getItemStack()))
					continue;
				if (ownsBasketball(player, item.getItemStack()))
					entity.remove();
			}

		}
	}

	private static boolean regionContainsBasketball(Player player) {
		Collection<Entity> entities = WGUtils.getEntitiesInRegion(player.getWorld(), gameRg);
		for (Entity entity : entities) {
			if (entity instanceof Item) {
				Item item = (Item) entity;
				if (!isBasketball(item.getItemStack()))
					continue;
				if (ownsBasketball(player, item.getItemStack())) {
					return true;
				}
			}
		}
		return false;
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
		return itemStack.getItemMeta().getLore().get(0).contains("BearFair20 Basketball");
	}

	private static boolean isBasketball(Entity entity) {
		return entity instanceof Item && new NBTEntity(entity).asNBTString().contains("BearFair20 Basketball");
	}

	private static boolean hasBasketball(Player player) {
		return player.getInventory().containsAtLeast(getBasketball(player), 1);
	}

	private static Collection<Entity> getRegionEntities() {
		return WGUtils.getEntitiesInRegion(world, gameRg);
	}

	private static void cleanupBasketballs() {
		getRegionEntities().forEach(entity -> {
			if (!WGUtils.isInRegion(entity.getLocation(), courtRg))
				if (isBasketball(entity))
					entity.remove();
		});
	}

	private static void janitor() {
		Tasks.repeat(0, 20 * 20, () -> {
			cleanupBasketballs();

			List<Player> players = Bukkit.getOnlinePlayers().stream()
					.filter(player -> player.getWorld() == world)
					.collect(Collectors.toList());

			players.forEach(player -> {
				if (WGUtils.isInRegion(player.getLocation(), courtRg)) {
					if (!hasBasketball(player)) {
						boolean found = false;
						for (Entity entity : getRegionEntities())
							if (WGUtils.isInRegion(entity.getLocation(), courtRg)) {
								found = true;
								break;
							}

						if (!found)
							giveBasketball(player);
					}
				} else {
					removeBasketball(player);
				}
			});
		});
	}

	private class BasketballThrowWatcher {
		private int taskId;
		private int iteration;
		private Player player;
		private Item entity;

		BasketballThrowWatcher(Player player, Item item) {
			this.player = player;
			this.entity = item;
			start();
		}

		void start() {
			taskId = Tasks.repeat(0, 1, () -> {
				++iteration;

				if (!WGUtils.isInRegion(entity.getLocation(), courtRg)) {
					entity.remove();
					giveBasketball(player);
					stop();
				} else if (WGUtils.isInRegion(entity.getLocation(), hoopRg + "1")
						|| WGUtils.isInRegion(entity.getLocation(), hoopRg + "2")) {
					entity.remove();
					giveBasketball(player);
					player.sendMessage(colorize("&eTouchdown!!"));
					BFPointsUser.giveDailyPoints(player, 1, SOURCE);
					WGUtils.getPlayersInRegion(courtRg).forEach(loopPlayer ->
							loopPlayer.spawnParticle(Particle.LAVA, entity.getLocation(), 50, 2, 2, 2, .01));
					stop();
				} else if (WGUtils.isInRegion(entity.getLocation(), backboardRg + "1")
						|| WGUtils.isInRegion(entity.getLocation(), backboardRg + "2")) {
					entity.remove();
					giveBasketball(player);
					player.sendMessage(colorize("&eSo close..."));
					stop();
				}

				if (iteration == 60) {
					if (WGUtils.isInRegion(entity.getLocation(), stuckRg + "1")
							|| WGUtils.isInRegion(entity.getLocation(), stuckRg + "2")) {
						entity.remove();
						giveBasketball(player);
					}
					stop();
				}
			});
		}

		void stop() {
			Tasks.cancel(taskId);
		}
	}

	@EventHandler
	public void onPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		if (player.getWorld() != world) return;
		if (!isBasketball(event.getItem().getItemStack())) return;

		if (!WGUtils.isInRegion(player.getLocation(), courtRg))
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
		if (!event.getRegion().getId().equalsIgnoreCase(courtRg)) return;
		if (!regionContainsBasketball(event.getPlayer()))
			giveBasketball(event.getPlayer());
	}

	@EventHandler
	public void onRegionLeave(RegionLeftEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(courtRg)) return;
		removeBasketball(event.getPlayer());
		removeBasketballEntity(event.getPlayer());
	}
}
