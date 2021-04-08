package me.pugabyte.nexus.features.discord.commands;

import com.google.common.base.Strings;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.features.commands.staff.freeze.FreezeCommand;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.Bot.HandledBy;
import me.pugabyte.nexus.features.discord.DiscordId.Role;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.nexus.framework.exceptions.preconfigured.NoPermissionException;
import me.pugabyte.nexus.models.discord.DiscordService;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.models.freeze.Freeze;
import me.pugabyte.nexus.models.freeze.FreezeService;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.OfflinePlayer;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.RELAY)
public class FreezeDiscordCommand extends Command {
	public static final String PREFIX = StringUtils.getPrefix("Freeze");

	public FreezeDiscordCommand() {
		this.name = "freeze";
		this.requiredRole = Role.STAFF.name();
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		if (!event.getChannel().getId().equals(TextChannel.STAFF_BRIDGE.getId()))
			return;

		Tasks.sync(() -> {
			try {
				if (Strings.isNullOrEmpty(event.getArgs()))
					throw new InvalidInputException("Correct usage: /freeze <players...>");

				DiscordUser user = new DiscordService().getFromUserId(event.getAuthor().getId());
				if (user.getUuid() == null)
					throw new NoPermissionException();

				FreezeService service = new FreezeService();
				OfflinePlayer executor = PlayerUtils.getPlayer(user.getUuid());

				for (String arg : event.getArgs().split(" ")) {
					try {
						OfflinePlayer player = PlayerUtils.getPlayer(arg);
						if (!player.isOnline() || player.getPlayer() == null)
							throw new PlayerNotOnlineException(player);

						Freeze freeze = service.get(player);
						if (freeze.isFrozen()) {
							if (player.getPlayer().getVehicle() != null) {
								freeze.setFrozen(false);
								service.save(freeze);
								if (player.getPlayer().getVehicle() != null)
									player.getPlayer().getVehicle().remove();
								freeze.send("&cYou have been unfrozen.");
								Chat.broadcast(PREFIX + "&e" + executor.getName() + " &3has unfrozen &e" + player.getName(), StaticChannel.STAFF);
							} else
								FreezeCommand.freezePlayer(player.getPlayer());
							continue;
						}

						FreezeCommand.freezePlayer(player.getPlayer());
						freeze.setFrozen(true);
						service.save(freeze);

						Chat.broadcast(PREFIX + "&e" + executor.getName() + " &3has frozen &e" + player.getName(), StaticChannel.STAFF);
						freeze.send("&cYou have been frozen! This likely means you are breaking a rule; please pay attention to staff in chat");
					} catch (Exception ex) {
						event.reply(stripColor(ex.getMessage()));
						if (!(ex instanceof NexusException))
							ex.printStackTrace();
					}
				}
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof NexusException))
					ex.printStackTrace();
			}
		});
	}


}
