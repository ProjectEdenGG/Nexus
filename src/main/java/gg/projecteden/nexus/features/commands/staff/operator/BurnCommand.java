package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import org.bukkit.entity.Player;

@Permission(Group.SENIOR_STAFF)
public class BurnCommand extends CustomCommand {

	public BurnCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player> <seconds>")
	@Description("Temporarily set a player on fire")
	public void burn(Player player, int seconds) {
		player.setFireTicks((int) TickTime.SECOND.x(seconds));
		send(PREFIX + "&3Set &e" + Nickname.of(player) + "&3 on fire for &e" + seconds + plural(" &3second", seconds));
	}
}
