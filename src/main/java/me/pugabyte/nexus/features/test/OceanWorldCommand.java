package me.pugabyte.nexus.features.test;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.annotations.Environments;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.Env;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.LinkedList;
import java.util.Queue;

@NoArgsConstructor
@Environments(Env.DEV)
public class OceanWorldCommand extends CustomCommand implements Listener {
	private static boolean process;
	private static boolean listen;
	private static int blocksPerLoop = 500;
	private static int chunks;

	public OceanWorldCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("count")
	void count() {
		send(PREFIX + locations.size() + " locations in queue");
	}

	@Path("toggle listen")
	void listen() {
		listen = !listen;
		send(PREFIX + "Chunk load listening " + (listen ? "enabled" : "disabled"));
	}

	@Path("toggle process")
	void process() {
		process = !process;
		send(PREFIX + "Processing " + (process ? "enabled" : "disabled"));
	}

	@Path("setBlocksPerLoop <amount>")
	void setBlocksPerLoop(int amount) {
		blocksPerLoop = amount;
		send(PREFIX + "Processing " + blocksPerLoop + " blocks per loop");
	}

	@Path("processChunk")
	void processChunk() {
		process(player().getLocation().getChunk());
		send(PREFIX + "Added chunk to queue");
	}

	private static World ocean = null;
	static {
		getOceanWorld();
	}

	private static void getOceanWorld() {
		try {
			ocean = Bukkit.getWorld("ocean");
		} catch (Exception ignored) {}
	}

	public boolean isOceanWorld(World world) {
		return world.equals(ocean);
	}

	private static final Queue<Location> locations = new LinkedList<>();

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		if (!listen)
			return;

		if (ocean == null)
			getOceanWorld();

		if (!isOceanWorld(event.getWorld()))
			return;

		if (!event.isNewChunk())
			return;

		Nexus.log("new chunk");
		Chunk chunk = event.getChunk();

		process(chunk);
	}

	public void process(Chunk chunk) {
		++chunks;
		double wait = 0;
		for (int y = 1; y < 200; y++) {
			int finalY = y;
			Tasks.wait((int) (wait += .2), () -> {
				for (int x = 0; x <= 15; x++)
					for (int z = 0; z <= 15; z++)
						locations.add(chunk.getBlock(x, finalY, z).getLocation());
			});
		}
	}

	static {
		Tasks.repeat(Time.SECOND.x(15), Time.TICK.x(4), () -> {
			if (!process)
				return;

			for (int i = 0; i < blocksPerLoop; i++) {
				Location poll = locations.poll();

				if (poll == null)
					return;

				if (!poll.isWorldLoaded())
					return;

				Block block = poll.getBlock();

				if (MaterialTag.ALL_AIR.isTagged(block.getType()) || Material.ICE == block.getType() || Material.WATER == block.getType()) {
					block.setType(Material.WATER, false);
					Block down = block.getRelative(BlockFace.DOWN);
					if (down.getType() == Material.GRASS_BLOCK)
						down.setType(Material.DIRT);
				} else if (block.getBlockData() instanceof Waterlogged) {
					Waterlogged waterlogged = (Waterlogged) block.getBlockData();
					waterlogged.setWaterlogged(true);
					block.setBlockData(waterlogged);
				}
			}

			Nexus.log("Processed " + blocksPerLoop + " blocks, " + locations.size() + " left in queue");
		});
	}

	@EventHandler
	public void onBlockForm(BlockFormEvent event) {
		if (!isOceanWorld(event.getBlock().getWorld()))
			return;

		if (event.getNewState().getType() == Material.ICE)
			if (event.getBlock().getLocation().getY() < 200)
				event.setCancelled(true);
	}
}
