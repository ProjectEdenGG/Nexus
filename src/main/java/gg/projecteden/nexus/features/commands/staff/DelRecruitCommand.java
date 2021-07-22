package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.entity.Player;

@Permission("group.staff")
public class DelRecruitCommand extends CustomCommand {

	public DelRecruitCommand(CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Recruit");
	}

	@Path("<player>")
	void recruit(Player player) {
		runCommand("rg removemember entry-deny " + player.getName() + " -w buildadmin");
		send(PREFIX + "Removed &e" + player.getName());
	}

}
