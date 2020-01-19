package me.pugabyte.bncore.features.commands;

import de.tr7zw.itemnbtapi.NBTEntity;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
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
		reply(nbtEntity.asNBTString());
	}
}
