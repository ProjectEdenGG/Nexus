package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import me.pugabyte.nexus.features.events.models.BearFairIsland;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;

public interface BearFair21Island extends BearFairIsland {
	@Override
	default String getEventRegion() {
		return BearFair21.getRegion();
	}

}
