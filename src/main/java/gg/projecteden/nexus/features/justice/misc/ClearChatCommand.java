package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Aliases("cc")
@Permission(Group.STAFF)
public class ClearChatCommand extends CustomCommand {

	public ClearChatCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Clear the chat of all online non-staff players")
	void run() {
		for (Player player : OnlinePlayers.getAll())
			if (!isStaff(player))
				line(player, 40);

		Koda.say("Chat has been cleared, sorry for any inconvenience.");
	}

}
