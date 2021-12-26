package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.entity.Player;

@Permission(Group.STAFF)
public class SlapCommand extends CustomCommand {

	public SlapCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(Player player) {
		slap(Nerd.of(player));
	}

	public static void slap(Nerd nerd) {
		final Player player = nerd.getOnlinePlayer();
		player.setVelocity(player.getLocation().getDirection().multiply(-2).setY(player.getEyeLocation().getPitch() > 0 ? 1.5 : -1.5));
		PlayerUtils.send(nerd, "&6You have been slapped!");
	}

}
