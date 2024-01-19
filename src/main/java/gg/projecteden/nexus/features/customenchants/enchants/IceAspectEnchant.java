package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.features.customenchants.CustomEnchant;
import gg.projecteden.nexus.models.pvp.PVPService;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class IceAspectEnchant extends CustomEnchant implements Listener {
	public IceAspectEnchant(@NotNull NamespacedKey key) {
		super(key);
	}


	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof LivingEntity entity))
			return;

		if (entity instanceof Player victim)
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
		if (isNullOrAir(item))
			return;
		if (event.getCause() == DamageCause.THORNS)
			return;

		int level = getLevel(item);
		if (level == 0)
			return;

		new PotionEffectBuilder(PotionEffectType.SLOW)
			.amplifier(5)
			.duration(5)
			.build()
			.apply(entity);
	}
}
