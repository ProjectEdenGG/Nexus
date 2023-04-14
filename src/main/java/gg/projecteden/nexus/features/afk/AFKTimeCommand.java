package gg.projecteden.nexus.features.afk;

import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import org.bukkit.entity.Player;

@Aliases("timeafk")
public class AFKTimeCommand extends CustomCommand {

	public AFKTimeCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("View how long a player has been AFK")
	void timeAfk(@Optional("self") Player player) {
		String timespan = Timespan.of(AFK.get(player).getTime()).format();
		send(PREFIX + "&3" + nickname(player) + " has been AFK for &e" + timespan);
	}

}
