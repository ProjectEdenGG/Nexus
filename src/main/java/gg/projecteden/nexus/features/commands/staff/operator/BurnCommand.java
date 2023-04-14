package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import org.bukkit.entity.Player;

@Permission(Group.SENIOR_STAFF)
public class BurnCommand extends CustomCommand {

	public BurnCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Temporarily set a player on fire")
	void burn(Player player, int seconds) {
		player.setFireTicks((int) TickTime.SECOND.x(seconds));
		send(PREFIX + "&3Set &e" + player.getName() + "&3 on fire for &e" + seconds + plural(" &3second", seconds));
	}
}
