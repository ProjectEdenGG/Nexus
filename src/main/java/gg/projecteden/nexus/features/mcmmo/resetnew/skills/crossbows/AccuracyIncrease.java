package gg.projecteden.nexus.features.mcmmo.resetnew.skills.crossbows;

import gg.projecteden.nexus.utils.ArrowSnapshot;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.parchment.event.entity.PreEntityShootBowEvent;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

public class AccuracyIncrease implements Listener {

	@EventHandler
	public void onPreShoot(PreEntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (ItemUtils.getTool(player).getType() != Material.CROSSBOW) return;
		if (!player.hasPermission("nexus.crossbowaccuracy")) return;

		event.setRelative(false);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onShootArrow(EntityShootBowEvent event) {
		if (!(event.getProjectile() instanceof AbstractArrow arrow)) return;
		if (!(arrow.getShooter() instanceof Player player)) return;
		if (event.getBow() == null || event.getBow().getType() != Material.CROSSBOW) return;
		if (!player.hasPermission("nexus.crossbowaccuracy")) return;

		ArrowSnapshot snapshot = ArrowSnapshot.of(arrow);

		AbstractArrow arrow2 = snapshot.world().spawnArrow(arrow.getLocation(), arrow.getVelocity(), snapshot.speed(), 0);
		snapshot.apply(arrow2);
		event.setProjectile(arrow2);

		arrow.remove();
	}

}
