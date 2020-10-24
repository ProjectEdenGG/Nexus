package me.pugabyte.bncore.features.discord.commands;

import com.google.common.base.Strings;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.chat.Chat.StaticChannel;
import me.pugabyte.bncore.features.commands.staff.freeze.FreezeCommand;
import me.pugabyte.bncore.features.discord.Bot;
import me.pugabyte.bncore.features.discord.Bot.HandledBy;
import me.pugabyte.bncore.features.discord.DiscordId.Channel;
import me.pugabyte.bncore.features.discord.DiscordId.Role;
import me.pugabyte.bncore.framework.exceptions.BNException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.NoPermissionException;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.models.freeze.Freeze;
import me.pugabyte.bncore.models.freeze.FreezeService;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;

import static me.pugabyte.bncore.utils.StringUtils.stripColor;

@HandledBy(Bot.RELAY)
public class FreezeDiscordCommand extends Command {
	public static final String PREFIX = StringUtils.getPrefix("Freeze");

	public FreezeDiscordCommand() {
		this.name = "freeze";
		this.requiredRole = Role.STAFF.name();
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		if (!event.getChannel().getId().equals(Channel.STAFF_BRIDGE.getId()))
			return;

		Tasks.sync(() -> {
			try {
				if (Strings.isNullOrEmpty(event.getArgs()))
					throw new InvalidInputException("Correct usage: /freeze <players...>");

				DiscordUser user = new DiscordService().getFromUserId(event.getAuthor().getId());
				if (user.getUuid() == null)
					throw new NoPermissionException();

				FreezeService service = new FreezeService();
				OfflinePlayer executor = Utils.getPlayer(user.getUuid());

				for (String arg : event.getArgs().split(" ")) {
					try {
						OfflinePlayer player = Utils.getPlayer(arg);
						if (!player.isOnline() || player.getPlayer() == null)
							throw new PlayerNotOnlineException(player);

						Freeze freeze = service.get(player);
						if (freeze.isFrozen()) {
							if (player.getPlayer().getVehicle() != null) {
								freeze.setFrozen(false);
								service.save(freeze);
								if (player.getPlayer().getVehicle() != null)
									player.getPlayer().getVehicle().remove();
								Utils.send(player, "&cYou have been unfrozen.");
								Chat.broadcast(PREFIX + "&e" + executor.getName() + " &3has unfrozen &e" + player.getName(), StaticChannel.STAFF);
							} else
								FreezeCommand.freezePlayer(player.getPlayer());
							continue;
						}

						FreezeCommand.freezePlayer(player.getPlayer());
						freeze.setFrozen(true);
						service.save(freeze);

						Chat.broadcast(PREFIX + "&e" + executor.getName() + " &3has frozen &e" + player.getName(), StaticChannel.STAFF);
						Utils.send(player, "&cYou have been frozen! This likely means you are breaking a rule; please pay attention to staff in chat");
					} catch (Exception ex) {
						event.reply(stripColor(ex.getMessage()));
						if (!(ex instanceof BNException))
							ex.printStackTrace();
					}
				}
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof BNException))
					ex.printStackTrace();
			}
		});
	}


}
