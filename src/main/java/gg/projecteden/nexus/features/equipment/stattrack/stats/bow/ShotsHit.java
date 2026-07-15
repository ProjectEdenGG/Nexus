package gg.projecteden.nexus.features.equipment.stattrack.stats.bow;

import gg.projecteden.nexus.features.equipment.stattrack.StatTrack;
import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.UUID;

@Id("shots_hit")
@DisplayName("Shots Hit")
public class ShotsHit extends StatTrackStatistic {

	@Override
	public MaterialTag getApplicableTools() {
		return new MaterialTag(Material.BOW, Material.CROSSBOW);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getFinalDamage() <= 0)
			return;

		if (!(event.getDamager() instanceof Projectile projectile))
			return;

		if (!(projectile instanceof AbstractArrow))
			return;

		ProjectileSource shooter = projectile.getShooter();
		if (!(shooter instanceof Player))
			return;

		String storedId = projectile.getPersistentDataContainer().get(StatTrack.TRACKING_KEY, PersistentDataType.STRING);
		if (storedId == null)
			return;

		UUID statTrackId;
		try { statTrackId = UUID.fromString(storedId); }
		catch (IllegalArgumentException ignore) { return; }

		track(statTrackId, (int) event.getFinalDamage());
	}

}
