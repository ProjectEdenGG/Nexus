package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.entity.Player;

@Permission("group.seniorstaff")
public class BurnCommand extends CustomCommand {

	public BurnCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player> <seconds>")
	public void burn(Player player, int seconds) {
		player.setFireTicks(Time.SECOND.x(seconds));
		String plural = seconds == 1 ? " second" : " seconds";
		send("&3Set &e" + player.getName() + "&3 on fire for &e" + seconds + "&3" + plural);
	}
}
