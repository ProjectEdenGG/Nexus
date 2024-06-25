package gg.projecteden.nexus.features.events.y2024.vulan24;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.IEventCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Aliases("vulan")
@Permission(Group.STAFF)
public class VuLan24Command extends IEventCommand {

	public VuLan24Command(CommandEvent event) {
		super(event);
	}

	@Override
	public EdenEvent getEdenEvent() {
		return VuLan24.get();
	}

}
