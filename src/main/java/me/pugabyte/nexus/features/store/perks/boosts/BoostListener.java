package me.pugabyte.nexus.features.store.perks.boosts;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import me.pugabyte.nexus.models.boost.BoostConfig;
import me.pugabyte.nexus.models.boost.Boostable;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class BoostListener implements Listener {

	private double get(Boostable experience) {
		return BoostConfig.multiplierOf(experience);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onMcMMOExpGain(McMMOPlayerXpGainEvent event) {
		event.setRawXpGained((float) (event.getRawXpGained() * get(Boostable.MCMMO_EXPERIENCE)));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onExpGain(PlayerPickupExperienceEvent event) {
		ExperienceOrb orb = event.getExperienceOrb();
		orb.setExperience((int) (orb.getExperience() * get(Boostable.EXPERIENCE)));
	}

}
