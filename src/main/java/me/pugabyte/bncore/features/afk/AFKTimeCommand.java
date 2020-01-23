package me.pugabyte.bncore.features.afk;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Aliases("timeafk")
public class AFKTimeCommand extends CustomCommand {

	public AFKTimeCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void timeAfk(@Arg("self") Player player) {
		AFKPlayer afkPlayer = AFK.get(player);
		String timespan = Utils.timespanFormat(Long.valueOf(afkPlayer.getTime().until(LocalDateTime.now(), ChronoUnit.SECONDS)).intValue());
		send("&3" + player.getName() + " has been AFK for &e" + timespan);
	}

}
