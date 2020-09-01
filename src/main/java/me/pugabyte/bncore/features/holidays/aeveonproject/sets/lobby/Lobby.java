package me.pugabyte.bncore.features.holidays.aeveonproject.sets.lobby;

import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSet;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.Regions;
import me.pugabyte.bncore.features.holidays.annotations.Region;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

import static me.pugabyte.bncore.features.holidays.aeveonproject.APUtils.APLoc;

@Region("lobby")
public class Lobby implements APSet {
	public static final Location shipRobot = APLoc(-1765, 98, -1165);

	public Lobby() {

	}


	@Override
	public List<String> getUpdateRegions() {
		return Collections.singletonList(Regions.lobby_shipColor);
	}
}
