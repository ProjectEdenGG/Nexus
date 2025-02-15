package gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.api.common.utils.Utils.MinMaxResult;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.scheduledjobs.jobs.BearFair21TreeRegenJob;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import gg.projecteden.nexus.utils.WorldEditUtils.Paster;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BearFair21WoodCutting implements Listener {
	private static final String tree_region = BearFair21.getRegion() + "_trees";
	@Getter
	private static boolean treeAnimating = false;

	public BearFair21WoodCutting() {
		Nexus.registerListener(this);
	}

	public static boolean breakBlock(BlockBreakEvent event) {
		if (!BearFair21.isInRegionRegex(event.getBlock().getLocation(), tree_region + ".*"))
			return false;

		BearFair21TreeType treeType = BearFair21TreeType.of(event.getBlock().getType());
		if (treeType == null)
			return false;

		Set<ProtectedRegion> regions = BearFair21.worldguard().getRegionsLike(tree_region + "_" + treeType.name() + "_[\\d]+");

		MinMaxResult<ProtectedRegion> result = Utils.getMin(regions, region ->
			Distance.distance(event.getBlock(), BearFair21.worldguard().toLocation(region.getMinimumPoint())).get());

		ProtectedRegion region = result.getObject();
		double distance = Math.sqrt(result.getValue().doubleValue());

		if (region == null)
			return false;

		int tree = Integer.parseInt(region.getId().split("_")[3]);

		if (tree < 1 || distance > 5)
			return false;

		treeType.feller(event.getPlayer(), tree);
		return true;
	}

	public enum BearFair21TreeType {
		OAK(Material.OAK_WOOD, Material.OAK_LEAVES),
		;

		@Getter
		private final Material logs;
		@Getter
		private final List<Material> others;

		@Getter
		private final Map<Integer, Paster> pasters = new ConcurrentHashMap<>();
		@Getter
		private final Map<Integer, CompletableFuture<Queue<Location>>> queues = new ConcurrentHashMap<>();
		@Getter
		private final Map<Integer, ProtectedRegion> regions = new ConcurrentHashMap<>();

		private static final long animationTime = TickTime.SECOND.x(3);

		BearFair21TreeType(Material logs, Material... others) {
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

		public ItemBuilder getDrop() {
			return new ItemBuilder(logs).name(StringUtils.camelCase(name() + " Logs")).amount(1);
		}

		public List<ItemStack> getDrops(ItemStack tool) {
			List<ItemStack> drops = new ArrayList<>();
			Material toolType = Nullables.isNullOrAir(tool) ? Material.AIR : tool.getType();
			boolean chance = Tool.from(toolType).chance();
			if (chance) {
				drops.add(new ItemBuilder(logs).name(StringUtils.camelCase(name() + " Logs")).amount(1).build());
				if (RandomUtils.chanceOf(25))
					drops.add(new ItemBuilder(Material.STICK).amount(RandomUtils.randomInt(1, 3)).build());
			}

			return drops;
		}

		public List<Material> getAllMaterials() {
			ArrayList<Material> materials = new ArrayList<>(others);
			materials.add(logs);
			return materials;
		}

		public String getAllMaterialsString() {
			return getAllMaterials().stream().map(material -> material.name().toLowerCase()).collect(Collectors.joining(","));
		}

		public static BearFair21TreeType of(Material logs) {
			for (BearFair21TreeType treeType : BearFair21TreeType.values())
				if (treeType.getLogs() == logs)
					return treeType;

			return null;
		}

		public CompletableFuture<Void> build(int id) {
			treeAnimating = true;
			final CompletableFuture<Void> future = getPaster(id).buildQueue();
			future.thenRun(() -> treeAnimating = false);
			return future;
		}

		private CompletableFuture<Queue<Location>> getQueue(int id) {
			return queues.computeIfAbsent(id, $ -> {
				final CompletableFuture<Queue<Location>> future = new CompletableFuture<>();

				ProtectedRegion region = getRegion(id);
				if (region == null)
					return null;

				Location base = BearFair21.worldedit().toLocation(region.getMinimumPoint());
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
				return BearFair21.worldedit().paster()
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
					return BearFair21.worldguard().getProtectedRegion(tree_region + "_" + name().toLowerCase() + "_" + id);
				} catch (InvalidInputException ex) {
					return null;
				}
			});

			return regions.get(id);
		}

		public void feller(Player player, int id) {
			if (!new CooldownService().check(UUIDUtils.UUID0, getRegion(id).getId(), TickTime.SECOND.x(3)))
				return;

			treeAnimating = true;
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

						Tasks.wait(wait, () -> poll.getBlock().setType(Material.AIR, true));
					}
				}

				Tasks.wait(++wait, () -> treeAnimating = false);

				Tasks.Countdown.builder()
					.duration(gg.projecteden.api.common.utils.RandomUtils.randomLong(8, 12) * 4)
					.onTick(i -> {
						if (i % 2 == 0)
							PlayerUtils.giveItems(player, getDrops(ItemUtils.getTool(player)));
					})
					.start();

				Jingle.TREE_FELLER.play(player);

				new BearFair21TreeRegenJob(this, id).schedule(RandomUtils.randomInt(3 * 60, 5 * 60));
			}));
		}

	}

	@AllArgsConstructor
	private enum Tool {
		AIR(10, 15),
		WOODEN(15, 25),
		STONE(25, 40),
		GOLDEN(40, 45),
		IRON(45, 60),
		DIAMOND(60, 75),
		NETHERITE(75, 90),
		;

		int min;
		int max;

		public static Tool from(Material material) {
			return switch (material) {
				case WOODEN_AXE -> WOODEN;
				case STONE_AXE -> STONE;
				case GOLDEN_AXE -> GOLDEN;
				case IRON_AXE -> IRON;
				case DIAMOND_AXE -> DIAMOND;
				case NETHERITE_AXE -> NETHERITE;
				default -> AIR;
			};

		}

		public boolean chance() {
			return RandomUtils.chanceOf(RandomUtils.randomInt(min, max));
		}
	}

}
