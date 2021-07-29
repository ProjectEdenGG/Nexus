package gg.projecteden.nexus.features.commands;

import de.bluecolored.bluemap.api.BlueMapAPI;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

@Aliases({"maplink", "livemap"})
@Description("Generate a link to our web map, allowing you to see the entire world from your browser")
public class MapCommand extends CustomCommand {
	public static final String URL = "https://map." + Nexus.DOMAIN;
	public static final String STAFF_URL = "https://staffmap." + Nexus.DOMAIN;

	public MapCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void map(@Arg(value = "self", permission = "group.staff") Nerd nerd) {
		Location location = nerd.getLocation();
		String world = location.getWorld().getName().toLowerCase();
		int x = (int) location.getX();
		int z = (int) location.getZ();

		Map<String, String> names = new HashMap<>();
		BlueMapAPI.getInstance().ifPresent(api -> api.getMaps().forEach(map -> names.put(map.getWorld().getSaveFolder().toFile().getName().toLowerCase(), map.getId())));

		String URL = MapCommand.URL;
		if (isStaff())
			if (!names.isEmpty() && !names.containsKey(world))
				URL = MapCommand.STAFF_URL;

		String link = URL + "/#" + names.getOrDefault(world, world) + ":" + x + ":0:" + z + ":30:0:0:0:0:perspective";

		send(json("&3Map: &e" + URL).url(URL));
		send(json("&3Current Location: &e" + link).url(link));
		send("&eTip: &3Zoom in, right click and drag");
	}
}
