package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.Tasks;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class OverchargeEnchant extends CustomEnchant implements Listener {

	private static final int FULL_DRAW_TICKS = 20;
	private static final int TICKS_PER_INCREASE = 6;

	private final Map<UUID, Integer> CHARGING = new HashMap<>();

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public boolean conflictsWith(@NotNull Enchantment enchantment) {
		return enchantment.getKey().getKey().contains("barrage");
	}

	@Override
	public List<Material> getSupportedMaterials() {
		return List.of(Material.BOW);
	}

	public OverchargeEnchant() {
		Tasks.repeat(1, 1, this::tickCharging);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerInteractEvent event) {
		if (!event.getAction().isRightClick()) return;
		if (event.useItemInHand() == Result.DENY) return;

		ItemStack item = event.getItem();
		if (item == null || item.getType() != Material.BOW) return;

		if (getLevel(item) <= 0) return;

		CHARGING.putIfAbsent(event.getPlayer().getUniqueId(), 0);
	}

	private void tickCharging() {
		Iterator<Entry<UUID, Integer>> iterator = CHARGING.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<UUID, Integer> entry = iterator.next();
			Player player = Bukkit.getPlayer(entry.getKey());

			if (player == null || !player.isOnline() || !player.hasActiveItem()) {
				iterator.remove();
				continue;
			}

			ItemStack bow = player.getActiveItem();
			int level = getLevel(bow);

			if (bow.getType() != Material.BOW || level <= 0) {
				iterator.remove();
				continue;
			}

			int usedTicks = player.getActiveItemUsedTime();
			if (usedTicks <= FULL_DRAW_TICKS)
				continue;

			int maxArrows = 2 + level;
			int overchargeTicks = usedTicks - FULL_DRAW_TICKS - 1;
			int arrows = Math.min(maxArrows, 1 + overchargeTicks / TICKS_PER_INCREASE);

			Integer charge = entry.getValue();

			while (charge < arrows) {
				CHARGING.put(entry.getKey(), ++charge);
				playChargeClick(player, charge, maxArrows);
			}
		}
	}

	private void playChargeClick(Player player, int arrows, int maxArrows) {
		float progress = (arrows - 1f) / Math.max(1, maxArrows - 1);
		float pitch = .75f + progress * 1.25f;

		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, SoundCategory.PLAYERS, .8f, pitch);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShoot(EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		Integer charge = CHARGING.remove(player.getUniqueId());
		if (charge == null || charge <= 1)
			return;

		ItemStack bow = event.getBow();
		if (bow == null || getLevel(bow) <= 0)
			return;

		if (!(event.getProjectile() instanceof AbstractArrow arrow))
			return;

		// base level - higher levels get > 2
		double rate = charge / 3d;
		arrow.setVelocity(arrow.getVelocity().multiply(1 + rate));
	}


}
