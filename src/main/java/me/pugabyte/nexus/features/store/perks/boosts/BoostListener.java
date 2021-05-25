package me.pugabyte.nexus.features.store.perks.boosts;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.models.boost.BoostConfig;
import me.pugabyte.nexus.models.boost.Boostable;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Set;

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
		orb.setExperience((int) Math.round(orb.getExperience() * get(Boostable.EXPERIENCE)));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
		Tasks.wait(Time.SECOND.x(2), () -> {
			if (!event.getPlayer().isOnline())
				return;

			Set<Boostable> boosts = BoostConfig.get().getBoosts().keySet();
			if (boosts.isEmpty())
				return;

			PlayerUtils.runCommand(event.getPlayer(), "boosts");
		});
	}

}
