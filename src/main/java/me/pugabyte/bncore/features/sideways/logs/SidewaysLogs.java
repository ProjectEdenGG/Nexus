package me.pugabyte.bncore.features.sideways.logs;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class SidewaysLogs {
	static Set<Player> enabledPlayers = new HashSet<>();

	public SidewaysLogs() {
		new SidewaysLogsCommand();
		new SidewaysLogsListener();
	}

}
