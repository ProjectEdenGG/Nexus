package gg.projecteden.nexus.features.discord.commands.justice;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import gg.projecteden.exceptions.EdenException;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.DiscordId.Role;
import gg.projecteden.nexus.features.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.utils.Tasks;

import java.util.Arrays;

import static com.google.common.base.Strings.isNullOrEmpty;
import static gg.projecteden.nexus.features.justice.Justice.DISCORD_PREFIX;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

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
