package me.pugabyte.bncore.features.holidays.pugmas20.models;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.pugabyte.bncore.features.holidays.pugmas20.Pugmas20;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.task.Task;
import me.pugabyte.bncore.models.task.TaskService;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.ItemUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.WorldEditUtils.Paste;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.BlockUtils.createDistanceSortedQueue;
import static me.pugabyte.bncore.utils.StringUtils.camelCase;

public enum PugmasTreeType {
	BLISTERWOOD(Material.BONE_BLOCK, Material.QUARTZ_SLAB, Material.QUARTZ_STAIRS, Material.HONEY_BLOCK),
	BLOODWOOD(Material.CRIMSON_HYPHAE, Material.BLACK_STAINED_GLASS),
	CRYSTAL(Material.ICE, Material.BLUE_STAINED_GLASS, Material.BLUE_STAINED_GLASS_PANE, Material.PURPLE_STAINED_GLASS,
			Material.PURPLE_STAINED_GLASS_PANE, Material.CYAN_STAINED_GLASS, Material.CYAN_STAINED_GLASS_PANE),
	EUCALYPTUS(Material.STRIPPED_BIRCH_WOOD, Material.BIRCH_SLAB, Material.BIRCH_FENCE, Material.BIRCH_LEAVES,
			Material.BIRCH_STAIRS, Material.BIRCH_PLANKS),
	MAGIC(Material.STRIPPED_WARPED_HYPHAE, Material.SPRUCE_LEAVES),
	MAHOGANY(Material.DARK_OAK_WOOD, Material.DARK_OAK_LEAVES, Material.DARK_OAK_SLAB),
	MAPLE(Material.ACACIA_WOOD, Material.FIRE_CORAL_BLOCK, Material.NETHER_WART_BLOCK),
	OAK(Material.OAK_WOOD, Material.OAK_LEAVES),
	TEAK(Material.STRIPPED_OAK_WOOD, Material.JUNGLE_LEAVES, Material.OAK_LEAVES),
	WILLOW(Material.STRIPPED_SPRUCE_WOOD, Material.OAK_LEAVES);

	@Getter
	private final Material logs;
	@Getter
	private final List<Material> others;

	@Getter
	private final Map<Integer, Paste> pasters = new HashMap<>();
	@Getter
	private final Map<Integer, Queue<Location>> queues = new HashMap<>();

	PugmasTreeType(Material logs, Material... others) {
		this.logs = logs;
		this.others = Arrays.asList(others);

		Tasks.async(() -> {
			for (int id = 1; id <= 10; id++) {
				if (getRegion(id) == null)
					continue;

				getPaster(id);
				getQueue(id);
			}
		});
	}

	public List<Material> getAllMaterials() {
		ArrayList<Material> materials = new ArrayList<>(others);
		materials.add(logs);
		return materials;
	}

	public String getAllMaterialsString() {
		return getAllMaterials().stream().map(material -> material.name().toLowerCase()).collect(Collectors.joining(","));
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

	public ProtectedRegion getRegion(int id) {
		try {
			String regionName = "pugmas20_trees_" + name().toLowerCase() + "_" + id;
			return Pugmas20.WGUtils.getProtectedRegion(regionName);
		} catch (InvalidInputException ex) {
			return null;
		}
	}

	private static final ItemStack silk = new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.SILK_TOUCH).build();

	public void feller(Player player, int id) {
		Tasks.async(() -> {
			Queue<Location> queue = new PriorityQueue<>(getQueue(id));

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
							ItemUtils.giveItems(player, block.getDrops(silk));
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
		}}, LocalDateTime.now().plusSeconds(1)));
//		}}, LocalDateTime.now().plusSeconds(RandomUtils.randomInt(3 * 60, 5 * 60))));
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

