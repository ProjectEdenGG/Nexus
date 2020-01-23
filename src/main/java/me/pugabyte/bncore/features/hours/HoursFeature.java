package me.pugabyte.bncore.features.hours;

import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.models.Rank;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HoursFeature {

	public HoursFeature() {
		scheduler();
	}

	private void scheduler() {
		Utils.repeat(10, 5 * 20, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (AFK.get(player).isAfk()) continue;

				HoursService service = new HoursService();
				Hours hours = service.get(player);
				hours.increment();
				service.save(hours);

				if (hours.getTotal() > (60 * 60 * 24)) {
					if (Rank.getHighestRank(player) == Rank.GUEST) {
						// promote
					}
				}
			}
		});
	}

}
