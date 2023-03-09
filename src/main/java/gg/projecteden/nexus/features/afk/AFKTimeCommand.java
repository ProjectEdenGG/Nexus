package gg.projecteden.nexus.features.afk;

import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Aliases("timeafk")
public class AFKTimeCommand extends CustomCommand {

	public AFKTimeCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	@Description("View how long a player has been AFK")
	void timeAfk(@Arg("self") Player player) {
		String timespan = Timespan.of(AFK.get(player).getTime()).format();
		send(PREFIX + "&3" + nickname(player) + " has been AFK for &e" + timespan);
	}

}
