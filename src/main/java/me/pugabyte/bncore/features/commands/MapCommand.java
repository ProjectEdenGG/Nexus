package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases({"maplink", "livemap"})
public class MapCommand extends CustomCommand {

	public MapCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void map() {
		send(json("&3Map: &ehttp://map.bnn.gg").url("http://map.bnn.gg"));
		String link = "http://map.bnn.gg/" + player().getWorld().getName().toLowerCase() +
				"/" + (int) player().getLocation().getX() + "/" + (int) player().getLocation().getZ();
		send(json("&3Current Location: &e" + link).url(link));
	}
}
