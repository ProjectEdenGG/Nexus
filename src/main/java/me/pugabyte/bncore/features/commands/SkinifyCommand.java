package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Aliases("skinifier")
public class SkinifyCommand extends CustomCommand {

	public SkinifyCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void skin(@Arg("self") Player player) {
		send(json2("&eClick here &3to Bear Nation-ify your skin!").url("http://bnn.gg/skins/uuid=" + player.getUniqueId()));
	}
}
