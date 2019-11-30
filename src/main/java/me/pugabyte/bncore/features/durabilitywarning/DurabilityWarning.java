package me.pugabyte.bncore.features.durabilitywarning;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class DurabilityWarning {
	static final ArrayList<Player> disabledPlayers = new ArrayList<>();

	public DurabilityWarning() {
		new DurabilityWarningListener();
	}

}
