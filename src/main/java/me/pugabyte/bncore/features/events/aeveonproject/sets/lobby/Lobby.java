package me.pugabyte.bncore.features.events.aeveonproject.sets.lobby;

import me.pugabyte.bncore.features.events.aeveonproject.sets.APRegions;
import me.pugabyte.bncore.features.events.aeveonproject.sets.APSet;
import me.pugabyte.bncore.features.events.annotations.Region;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

import static me.pugabyte.bncore.features.events.aeveonproject.APUtils.APLoc;

@Region("lobby")
public class Lobby implements APSet {
	public boolean active = false;
	public static final Location shipRobot = APLoc(-1765, 98, -1165);

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
