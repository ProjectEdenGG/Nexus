package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import org.bukkit.entity.Player;

public class DistanceCommand extends CustomCommand {

	public DistanceCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("<player>")
	@Description("View the distance between you and another player")
	void distance(Player target) {
		if (Minigames.isMinigameWorld(world()))
			error("You can't use that here, that's cheating!");

		if (isDifferentWorld(target))
			error("Player is not in the same world.");

		if (Vanish.isVanished(target) && !player().hasPermission("pv.see"))
			throw new PlayerNotOnlineException(target);

		send(PREFIX + StringUtils.getDf().format(distanceTo(target).getRealDistance()) + " blocks.");
	}

}
