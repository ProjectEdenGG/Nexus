package me.pugabyte.nexus.features.events.aeveonproject.sets;

import com.google.common.base.Strings;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;

import static me.pugabyte.nexus.features.events.aeveonproject.AeveonProject.getWGUtils;

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
						int players = getWGUtils().getPlayersInRegion(region).size();

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
