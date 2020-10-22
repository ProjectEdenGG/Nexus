package me.pugabyte.bncore.features.commands;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Aliases({"maplink", "livemap"})
public class MapCommand extends CustomCommand {

	public MapCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void map() {
		send(json("&3Map: &ehttp://map.bnn.gg").url("http://map.bnn.gg"));

		String world = player().getWorld().getName().toLowerCase();
		int x = (int) player().getLocation().getX();
		int z = (int) player().getLocation().getZ();

		String link = "http://map.bnn.gg/" + world + "/" + x + "/" + z;
		send(json("&3Current Location: &e" + link).url(link));

		if (!isStaff()) return; // temporary until release
		// also delete Censor#dynmapLinkShorten()

		String subdomain = "bluemap"; // "map";
		if (isStaff()) {
			Set<String> names = new HashSet<>();
			BlueMapAPI.getInstance().ifPresent(api -> names.addAll(api.getMaps().stream().map(BlueMapMap::getId).collect(Collectors.toSet())));

			if (!names.isEmpty() && !names.contains(world))
				subdomain = "staffmap";
		}

		link = "http://" + subdomain + ".bnn.gg/#" + world + ":" + x + ":" + z + ":0:30:0";
		send(json("&3Current Location: &e" + link).url(link));
	}
}
