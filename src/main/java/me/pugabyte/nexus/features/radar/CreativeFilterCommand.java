package me.pugabyte.nexus.features.radar;

import eden.utils.StringUtils;
import eden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.events.DyeBombCommand;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.models.nerd.Rank;
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
import java.util.function.Consumer;
import java.util.function.Supplier;

@NoArgsConstructor
public class CreativeFilterCommand extends CustomCommand implements Listener {

	public CreativeFilterCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private boolean shouldFilterItems(HumanEntity player) {
		return WorldGroup.get(player.getWorld()) == WorldGroup.CREATIVE && Rank.of((Player) player) == Rank.GUEST;
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

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onItemSpawn(ItemSpawnEvent event) {
		if (WorldGroup.get(event.getEntity().getWorld()) != WorldGroup.CREATIVE)
			return;

		Tasks.wait(2, () -> limitDrops(event.getLocation()));
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
		if (WorldGroup.get(player.getWorld()) != WorldGroup.CREATIVE)
			return;

		if (Rank.of(player) != Rank.GUEST)
			return;

		Material type = event.getBlock().getType();
		if (disallowedPlacement.contains(type)) {
			if (!new CooldownService().check(player, player.getUniqueId() + "-" + type.name(), Time.MINUTE))
				return;

			player.sendMessage("You must be Member rank to place " + StringUtils.camelCase(type));
			event.setCancelled(true);
		}
	}

	private static final List<Material> disallowedPlacement = Arrays.asList(
			Material.DISPENSER,
			Material.STICKY_PISTON,
			Material.PISTON,
			Material.REDSTONE_TORCH,
			Material.DROPPER,
			Material.OBSERVER,
			Material.REPEATER,
			Material.COMPARATOR,
			Material.REDSTONE_WIRE
	);

}
