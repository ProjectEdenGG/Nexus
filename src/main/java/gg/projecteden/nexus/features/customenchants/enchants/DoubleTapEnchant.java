package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.features.customenchants.EnchantUtils;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.features.mcmmo.resetnew.skills.archery.Quiver;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CrossbowMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DoubleTapEnchant extends CustomEnchant implements Listener {

	private static final int ARROWS_PER_LEVEL = 5;
	private final Set<RechargeKey> pendingRecharges = new HashSet<>();

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public List<Material> getSupportedMaterials() {
		return List.of(Material.CROSSBOW);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLoad(EntityLoadCrossbowEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		if (getLevel(event.getCrossbow()) <= 0)
			return;

		EquipmentSlot hand = event.getHand();
		Tasks.wait(1, () -> loadAdditionalProjectiles(player, hand));
	}

	public static void loadAdditionalProjectiles(Player player, EquipmentSlot hand) {
		ItemStack crossbow = getHeldItem(player, hand);

		if (crossbow.getType() != Material.CROSSBOW) return;
		if (!(crossbow.getItemMeta() instanceof CrossbowMeta meta)) return;
		if (!meta.hasChargedProjectiles()) return;

		int capacity = getCapacity(crossbow);
		if (capacity <= 0)
			return;

		List<ItemStack> stored = getStoredProjectiles(crossbow, capacity);

		if (hasProjectile(stored)) return;

		ItemStack creativeFallback = meta.getChargedProjectiles().getFirst();
		boolean changed = false;

		for (int slot = 0; slot < capacity; slot++) {
			ItemStack projectile = takeProjectile(player, hand);

			if (projectile == null && player.getGameMode() == GameMode.CREATIVE)
				projectile = copyOne(creativeFallback);

			if (projectile == null)
				break;

			stored.set(slot, projectile);
			changed = true;
		}

		if (!changed)
			return;

		crossbow = ItemUtils.setNBTContentsOfNonInventoryItem(crossbow, stored);
		setHeldItem(player, hand, crossbow);
	}

	private static ItemStack getHeldItem(Player player, EquipmentSlot hand) {
		if (hand == EquipmentSlot.OFF_HAND)
			return player.getInventory().getItemInOffHand();

		return player.getInventory().getItemInMainHand();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShoot(EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		ItemStack crossbow = event.getBow();

		if (crossbow == null || crossbow.getType() != Material.CROSSBOW) return;
		if (getLevel(crossbow) <= 0) return;

		RechargeKey key = new RechargeKey(player.getUniqueId(), event.getHand());
		if (!pendingRecharges.add(key))
			return;

		Tasks.wait(1, () -> {
			try {
				chargeNextProjectile(player, event.getHand());
			} finally {
				pendingRecharges.remove(key);
			}
		});
	}

	private void chargeNextProjectile(Player player, EquipmentSlot hand) {
		if (!player.isOnline()) return;
		ItemStack crossbow = getHeldItem(player, hand);
		if (crossbow.getType() != Material.CROSSBOW) return;

		int capacity = getCapacity(crossbow);
		if (capacity <= 0)
			return;

		List<ItemStack> stored = getStoredProjectiles(crossbow, capacity);
		int nextSlot = findNextProjectile(stored);

		if (nextSlot == -1) return;

		ItemStack projectile = stored.get(nextSlot);
		stored.set(nextSlot, null);

		if (!(crossbow.getItemMeta() instanceof CrossbowMeta meta)) return;

		meta.setChargedProjectiles(List.of(projectile));
		crossbow.setItemMeta(meta);

		crossbow = ItemUtils.setNBTContentsOfNonInventoryItem(crossbow, stored);
		setHeldItem(player, hand, crossbow);
	}

	private static List<ItemStack> getStoredProjectiles(ItemStack crossbow, int expectedSize) {
		List<ItemStack> projectiles = new ArrayList<>(ItemUtils.getNBTContentsOfNonInventoryItem(crossbow, expectedSize));

		while (projectiles.size() < expectedSize)
			projectiles.add(null);

		return projectiles;
	}

	private static int findNextProjectile(List<ItemStack> projectiles) {
		for (int slot = 0; slot < projectiles.size(); slot++)
			if (!Nullables.isNullOrAir(projectiles.get(slot)))
				return slot;

		return -1;
	}

	private static boolean hasProjectile(List<ItemStack> projectiles) {
		return findNextProjectile(projectiles) != -1;
	}

	private static ItemStack takeProjectile(Player player, EquipmentSlot crossbowHand) {
		PlayerInventory inventory = player.getInventory();
		EquipmentSlot projectileHand = crossbowHand == EquipmentSlot.HAND ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;

		ItemStack heldProjectile = getHeldItem(player, projectileHand);

		if (isProjectile(heldProjectile)) {
			if (ItemUtils.isModelMatch(heldProjectile, Quiver.get(), false)) {
				ItemStack item = Quiver.retrieveArrow(heldProjectile);
				if (item != null)
					return item;
			}
			else {
				ItemStack result = copyOne(heldProjectile);

				if (player.getGameMode() != GameMode.CREATIVE)
					setHeldItem(player, projectileHand, decrement(heldProjectile));

				return result;
			}
		}

		ItemStack[] contents = inventory.getStorageContents();

		for (int slot = 0; slot < contents.length; slot++) {
			ItemStack item = contents[slot];

			if (!isProjectile(item))
				continue;

			if (ItemUtils.isModelMatch(item, Quiver.get(), false)) {
				ItemStack arrow = Quiver.retrieveArrow(item);
				if (arrow != null)
					return arrow;
				continue;
			}

			ItemStack result = copyOne(item);

			if (player.getGameMode() != GameMode.CREATIVE)
				inventory.setItem(slot, decrement(item));

			return result;
		}

		return null;
	}

	private static boolean isProjectile(ItemStack item) {
		if (Nullables.isNullOrAir(item))
			return false;

		return switch (item.getType()) {
			case ARROW, SPECTRAL_ARROW, TIPPED_ARROW -> true;
			default -> false;
		};
	}

	private static ItemStack copyOne(ItemStack item) {
		if (Nullables.isNullOrAir(item))
			return null;

		ItemStack result = item.clone();
		result.setAmount(1);
		return result;
	}

	private static ItemStack decrement(ItemStack item) {
		if (item.getAmount() <= 1)
			return null;

		ItemStack result = item.clone();
		result.setAmount(result.getAmount() - 1);
		return result;
	}

	private static void setHeldItem(Player player, EquipmentSlot hand, ItemStack item) {
		if (hand == EquipmentSlot.OFF_HAND)
			player.getInventory().setItemInOffHand(item);
		else
			player.getInventory().setItemInMainHand(item);
	}

	private static int getCapacity(ItemStack crossbow) {
		return EnchantUtils.getLevel(Enchant.DOUBLE_TAP, crossbow) * ARROWS_PER_LEVEL;
	}

	private record RechargeKey(UUID player, EquipmentSlot hand) { }

}
