package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.entity.Player;

@Description("View the distance between you and another player")
public class DistanceCommand extends CustomCommand {

	public DistanceCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void distance(Player target) {
		if (Minigames.isMinigameWorld(world()))
			error("You can't use that here, that's cheating!");

		if (!world().equals(target.getWorld()))
			error("Player is not in the same world.");

		if (PlayerUtils.isVanished(target) && !player().hasPermission("pv.see"))
			throw new PlayerNotOnlineException(target);

		send(PREFIX + location().distance(target.getLocation()) + " blocks.");
	}
}
