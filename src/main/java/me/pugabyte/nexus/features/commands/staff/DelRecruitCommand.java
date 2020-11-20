package me.pugabyte.nexus.features.commands.staff;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.StringUtils;
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
