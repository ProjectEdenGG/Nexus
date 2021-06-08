package me.pugabyte.nexus.features.radar;

import eden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.DyeBombCommand;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

@NoArgsConstructor
public class CreativeFilterCommand extends CustomCommand implements Listener {

	public CreativeFilterCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private boolean shouldFilterItems(HumanEntity player) {
		return WorldGroup.of(player.getWorld()) == WorldGroup.CREATIVE && Rank.of((Player) player) == Rank.GUEST;
	}

	private void filter(Supplier<HumanEntity> player, Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
		boolean isWhiteWolfKnight = player.get().getUniqueId().toString().equals("f325c439-02c2-4043-995e-668113c7eb9f");
		boolean isDyeBomb = DyeBombCommand.isDyeBomb(getter.get());

		if (!(isWhiteWolfKnight && isDyeBomb))
			if (!shouldFilterItems(player.get()))
				return;

		ItemStack item = getter.get();
		if (item != null)
			setter.accept(new ItemStack(item.getType(), item.getAmount()));
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

	private static final int RADIUS = 128;
	private static final int MAX_DROPPED_ENTITIES = 200;

	private static final int MAX_DROPS_PER_TICK = 27*5; // these are item stacks, not individual items. breaking a chest gives 27 items. 5 players all breaking a chest in creative at once is a generous max
	private static final AtomicInteger DROPS_THIS_TICK = new AtomicInteger(); // count of all item stacks dropped in the creative worlds this tick
	private static final AtomicInteger resetDropsTaskId = new AtomicInteger(-1); // we don't need a million tasks trying to reset DROPS_THIS_TICK!
	private static void resetDrops() {
		DROPS_THIS_TICK.set(0);
		resetDropsTaskId.set(-1);
	}

	// does not run the limiter if it has been run by another item within IGNORE_LIMITER_RADIUS this tick
	private static final List<Location> LIMIT_CHECKS_THIS_TICK = new ArrayList<>();
	private static final int IGNORE_LIMITER_RADIUS = 5; // square radius

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onItemSpawn(ItemSpawnEvent event) {
		if (WorldGroup.of(event.getEntity().getWorld()) != WorldGroup.CREATIVE)
			return;

		synchronized (resetDropsTaskId) {
			if (resetDropsTaskId.get() == -1)
				resetDropsTaskId.set(Tasks.wait(1, CreativeFilterCommand::resetDrops));
		}

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
		Tasks.wait(1, () -> LIMIT_CHECKS_THIS_TICK.remove(location));
		Tasks.wait(2, () -> limitDrops(location));
	}

	private void limitDrops(Location location) {
		Collection<Item> entities = location.getNearbyEntitiesByType(Item.class, RADIUS, item -> !item.isDead());

		Tasks.async(() -> {
			if (entities.size() <= MAX_DROPPED_ENTITIES)
				return;

			List<Item> sortedEntities = new ArrayList<>(entities);
			sortedEntities.sort(Comparator.comparing(Entity::getTicksLived).reversed());

			while (sortedEntities.size() > MAX_DROPPED_ENTITIES) {
				sortedEntities.remove(0).remove();
			}
		});
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlaceBlock(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (WorldGroup.of(player.getWorld()) != WorldGroup.CREATIVE)
			return;

		if (Rank.of(player) != Rank.GUEST)
			return;

		Material type = event.getBlock().getType();
		if (disallowedPlacement.contains(type)) {
			if (new CooldownService().check(player, player.getUniqueId() + "-" + type.name(), Time.MINUTE))
				player.sendMessage("You must be Member rank to place " + StringUtils.camelCase(type));
			event.setCancelled(true);
		}
	}

	private static final List<Material> disallowedPlacement = Arrays.asList(
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
