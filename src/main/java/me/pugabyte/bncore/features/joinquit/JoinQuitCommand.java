package me.pugabyte.bncore.features.joinquit;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.StringUtils;

public class JoinQuitCommand extends CustomCommand {

	public JoinQuitCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("JQ");
	}

	@Path("reload")
	@Permission("group.seniorstaff")
	void reload() {
		JoinQuit.reloadConfig();
		send(PREFIX + "Successfully loaded " + JoinQuit.getJoinMessages().size() + " join and " + JoinQuit.getQuitMessages().size() + " quit messages");
	}

}
