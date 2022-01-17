package gg.projecteden.nexus.features.commands.staff;

import de.tr7zw.nbtapi.NBTEntity;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

import java.util.UUID;

@Permission(Group.STAFF)
public class EntityNBTCommand extends CustomCommand {

	public EntityNBTCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void nbt() {
		NBTEntity nbtEntity = new NBTEntity(getTargetEntityRequired());
		send(nbtEntity.asNBTString());
	}

	@Path("uuid")
	void getUuid() {
		final UUID uuid = getTargetEntityRequired().getUniqueId();
		send(json("&e" + uuid).copy(uuid.toString()));
	}

}
