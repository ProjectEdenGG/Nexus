package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.StringUtils;
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
