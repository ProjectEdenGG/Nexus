package gg.projecteden.nexus.features.quests;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.admin")
public class QuestsCommand extends CustomCommand {

	public QuestsCommand(CommandEvent event) {
		super(event);
	}

}
