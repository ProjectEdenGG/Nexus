package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

public class HungerCommand extends CustomCommand {

	public HungerCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player] [number]")
	@Description("View or set a player's hunger")
	void hunger(@Arg("self") Player player, @Arg(permission = Group.STAFF) Integer hunger) {
		if (hunger == null)
			send(PREFIX + stripColor(player.getName()) + "'s hunger is " + player.getFoodLevel());
		else {
			player.setFoodLevel(hunger);
			send(PREFIX + stripColor(player.getName()) + "'s hunger set to " + player.getFoodLevel());
		}
	}

	@Path("target [number]")
	@Description("Set the target player's hunger")
	void target(Integer hunger) {
		hunger(getTargetPlayerRequired(), hunger);
	}
}
