package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Permission("group.staff")
public class SlapCommand extends CustomCommand {

	public SlapCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void slap(Player player) {
		player.setVelocity(player.getLocation().getDirection().multiply(-2).setY(player.getEyeLocation().getPitch() > 0 ? 1.5 : -1.5));
		send(player, "&6You have been slapped!");
	}

}
