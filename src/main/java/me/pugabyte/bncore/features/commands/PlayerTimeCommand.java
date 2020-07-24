package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.DescParseTickFormat;
import org.bukkit.entity.Player;

@Aliases("ptime")
public class PlayerTimeCommand extends CustomCommand {

	public PlayerTimeCommand(CommandEvent event) {
		super(event);
	}

	@Path("reset")
	void reset() {
		player().resetPlayerTime();
		send(PREFIX + "Reset player time");
	}

	@Path("<time> [player]")
	public void time(String time, @Arg("self") Player player) {
		long ticks = 0;
		try {
			ticks = DescParseTickFormat.parse(time);
		} catch (Exception ex) {
			error("Unable to process time");
		}
		boolean move = !time.startsWith("@");
		long dayTime = player.getPlayerTime();
		dayTime -= dayTime % 24000;
		dayTime += 24000 + ticks;
		if (move) {
			dayTime -= player.getWorld().getTime();
		}
		player.setPlayerTime(dayTime, move);
		if (player == player().getPlayer())
			send(PREFIX + "Player time set to &e" + DescParseTickFormat.format12(ticks) + " &3or &e" + ticks + " ticks");
		else {
			send(player, PREFIX + "Player time set to &e" + DescParseTickFormat.format12(ticks) + " &3or &e" + ticks + " ticks");
			send(PREFIX + "&e" + player.getName() + "'s&3 player time set to &e" + DescParseTickFormat.format12(ticks) + " &3or &e" + ticks + " ticks");
		}
	}

}
