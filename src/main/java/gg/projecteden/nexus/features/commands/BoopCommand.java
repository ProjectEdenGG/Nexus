package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

@Description("Boop")
@Cooldown(value = TickTime.SECOND, x = 5, bypass = Group.ADMIN)
public class BoopCommand extends CustomCommand {

	public BoopCommand(CommandEvent event) {
		super(event);
	}

	@Path("all [message...] [--anonymous]")
	@Description("boop all players")
	@Permission(Group.ADMIN)
	void boopAll(String message, @Switch(shorthand = 'a') boolean anonymous) {
		final List<Player> players = OnlinePlayers.where().viewer(player()).get().stream().toList();

		if (players.isEmpty())
			error("No players to boop");

		for (Player player : players) {
			try {
				run(player(), player, message, anonymous);
			} catch (Exception ignore) {}
		}
	}

	@Path("<player> [message...] [--anonymous]")
	@Description("boop a player")
	void boop(Player player, String message, @Switch(shorthand = 'a') boolean anonymous) {
		run(player(), player, message, anonymous);
	}

	public void run(Player booper, Player booped, String message, boolean anonymous) {
		if (message == null)
			message = "";

		if (isSelf(booped))
			error("You cannot boop yourself!");

		if (MuteMenuUser.hasMuted(booper, MuteMenuItem.BOOPS))
			error("You have boops disabled!");

		if (MuteMenuUser.hasMuted(booped, MuteMenuItem.BOOPS))
			error(booped.getName() + " has boops disabled!");

		if (Minigames.isMinigameWorld(booper.getWorld()))
			error("You cannot boop in minigames!");

		String boopedName = Nickname.of(booped);
		if (Minigames.isMinigameWorld(booped.getWorld()))
			error("You cannot boop " + boopedName + ", they are in minigames");

		String toBooper = PREFIX;
		String toBooped = PREFIX;
		if (!message.equalsIgnoreCase(""))
			message = " &3and said &e" + message;

		if (anonymous) {
			toBooper += "&3You anonymously booped &e" + boopedName + message;
			toBooped += "&eSomebody &3booped you" + message;
		} else {
			toBooper += "&3You booped &e" + boopedName + message;
			toBooped += "&e" + nickname() + " &3booped you" + message;
		}

		send(toBooper);
		JsonBuilder json = new JsonBuilder(toBooped);
		if (!anonymous)
			json.next("&3. &eClick to boop back").suggest("/boop " + Nickname.of(booper) + " ");
		booped.sendMessage(booper, json);
		booped.playSound(booped.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10.0F, 0.1F);
	}
}
