package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import me.pugabyte.nexus.features.events.models.BearFairIsland;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import org.bukkit.event.Listener;

public class PugmasIsland implements Listener, BearFairIsland {
	@Override
	public String getEventRegion() {
		return BearFair21.getRegion();
	}
}
