package gg.projecteden.nexus.features.mcmmo.resetnew.skills.archery;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import org.bukkit.Particle;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class HeadshotHandler implements Listener {

	public static final String PERMISSION = "nexus.archeryheadshots";

	@EventHandler(ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof AbstractArrow arrow)) return;
		if (!(arrow.getShooter() instanceof Player player)) return;
		if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
		if (event.getEntity() instanceof ArmorStand) return;

		if (arrow.getLocation().distance(livingEntity.getEyeLocation()) > .3 && arrow.getLocation().getY() <= livingEntity.getEyeLocation().getY()) return;

		if (!player.hasPermission(PERMISSION)) return;
		event.setDamage(event.getDamage() * 2);
		new ParticleBuilder(Particle.CRIT)
			.offset(.3, .3, .3)
			.extra(.1)
			.count(10)
			.location(livingEntity.getEyeLocation())
			.allPlayers()
			.spawn();
	}

}
