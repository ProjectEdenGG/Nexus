package me.pugabyte.bncore.features.chat.commands;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.chat.Channel;
import org.bukkit.entity.Player;

@Permission("group.staff")
public class ForceChannelCommand extends CustomCommand {

	public ForceChannelCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> <channel>")
	void forceChannel(Player player, @Arg(tabCompleter = Channel.class) String channel) {
		runCommand(player, "ch " + channel);
	}

}
