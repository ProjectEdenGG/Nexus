package me.pugabyte.bncore.features.chat.commands;

import lombok.NonNull;
import me.pugabyte.bncore.features.chat.koda.Koda;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Aliases("cc")
@Permission("group.staff")
public class ClearChatCommand extends CustomCommand {

	public ClearChatCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		for (Player player : Bukkit.getOnlinePlayers())
			if (!player.hasPermission("group.staff"))
				for (int i = 0; i < 40; i++)
					player.sendMessage("");

		Koda.say("Chat has been cleared. Sorry for any inconvenience.");
	}


}
