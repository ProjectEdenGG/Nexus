package me.pugabyte.bncore.features.minigames.listeners;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MatchListener implements Listener {

	public MatchListener() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onMatchQuit(MatchQuitEvent event) {
		MatchManager.janitor();
	}

	// TODO: Add quit/kick listener


}
