package me.pugabyte.nexus.features.discord.commands.justice;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import eden.exceptions.EdenException;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.DiscordId.Role;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.features.discord.HandledBy;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.models.discord.DiscordUserService;
import me.pugabyte.nexus.models.punishments.PunishmentType;
import me.pugabyte.nexus.utils.Tasks;

import java.util.Arrays;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.nexus.features.justice.Justice.DISCORD_PREFIX;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.RELAY)
public abstract class _PunishmentDiscordCommand extends Command {

	public _PunishmentDiscordCommand() {
		this.name = getClass().getSimpleName().replace("DiscordCommand", "").toLowerCase();
		this.requiredRole = Role.STAFF.name();
		this.guildOnly = true;
	}

	protected PunishmentType getType() {
		return PunishmentType.valueOf(this.getClass().getSimpleName()
				.replaceFirst("DiscordCommand", "")
				.replaceFirst("Un", "")
				.toUpperCase());
	}

	abstract protected void execute(DiscordUser author, String name, String reason, boolean now);

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			if (!event.getChannel().getId().equals(TextChannel.STAFF_BRIDGE.getId()))
				event.getMessage().delete().queue();

			DiscordUser author = new DiscordUserService().checkVerified(event.getAuthor().getId());
			try {
				String[] args = event.getArgs().split(" ");

				if (args.length >= 1) {
					String reason = args.length >= 2 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : null;
					boolean now = false;
					if (!isNullOrEmpty(reason) && reason.contains(" --now")) {
						now = true;
						reason = reason.replaceFirst(" --now", "");
					}

					for (String name : args[0].split(",")) {
						try {
							execute(author, name, reason, now);
						} catch (Exception ex) {
							event.reply(DISCORD_PREFIX + stripColor(ex.getMessage()));
							if (!(ex instanceof EdenException))
								ex.printStackTrace();
						}
					}
				}
			} catch (Exception ex) {
				event.reply(DISCORD_PREFIX + stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			}
		});
	}

}
