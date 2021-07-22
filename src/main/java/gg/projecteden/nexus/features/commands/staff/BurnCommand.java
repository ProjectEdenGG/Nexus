package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.utils.TimeUtils.Time;
import org.bukkit.entity.Player;

@Permission("group.seniorstaff")
public class BurnCommand extends CustomCommand {

	public BurnCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player> <seconds>")
	public void burn(Player player, int seconds) {
		player.setFireTicks(Time.SECOND.x(seconds));
		send(PREFIX + "&3Set &e" + player.getName() + "&3 on fire for &e" + seconds + plural(" &3second", seconds));
	}
}
