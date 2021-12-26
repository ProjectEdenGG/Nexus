package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.event.Listener;

@NoArgsConstructor
@Permission(Group.STAFF)
@Aliases("letmefuckingedit")
public class LetMeEditCommand extends CustomCommand implements Listener {

	public LetMeEditCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("vanish off");
		runCommand("wgedit on");

		if (hasPermission("essentials.gamemode.creative"))
			runCommand("gm 1");
	}

}
