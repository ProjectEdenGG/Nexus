package gg.projecteden.nexus.features.events.y2021.pugmas21.models;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.scheduledjobs.jobs.Pugmas21TreeRegenJob;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils.Paster;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public enum Pugmas21TreeType {
	BLISTERWOOD(Material.BONE_BLOCK, Material.QUARTZ_SLAB, Material.QUARTZ_STAIRS, Material.HONEY_BLOCK),
	BLOODWOOD(Material.CRIMSON_HYPHAE, Material.BLACK_STAINED_GLASS),
	CRYSTAL(Material.ICE, Material.BLUE_STAINED_GLASS, Material.BLUE_STAINED_GLASS_PANE, Material.PURPLE_STAINED_GLASS,
		Material.PURPLE_STAINED_GLASS_PANE, Material.CYAN_STAINED_GLASS, Material.CYAN_STAINED_GLASS_PANE, Material.SOUL_LANTERN),
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
	private final Map<Integer, Paster> pasters = new HashMap<>();
	@Getter
	private final Map<Integer, CompletableFuture<Queue<Location>>> queues = new HashMap<>();
	@Getter
	private final Map<Integer, ProtectedRegion> regions = new HashMap<>();

	private static final long animationTime = TickTime.SECOND.x(3);

	Pugmas21TreeType(Material logs, Material... others) {
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

	public ItemStack item() {
		return item(1);
	}

	public ItemStack item(int amount) {
		return new ItemBuilder(logs).name(StringUtils.camelCase(name() + " Log")).amount(amount).build();
	}

	public List<Material> getAllMaterials() {
		ArrayList<Material> materials = new ArrayList<>(others);
		materials.add(logs);
		return materials;
	}

	public String getAllMaterialsString() {
		return getAllMaterials().stream().map(material -> material.name().toLowerCase()).collect(Collectors.joining(","));
	}

	public static Pugmas21TreeType of(Material logs) {
		for (Pugmas21TreeType treeType : Pugmas21TreeType.values())
			if (treeType.getLogs() == logs)
				return treeType;

		return null;
	}

	public CompletableFuture<Void> build(int id) {
		Pugmas21.setTreeAnimating(true);
		return getPaster(id).buildQueue().thenRun(() -> Pugmas21.setTreeAnimating(false));
	}

	private CompletableFuture<Queue<Location>> getQueue(int id) {
		return queues.computeIfAbsent(id, $ -> {
			final CompletableFuture<Queue<Location>> future = new CompletableFuture<>();

			ProtectedRegion region = getRegion(id);
			if (region == null)
				return null;

			Location base = Pugmas21.worldedit().toLocation(region.getMinimumPoint());
			Queue<Location> queue = BlockUtils.createDistanceSortedQueue(base);
			getBlocks(id).thenAccept(blocks -> {
				queue.addAll(blocks.keySet());
				future.complete(queue);
			});

			return future;
		});
	}

	private CompletableFuture<Map<Location, BlockData>> getBlocks(int id) {
		return getPaster(id).getComputedBlocks();
	}

	private Paster getPaster(int id) {
		return pasters.computeIfAbsent(id, $ -> {
			ProtectedRegion region = getRegion(id);
			if (region == null)
				return null;

			String schematicName = region.getId().replaceAll("_", "/");
			return Pugmas21.worldedit().paster()
				.air(false)
				.at(region.getMinimumPoint())
				.duration(animationTime)
				.file(schematicName)
				.inspect();
		});
	}

	public ProtectedRegion getRegion(int id) {
		regions.computeIfAbsent(id, $ -> {
			try {
				String regionName = "pugmas21_trees_" + name().toLowerCase() + "_" + id;
				return Pugmas21.worldguard().getProtectedRegion(regionName);
			} catch (InvalidInputException ex) {
				return null;
			}
		});

		return regions.get(id);
	}

	public void feller(Player player, int id) {
		if (CooldownService.isOnCooldown(UUIDUtils.UUID0, getRegion(id).getId(), TickTime.SECOND.x(3)))
			return;

		Pugmas21.setTreeAnimating(true);
		Tasks.async(() -> getQueue(id).thenAccept(queueCopy -> {
			Queue<Location> queue = new PriorityQueue<>(queueCopy);

			int wait = 0;
			long blocksPerTick = Math.max(queue.size() / animationTime, 1);

			queueLoop:
			while (true) {
				++wait;
				for (int i = 0; i < blocksPerTick; i++) {
					Location poll = queue.poll();
					if (poll == null)
						break queueLoop;

					Tasks.wait(wait, () -> poll.getBlock().setType(Material.AIR, this != CRYSTAL));
				}
			}

			Tasks.wait(++wait, () -> Pugmas21.setTreeAnimating(false));

			Tasks.Countdown.builder()
				.duration(RandomUtils.randomInt(8, 12) * 4)
				.onTick(i -> {
					if (i % 4 == 0)
						PlayerUtils.giveItem(player, item());
				})
				.start();

			Jingle.TREE_FELLER.play(player);

			new Pugmas21TreeRegenJob(this, id).schedule(RandomUtils.randomInt(3 * 60, 5 * 60));
		}));
	}

}
