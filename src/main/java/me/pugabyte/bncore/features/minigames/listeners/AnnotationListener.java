package me.pugabyte.bncore.features.minigames.listeners;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.mechanics.common.AntiCampingTask;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.annotations.AntiCamp;
import me.pugabyte.bncore.features.minigames.models.annotations.Regenerating;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchInitializeEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
		String name = match.getArena().getMechanic().getName().toLowerCase();
		Minigames.getWorldGuardUtils().getRegionsLike(name + "_" + match.getArena().getName() + "_" + type + "_[0-9]+")
				.forEach(region -> {
					String file = (name + "/" + region.getId().replaceFirst(name + "_", "")).toLowerCase();
					Minigames.getWorldEditUtils().paste(file, region.getMinimumPoint());
				});
	}

}
