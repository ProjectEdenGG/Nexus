package gg.projecteden.nexus.features.minigames.listeners;

import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.minigames.mechanics.common.AntiCampingTask;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.AntiCamp;
import gg.projecteden.nexus.features.minigames.models.annotations.Railgun;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.utils.Gun;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

@NoArgsConstructor
public class AnnotationListener implements Listener {

	@EventHandler
	public void onMatchStart_AntiCamp(MatchStartEvent event) {
		AntiCamp antiCamp = event.getMatch().getMechanic().getAnnotation(AntiCamp.class);
		if (antiCamp != null)
			new AntiCampingTask(event.getMatch());
	}

	@EventHandler
	public void onMatchInitialize_Regeneration(MatchInitializeEvent event) {
		event.getMatch().getArena().regenerate();
	}

	@EventHandler
	public void onMatchEnd_Regeneration(MatchEndEvent event) {
		event.getMatch().getArena().regenerate();
	}

	@EventHandler
	public void onGunShoot(PlayerInteractEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying()) return;
		if (!ActionGroup.RIGHT_CLICK.applies(event)) return;

		Railgun railgun = minigamer.getMatch().getMechanic().getAnnotation(Railgun.class);
		if (railgun == null) return;

		if (!minigamer.getPlayer().getInventory().getItemInMainHand().getType().name().contains("HOE")) return;

		Gun gun = new Gun(minigamer);
		gun.setShouldDamageWithConsole(railgun.damageWithConsole());
		gun.setCooldown(railgun.cooldownTicks());
		gun.shoot();
	}

}
