package me.pugabyte.nexus.features.commands.staff;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.lang.management.ManagementFactory;
import java.util.List;

@Permission("group.admin")
@Aliases({"gc", "memory", "uptime"})
public class LagCommand extends CustomCommand {

	public LagCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void lag() {
		double tps = Bukkit.getTPS()[0];
		ChatColor color = ChatColor.RED;
		if (tps >= 18.0)
			color = ChatColor.GREEN;
		else if (tps >= 15.0)
			color = ChatColor.YELLOW;

		send("Uptime: " + StringUtils.shortDateTimeFormat(Utils.epochSecond(ManagementFactory.getRuntimeMXBean().getStartTime())));
		send("TPS: " + color + Bukkit.getTPS()[0]);
		send("Max ram: " + Runtime.getRuntime().maxMemory() / 1024 / 1024);
		send("Allocated ram: " + Runtime.getRuntime().totalMemory() / 1024 / 1024);
		send("Free ram: " + Runtime.getRuntime().freeMemory() / 1024 / 1024);

		List<World> worlds = Bukkit.getWorlds();
		for (World world : worlds) {
			// Don't show the world, if its not loaded
			if (world.getLoadedChunks().length == 0)
				continue;

			String type = "World";
			switch (world.getEnvironment()) {
				case NETHER:
					type = "Nether";
					break;
				case THE_END:
					type = "The End";
			}

			int tileEntities = 0;
			try {
				for (Chunk chunk : world.getLoadedChunks()) {
					tileEntities += chunk.getTileEntities().length;
				}
			} catch (ClassCastException ex) {
				Nexus.severe("Corrupted chunk data on world: " + world.getName());
			}

			send(type + " \"" + world.getName() + "\": "
					+ world.getLoadedChunks().length + " chunks, "
					+ world.getEntities().size() + " entities, "
					+ tileEntities + " tiles");
		}
	}


}
