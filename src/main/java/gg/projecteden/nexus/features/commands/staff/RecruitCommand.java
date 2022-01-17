package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Permission(Group.STAFF)
public class RecruitCommand extends CustomCommand {

	public RecruitCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void recruit(Player player) {
		runCommand("rg addmember entry-deny " + player.getName() + " -w buildadmin");
		send(PREFIX + "Added &e" + player.getName());
	}

}
