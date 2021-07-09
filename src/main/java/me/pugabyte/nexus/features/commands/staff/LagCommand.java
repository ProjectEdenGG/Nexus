package me.pugabyte.nexus.features.commands.staff;

import eden.utils.TimeUtils.Timespan;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.scoreboard.ScoreboardLine;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.util.List;

import static eden.utils.TimeUtils.shortDateTimeFormat;

@Permission("group.seniorstaff")
@Aliases({"gc", "memory", "uptime"})
public class LagCommand extends CustomCommand {

	public LagCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void lag() {
		LocalDateTime startTime = Utils.epochSecond(ManagementFactory.getRuntimeMXBean().getStartTime());
		send(json("&3Uptime: &e" + Timespan.of(startTime).format()).hover("&3" + shortDateTimeFormat(startTime)));
		send(ScoreboardLine.TPS.render(null));
		send("&3Max ram: &e" + Runtime.getRuntime().maxMemory() / 1024 / 1024);
		send("&3Allocated ram: &e" + Runtime.getRuntime().totalMemory() / 1024 / 1024);
		send("&3Free ram: &e" + Runtime.getRuntime().freeMemory() / 1024 / 1024);

		List<World> worlds = Bukkit.getWorlds();
		for (World world : worlds) {
			// Don't show the world, if its not loaded
			if (world.getLoadedChunks().length == 0)
				continue;

			String type = switch (world.getEnvironment()) {
				case NETHER -> "Nether";
				case THE_END -> "&3 &3 &3 &3 End";
				default -> "Normal";
			};

			int tileEntities = 0;
			try {
				for (Chunk chunk : world.getLoadedChunks())
					tileEntities += chunk.getTileEntities().length;
			} catch (ClassCastException ex) {
				Nexus.severe("Corrupted chunk data on world: " + world.getName());
				ex.printStackTrace();
			}

			send("&3" + type + " &e" + world.getName() + " &7- "
					+ "&e" + world.getLoadedChunks().length + " &7chunks, "
					+ "&e" + world.getEntities().size() + " &7entities, "
					+ "&e" + tileEntities + " &7tiles");
		}
	}


}
