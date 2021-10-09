package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.CustomEnchant;
import gg.projecteden.nexus.models.pvp.PVPService;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.NotNull;

public class ThunderingBlowEnchant extends CustomEnchant implements Listener {

	public ThunderingBlowEnchant(@NotNull NamespacedKey key) {
		super(key);
		Nexus.registerListener(this);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player victim)
			if (!new PVPService().get(victim).isEnabled())
				return;

		Player player = null;
		if (event.getDamager() instanceof Player damager)
			player = damager;

		if (event.getDamager() instanceof Arrow arrow)
			if (arrow.getShooter() instanceof Player damager)
				player = damager;

		if (player == null)
			return;
		if (event.getCause() == DamageCause.THORNS)
			return;
		if (ItemUtils.isNullOrAir(player.getInventory().getItemInMainHand()))
			return;

		int level = getLevel(player.getInventory().getItemInMainHand());

		while (level > 0) {
			if (RandomUtils.chanceOf(20))
				player.getWorld().strikeLightning(event.getEntity().getLocation());
			--level;
		}
	}

}
