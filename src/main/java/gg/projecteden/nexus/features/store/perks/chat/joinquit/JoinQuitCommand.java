package gg.projecteden.nexus.features.store.perks.chat.joinquit;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;

@Aliases("jq")
@Permission(Group.SENIOR_STAFF)
@WikiConfig(rank = "Store", feature = "Chat")
public class JoinQuitCommand extends CustomCommand {

	public JoinQuitCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("JQ");
	}

	@Path("reload")
	@Description("Reload join/quit messages from disk")
	void reload() {
		JoinQuit.reloadConfig();
		send(PREFIX + "Successfully loaded " + JoinQuit.getJoinMessages().size() + " join and " + JoinQuit.getQuitMessages().size() + " quit messages");
	}

}
