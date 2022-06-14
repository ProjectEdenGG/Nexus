package gg.projecteden.nexus.features.nameplates;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.Tab.Presence;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.push.PushService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
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
	private final PushService pushService = new PushService();
	private final NameplateManager nameplateManager;
	private final Team pushTeam;
	private final Team noPushTeam;
	private final String pushTeamName = "NP_HIDE_PUSH";
	private final String noPushTeamName = "NP_HIDE_NO_PUSH";
	private final static int RADIUS = 75;

	@Getter
	private static boolean debug;

	public Nameplates() {
		nameplateManager = new NameplateManager();

		new NameplatesListener();

		final Scoreboard scoreboard = Nexus.getInstance().getServer().getScoreboardManager().getMainScoreboard();

		this.pushTeam = initializeTeam(scoreboard, pushTeamName, true);
		this.noPushTeam = initializeTeam(scoreboard, noPushTeamName, false);
	}

	private Team initializeTeam(Scoreboard scoreboard, String name, boolean allowPush) {
		Team team = scoreboard.getTeam(name);

		if (team != null)
			team.unregister();

		team = scoreboard.registerNewTeam(pushTeamName);
		team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
		team.setOption(Option.COLLISION_RULE, allowPush ? OptionStatus.ALWAYS : OptionStatus.NEVER);

		return team;
	}

	public static void addToTeam(Player player) {
		Nameplates nameplates = Nameplates.get();

		// determine whether to allow pushing
		boolean allowPushing;
		if (WorldGroup.of(player) == WorldGroup.MINIGAMES)
			allowPushing = false;
		else
			allowPushing = nameplates.getPushService().get(player).isEnabled();

		// set team
		Team addTo = nameplates.teamFor(allowPushing);
		addTo.addEntry(player.getName());
		Team removeFrom = nameplates.teamFor(!allowPushing);
		removeFrom.removeEntry(player.getName());
	}

	public Team teamFor(boolean allowPush) {
		return allowPush ? this.pushTeam : this.noPushTeam;
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
