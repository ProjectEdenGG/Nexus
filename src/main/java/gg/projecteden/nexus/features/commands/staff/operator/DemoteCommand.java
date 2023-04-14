package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange;

@Permission(Group.SENIOR_STAFF)
public class DemoteCommand extends CustomCommand {

	public DemoteCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Demote a player to the next rank down")
	void promote(Nerd player) {
		Rank rank = player.getRank();
		Rank previous = rank.previous();
		if (rank == previous)
			error("User is already min rank");

		GroupChange.set().player(player).group(rank).runAsync();
		send(PREFIX + "Demoted " + player.getName() + " to " + previous.getColoredName());
	}

}
