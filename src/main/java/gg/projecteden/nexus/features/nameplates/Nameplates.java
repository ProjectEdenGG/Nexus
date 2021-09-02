package gg.projecteden.nexus.features.nameplates;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.Tab;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.nameplates.protocol.FakeEntityManager;
import gg.projecteden.nexus.features.nameplates.protocol.ProtocolManager;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.Env;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.jetbrains.annotations.NotNull;

@Getter
@Environments({Env.DEV, Env.TEST})
public class Nameplates extends Feature implements Listener {
	private final ProtocolManager protocolManager;
	private final FakeEntityManager fakeEntityManager;
	private final Team team;
	private final String teamName = "NP_HIDE";
	private final boolean manageTeams = true;

	public Nameplates() {
		protocolManager = new ProtocolManager();
		fakeEntityManager = new FakeEntityManager();

		final Scoreboard scoreboard = Nexus.getInstance().getServer().getScoreboardManager().getMainScoreboard();

		Team team;

		if (manageTeams) {
			team = scoreboard.getTeam(teamName);
			if (team != null)
				team.unregister();

			team = scoreboard.registerNewTeam(teamName);
			team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
		} else {
			team = scoreboard.getTeam(teamName);
			if (team != null)
				team.unregister();
		}

		this.team = team;
	}

	public static Nameplates get() {
		return Features.get(Nameplates.class);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (manageTeams)
			team.addEntry(player.getName());

		fakeEntityManager.addPlayer(player);
		Tasks.waitAsync(10, () -> {
			fakeEntityManager.spawnFakeEntityForSelf(player);
			fakeEntityManager.updateFakeEntityAroundPlayer(player);
		});
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerKickEvent event) {
		Player player = event.getPlayer();
		fakeEntityManager.removeFakeEntityAroundPlayer(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		fakeEntityManager.removeFakeEntityAroundPlayer(player);
		fakeEntityManager.removeFromCache(player);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		fakeEntityManager.removeFakeEntityAroundPlayer(player);
		Tasks.waitAsync(10, () -> fakeEntityManager.spawnFakeEntityForSelf(player));
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(PlayerGameModeChangeEvent event) {
		Player player = event.getPlayer();
		fakeEntityManager.removeFakeEntityAroundPlayer(player);
		Tasks.waitAsync(10, () -> fakeEntityManager.spawnFakeEntityForSelf(player));
	}

	public static String of(Player player, Player viewer) {
		final Minigamer minigamer = PlayerManager.get(player);
		String nameplate = Nerd.of(player).getChatFormat();

		if (minigamer.isPlaying())
			nameplate = minigamer.getColoredName();

		nameplate = Tab.addStateTags(player, nameplate);

		return new JsonBuilder(nameplate).serialize();
	}

	public static String jsonFormatSimple(@NotNull String text) {
		return "{\"text\":\"" + text + "\",\"font\":\"minecraft:default\"}";
	}

	public static String jsonArray(@NotNull String... texts) {
		StringBuilder builder = new StringBuilder("[");

		for (String text : texts)
			builder.append(text).append(",");

		builder.deleteCharAt(builder.length() - 1).append("]");
		return builder.toString();
	}
}
