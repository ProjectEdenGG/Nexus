package gg.projecteden.nexus.features.events.aeveonproject.sets.lobby;

import gg.projecteden.nexus.features.events.aeveonproject.APUtils;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APRegions;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APSet;
import gg.projecteden.nexus.features.events.annotations.Region;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

@Region("lobby")
public class Lobby implements APSet {
	public boolean active = false;
	public static final Location shipRobot = APUtils.APLoc(-1765, 98, -1165);

	public Lobby() {

	}

	@Override
	public List<String> getUpdateRegions() {
		return Collections.singletonList(APRegions.lobby_shipColor);
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean bool) {
		active = bool;
	}
}
