package gg.projecteden.nexus.features.chat.alerts;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import org.bukkit.entity.Player;

@Permission(Group.STAFF)
public class AlertCommand extends CustomCommand {

	public AlertCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void alert(Player player) {
		Jingle.PING.play(player);
	}

}
