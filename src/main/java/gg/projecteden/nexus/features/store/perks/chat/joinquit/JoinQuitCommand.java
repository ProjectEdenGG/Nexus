package gg.projecteden.nexus.features.store.perks.chat.joinquit;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
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
