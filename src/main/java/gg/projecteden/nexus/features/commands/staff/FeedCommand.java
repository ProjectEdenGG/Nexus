package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Permission(Group.STAFF)
public class FeedCommand extends CustomCommand {

	public FeedCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Fills a players saturation and hunger")
	void run(@Optional("self") Player player) {
		player.setFoodLevel(20);
		player.setSaturation(10);
		player.setExhaustion(0);
		send(PREFIX + "Fed " + player.getName());
	}

}
