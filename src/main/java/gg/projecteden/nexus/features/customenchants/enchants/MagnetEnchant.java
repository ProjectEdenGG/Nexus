package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.customenchants.CustomEnchant;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MagnetEnchant extends CustomEnchant {

	public MagnetEnchant(@NotNull NamespacedKey key) {
		super(key);
	}

	private static final int RADIUS_MULTIPLIER = 5;

	static {
		Tasks.repeat(TickTime.TICK.x(10), TickTime.TICK, () -> {
			for (Player player : OnlinePlayers.getAll()) {
				if (WorldGroup.of(player) != WorldGroup.SURVIVAL)
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
		final double distance = player.getLocation().distance(entity.getLocation());
		if (distance < 1)
			return null;

		final Vector subtract = player.getLocation().toVector().subtract(entity.getLocation().toVector());
		final Vector normalized = subtract.normalize();
		final double multiplier = .6 - distance / 10d;
		return normalized.multiply(Math.max(.1, multiplier));
	}

	private static List<Item> getDroppedItems(Player player, int radius) {
		return new ArrayList<>() {{
			for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
				if (!(entity instanceof Item))
					continue;

				// https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8278834
				Item item = (Item) entity;

				// TODO Metadata check

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

}
