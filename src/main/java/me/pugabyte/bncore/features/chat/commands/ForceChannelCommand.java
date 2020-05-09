package me.pugabyte.bncore.features.chat.commands;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.chat.Chatter;
import me.pugabyte.bncore.models.chat.PublicChannel;

@Permission("group.staff")
public class ForceChannelCommand extends CustomCommand {

	public ForceChannelCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> <channel>")
	void forceChannel(Chatter chatter, PublicChannel channel) {
		chatter.setActiveChannel(channel);
		send("&3Forced &e" + chatter.getOfflinePlayer().getName() + " &3to " + channel.getColor() + channel.getName());
	}

}
