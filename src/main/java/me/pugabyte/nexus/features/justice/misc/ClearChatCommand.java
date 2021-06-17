package me.pugabyte.nexus.features.justice.misc;

import lombok.NonNull;
import me.pugabyte.nexus.features.chat.Koda;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
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
			if (!isStaff(player))
				line(player, 40);

		Koda.say("Chat has been cleared, sorry for any inconvenience.");
	}

}
