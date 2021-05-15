package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

@Aliases("skinifier")
public class SkinifyCommand extends CustomCommand {

	public SkinifyCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void skin(@Arg("self") OfflinePlayer player) {
		send(json("&eClick here &3to Project Eden-ify your skin!").url("http://projecteden.gg/skins/?uuid=" + player.getUniqueId()));
	}
}
