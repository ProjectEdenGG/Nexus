package me.pugabyte.bncore.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.bncore.features.discord.Bot;
import me.pugabyte.bncore.features.discord.Bot.HandledBy;
import me.pugabyte.bncore.features.discord.DiscordId.Channel;
import me.pugabyte.bncore.features.discord.DiscordId.Role;
import me.pugabyte.bncore.framework.exceptions.BNException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.litebans.LiteBansService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;

import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.StringUtils.stripColor;

@HandledBy(Bot.RELAY)
public class AltsDiscordCommand extends Command {

	public AltsDiscordCommand() {
		this.name = "alts";
		this.requiredRole = Role.STAFF.name();
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		if (!event.getChannel().getId().equals(Channel.STAFF_BRIDGE.getId()))
			return;

		Tasks.async(() -> {
			try {
				String[] args = event.getArgs().split(" ");
				if (args.length == 0)
					throw new InvalidInputException("Correct usage: `/alts <player>`");

				LiteBansService service = new LiteBansService();
				OfflinePlayer player = Utils.getPlayer(args[0]);

				String alts = service.getAlts(player.getUniqueId().toString()).stream()
						.map(Utils::getPlayer).map(_player -> {
							if (service.isBanned(_player.getUniqueId().toString()))
								return "**" + _player.getName() + "**";
							else if (_player.isOnline())
								return "_" + _player.getName() + "_";
							else return _player.getName();
						}).distinct().collect(Collectors.joining(", "));

				event.reply("Alts of `" + player.getName() + "` [_Online_ Offline **Banned**]:" + System.lineSeparator() + alts);
			} catch (Throwable ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof BNException))
					ex.printStackTrace();
			}
		});
	}


}
