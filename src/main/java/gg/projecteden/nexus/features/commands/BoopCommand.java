package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

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

	@Path("all [message...] [--anonymous]")
	@Description("Boop all players")
	@Permission(Group.ADMIN)
	void boopAll(
		String message,
		@Switch(shorthand = 'a') boolean anonymous
	) {
		final List<Player> players = OnlinePlayers.where().viewer(player()).get().stream().toList();

		if (players.isEmpty())
			error("No players to boop");

		for (Player player : players) {
			try {
				run(player(), player, message, anonymous);
			} catch (Exception ignore) {}
		}
	}

	@Path("<player> [message...] [--anonymous] [--revenge]")
	@Description("Boop a player")
	@Cooldown(value = TickTime.SECOND, x = 5, bypass = Group.ADMIN)
	void boop(
		Player player,
		String message,
		@Switch(shorthand = 'a') boolean anonymous
	) {
		run(player(), player, message, anonymous);
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("expose <nerd> <uuid>")
	void expose(Nerd nerd, UUID uuid) {
		if (CooldownService.isOnCooldown(player(), "boop-expose-" + nerd.getNickname() + "-" + uuid.toString(), TickTime.HOUR))
			error("You already exposed this booper as " + nerd.getColoredName());

		new BankerService().withdraw(player(), -250, ShopGroup.SURVIVAL, TransactionCause.BOOP_EXPOSE);
		send(json(PREFIX + "Your booper was " + nerd.getColoredName() + "&3! ").next("&eClick to boop them back anonymously").suggest("/boop " + nerd.getNickname() + " -a "));
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
		else
			json.next("&3. &eClick to pay $250 to expose your booper!").command("/boop expose " + Nickname.of(booper) + " " + UUID.randomUUID());
		booped.sendMessage(json);
		booped.playSound(SOUND);
	}
}
