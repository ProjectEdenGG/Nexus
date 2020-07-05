package me.pugabyte.bncore.features.minigames.listeners;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.mechanics.common.AntiCampingTask;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.AntiCamp;
import me.pugabyte.bncore.features.minigames.models.annotations.Railgun;
import me.pugabyte.bncore.features.minigames.models.annotations.Regenerating;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchInitializeEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.bncore.features.minigames.utils.Gun;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.List;

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
		for (Class<? extends Mechanic> mechanic : event.getMatch().getArena().getMechanic().getSuperclasses()) {
			Regenerating annotation = mechanic.getAnnotation(Regenerating.class);
			if (annotation != null)
				for (String type : annotation.value())
					regenerate(event.getMatch(), type);
		}
	}

	@EventHandler
	public void onMatchEnd_Regeneration(MatchEndEvent event) {
		for (Class<? extends Mechanic> mechanic : event.getMatch().getArena().getMechanic().getSuperclasses()) {
			Regenerating annotation = mechanic.getAnnotation(Regenerating.class);
			if (annotation != null)
				for (String type : annotation.value())
					regenerate(event.getMatch(), type);
		}
	}

	private void regenerate(Match match, String type) {
		String name = match.getArena().getRegionBaseName().split("_")[0];
		String regex = match.getArena().getRegionTypeRegex(type);

		match.getArena().getWGUtils().getRegionsLike(regex).forEach(region -> {
			String file = name + "/" + region.getId().replaceFirst(name + "_", "");
			Minigames.getWorldEditUtils().paster().file(file.toLowerCase()).at(region.getMinimumPoint()).paste();
		});
	}

	@EventHandler
	public void onGunShoot(PlayerInteractEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying()) return;

		List<Action> actions = Arrays.asList(Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR);
		if (!actions.contains(event.getAction())) return;

		Railgun railgun = minigamer.getMatch().getArena().getMechanic().getAnnotation(Railgun.class);
		if (railgun == null) return;

		if (!minigamer.getPlayer().getInventory().getItemInMainHand().getType().name().contains("HOE")) return;

		Gun gun = new Gun(minigamer);
		gun.setShouldDamageWithConsole(railgun.damageWithConsole());
		gun.setCooldown(railgun.cooldownTicks());
		gun.shoot();
	}

}
