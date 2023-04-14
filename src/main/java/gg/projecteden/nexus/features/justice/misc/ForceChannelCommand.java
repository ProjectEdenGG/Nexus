package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.PublicChannel;
import gg.projecteden.nexus.models.nickname.Nickname;
import lombok.NonNull;

@Aliases("fc")
@Permission(Group.MODERATOR)
public class ForceChannelCommand extends CustomCommand {

	public ForceChannelCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("<player> <channel>")
	@Description("Force a player into a channel")
	void forceChannel(Chatter chatter, PublicChannel channel) {
		chatter.setActiveChannel(channel, true);
		send("&3Forced &e" + Nickname.of(chatter) + " &3to " + channel.getColor() + channel.getName());
	}

}
