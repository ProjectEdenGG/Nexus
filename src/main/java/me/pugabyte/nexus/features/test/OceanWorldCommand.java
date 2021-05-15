package me.pugabyte.nexus.features.test;

import eden.annotations.Disabled;
import eden.annotations.Environments;
import eden.utils.Env;
import eden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
@Environments(Env.DEV)
@Disabled
public class OceanWorldCommand extends CustomCommand implements Listener {
	private static boolean process;
	private static boolean listen;
	private static int blocksPerLoop = 500;
	private static int chunks;
	private static final List<Long> worldChunkKeys = new ArrayList<>();
	private static String filePath = "oceanworld.yml";
	private static YamlConfiguration config;
	private static List<Long> tempList = new ArrayList<>();

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

//	@Path("checkWorld")
//	void checkWorld(){
//		Location spawn = new Location(ocean, 0, 199, 0);
//		Chunk origin = spawn.getChunk();
//		int radius = 150;
//
//		//
//		int originX = origin.getX();
//		int originZ = origin.getZ();
//
//		int wait = 0;
//		for (int x = -radius; x <= radius; x++) {
//			int finalX = x;
//			Tasks.wait(wait += Time.SECOND.x(2), () -> {
//				for (int z = -radius; z <= radius; z++) {
//					Chunk chunk = ocean.getChunkAt(originX + finalX, originZ + z);
//					int cX = chunk.getX();
//					int cZ = chunk.getZ();
//
//					send("Checking chunk at: x: " + cX + " z: " + cZ);
//					Location loc = new Location(ocean,cX << 4, 199, cZ << 4);
//
//					if(!worldBorder.isInside(loc)) continue;
//					if(loc.getBlock().getType().equals(Material.WATER)) continue;
//
//					worldChunks.add(chunk);
//					send("  added");
//				}
//				send("Next row...");
//			});
//		}
//		//
//	}

	@Path("checkWorldChunks")
	@SneakyThrows
	void checkWorldChunks() {
		ConfigurationSection section = config.getConfigurationSection("oceanworld");
		if (section == null)
			error("section is null");

		if (!section.isSet("done")) {
			List<Long> completed = new ArrayList<>();
			section.set("done", completed);
			config.save(Nexus.getFile(filePath));
		}

		int wait = 0;
		for (Long key : new ArrayList<>(worldChunkKeys)) {
			Tasks.wait(wait += 2, () -> {
				Chunk chunk = ocean.getChunkAt(key);
				int x = chunk.getX() << 4;
				int z = chunk.getZ() << 4;
				Location loc = new Location(ocean, x, 199, z);

				send("checking chunk...");
				if (!worldBorder.isInside(loc) || loc.getBlock().getType().equals(Material.WATER)) {
					worldChunkKeys.remove(key);
					tempList.add(key);
				}
			});
		}
	}

	@Path("sizeWorldChunks")
	void size() {
		send("Size: " + worldChunkKeys.size());
	}

	@Path("saveWorldChunks")
	@SneakyThrows
	void save() {
		ConfigurationSection section = config.getConfigurationSection("oceanworld");
		if (section == null)
			error("section is null");

		section.set("chunks", worldChunkKeys);
		section.set("done", tempList);
		config.save(Nexus.getFile(filePath));
	}

	@Path("processWorld")
	void processWorld() {
		int wait = 0;
		AtomicInteger count = new AtomicInteger(1);
		int size = worldChunkKeys.size();
		for (Long key : worldChunkKeys) {
			Tasks.wait(wait += 20, () -> {
				process(ocean.getChunkAt(key));
				send("processing chunk " + count.getAndIncrement() + " / " + size);
			});
		}
	}

	@Path("processChunk [radius]")
	void processChunk(@Arg("1") int radius) {
		Chunk origin = location().getChunk();
		if (radius > 1) {
			int count = 0;
			for (Chunk chunk : getChunksInRadius(origin, radius)) {
				int x = chunk.getX() << 4;
				int z = chunk.getZ() << 4;
				Location loc = new Location(ocean, x, 199, z);

				if (!worldBorder.isInside(loc))
					continue;

				if (loc.getBlock().getType().equals(Material.WATER))
					continue;

				process(chunk);
				++count;
			}
			send(PREFIX + "Added " + count + " chunks to queue");
		} else {
			process(origin);
			send(PREFIX + "Added chunk to queue");
		}
	}

	public static Collection<Chunk> getChunksInRadius(Chunk origin, int radius) {
		World world = origin.getWorld();

		int length = (radius * 2) + 1;
		Set<Chunk> chunks = new HashSet<>(length * length);

		int cX = origin.getX();
		int cZ = origin.getZ();

		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				chunks.add(world.getChunkAt(cX + x, cZ + z));
			}
		}
		return chunks;
	}

	private static World ocean = null;
	private static WorldBorder worldBorder = null;

	static {
		getOceanWorld();
	}

	private static void getOceanWorld() {
		try {
			ocean = Bukkit.getWorld("ocean");
			if (ocean != null) {
				worldBorder = ocean.getWorldBorder();
			}
		} catch (Exception ignored) {
		}
	}

	public boolean isOceanWorld(World world) {
		return world.equals(ocean);
	}

	private static final Queue<Location> locations = new LinkedList<>();

//	@EventHandler
//	public void onChunkLoad(ChunkLoadEvent event) {
//		if (!listen)
//			return;
//
//		if (ocean == null)
//			getOceanWorld();
//
//		if (!isOceanWorld(event.getWorld()))
//			return;
//
//		if (!event.isNewChunk())
//			return;
//
//		Chunk chunk = event.getChunk();
//		int x = chunk.getX() << 4;
//		int z = chunk.getZ() << 4;
//		Nexus.log("new chunk at x:" + x + " z:" + z);
//
//		Location chunkLoc = new Location(ocean, x, 1, z);
//		if(!worldBorder.isInside(chunkLoc)) {
//			Nexus.log(" is outside wb");
//			return;
//		}
//
//		process(chunk);
//	}

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
		config = Nexus.getConfig(filePath);
		ConfigurationSection section = config.getConfigurationSection("oceanworld");
		if (section != null) {
			List<Long> chunks = section.getLongList("chunks");
			worldChunkKeys.addAll(chunks);
		}

		Tasks.repeat(Time.SECOND.x(15), Time.TICK.x(4), () -> {
			if (!process)
				return;

			checkTPS();

			for (int i = 0; i < blocksPerLoop; i++) {
				Location poll = locations.poll();

				if (poll == null)
					return;

				if (!poll.isWorldLoaded())
					return;

				Block block = poll.getBlock();
				Material type = block.getType();

				switch (type) {
					case STONE:
					case DIRT:
					case SANDSTONE:
					case SAND:
					case TERRACOTTA:
					case BEDROCK:
						continue;
					case GRASS_BLOCK:
						block.setType(Material.DIRT);
						continue;
				}

				if (MaterialTag.ALL_AIR.isTagged(type) || Material.ICE == type || Material.WATER == type)
					block.setType(Material.WATER, false);

				if (block.getBlockData() instanceof Waterlogged waterlogged) {
					waterlogged.setWaterlogged(true);
					block.setBlockData(waterlogged);
				}
			}

			Nexus.log("Processed " + blocksPerLoop + " blocks, " + locations.size() + " left in queue");
		});
	}

	private static void checkTPS() {
		double tps = Bukkit.getTPS()[0];

		if (tps < 15)
			blocksPerLoop = 500;
		else if (tps < 16)
			blocksPerLoop = 1000;
		else if (tps < 18)
			blocksPerLoop = 2500;
		else if (tps < 19)
			blocksPerLoop = 5000;
	}

//	@EventHandler
//	public void onBlockForm(BlockFormEvent event) {
//		if (!isOceanWorld(event.getBlock().getWorld()))
//			return;
//
//		if (event.getNewState().getType() == Material.ICE)
//			if (event.getBlock().getLocation().getY() < 200)
//				event.setCancelled(true);
//	}
}
