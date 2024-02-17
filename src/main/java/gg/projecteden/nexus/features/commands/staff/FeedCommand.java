package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Permission(Group.SENIOR_STAFF)
public class FeedCommand extends CustomCommand {

	public FeedCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	@Description("Fills a players saturation and hunger")
	void run(@Arg("self") Player player) {
		if (isSelf(player))
			if (!CheatsCommand.canEnableCheats(player))
				error("You cannot use cheats in this world");

		player.setFoodLevel(20);
		player.setSaturation(10);
		player.setExhaustion(0);
		send(PREFIX + "Fed " + player.getName());
	}

}
