package me.pugabyte.bncore.features.events.aeveonproject.sets;

import com.google.common.base.Strings;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;

import static me.pugabyte.bncore.features.events.aeveonproject.AeveonProject.WGUtils;

public class APSetToggler {

	public APSetToggler() {
		SetToggleTask();
	}

	private void SetToggleTask() {
		Tasks.repeatAsync(0, Time.SECOND.x(1), () -> {
			for (APSetType setType : APSetType.values()) {
				APSet set = setType.get();
				String region = set.getRegion();

				if (!Strings.isNullOrEmpty(region)) {
					Tasks.sync(() -> {
						int players = WGUtils.getPlayersInRegion(region).size();

						if (set.isActive() && players == 0)
							set.setActive(false);

						else if (!set.isActive() && players > 0)
							set.setActive(true);
					});
				}
			}
		});
	}
}
