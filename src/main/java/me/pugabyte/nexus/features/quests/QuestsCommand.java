package me.pugabyte.nexus.features.quests;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.admin")
public class QuestsCommand extends CustomCommand {

	public QuestsCommand(CommandEvent event) {
		super(event);
	}

}
