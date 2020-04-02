package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;

public class DistanceCommand extends CustomCommand {

	public DistanceCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void distance(Player target) {
		if (player().getWorld().equals(Minigames.getWorld()))
			error("You can't use that here, that's cheating!");

		if (!player().getWorld().equals(target.getWorld()))
			error("Player is not in the same world.");

		if (Utils.isVanished(target) && !player().hasPermission("vanish.see"))
			throw new PlayerNotOnlineException(target);

		send(PREFIX + player().getLocation().distance(target.getLocation()) + " blocks.");
	}
}
