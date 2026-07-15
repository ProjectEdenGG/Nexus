package gg.projecteden.nexus.features.equipment.stattrack.stats.melee;

import gg.projecteden.nexus.features.equipment.stattrack.StatTrack;
import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.MaterialTag.MatchMode;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.UUID;

@Id("mobs_killed")
@DisplayName("Mobs Killed")
public class MobsKilled extends StatTrackStatistic {

	@Override
	public MaterialTag getApplicableTools() {
		return new MaterialTag("_SWORD", MatchMode.SUFFIX)
			.append(Material.BOW, Material.CROSSBOW, Material.MACE).append(MaterialTag.SPEARS);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getFinalDamage() <= 0)
			return;

		if (!(event.getEntity() instanceof LivingEntity livingEntity))
			return;

		if (event.getFinalDamage() < livingEntity.getHealth())
			return;

		if (event.getDamager() instanceof Player player) {
			handleMeleeDamage(player);
			return;
		}

		if (event.getDamager() instanceof Projectile projectile)
			handleProjectileDamage(projectile);
	}

	private void handleMeleeDamage(Player player) {
		ItemStack weapon = ItemUtils.getTool(player);
		track(weapon);
	}

	private void handleProjectileDamage(Projectile projectile) {
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

		track(statTrackId, 1);
	}

}
