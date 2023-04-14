package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Vararg;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Cooldown;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Switch;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;

import java.util.List;

@Cooldown(value = TickTime.SECOND, x = 5, bypass = Group.ADMIN)
public class BoopCommand extends CustomCommand {

	public static final Sound SOUND = Sound.sound(
		org.bukkit.Sound.BLOCK_NOTE_BLOCK_XYLOPHONE,
		Sound.Source.MASTER,
		1f,
		0.1f
	);

	public BoopCommand(CommandEvent event) {
		super(event);
	}

	@Description("Boop all players")
	@Permission(Group.ADMIN)
	void all(@Optional @Vararg String message, @Switch(shorthand = 'a') @Optional boolean anonymous) {
		final List<Player> players = OnlinePlayers.where().viewer(player()).get().stream().toList();

		if (players.isEmpty())
			error("No players to boop");

		for (Player player : players) {
			try {
				run(player(), player, message, anonymous);
			} catch (Exception ignore) {}
		}
	}

	@NoLiterals
	@Description("Boop a player")
	void boop(Player player, @Optional @Vararg String message, @Switch(shorthand = 'a') @Optional boolean anonymous) {
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

		String boopedName = Nickname.of(booped);
		if (Minigamer.of(booped).isPlaying())
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
		// TODO - 1.19.2 Chat Validation Kick
		// booped.sendMessage(booper, json);
		booped.sendMessage(json);
		booped.playSound(SOUND);
	}
}
