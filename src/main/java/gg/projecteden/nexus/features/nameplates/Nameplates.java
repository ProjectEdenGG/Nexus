package gg.projecteden.nexus.features.nameplates;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.Tab.Presence;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
			team.setCanSeeFriendlyInvisibles(false);
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

	public static String of(Player target, Player viewer) {
		// get minigame nameplate
		Component name = getMinigamerNameplate(target, viewer);
		// use default nameplate if minigame nameplate is null
		if (name == null) {
			final JsonBuilder nameplate = new JsonBuilder();
			final Presence presence = Presence.of(target);
			nameplate.next(presence.ingame()).next(" ").next(Nerd.of(target).getChatFormat(new ChatterService().get(viewer)));
			name = nameplate.build();
		}
		// serialize & return
		return GsonComponentSerializer.gson().serialize(name);
	}

	@Nullable
	private static Component getMinigamerNameplate(Player target, Player viewer) {
		final Minigamer targetMinigamer = Minigamer.of(target);
		if (!targetMinigamer.isPlaying())
			return null;
		return targetMinigamer.getMatch().getMechanic().getNameplate(targetMinigamer, Minigamer.of(viewer));
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
