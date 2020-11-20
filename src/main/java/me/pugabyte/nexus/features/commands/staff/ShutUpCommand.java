package me.pugabyte.nexus.features.commands.staff;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

@Permission("group.staff")
@Aliases("toggleEntityNoise")
public class ShutUpCommand extends CustomCommand {

	public ShutUpCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	void shutUp() {
		Entity entity = Utils.getTargetEntity(player());
		if (!(entity instanceof LivingEntity))
			error("You must be looking at a living entity");
		LivingEntity livingEntity = (LivingEntity) entity;
		boolean isSilent = livingEntity.isSilent();
		livingEntity.setSilent(!isSilent);
		send(PREFIX + livingEntity.getClass().getSimpleName() + " is now " + (isSilent ? "unmuted" : "muted"));
	}

}
