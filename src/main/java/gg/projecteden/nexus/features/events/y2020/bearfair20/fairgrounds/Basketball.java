package gg.projecteden.nexus.features.events.y2020.bearfair20.fairgrounds;

import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2020.bearfair20.BearFair20;
import gg.projecteden.nexus.features.events.y2020.bearfair20.Fairgrounds;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.models.bearfair20.BearFair20User;
import gg.projecteden.nexus.models.bearfair20.BearFair20User.BF20PointSource;
import gg.projecteden.nexus.models.bearfair20.BearFair20UserService;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
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

import java.util.*;

@Disabled
public class Basketball implements Listener {

	@Getter
	private static ItemStack basketballItem = Fairgrounds.BearFairKit.BASKETBALL.getItem();
	private static Map<UUID, ItemStack> basketballs = new HashMap<>();
	private static World world = BearFair20.getWorld();
	public static String gameRg = BearFair20.getRegion() + "_basketball";
	public static String courtRg = gameRg + "_court";
	private static String stuckRg = gameRg + "_stuck_";
	private static String backboardRg = gameRg + "_backboard_";
	private static String hoopRg = gameRg + "_hoop_";
	private BF20PointSource SOURCE = BF20PointSource.BASKETBALL;

	static {
		// TODO BEARFAIR
//		janitor();
	}

	public Basketball() {
		Nexus.registerListener(this);
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
		if (!Nullables.isNullOrAir(inv.getHelmet()) && isBasketball(inv.getHelmet()))
			inv.setHelmet(new ItemStack(Material.AIR));
		if (!Nullables.isNullOrAir(inv.getItemInOffHand()) && isBasketball(inv.getItemInOffHand()))
			inv.setItemInOffHand(new ItemStack(Material.AIR));

	}

	private static void removeBasketball(ItemStack[] itemStacks, Player player) {
		for (ItemStack item : itemStacks) {
			if (Nullables.isNullOrAir(item))
				continue;
			if (isBasketball(item)) {
				player.getInventory().remove(item);
			}
		}
	}

	private static void removeBasketballEntity(Player player) {
		Collection<Entity> entities = BearFair20.worldguard().getEntitiesInRegion(gameRg);
		for (Entity entity : entities) {
			if (entity instanceof Item item) {
				if (!isBasketball(item.getItemStack()))
					continue;
				if (ownsBasketball(player, item.getItemStack()))
					entity.remove();
			}

		}
	}

	private static boolean regionContainsBasketball(Player player) {
		Collection<Entity> entities = BearFair20.worldguard().getEntitiesInRegion(gameRg);
		for (Entity entity : entities) {
			if (entity instanceof Item item) {
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
		return BearFair20.worldguard().getEntitiesInRegion(gameRg);
	}

	private static void cleanupBasketballs() {
		getRegionEntities().forEach(entity -> {
			if (!BearFair20.worldguard().isInRegion(entity.getLocation(), courtRg))
				if (isBasketball(entity))
					entity.remove();
		});
	}

	private static void janitor() {
		Tasks.repeat(0, 20 * 20, () -> {
			cleanupBasketballs();

			List<Player> players = OnlinePlayers.where().world(world).get();

			players.forEach(player -> {
				if (BearFair20.worldguard().isInRegion(player.getLocation(), courtRg)) {
					if (!hasBasketball(player)) {
						boolean found = false;
						for (Entity entity : getRegionEntities())
							if (BearFair20.worldguard().isInRegion(entity.getLocation(), courtRg)) {
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

				if (!BearFair20.worldguard().isInRegion(entity.getLocation(), courtRg)) {
					entity.remove();
					giveBasketball(player);
					stop();
				} else if (BearFair20.worldguard().isInRegion(entity.getLocation(), hoopRg + "1")
						|| BearFair20.worldguard().isInRegion(entity.getLocation(), hoopRg + "2")) {
					entity.remove();
					giveBasketball(player);
					BearFair20.send("&eTouchdown!!", player);

					if (BearFair20.giveDailyPoints) {
						BearFair20User user = new BearFair20UserService().get(player);
						user.giveDailyPoints(SOURCE);
						new BearFair20UserService().save(user);
					}

					BearFair20.worldguard().getPlayersInRegion(courtRg).forEach(loopPlayer ->
							loopPlayer.spawnParticle(Particle.LAVA, entity.getLocation(), 50, 2, 2, 2, .01));
					stop();
				} else if (BearFair20.worldguard().isInRegion(entity.getLocation(), backboardRg + "1")
						|| BearFair20.worldguard().isInRegion(entity.getLocation(), backboardRg + "2")) {
					entity.remove();
					giveBasketball(player);
					BearFair20.send("&eSo close...", player);
					stop();
				}

				if (iteration == 60) {
					if (BearFair20.worldguard().isInRegion(entity.getLocation(), stuckRg + "1")
							|| BearFair20.worldguard().isInRegion(entity.getLocation(), stuckRg + "2")) {
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
		if (!(event.getEntity() instanceof Player player)) return;
		if (player.getWorld() != world) return;
		if (!isBasketball(event.getItem().getItemStack())) return;

		if (!BearFair20.isInRegion(player, courtRg))
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
	public void onRegionEnter(PlayerEnteredRegionEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(courtRg)) return;
		Player player = event.getPlayer();
		if (new CooldownService().check(player, "basketball-doublejump-tip", TickTime.SECOND.x(30)))
			BearFair20.send("&aDouble Jump enabled!", player);
		if (!regionContainsBasketball(player))
			giveBasketball(player);
	}

	@EventHandler
	public void onRegionLeave(PlayerLeftRegionEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(courtRg)) return;
		removeBasketball(event.getPlayer());
		removeBasketballEntity(event.getPlayer());
	}
}
