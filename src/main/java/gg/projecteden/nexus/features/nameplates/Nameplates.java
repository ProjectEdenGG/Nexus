package gg.projecteden.nexus.features.nameplates;

import com.sk89q.worldguard.protection.flags.Flags;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.Tab.Presence;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.hooks.Hook;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.godmode.Godmode;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.push.PushService;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.AccessLevel;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
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
		Tasks.wait(1, () -> {
			this.nameplateManager.onStart();
			fixNPCNameplates();
		});
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
		if (CitizensUtils.isNPC(player))
			return;

		Team oldTeam = TeamAssigner.scoreboard().getPlayerTeam(player);
		Team newTeam = teamAssigners.getOrDefault(player.getUniqueId(), defaultTeamAssigner).teamFor(player);

		if (oldTeam != null && !oldTeam.equals(newTeam))
			oldTeam.removePlayer(player);
		newTeam.addPlayer(player);
	}

	/**
	 * Updates the {@link Team} of a {@link NPC} if it is a {@link Player}.
	 *
	 * @param npc the npc to update the team of
	 */
	public void updateTeamOf(@NotNull NPC npc) {
		if (npc.getEntity() instanceof Player player)
			Features.get(Nameplates.class).updateTeamOf(player);
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

	public static JsonBuilder of(Player target, Player viewer) {
		final Minigamer minigamer = Minigamer.of(target);
		JsonBuilder nameplate = null;
		if (minigamer.isPlaying())
			nameplate = minigamer.getMatch().getMechanic().getNameplate(minigamer, Minigamer.of(viewer));

		if (nameplate == null)
			nameplate = new JsonBuilder()
				.next(Presence.of(target).ingame())
				.next(" ")
				.next(Nerd.of(target).getChatFormat(new ChatterService().get(viewer)));

		if (showHearts(target))
			nameplate.next(getHealthFormatted(target));

		return nameplate;
	}

	private static boolean showHearts(Player player) {
		if (!GameModeWrapper.of(player).isSurvival())
			return false;

		if (Godmode.of(player).isEnabled())
			return false;

		if (Vanish.isVanished(player))
			return false;

		if (WorldGuardFlagUtils.test(player, Flags.INVINCIBILITY))
			return false;

		final Minigamer minigamer = Minigamer.of(player);
		if (minigamer.isPlaying())
			if (!minigamer.getMatch().getMechanic().shouldShowHealthInNameplate())
				return false;

		return true;
	}

	public static DecimalFormat HP_FORMAT = new DecimalFormat("#.0");

	public static String getHealthFormatted(Player target) {
		return " &#cccccc" + HP_FORMAT.format(target.getHealth()) + " &f♥";
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

	public static void fixNPCNameplates() {
		for (NPC npc : Hook.CITIZENS.getRegistry()) {
			if (!npc.isSpawned())
				continue;

			if (!(npc.getEntity() instanceof Player player))
				continue;

			Features.get(Nameplates.class).updateTeamOf(player);
		}
	}

}
