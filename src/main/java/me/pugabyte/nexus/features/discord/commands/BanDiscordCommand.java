package me.pugabyte.nexus.features.discord.commands;

import com.google.common.base.Strings;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.DiscordId.Role;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.features.discord.HandledBy;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.models.discord.DiscordUserService;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;

import static me.pugabyte.nexus.utils.PlayerUtils.runCommandAsConsole;
import static me.pugabyte.nexus.utils.StringUtils.trimFirst;

@HandledBy(Bot.RELAY)
public class BanDiscordCommand extends Command {

	public BanDiscordCommand() {
		this.name = "ban";
		this.aliases = new String[]{"tempban", "unban", "banip", "ipban", "kick", "warn", "unwarn", "mute", "unmute"};
		this.requiredRole = Role.STAFF.name();
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		if (!event.getChannel().getId().equals(TextChannel.STAFF_BRIDGE.getId()))
			return;

		Tasks.async(() -> {
			DiscordUser user = new DiscordUserService().getFromUserId(event.getAuthor().getId());
			if (!Strings.isNullOrEmpty(user.getUserId()))
				Tasks.sync(() ->
						runCommandAsConsole(trimFirst(event.getMessage().getContentRaw() + " --sender=" + PlayerUtils.getPlayer(user.getUuid()).getName())));
		});
	}


}
