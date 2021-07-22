package gg.projecteden.nexus.features.achievements.listeners;

import gg.projecteden.nexus.models.achievement.Achievement;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class CombatListener implements Listener {

	@EventHandler
	public void onPlayerKillEntity(EntityDeathEvent event) {
		Player player = event.getEntity().getKiller();
		if (player == null) return;

		switch (event.getEntity().getType()) {
			case ENDER_DRAGON:
				Achievement.KILL_DA_DRAGON.check(player);
				Achievement.DRAGON_SLAYER.check(player);
				break;
			case WITHER:
				Achievement.WITHERED.check(player);
				break;
			case ELDER_GUARDIAN:
				Achievement.GUARDIANS_OF_THE_DEEP.check(player);
				Achievement.WATCHA_GUARDIAN.check(player);
				break;
			case EVOKER:
				Achievement.EVOKING_A_MEMORY.check(player);
				break;
			case PLAYER:
				if (new WorldGuardUtils(player).getRegionNamesAt(player.getLocation()).contains("survivalpvp_arena"))
					Achievement.SHOWING_DOMINANCE.check(player);
				break;
		}
	}

	@EventHandler
	public void onPlayerShootThemselves(ProjectileHitEvent event) {
		if (event.getHitEntity() instanceof Player victim && event.getEntity().getShooter() instanceof Player damager) {
			if (victim.getUniqueId().toString().equals(damager.getUniqueId().toString()))
				Achievement.WRONG_WAY.check((Player) event.getHitEntity());
		}
	}

	@EventHandler
	public void onPlayerDeath(EntityResurrectEvent event) {
		if (event.getEntity() instanceof Player player) {
			if (WorldGroup.of(player) != WorldGroup.SURVIVAL) return;
			Achievement.AVOIDING_DEATH.check(player);
		}
	}

}
