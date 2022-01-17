package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Permission(Group.STAFF)
@Aliases({"lightning", "thor"})
public class SmiteCommand extends CustomCommand {

	public SmiteCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void run(Player player) {
		if (player == null)
			world().strikeLightning(getTargetBlockRequired().getLocation());
		else {
			player.getWorld().strikeLightningEffect(player.getLocation());
			send(PREFIX + "Smiting " + player.getName());
			send(player, "&cThou hast been smitten!");
		}
	}

}
