package gg.projecteden.nexus.features.listeners;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CreativeFilter implements Listener {

	private static boolean shouldFilterItems(Player player) {
		return WorldGroup.of(player.getWorld()) == WorldGroup.CREATIVE && Rank.of(player) == Rank.GUEST;
	}

	private static void filter(Supplier<HumanEntity> playerSupplier, Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
		if (!(playerSupplier.get() instanceof Player player))
			return;
		if (!shouldFilterItems(player))
			return;

		ItemStack item = getter.get();
		if (item != null) {
			Material type = item.getType();
			if (type == Material.COMMAND_BLOCK_MINECART)
				type = Material.MINECART;
			ItemStack replacement = new ItemStack(type, item.getAmount());
			if (!replacement.isSimilar(item))
				setter.accept(replacement);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		filter(event::getWhoClicked, event::getCurrentItem, event::setCurrentItem);
		filter(event::getWhoClicked, event::getCursor, event::setCursor);
	}

	@EventHandler
	public void onInventoryCreative(InventoryCreativeEvent event) {
		filter(event::getWhoClicked, event::getCurrentItem, event::setCurrentItem);
		filter(event::getWhoClicked, event::getCursor, event::setCursor);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		filter(event::getPlayer, event::getItemInHand, item -> event.getPlayer().getInventory().setItem(event.getHand(), item));
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		filter(event::getPlayer, event::getItem, item -> item.setItemMeta(Bukkit.getItemFactory().getItemMeta(item.getType())));
	}

	static {
		Tasks.repeat(TickTime.TICK, TickTime.TICK.x(2), () -> {
			for (World world : WorldGroup.CREATIVE.getWorlds()) {
				for (Player player : OnlinePlayers.where().world(world).get()) {
					final PlayerInventory inventory = player.getInventory();

					for (int i = 0; i < inventory.getSize(); i++) {
						int slot = i;
						final ItemStack item = inventory.getItem(slot);
						filter(() -> player, () -> item, filteredItem -> inventory.setItem(slot, filteredItem));
					}
				}
			}
		});
	}

	private static final int RADIUS = 128;
	private static final int MAX_DROPPED_ENTITIES = 200;

	private static final int MAX_DROPS_PER_TICK = 27*5; // these are item stacks, not individual items. breaking a chest gives 27 items. 5 players all breaking a chest in creative at once is a generous max
	private static final AtomicInteger DROPS_THIS_TICK = new AtomicInteger(); // count of all item stacks dropped in the creative worlds this tick
	private static int resetDropsTaskId = -1; // we don't need a million tasks trying to reset DROPS_THIS_TICK!
	private static void resetDrops() {
		LIMIT_CHECKS_THIS_TICK.clear();
		DROPS_THIS_TICK.set(0);
		resetDropsTaskId = -1;
	}

	// does not run the limiter if it has been run by another item within IGNORE_LIMITER_RADIUS this tick
	private static final List<Location> LIMIT_CHECKS_THIS_TICK = new ArrayList<>();
	private static final int IGNORE_LIMITER_RADIUS = 5; // square radius

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onItemSpawn(ItemSpawnEvent event) {
		if (WorldGroup.of(event.getEntity().getWorld()) != WorldGroup.CREATIVE)
			return;

		if (resetDropsTaskId == -1)
			resetDropsTaskId = Tasks.wait(1, CreativeFilter::resetDrops);

		if (DROPS_THIS_TICK.incrementAndGet() > MAX_DROPS_PER_TICK) {
			event.setCancelled(true);
			if (DROPS_THIS_TICK.get() == MAX_DROPS_PER_TICK+1)
				Nexus.warn("Cancelling creative world item spawns due to spawn at " + StringUtils.stripColor(StringUtils.getLocationString(event.getLocation())));
			return;
		}

		Location location = event.getLocation();
		for (Location otherLocation : LIMIT_CHECKS_THIS_TICK) {
			if (!location.getWorld().equals(otherLocation.getWorld()))
				continue;
			if (Math.abs(location.getX() - otherLocation.getX()) <= IGNORE_LIMITER_RADIUS &&
					Math.abs(location.getY() - otherLocation.getY()) <= IGNORE_LIMITER_RADIUS &&
					Math.abs(location.getZ() - otherLocation.getZ()) <= IGNORE_LIMITER_RADIUS)
				return;
		}

		LIMIT_CHECKS_THIS_TICK.add(location);
		Tasks.wait(1, () -> limitDrops(location));
	}

	private void limitDrops(Location location) {
		Collection<Item> entities = location.getNearbyEntitiesByType(Item.class, RADIUS, item -> !item.isDead());
		if (entities.size() <= MAX_DROPPED_ENTITIES)
			return;

		Tasks.async(() -> {
			List<Item> sortedEntities = new ArrayList<>(entities);
			sortedEntities.sort(Comparator.comparing(Entity::getTicksLived).reversed());
			Tasks.sync(() -> sortedEntities.subList(0, sortedEntities.size() - MAX_DROPPED_ENTITIES).forEach(Item::remove));
		});
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlaceBlock(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!shouldFilterItems(player))
			return;

		Material type = event.getBlock().getType();
		if (disallowedPlacement.contains(type)) {
			if (new CooldownService().check(player, player.getUniqueId() + "-" + type.name(), TickTime.MINUTE))
				player.sendMessage("You must be Member rank to place " + StringUtils.camelCase(type));
			event.setCancelled(true);
		}
	}

	private static final List<Material> disallowedPlacement = Arrays.asList(
			Material.FURNACE,
			Material.SMOKER,
			Material.BLAST_FURNACE,
			Material.DISPENSER,
			Material.STICKY_PISTON,
			Material.PISTON,
			Material.DROPPER,
			Material.OBSERVER,
			Material.REPEATER,
			Material.COMPARATOR,
			Material.REDSTONE_WIRE
	);

}
