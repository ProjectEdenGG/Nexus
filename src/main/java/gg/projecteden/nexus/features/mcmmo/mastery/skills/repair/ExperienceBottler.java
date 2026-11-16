package gg.projecteden.nexus.features.mcmmo.mastery.skills.repair;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ExperienceBottler implements Listener {

	private static final NamespacedKey STORED_XP_KEY = new NamespacedKey(Nexus.getInstance(), "bottled_xp");

	public static final ItemStack ITEM = setStoredXp(new ItemBuilder(ItemModelType.EXPERIENCE_BOTTLER)
		.maxStackSize(1)
		.name("&eExperience Bottler")
		.pdc(pdc -> pdc.set(STORED_XP_KEY, PersistentDataType.INTEGER, 0))
		.build(), 0);

	private static ItemStack setStoredXp(ItemStack itemStack, int points) {
		int level = level(points);
		return new ItemBuilder(itemStack, true)
			.resetLore()
			.lore("&3Stored XP: &aLv. " + level)
			.lore("")
			.lore("&eShift + Right Click &3to store")
			.lore("&eShift + Left Click to withdraw")
			.pdc(pdc -> pdc.set(STORED_XP_KEY, PersistentDataType.INTEGER, points))
			.build();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBottleInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!player.isSneaking())
			return;

		Action action = event.getAction();
		boolean deposit = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
		boolean withdraw = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
		if (!deposit && !withdraw)
			return;

		ItemStack bottle = player.getInventory().getItemInMainHand();
		Integer storedXp = bottle.getPersistentDataContainer().get(STORED_XP_KEY, PersistentDataType.INTEGER);
		if (storedXp == null || bottle.getAmount() != 1)
			return;

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		event.setCancelled(true);

		if (deposit) {
			deposit(player, bottle, storedXp);
			return;
		}

		if (action == Action.LEFT_CLICK_BLOCK) {
			UUID playerId = player.getUniqueId();
			if (!blockWithdrawals.add(playerId))
				return;

			Tasks.wait(1, () -> blockWithdrawals.remove(playerId));
		}

		withdraw(player, bottle, storedXp);
	}

	private void deposit(Player player, ItemStack bottle, int storedXp) {
		int level = player.getLevel();
		if (level == 0)
			return;

		int currentXp = player.calculateTotalExperiencePoints();
		int previousLevel = level - 1;
		int levelCost = xpToNextLevel(previousLevel);

		int currentProgress = Math.round(player.getExp() * xpToNextLevel(level));
		int previousProgress = Math.min(levelCost - 1, Math.round(player.getExp() * levelCost));
		int points = levelCost + currentProgress - previousProgress;
		if (points <= 0 || points > currentXp || points > Integer.MAX_VALUE - storedXp)
			return;

		player.getInventory().setItemInMainHand(setStoredXp(bottle, storedXp + points));
		player.setExperienceLevelAndProgress(currentXp - points);
	}

	private final Set<UUID> blockWithdrawals = new HashSet<>();

	private void withdraw(Player player, ItemStack bottle, int storedXp) {
		if (storedXp <= 0)
			return;

		int level = player.getLevel();
		float progress = player.getExp();
		int currentLevelCost = xpToNextLevel(level);
		int nextLevelCost = xpToNextLevel(level + 1);

		int currentProgress = Math.round(progress * currentLevelCost);
		int nextProgress = Math.min(nextLevelCost - 1, Math.round(progress * nextLevelCost));
		int pointsForOneLevel = currentLevelCost - currentProgress + nextProgress;

		int room = Integer.MAX_VALUE - player.calculateTotalExperiencePoints();
		int points = Math.min(storedXp, Math.min(pointsForOneLevel, room));
		if (points <= 0)
			return;

		player.getInventory().setItemInMainHand(setStoredXp(bottle, storedXp - points));
		player.giveExp(points, true);
	}

	private static int level(int points) {
		int level = 0;
		int cost = xpToNextLevel(level);
		while (points >= cost) {
			points -= cost;
			cost = xpToNextLevel(++level);
		}
		return level;
	}

	private static int xpToNextLevel(int level) {
		if (level >= 30)
			return 112 + (level - 30) * 9;
		if (level >= 15)
			return 37 + (level - 15) * 5;
		return 7 + level * 2;
	}

	static {
		Nexus.registerListener(new ExperienceBottler());
	}
}
