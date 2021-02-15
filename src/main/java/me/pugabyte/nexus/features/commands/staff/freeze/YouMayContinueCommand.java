package me.pugabyte.nexus.features.commands.staff.freeze;

import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.chat.ChatService;
import me.pugabyte.nexus.models.chat.Chatter;
import org.bukkit.OfflinePlayer;

import java.util.List;

@Aliases("ymc")
@Permission("group.staff")
public class YouMayContinueCommand extends CustomCommand {
	private final ChatService chatService = new ChatService();

	public YouMayContinueCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player> [warn...]")
	void player(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players, String reason) {
		for (OfflinePlayer player : players) {
			runCommand("unfreeze " + player.getName());
			((Chatter) chatService.get(player)).setActiveChannel(StaticChannel.GLOBAL.getChannel());
			if (!isNullOrEmpty(reason))
				runCommand("warn " + player.getName() + " " + reason);
		}

		line(2);
		runCommand("vanish on");
		((Chatter) chatService.get(player())).setActiveChannel(StaticChannel.STAFF.getChannel());
	}

}
