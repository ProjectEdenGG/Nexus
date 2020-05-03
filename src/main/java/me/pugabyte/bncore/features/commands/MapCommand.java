package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Nerd;

import java.util.Arrays;
import java.util.List;

@Aliases({"maplink", "livemap"})
public class MapCommand extends CustomCommand {

	public MapCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void map() {
		boolean isMapWorld = false;
		List<String> worlds = Arrays.asList("world", "pirate", "creative", "2y", "buildcontest", "skyblock");

		for (String world : worlds) {
			if (player().getWorld().getName().toLowerCase().contains(world)) {
				isMapWorld = true;
				break;
			}
		}

		if (new Nerd(player()).getRank().isStaff()) isMapWorld = true;

		send(json("&3Map: &ehttp://map.bnn.gg").url("http://map.bnn.gg"));
		if (isMapWorld) {
			String link = "http://map.bnn.gg/" + player().getWorld().getName().toLowerCase() + "/" + (int) player().getLocation().getX() + "/" + (int) player().getLocation().getZ();
			send(json("&3Current Location: &e" + link).url(link));
		} else {
			send("&3You are not currently in a world with a map, so you will be viewing the survival world.");
		}
	}
}
