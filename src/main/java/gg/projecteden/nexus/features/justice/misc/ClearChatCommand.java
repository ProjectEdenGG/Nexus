package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Aliases("cc")
@Permission(Group.STAFF)
public class ClearChatCommand extends CustomCommand {

	public ClearChatCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Clear the chat of all online non-staff players")
	void run() {
		for (Player player : OnlinePlayers.getAll())
			if (!isStaff(player))
				line(player, 40);

		Koda.say("Chat has been cleared, sorry for any inconvenience.");
	}

}
