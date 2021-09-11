package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.CustomEnchant;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class ThunderingBlowEnchant extends CustomEnchant implements Listener {

	public ThunderingBlowEnchant(@NotNull NamespacedKey key) {
		super(key);
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		Player player = null;
		if (event.getDamager() instanceof Player)
			player = (Player) event.getDamager();
		if (event.getDamager() instanceof Arrow)
			if (((Arrow) event.getDamager()).getShooter() instanceof Player)
				player = (Player) ((Arrow) event.getDamager()).getShooter();
		if (player == null)
			return;
		if (event.getCause() == EntityDamageEvent.DamageCause.THORNS)
			return;
		if (ItemUtils.isNullOrAir(player.getInventory().getItemInMainHand()))
			return;
		int level = getLevel(player.getInventory().getItemInMainHand());
		while (level > 0) {
			if (RandomUtils.chanceOf(20)) {
				player.getWorld().strikeLightning(event.getEntity().getLocation());
			}
			--level;
		}
	}



}
