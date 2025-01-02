package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.models.pvp.PVPService;
import gg.projecteden.nexus.utils.Nullables;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class VampireEnchant extends CustomEnchant implements Listener {

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player victim)
			if (!new PVPService().get(victim).isEnabled())
				return;

		Player player = null;
		ItemStack item = null;
		if (event.getDamager() instanceof Player damager) {
			player = damager;
			item = player.getInventory().getItemInMainHand();
		}

		if (event.getDamager() instanceof ThrowableProjectile projectile)
			if (projectile.getShooter() instanceof Player damager) {
				player = damager;
				item = projectile.getItem();
			}

		if (player == null)
			return;
		if (Nullables.isNullOrAir(item))
			return;
		if (event.getCause() == DamageCause.THORNS)
			return;

		int level = getLevel(item);
		if (level == 0)
			return;

		double newHealth = player.getHealth() + (.5 * level);
		double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
		player.setHealth(Math.min(newHealth, maxHealth));
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}
}
