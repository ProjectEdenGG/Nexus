package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.preconfigured.PlayerNotFoundException;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;

public class DistanceCommand extends CustomCommand {

	public DistanceCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void distance(@Arg Player target) {
		if (player().getWorld().equals(Minigames.getGameworld()))
			error(PREFIX + "You can't use that here, that's cheating!");

		if (!player().getWorld().equals(target.getWorld()))
			error(PREFIX + "Player is not in the same world.");

		if (Utils.isVanished(target) && !player().hasPermission("vanish.see"))
			throw new PlayerNotFoundException();

		reply(PREFIX + player().getLocation().distance(target.getLocation()) + " blocks.");
	}
}
