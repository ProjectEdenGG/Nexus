package me.pugabyte.nexus.features.commands.staff;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
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
