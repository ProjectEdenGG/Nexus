package me.pugabyte.nexus.features.commands.staff.freeze;

import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.chat.ChatService;
import me.pugabyte.nexus.models.chat.Chatter;
import org.bukkit.entity.Player;

@Aliases("ymc")
@Permission("group.staff")
public class YouMayContinueCommand extends CustomCommand {
	private final ChatService chatService = new ChatService();

	public YouMayContinueCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player> [warn...]")
	void player(Player player, String reason) {
		line(2);
		runCommand("unfreeze " + player.getName());
		runCommand("vanish on");
		((Chatter) chatService.get(player)).setActiveChannel(StaticChannel.GLOBAL.getChannel());
		((Chatter) chatService.get(player())).setActiveChannel(StaticChannel.STAFF.getChannel());
		if (!isNullOrEmpty(reason))
			runCommand("warn " + player.getName() + " " + reason);
	}

}
