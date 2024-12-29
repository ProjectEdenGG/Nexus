package gg.projecteden.nexus.features.minigames.lobby;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.nexus.features.commands.BoopCommand;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.utils.MinigameNight;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.exceptions.preconfigured.NoPermissionException;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class MinigameInviter {
	private Player inviter;
	private Arena arena;
	private Location location;
	private String message;

	public MinigameInviter create(Player inviter, Arena arena) {
		String message;
		String mechanic = arena.getMechanic().getName();
		if (arena.getName().equalsIgnoreCase(mechanic))
			message = "&3play &e" + arena.getName();
		else
			message = "&3play &e" + mechanic + " &3on &e" + arena.getName();

		return create(inviter, arena, null, message);
	}

	public MinigameInviter create(Player inviter, Location location) {
		return create(inviter, location, "teleport to them");
	}

	public MinigameInviter create(Player inviter, Location location, String message) {
		return create(inviter, null, location, message);
	}

	private MinigameInviter create(Player inviter, Arena arena, Location location, String message) {
		validate(inviter, arena);

		this.inviter = inviter;
		this.arena = arena;
		this.location = location;
		this.message = message;

		return this;
	}

	private void validate(Player inviter, Arena arena) {
		if (!canSendInvite(inviter))
			throw new NoPermissionException();

		if (arena != null) {
			if (arena.getMaxPlayers() == 1)
				throw new InvalidInputException("Cannot invite to " + arena.getDisplayName() + ", max players is 1");

			if (!Minigames.isInMinigameLobby(inviter))
				throw new InvalidInputException("You must be in the Minigame Lobby to use this command");
		}

		if (!new CooldownService().check(UUIDUtils.UUID0, "minigame_invite", TickTime.SECOND.x(3)))
			throw new InvalidInputException("Another minigame invite was recently created, please wait before sending another");
	}

	public static boolean canSendInvite(Player inviter) {
		final boolean staffInMinigames = !OnlinePlayers.where()
			.worldGroup(WorldGroup.MINIGAMES)
			.rank(Rank::isStaff)
			.get()
			.isEmpty();

		return !new MinigameNight().isNow() || !staffInMinigames || inviter.hasPermission("minigames.invite");
	}

	public void inviteLobby() {
		invite(Minigames.getPlayersInLobby().exclude(inviter).get());
	}

	public void inviteAll() {
		invite(OnlinePlayers.where().exclude(inviter).get());
	}

	private void invite(List<Player> players) {
		if (players.size() == 0)
			throw new InvalidInputException("There is no one to invite!");

		PlayerUtils.send(inviter, "&3Invite sent to &e" + players.size() + " &3players to &e" + message);
		players.forEach(this::invite);
		accept(inviter);
	}

	private void invite(Player player) {
		PlayerUtils.send(player, new JsonBuilder("")
			.newline()
			.next(" &e" + Nickname.of(inviter) + " &3has invited you to &e" + message).group()
			.newline()
			.next("&e Click here to &a&laccept")
			.command("/mgm accept")
			.hover("&eClick &3to accept"));

		player.playSound(BoopCommand.SOUND);
	}

	public void accept(Player player) {
		accept(Minigamer.of(player));
	}

	public void accept(Minigamer minigamer) {
		if (arena != null)
			minigamer.join(arena);
		else if (location != null)
			minigamer.teleportAsync(location);
		else
			throw new InvalidInputException("There is no pending minigame invite");
	}
}
