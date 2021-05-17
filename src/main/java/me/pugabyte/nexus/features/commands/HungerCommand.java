package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

public class HungerCommand extends CustomCommand {

	public HungerCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player] [number]")
	void hunger(@Arg("self") Player player, @Arg(permission = "group.staff") Integer hunger) {
		if (hunger == null)
			send(PREFIX + stripColor(player.getName()) + "'s hunger is " + player.getFoodLevel());
		else {
			player.setFoodLevel(hunger);
			send(PREFIX + stripColor(player.getName()) + "'s hunger set to " + player.getFoodLevel());
		}
	}

	@Path("target [number]")
	void target(Integer hunger) {
		hunger(getTargetPlayerRequired(), hunger);
	}
}
