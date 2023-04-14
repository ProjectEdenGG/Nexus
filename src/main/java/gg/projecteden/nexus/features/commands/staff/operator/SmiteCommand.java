package gg.projecteden.nexus.features.commands.staff.operator;

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

@Permission(Group.SENIOR_STAFF)
@Aliases({"lightning", "thor"})
public class SmiteCommand extends CustomCommand {

	public SmiteCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Strike a player or your target block with lightning")
	void run(@Optional Player player) {
		if (player == null)
			world().strikeLightning(getTargetBlockRequired().getLocation());
		else {
			player.getWorld().strikeLightningEffect(player.getLocation());
			send(PREFIX + "Smiting " + player.getName());
			send(player, "&cThou hast been smitten!");
		}
	}

}
