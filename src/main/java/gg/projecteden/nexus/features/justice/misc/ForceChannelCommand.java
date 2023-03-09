package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.PublicChannel;
import gg.projecteden.nexus.models.nickname.Nickname;
import lombok.NonNull;

@Aliases("fc")
@Permission(Group.STAFF)
public class ForceChannelCommand extends CustomCommand {

	public ForceChannelCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> <channel>")
	@Description("Force a player into a channel")
	void forceChannel(Chatter chatter, PublicChannel channel) {
		chatter.setActiveChannel(channel, true);
		send("&3Forced &e" + Nickname.of(chatter) + " &3to " + channel.getColor() + channel.getName());
	}

}
