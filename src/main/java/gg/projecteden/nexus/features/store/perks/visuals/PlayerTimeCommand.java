package gg.projecteden.nexus.features.store.perks.visuals;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.DescParseTickFormat;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.entity.Player;

@Aliases("ptime")
@Permission(PlayerTimeCommand.PERMISSION)
@WikiConfig(rank = "Store", feature = "Visuals")
public class PlayerTimeCommand extends CustomCommand {
	public static final String PERMISSION = "essentials.ptime";

	public PlayerTimeCommand(CommandEvent event) {
		super(event);
	}

	@Path("<time> [player]")
	@Description("Change your client-side time of day")
	@DescriptionExtra("Does not change on the server, therefore does not affect things like mob spawning/burning")
	public void time(String time, @Arg(value = "self", permission = Group.STAFF) Player player) {
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

	@Path("reset")
	@Description("Sync your time with the server")
	void reset() {
		player().resetPlayerTime();
		send(PREFIX + "Reset player time");
	}

}
