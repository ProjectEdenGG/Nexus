package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;

@Permission(Group.ADMIN)
public class SudoCommand extends CustomCommand {

	public SudoCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> <command...>")
	void run(Nerd nerd, String command) {
		PlayerUtils.runCommandAsOp(nerd.getOnlinePlayer(), command);
		send("&3Made &e" + nerd.getNickname() + " &3run &e/" + command);
	}

}
