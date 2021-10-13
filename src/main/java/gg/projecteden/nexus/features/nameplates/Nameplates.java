package gg.projecteden.nexus.features.nameplates;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.Tab.Presence;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import me.lucko.helper.scoreboard.ScoreboardTeam.NameTagVisibility;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.utils.PlayerUtils.canSee;

@Getter
public class Nameplates extends Feature {
	private final NameplateManager nameplateManager;
	private final Team team;
	private final String teamName = "NP_HIDE";
	private final boolean manageTeams = true;
	private final static int RADIUS = 75;

	@Getter
	private static boolean debug;

	public Nameplates() {
		nameplateManager = new NameplateManager();

		new NameplatesListener();

		final Scoreboard scoreboard = Nexus.getInstance().getServer().getScoreboardManager().getMainScoreboard();

		Team team;

		if (manageTeams) {
			team = scoreboard.getTeam(teamName);
			if (team != null)
				team.unregister();

			team = scoreboard.registerNewTeam(teamName);
			team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
		} else {
			team = scoreboard.getTeam(teamName);
			if (team != null)
				team.unregister();
		}

		this.team = team;
	}

	public static void addToTeam(Player player) {
		if (Nameplates.get().isManageTeams())
			Nameplates.get().getTeam().addEntry(player.getName());
	}

	@Override
	public void onStart() {
		Tasks.wait(1, this.nameplateManager::onStart);
	}

	@Override
	public void onStop() {
		this.nameplateManager.shutdown();
	}

	public static void toggleDebug() {
		debug = !debug;
	}

	public static void debug(String message) {
		if (debug)
			Nexus.log("[Nameplates] [DEBUG] " + message);
	}

	public static Nameplates get() {
		return Features.get(Nameplates.class);
	}

	public static String of(Player player, Player viewer) {
		final Presence presence = Presence.of(player);
		final Minigamer minigamer = PlayerManager.get(player);

		final JsonBuilder nameplate = new JsonBuilder();
		if (minigamer.isPlaying())
			if (minigamer.getTeam() != null && minigamer.getTeam().getNameTagVisibility() == NameTagVisibility.NEVER)
				nameplate.next(minigamer.getNickname());
			else
				nameplate.next(minigamer.getColoredName());
		else
			nameplate.next(presence.ingame()).next(" ").next(Nerd.of(player).getChatFormat(new ChatterService().get(viewer)));

		return nameplate.serialize();
	}

	private static OnlinePlayers getNearbyPlayers(@NotNull Player holder) {
		return OnlinePlayers.where()
			.world(holder.getWorld())
			.radius(RADIUS);
	}

	@NotNull
	public static OnlinePlayers getViewers(@NotNull Player holder) {
		return getNearbyPlayers(holder)
			.filter(viewer -> holder.getGameMode() != GameMode.SPECTATOR || viewer.getGameMode() == GameMode.SPECTATOR)
			.filter(viewer -> canSee(viewer, holder));
	}

	@NotNull
	public static OnlinePlayers getViewable(@NotNull Player viewer) {
		return getNearbyPlayers(viewer)
			.filter(holder -> holder.getGameMode() != GameMode.SPECTATOR || viewer.getGameMode() == GameMode.SPECTATOR)
			.filter(holder -> canSee(viewer, holder));
	}

}
