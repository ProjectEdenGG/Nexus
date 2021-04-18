package me.pugabyte.nexus.features.commands.staff;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.LivingEntity;

@Permission("group.staff")
public class ToggleEntityNoiseCommand extends CustomCommand {

	public ToggleEntityNoiseCommand(CommandEvent event) {
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
