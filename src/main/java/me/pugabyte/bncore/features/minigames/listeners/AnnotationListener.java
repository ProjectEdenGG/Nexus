package me.pugabyte.bncore.features.minigames.listeners;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.mechanics.common.AntiCampingTask;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.AntiCamp;
import me.pugabyte.bncore.features.minigames.models.annotations.Railgun;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchInitializeEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.utils.Gun;
import me.pugabyte.bncore.utils.Utils.ActionGroup;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

@NoArgsConstructor
public class AnnotationListener implements Listener {

	@EventHandler
	public void onMatchStart_AntiCamp(MatchStartEvent event) {
		AntiCamp antiCamp = event.getMatch().getArena().getMechanic().getAnnotation(AntiCamp.class);
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

		Railgun railgun = minigamer.getMatch().getArena().getMechanic().getAnnotation(Railgun.class);
		if (railgun == null) return;

		if (!minigamer.getPlayer().getInventory().getItemInMainHand().getType().name().contains("HOE")) return;

		Gun gun = new Gun(minigamer);
		gun.setShouldDamageWithConsole(railgun.damageWithConsole());
		gun.setCooldown(railgun.cooldownTicks());
		gun.shoot();
	}

}
