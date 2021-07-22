package gg.projecteden.nexus.features.commands.staff;

import de.tr7zw.nbtapi.NBTEntity;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.staff")
public class EntityNBTCommand extends CustomCommand {

	public EntityNBTCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void nbt() {
		NBTEntity nbtEntity = new NBTEntity(getTargetEntityRequired());
		send(nbtEntity.asNBTString());
	}
}
