package me.pugabyte.bncore.features.holidays.pugmas20.models;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.pugabyte.bncore.features.holidays.pugmas20.Pugmas20;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.task.Task;
import me.pugabyte.bncore.models.task.TaskService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils.Paste;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static me.pugabyte.bncore.utils.BlockUtils.createDistanceSortedQueue;
import static me.pugabyte.bncore.utils.StringUtils.camelCase;

public enum PugmasTreeType {
	BLOODWOOD(Material.CRIMSON_HYPHAE),
	MAHOGANY(Material.DARK_OAK_WOOD),
	EUCALYPTUS(Material.STRIPPED_BIRCH_WOOD),
	WILLOW(Material.STRIPPED_SPRUCE_WOOD),
	CRYSTAL(Material.ICE),
	MAGIC(Material.STRIPPED_WARPED_HYPHAE),
	OAK(Material.OAK_WOOD),
	TEAK(Material.STRIPPED_OAK_WOOD),
	MAPLE(Material.ACACIA_WOOD),
	BLISTERWOOD(Material.BONE_BLOCK);

	@Getter
	private final Material logs;

	private final Map<Integer, Paste> pasters = new HashMap<>();
	private final Map<Integer, Queue<Location>> queues = new HashMap<>();

	PugmasTreeType(Material logs) {
		this.logs = logs;

		Tasks.async(() -> {
			for (int id = 1; id <= 10; id++) {
				if (getRegion(id) == null)
					continue;

				getPaster(id);
				getQueue(id);
			}
		});
	}

	public static PugmasTreeType of(Material logs) {
		for (PugmasTreeType treeType : values())
			if (treeType.getLogs() == logs)
				return treeType;

		throw new InvalidInputException("Pugmas Tree with logs " + camelCase(logs) + " not found");
	}

	public void build(int id) {
		getPaster(id).buildQueue();
	}

	private Queue<Location> getQueue(int id) {
		queues.computeIfAbsent(id, $ -> {
			ProtectedRegion region = getRegion(id);
			if (region == null)
				return null;

			Location base = Pugmas20.WEUtils.toLocation(region.getMinimumPoint());
			Queue<Location> queue = createDistanceSortedQueue(base);
			queue.addAll(getBlocks(id).keySet());
			return queue;
		});

		return queues.get(id);
	}

	private Map<Location, BlockData> getBlocks(int id) {
		return getPaster(id).getComputedBlocks();
	}

	private Paste getPaster(int id) {
		pasters.computeIfAbsent(id, $ -> {
			ProtectedRegion region = getRegion(id);
			if (region == null)
				return null;

			String schematicName = region.getId().replaceAll("_", "/");
			return Pugmas20.WEUtils.paster()
					.air(false)
					.at(region.getMinimumPoint())
					.duration(Time.SECOND.x(3))
					.file(schematicName)
					.computeBlocks();
		});

		return pasters.get(id);
	}

	private ProtectedRegion getRegion(int id) {
		try {
			String regionName = "pugmas20_trees_" + name().toLowerCase() + "_" + id;
			return Pugmas20.WGUtils.getProtectedRegion(regionName);
		} catch (InvalidInputException ex) {
			return null;
		}
	}

	public void feller(Player player, int id) {
		Tasks.async(() -> {
			Queue<Location> queue = new LinkedList<>(getQueue(id));

			int wait = 0;
			int blocksPerTick = Math.max(queue.size() / 60, 1);

			queueLoop:
			while (true) {
				++wait;
				for (int i = 0; i < blocksPerTick; i++) {
					Location poll = queue.poll();
					if (poll == null)
						break queueLoop;

					Tasks.wait(wait, () -> {
						Block block = poll.getBlock();
						if (block.getType() == logs)
							Utils.giveItems(player, block.getDrops());
						block.setType(Material.AIR);
					});
				}
			}

			onBreak(id);
		});
	}

	public static String taskId = "pugmas-tree-regen";

	public void onBreak(int id) {
		new TaskService().save(new Task(taskId, new HashMap<String, Object>() {{
			put("tree", name());
			put("id", id);
		}}, LocalDateTime.now().plusSeconds(1))); //RandomUtils.randomInt(3, 5))));
	}

	static {
		Tasks.repeatAsync(Time.SECOND, Time.SECOND.x(15), () -> {
			TaskService service = new TaskService();
			service.process(taskId).forEach(task -> {
				Map<String, Object> data = task.getJson();

				PugmasTreeType treeType = PugmasTreeType.valueOf((String) data.get("tree"));
				int id = Double.valueOf((double) data.get("id")).intValue();

				treeType.build(id);

				service.complete(task);
			});
		});
	}

}

