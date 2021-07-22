package gg.projecteden.nexus.features.store.perks.joinquit;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;

@Aliases("jq")
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
