package gg.projecteden.nexus.features.nameplates;

import gg.projecteden.api.interfaces.HasUniqueId;
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
import lombok.AccessLevel;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.nexus.utils.PlayerUtils.canSee;

@Getter
public class Nameplates extends Feature {
	private final PushService pushService = new PushService();
	private final NameplateManager nameplateManager;
	private final TeamAssigner defaultTeamAssigner = new PushTeamAssigner();
	@Getter(value = AccessLevel.NONE)
	private final Map<UUID, TeamAssigner> teamAssigners = new HashMap<>();
	private final static int RADIUS = 75;

	@Getter
	private static boolean debug;

	public Nameplates() {
		this.nameplateManager = new NameplateManager();
		new NameplatesListener();
	}

	@Override
	public void onStart() {
		Tasks.wait(1, this.nameplateManager::onStart);
	}

	@Override
	public void onStop() {
		this.nameplateManager.shutdown();
	}

	/**
	 * Updates the {@link Team} of a {@link Player}.
	 *
	 * @param player the player to update the team of
	 */
	public void updateTeamOf(@NotNull Player player) {
		Team oldTeam = TeamAssigner.scoreboard().getPlayerTeam(player);
		Team newTeam = teamAssigners.getOrDefault(player.getUniqueId(), defaultTeamAssigner).teamFor(player);

		if (oldTeam != null && !oldTeam.equals(newTeam))
			oldTeam.removePlayer(player);
		newTeam.addPlayer(player);
	}

	/**
	 * Returns whether the player's team is being handled by a dedicated team assigner.
	 *
	 * @param player the player to check for a team assigner
	 * @return whether the player's team is being handled by a dedicated team assigner
	 */
	public boolean hasTeamAssigner(@NotNull HasUniqueId player) {
		return teamAssigners.containsKey(player.getUniqueId());
	}

	/**
	 * Registers a team assigner for a player.
	 *
	 * @param player       the player to register the team assigner for
	 * @param teamAssigner the team assigner to register
	 * @throws IllegalArgumentException if the default team assigner was provided
	 * @return {@code true} if the team assigner was registered,
	 *         {@code false} if another team assigner was already registered for the player
	 */
	public boolean registerTeamAssigner(@NotNull HasUniqueId player, @NotNull TeamAssigner teamAssigner) {
		UUID uuid = player.getUniqueId();
		if (teamAssigners.containsKey(uuid))
			return false;
		if (teamAssigner == defaultTeamAssigner)
			throw new IllegalArgumentException("The default team assigner should not be registered");
		teamAssigners.put(uuid, teamAssigner);
		return true;
	}

	/**
	 * Unregisters a team assigner for a player.
	 *
	 * @param player the player to unregister the team assigner for
	 * @return whether the player had a team assigner
	 */
	public boolean unregisterTeamAssigner(@NotNull HasUniqueId player) {
		return teamAssigners.remove(player.getUniqueId()) != null;
	}

	/**
	 * Unregisters a specific team assigner for a player.
	 *
	 * @param player       the player to unregister the team assigner for
	 * @param teamAssigner the team assigner to unregister
	 * @return whether the player had the team assigner
	 */
	public boolean unregisterTeamAssigner(@NotNull HasUniqueId player, @NotNull TeamAssigner teamAssigner) {
		return teamAssigners.remove(player.getUniqueId(), teamAssigner);
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
