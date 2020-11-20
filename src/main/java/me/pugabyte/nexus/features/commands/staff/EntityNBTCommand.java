package me.pugabyte.nexus.features.commands.staff;

import de.tr7zw.nbtapi.NBTEntity;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.entity.LivingEntity;

@Permission("group.staff")
public class EntityNBTCommand extends CustomCommand {

	public EntityNBTCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void nbt() {
		LivingEntity targetEntity = Utils.getTargetEntity(player());
		NBTEntity nbtEntity = new NBTEntity(targetEntity);
		send(nbtEntity.asNBTString());
	}
}
