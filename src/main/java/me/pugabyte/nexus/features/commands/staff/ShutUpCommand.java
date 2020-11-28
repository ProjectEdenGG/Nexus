package me.pugabyte.nexus.features.commands.staff;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.LivingEntity;

@Permission("group.staff")
@Aliases("toggleEntityNoise")
public class ShutUpCommand extends CustomCommand {

	public ShutUpCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	void run() {
		LivingEntity entity = getTargetLivingEntityRequired();
		boolean isSilent = entity.isSilent();
		entity.setSilent(!isSilent);
		send(PREFIX + entity.getClass().getSimpleName() + " is now " + (isSilent ? "unmuted" : "muted"));
	}

}
