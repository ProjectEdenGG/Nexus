package me.pugabyte.bncore.features.hours;

import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.models.nerds.NerdService;
import me.pugabyte.bncore.utils.Utils;

public class HoursFeature {

	public HoursFeature() {
		scheduler();
	}

	private void scheduler() {
		Utils.repeat(10, 20, () -> {
			for (Nerd nerd : new NerdService().getOnlineNerds()) {
				if (Utils.isAfk(nerd)) continue;

				HoursService hoursService = new HoursService();
				Hours hours = (Hours) hoursService.get(nerd);
				hours.increment();
				hoursService.save(hours);

				if (hours.getTotal() > (60 * 60 * 24)) {
					//if rank == guest
					//    promote
				}
			}
		});
	}

}
