package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.CustomEnchant;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.PlayerUtils;
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
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.nexus.utils.Distance.distance;

public class MagnetEnchant extends CustomEnchant implements Listener {
	public static final String NBT_KEY_OWNER = "nexus.magnet.owner";
	public static final String NBT_KEY_ENABLED = "nexus.magnet.enabled";

	public MagnetEnchant(@NotNull NamespacedKey key) {
		super(key);
		Nexus.registerListener(this);
	}

	private static final int RADIUS_MULTIPLIER = 5;

	static {
		Tasks.repeat(TickTime.TICK.x(10), TickTime.TICK, () -> {
			for (Player player : OnlinePlayers.getAll()) {
				if (WorldGroup.of(player) != WorldGroup.SURVIVAL)
					continue;

				if (PlayerUtils.isVanished(player) || player.getGameMode() == GameMode.SPECTATOR)
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
		final double distance = distance(player, entity).getRealDistance();
		if (distance < 1)
			return null;

		final Vector subtract = player.getLocation().toVector().subtract(entity.getLocation().toVector());
		final Vector normalized = subtract.normalize();
		final double multiplier = .6 - distance / 10d;
		return normalized.multiply(Math.max(.1, multiplier));
	}

	private static List<Item> getDroppedItems(Player player, int radius) {
		return new ArrayList<>() {{
			items:
			for (Item item : player.getWorld().getNearbyEntitiesByType(Item.class, player.getLocation(), radius)) {
				for (MetadataValue value : item.getMetadata(NBT_KEY_ENABLED))
					if (!value.asBoolean())
						continue items;

				for (MetadataValue value : item.getMetadata(NBT_KEY_OWNER))
					if (!player.getUniqueId().toString().equals(value.asString()))
						continue items;

				if (!hasRoomFor(player, item.getItemStack()))
					continue;

				add(item);
			}
		}};
	}

	private static final Map<UUID, Map<Material, Boolean>> hasRoomFor = new HashMap<>();
	private static final Map<UUID, Integer> maxLevel = new HashMap<>();

	static {
		Tasks.repeat(TickTime.SECOND, TickTime.SECOND.x(2), () -> {
			hasRoomFor.clear();
			maxLevel.clear();
		});
	}

	private static boolean hasRoomFor(Player player, ItemStack item) {
		return hasRoomFor.computeIfAbsent(player.getUniqueId(), $ -> new HashMap<>())
			.computeIfAbsent(item.getType(), $ -> PlayerUtils.hasRoomFor(player, item));
	}

	private static int getMaxLevel(Player player) {
		if (Enchant.MAGNET == null)
			return 0;

		return maxLevel.computeIfAbsent(player.getUniqueId(), $ -> {
			int maxLevel = 0;
			for (ItemStack item : getItems(player.getInventory()))
				maxLevel = Math.max(maxLevel, item.getItemMeta().getEnchantLevel(Enchant.MAGNET));

			return Math.min(Enchant.MAGNET.getMaxLevel(), maxLevel);
		});
	}

	public void setMetadata(Item item, Player owner, boolean enabled) {
		item.setMetadata(NBT_KEY_OWNER, new FixedMetadataValue(Nexus.getInstance(), owner.getUniqueId().toString()));
		item.setMetadata(NBT_KEY_ENABLED, new FixedMetadataValue(Nexus.getInstance(), enabled));
	}

	@EventHandler
	public void on(PlayerDropItemEvent event) {
		setMetadata(event.getItemDrop(), event.getPlayer(), false);
	}

	@EventHandler
	public void on(BlockDropItemEvent event) {
		for (Item item : event.getItems())
			setMetadata(item, event.getPlayer(), true);
	}

	@EventHandler
	public void on(EntityDropItemEvent event) {
		if (event.getEntity() instanceof Player)
			return;

		if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent casted))
			return;

		if (!(casted.getDamager() instanceof Player player))
			return;

		setMetadata(event.getItemDrop(), player, true);
	}

}
