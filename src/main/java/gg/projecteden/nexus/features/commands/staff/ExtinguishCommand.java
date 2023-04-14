package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Aliases("ext")
@Permission(Group.STAFF)
public class ExtinguishCommand extends CustomCommand {

	public ExtinguishCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Extinguish a player if they are on fire")
	void run(@Optional("self") Player player) {
		player.setFireTicks(0);
	}

}
