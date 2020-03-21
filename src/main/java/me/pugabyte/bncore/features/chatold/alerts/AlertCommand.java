package me.pugabyte.bncore.features.chatold.alerts;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Jingles;
import org.bukkit.entity.Player;

public class AlertCommand extends CustomCommand {

	public AlertCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void alert(Player player) {
		Jingles.ping(player);
	}

}
