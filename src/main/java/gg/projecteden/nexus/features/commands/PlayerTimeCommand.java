package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.DescParseTickFormat;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.entity.Player;

import static gg.projecteden.nexus.features.commands.PlayerTimeCommand.PERMISSION;

@Aliases("ptime")
@Description("Change the time of day for yourself. Does not change on the server, therefore does not affect things like mob spawning/burning.")
@Permission(PERMISSION)
public class PlayerTimeCommand extends CustomCommand {
	public static final String PERMISSION = "essentials.ptime";

	public PlayerTimeCommand(CommandEvent event) {
		super(event);
	}

	@Path("reset")
	void reset() {
		player().resetPlayerTime();
		send(PREFIX + "Reset player time");
	}

	@Path("<time> [player]")
	public void time(String time, @Arg(value = "self", permission = "group.staff") Player player) {
		long ticks = PlayerUtils.setPlayerTime(player, time);
		String type = "set";
		if (time.startsWith("@"))
			type = "locked";

		if (isSelf(player))
			send(PREFIX + "Player time " + type + " to &e" + DescParseTickFormat.format12(ticks) + " &3or &e" + ticks + " ticks");
		else {
			send(player, PREFIX + "Player time  " + type + "  to &e" + DescParseTickFormat.format12(ticks) + " &3or &e" + ticks + " ticks");
			send(PREFIX + "&e" + player.getName() + "'s&3 player time " + type + " to &e" + DescParseTickFormat.format12(ticks) + " &3or &e" + ticks + " ticks");
		}
	}

}
