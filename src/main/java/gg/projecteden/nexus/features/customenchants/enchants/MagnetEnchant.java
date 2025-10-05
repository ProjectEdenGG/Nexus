package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty;

public class MagnetEnchant extends CustomEnchant implements Listener {
	public static final NamespacedKey NBT_KEY_OWNER = new NamespacedKey(Nexus.getInstance(), "nexus-magnet-owner");
	public static final NamespacedKey NBT_KEY_ENABLED = new NamespacedKey(Nexus.getInstance(), "nexus-magnet-enabled");

	private static final int RADIUS_MULTIPLIER = 5;

	static {
		Tasks.repeat(TickTime.TICK.x(10), TickTime.TICK, () -> {
			for (Player player : OnlinePlayers.getAll()) {
				if (!WorldGroup.of(player).isSurvivalMode())
					continue;

				if (Vanish.isVanished(player) || player.getGameMode() == GameMode.SPECTATOR)
					continue;

				int maxLevel = getMaxLevel(player);
				if (maxLevel == 0)
					continue;

				int radius = maxLevel * RADIUS_MULTIPLIER;

				for (Item item : getDroppedItems(player, radius)) {
					final Vector vector = getVector(player, item);
					if (vector == null)
						continue;

					item.setVelocity(vector);
				}
			}
		});
	}

	@Nullable
	private static Vector getVector(Player player, Entity entity) {
		final double distance = Distance.distance(player, entity).getRealDistance();
		if (distance < 1)
			return null;

		final Vector subtract = player.getLocation().toVector().subtract(entity.getLocation().toVector());
		final Vector normalized = subtract.normalize();
		final double multiplier = .6 - distance / 10d;
		return normalized.multiply(Math.max(.1, multiplier));
	}

	private static List<Item> getDroppedItems(Player player, int radius) {
		return new ArrayList<>() {{
			for (Item item : player.getWorld().getNearbyEntitiesByType(Item.class, player.getLocation(), radius)) {
				final PersistentDataContainer pdc = item.getPersistentDataContainer();

				final Boolean enabled = pdc.get(NBT_KEY_ENABLED, PersistentDataType.BOOLEAN);
				if (enabled != null && !enabled)
					continue;

				var players = item.getWorld().getNearbyPlayers(item.getLocation(), radius).stream()
					.filter(player -> !(Vanish.isVanished(player) || GameModeWrapper.of(player).isCreative()))
					.toList();
				if (enabled == null && players.size() > 1)
					continue;

				final String uuid = pdc.get(NBT_KEY_OWNER, PersistentDataType.STRING);
				if (isNotNullOrEmpty(uuid))
					if (!player.getUniqueId().toString().equals(uuid))
						continue;

				if (!hasRoomFor(player, item.getItemStack()))
					continue;

				add(item);
			}
		}};
	}

	private static final Map<UUID, Map<Material, Boolean>> HAS_ROOM_FOR = new HashMap<>();
	private static final Map<UUID, Integer> MAX_LEVEL = new HashMap<>();

	static {
		Tasks.repeat(TickTime.SECOND, TickTime.SECOND.x(2), () -> {
			HAS_ROOM_FOR.clear();
			MAX_LEVEL.clear();
		});
	}

	private static boolean hasRoomFor(Player player, ItemStack item) {
		return HAS_ROOM_FOR.computeIfAbsent(player.getUniqueId(), $ -> new HashMap<>())
			.computeIfAbsent(item.getType(), $ -> PlayerUtils.hasRoomFor(player, item));
	}

	private static int getMaxLevel(Player player) {
		if (Dev.GRIFFIN.is(player) || Dev.DOM.is(player))
			return 5;

		if (Enchant.MAGNET == null)
			return 0;

		return MAX_LEVEL.computeIfAbsent(player.getUniqueId(), $ -> {
			int maxLevel = 0;
			for (ItemStack item : getItems(player.getInventory()))
				maxLevel = Math.max(maxLevel, item.getItemMeta().getEnchantLevel(Enchant.MAGNET));

			return Math.min(Enchant.MAGNET.getMaxLevel(), maxLevel);
		});
	}

	private void setNbt(PersistentDataContainer pdc, UUID uuid, boolean enabled) {
		pdc.set(NBT_KEY_ENABLED, PersistentDataType.BOOLEAN, enabled);
		pdc.set(NBT_KEY_OWNER, PersistentDataType.STRING, uuid.toString());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void on(PlayerDropItemEvent event) {
		setNbt(event.getItemDrop().getPersistentDataContainer(), event.getPlayer().getUniqueId(), false);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void on(BlockDropItemEvent event) {
		for (Item item : event.getItems())
			setNbt(item.getPersistentDataContainer(), event.getPlayer().getUniqueId(), true);
	}

	// TODO This is awful but Paper doesnt offer a better way

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void on(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player)
			return;

		if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent casted))
			return;

		if (!(casted.getDamager() instanceof Player player))
			return;

		event.getDrops().forEach(drop -> {
			ItemMeta meta = drop.getItemMeta();
			setNbt(meta.getPersistentDataContainer(), player.getUniqueId(), true);
			drop.setItemMeta(meta);
		});
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void on(EntitySpawnEvent event) {
		if (!(event.getEntity() instanceof Item item))
			return;

		boolean handled = false;
		final ItemMeta meta = item.getItemStack().getItemMeta();
		final PersistentDataContainer metaPdc = meta.getPersistentDataContainer();
		final PersistentDataContainer itemPdc = item.getPersistentDataContainer();

		final Boolean enabled = metaPdc.get(NBT_KEY_ENABLED, PersistentDataType.BOOLEAN);
		if (enabled != null) {
			itemPdc.set(NBT_KEY_ENABLED, PersistentDataType.BOOLEAN, enabled);
			metaPdc.remove(NBT_KEY_ENABLED);
			handled = true;
		}

		final String uuid = metaPdc.get(NBT_KEY_OWNER, PersistentDataType.STRING);
		if (uuid != null) {
			itemPdc.set(NBT_KEY_OWNER, PersistentDataType.STRING, uuid);
			metaPdc.remove(NBT_KEY_OWNER);
			handled = true;
		}

		if (handled)
			item.getItemStack().setItemMeta(meta);
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}
}
