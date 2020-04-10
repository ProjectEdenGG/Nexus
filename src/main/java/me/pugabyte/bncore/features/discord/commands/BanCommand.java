package me.pugabyte.bncore.features.discord.commands;

import com.google.common.base.Strings;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.bncore.features.discord.Bot;
import me.pugabyte.bncore.features.discord.Bot.HandledBy;
import me.pugabyte.bncore.features.discord.DiscordId.Channel;
import me.pugabyte.bncore.features.discord.DiscordId.Role;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;

import static me.pugabyte.bncore.utils.StringUtils.trimFirst;
import static me.pugabyte.bncore.utils.Utils.runConsoleCommand;

@HandledBy(Bot.RELAY)
public class BanCommand extends Command {

	public BanCommand() {
		this.name = "ban";
		this.aliases = new String[]{"tempban", "unban", "kick", "warn", "unwarn", "mute", "unmute"};
		this.requiredRole = Role.STAFF.name();
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		if (!event.getChannel().getId().equals(Channel.STAFF_BRIDGE.getId()))
			return;

		Tasks.async(() -> {
			DiscordUser user = new DiscordService().getFromUserId(event.getAuthor().getId());
			if (!Strings.isNullOrEmpty(user.getUserId()))
				runConsoleCommand(trimFirst(event.getMessage().getContentRaw() + " --sender=" + Utils.getPlayer(user.getUuid()).getName()));
		});
	}


}
