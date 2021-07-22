package gg.projecteden.nexus.features.chat.alerts;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import org.bukkit.entity.Player;

public class AlertCommand extends CustomCommand {

	public AlertCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void alert(Player player) {
		Jingle.PING.play(player);
	}

}
