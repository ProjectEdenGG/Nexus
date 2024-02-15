package gg.projecteden.nexus.features.titan;

import gg.projecteden.nexus.features.listeners.events.WorldGroupChangedEvent;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.titan.clientbound.ResetMinigame;
import gg.projecteden.nexus.features.titan.clientbound.UpdateState;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ClientboundListeners implements Listener {

	@EventHandler
	public void onGameModeChange(PlayerGameModeChangeEvent event) {
		ClientMessage.builder()
			.players(event.getPlayer())
			.message(UpdateState.builder()
				.mode(event.getNewGameMode().name())
				.build())
			.send();
	}

	@EventHandler
	public void onWorldChange(WorldGroupChangedEvent event) {
		ClientMessage.builder()
			.players(event.getPlayer())
			.message(UpdateState.builder()
				.worldGroup(StringUtils.camelCase(event.getNewWorldGroup()))
				.build())
			.send();
	}

	@EventHandler
	public void onMinigameStart(MatchStartEvent event) {
		ClientMessage.builder()
			.players(event.getMatch().getOnlinePlayers())
			.message(UpdateState.builder()
				.arena(ArenaManager.getAll(event.getMatch().getMechanic().getMechanicType()).size() == 1 ? null : event.getMatch().getArena().getDisplayName())
				.mechanic(event.getMatch().getMechanic().getName())
				.build())
			.send();
	}

	@EventHandler
	public void onMinigameEnd(MatchEndEvent event) {
		ClientMessage.builder()
			.players(event.getMatch().getOnlinePlayers())
			.message(new ResetMinigame())
			.send();
	}


}
