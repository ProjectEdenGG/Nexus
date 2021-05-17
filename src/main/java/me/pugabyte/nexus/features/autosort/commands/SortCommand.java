package me.pugabyte.nexus.features.autosort.commands;

import lombok.NonNull;
import me.pugabyte.nexus.features.autosort.tasks.FindChestsThread;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import me.pugabyte.nexus.models.autosort.AutoSortUserService;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;

@Aliases("depositall")
@Permission("autosort.use")
public class SortCommand extends CustomCommand {
	private final AutoSortUserService service = new AutoSortUserService();
	private AutoSortUser user;

	public SortCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path
	void run() {
		Location location = player().getLocation();
		Chunk centerChunk = location.getChunk();
		World world = location.getWorld();
		ChunkSnapshot[][] snapshots = new ChunkSnapshot[3][3];
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				Chunk chunk = world.getChunkAt(centerChunk.getX() + x, centerChunk.getZ() + z);
				snapshots[x + 1][z + 1] = chunk.getChunkSnapshot();
			}
		}

		//create a thread to search those snapshots and create a chain of quick deposit attempts
		int minY = Math.max(0, player().getEyeLocation().getBlockY() - 10);
		int maxY = Math.min(world.getMaxHeight(), player().getEyeLocation().getBlockY() + 10);
		int startY = player().getEyeLocation().getBlockY();
		int startX = player().getEyeLocation().getBlockX();
		int startZ = player().getEyeLocation().getBlockZ();
		Thread thread = new FindChestsThread(world, snapshots, minY, maxY, startX, startY, startZ, player());
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

}
