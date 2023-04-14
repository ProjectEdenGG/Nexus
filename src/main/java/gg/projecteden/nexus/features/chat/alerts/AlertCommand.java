package gg.projecteden.nexus.features.chat.alerts;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import org.bukkit.entity.Player;

@Permission(Group.STAFF)
public class AlertCommand extends CustomCommand {

	public AlertCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Play a ping sound to a player")
	void alert(Player player) {
		Jingle.PING.play(player);
	}

}
