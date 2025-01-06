package gg.projecteden.nexus.features.events.aeveonproject.sets;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.aeveonproject.AeveonProject;
import gg.projecteden.nexus.utils.Tasks;

public class APSetToggler {

	public APSetToggler() {
		SetToggleTask();
	}

	private void SetToggleTask() {
		Tasks.repeatAsync(0, TickTime.SECOND.x(1), () -> {
			for (APSetType setType : APSetType.values()) {
				APSet set = setType.get();
				String region = set.getRegion();

				if (!Nullables.isNullOrEmpty(region)) {
					Tasks.sync(() -> {
						int players = AeveonProject.worldguard().getPlayersInRegion(region).size();

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
