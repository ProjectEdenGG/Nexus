package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.LivingEntity;

@Permission(Group.STAFF)
public class ToggleEntityNoiseCommand extends CustomCommand {

	public ToggleEntityNoiseCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Make an entity silent")
	void run() {
		LivingEntity entity = getTargetLivingEntityRequired();
		boolean isSilent = entity.isSilent();
		entity.setSilent(!isSilent);
		send(PREFIX + entity.getClass().getSimpleName() + " is now " + (isSilent ? "unmuted" : "muted"));
	}

}
