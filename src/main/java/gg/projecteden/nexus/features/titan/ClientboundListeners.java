package gg.projecteden.nexus.features.titan;

import gg.projecteden.nexus.features.listeners.events.WorldGroupChangedEvent;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.titan.clientbound.ResetMinigame;
import gg.projecteden.nexus.features.titan.clientbound.UpdateState;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.features.vanish.events.VanishToggleEvent;
import gg.projecteden.nexus.models.afk.events.NotAFKEvent;
import gg.projecteden.nexus.models.afk.events.NowAFKEvent;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.utils.StringUtils;
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

	@EventHandler
	public void onMinigameQuit(MatchQuitEvent event) {
		ClientMessage.builder()
			.players(event.getMinigamer().getPlayer())
			.message(new ResetMinigame())
			.send();
	}

	@EventHandler
	public void onVanish(VanishToggleEvent event) {
		ClientMessage.builder()
			.players(event.getPlayer())
			.message(UpdateState.builder()
				.vanished(Vanish.isVanished(event.getPlayer()))
				.build())
			.send();
	}

	@EventHandler
	public void onAFK(NowAFKEvent event) {
		ClientMessage.builder()
			.players(event.getUser().getPlayer())
			.message(UpdateState.builder()
				.afk(event.getUser().isAfk())
				.build())
			.send();
	}

	@EventHandler
	public void onAFK(NotAFKEvent event) {
		ClientMessage.builder()
			.players(event.getUser().getPlayer())
			.message(UpdateState.builder()
				.afk(event.getUser().isAfk())
				.build())
			.send();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Chatter.of(event.getPlayer().getUniqueId()).notifyTitanOfChannelChange();
	}


}
