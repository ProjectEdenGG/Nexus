package me.pugabyte.nexus.features.commands;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.Switch;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

@Description("Boop")
@Cooldown(value = @Part(value = Time.SECOND, x = 5), bypass = "group.admin")
public class BoopCommand extends CustomCommand {

	public BoopCommand(CommandEvent event) {
		super(event);
	}

	@Path("all [message...] [--anonymous]")
	@Description("boop all players")
	@Permission("group.admin")
	void boopAll(String message, @Switch(shorthand = 'a') boolean anonymous) {
		final List<Player> players = PlayerUtils.getOnlinePlayers(player()).stream()
			.filter(player -> !isSelf(player) && !Minigames.isMinigameWorld(player.getWorld()))
			.toList();

		if (players.isEmpty())
			error("No players to boop");

		for (Player player : players)
			run(player(), player, message, anonymous);
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

		if (Minigames.isMinigameWorld(booper.getWorld()))
			error("You cannot boop in minigames!");

		if (Minigames.isMinigameWorld(booped.getWorld()))
			error("You cannot boop " + booped.getName() + " (in minigames)");

		String toBooper = PREFIX;
		String toBooped = PREFIX;
		if (!message.equalsIgnoreCase(""))
			message = " &3and said &e" + message;

		if (anonymous) {
			toBooper += "&3You anonymously booped &e" + Nickname.of(booped) + message;
			toBooped += "&eSomebody &3booped you" + message;
		} else {
			toBooper += "&3You booped &e" + Nickname.of(booped) + message;
			toBooped += "&e" + nickname() + " &3booped you" + message;
		}

		send(toBooper);
		JsonBuilder json = new JsonBuilder(toBooped);
		if (!anonymous)
			json.next("&3. &eClick to boop back").suggest("/boop " + booper.getName() + " ");
		send(booped, json);
		booped.playSound(booped.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10.0F, 0.1F);
	}
}
