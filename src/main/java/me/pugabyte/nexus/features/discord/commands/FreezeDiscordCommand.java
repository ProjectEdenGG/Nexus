package me.pugabyte.nexus.features.discord.commands;

import com.google.common.base.Strings;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.DiscordId.Role;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.features.discord.HandledBy;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.nexus.framework.exceptions.preconfigured.NoPermissionException;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.models.discord.DiscordUserService;
import me.pugabyte.nexus.models.punishments.Punishment;
import me.pugabyte.nexus.models.punishments.PunishmentType;
import me.pugabyte.nexus.models.punishments.Punishments;
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

				DiscordUser user = new DiscordUserService().getFromUserId(event.getAuthor().getId());
				if (user.getUuid() == null)
					throw new NoPermissionException();

				for (String arg : event.getArgs().split(" ")) {
					try {
						OfflinePlayer player = PlayerUtils.getPlayer(arg);
						if (!player.isOnline() || player.getPlayer() == null)
							throw new PlayerNotOnlineException(player);

						Punishments.of(player).add(Punishment.ofType(PunishmentType.FREEZE).punisher(user.getUuid()));
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
