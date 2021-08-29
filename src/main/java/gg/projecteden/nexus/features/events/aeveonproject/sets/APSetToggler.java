package gg.projecteden.nexus.features.events.aeveonproject.sets;

import com.google.common.base.Strings;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;

import static gg.projecteden.nexus.features.events.aeveonproject.AeveonProject.getWGUtils;

public class APSetToggler {

	public APSetToggler() {
		SetToggleTask();
	}

	private void SetToggleTask() {
		Tasks.repeatAsync(0, TickTime.SECOND.x(1), () -> {
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
