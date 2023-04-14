package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import org.bukkit.entity.LivingEntity;

@Permission(Group.STAFF)
public class ToggleEntityNoiseCommand extends CustomCommand {

	public ToggleEntityNoiseCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Make an entity silent")
	public void help() {
		LivingEntity entity = getTargetLivingEntityRequired();
		boolean isSilent = entity.isSilent();
		entity.setSilent(!isSilent);
		send(PREFIX + entity.getClass().getSimpleName() + " is now " + (isSilent ? "unmuted" : "muted"));
	}

}
