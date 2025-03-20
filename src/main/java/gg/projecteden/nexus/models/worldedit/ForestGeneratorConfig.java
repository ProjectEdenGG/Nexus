package gg.projecteden.nexus.models.worldedit;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.DebugDotCommand;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.WorldEditUtils.Paster;
import lombok.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "forest_generator_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class ForestGeneratorConfig implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<TreeList> treeLists = new ArrayList<>();

	public TreeList getTreeList(String id) {
		return treeLists.stream().filter(treeList -> treeList.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
	}

	@Data
	public static class TreeList {
		private String id;
		private UUID creator;
		private List<Tree> trees;
		private LocalDateTime createdTime;
		private int defaultPadding;

		public TreeList(List<Tree> trees, UUID creator) {
			this.trees = trees;
			this.creator = creator;
			this.createdTime = LocalDateTime.now();
		}

		@Data
		public static class Tree {
			private int id;
			private int padding;
			private int weight;

			private transient Map<Vector, BlockData> blocks = new ConcurrentHashMap<>();

			public Tree(int id) {
				this.id = id;
			}

			public static Tree at(Location location) {
				location.setY(4);
				final var regions = new WorldGuardUtils(location).getRegionsLikeAt("tree_\\d+", location);
				if (regions.isEmpty())
					throw new InvalidInputException("Unable to determine id for tree at " + StringUtils.getShortLocationString(location));

				return new Tree(Integer.parseInt(regions.iterator().next().getId().replace("tree_", "")));

			}

			public Map<Vector, BlockData> getBlocks() {
				if (gg.projecteden.api.common.utils.Nullables.isNullOrEmpty(blocks))
					blocks = new TreeScanner().scan(this);

				return blocks;
			}

			public void showSelected(Player player) {
				getRegionBlocks().stream()
					.map(block -> block.getRelative(BlockFace.DOWN))
					.filter(block -> block.getType() == Material.WHITE_WOOL)
					.forEach(block -> player.sendBlockChange(block.getLocation(), Material.GREEN_CONCRETE.createBlockData()));
			}

			public Region getRegion() {
				return worldedit().worldguard().getRegion("tree_" + id);
			}

			@NotNull
			private WorldEditUtils worldedit() {
				return new WorldEditUtils("buildadmin");
			}

			private List<Block> getRegionBlocks() {
				return worldedit().getBlocks(getRegion(), Nullables::isNotNullOrAir);
			}

			public void show(Player player) {
				paster().buildClientSide(player);
			}

			private Paster paster() {
				final Region region = getRegion();
				final BlockVector3 min = region.getMinimumPoint();
				final BlockVector3 max = BlockVector3.at(region.getMaximumPoint().x(), 150, region.getMaximumPoint().z());
				final CuboidRegion cuboidRegion = new CuboidRegion(worldedit().worldguard().getWorldEditWorld(), min, max);
				return worldedit().paster().clipboard(cuboidRegion).air(false);
			}

			public static class TreeScanner {
				private Tree tree;
				private Location startLocation;
				private final List<Location> locations = new ArrayList<>();
				private final Map<Vector, BlockData> blocks = new ConcurrentHashMap<>();

				public Map<Vector, BlockData> scan(Tree tree) {
					this.tree = tree;
					final List<Block> blocks = tree.getRegionBlocks();
					if (blocks.isEmpty())
						throw new InvalidInputException("Could not find any tree blocks for tree &e#" + tree.getId());

					return scan(blocks.get(0).getLocation());
				}

				public Map<Vector, BlockData> scan(Location start) {
					while (true)
						try {
							locations.remove(start);
							recurse(start);
							break;
						} catch (StackOverflowError ignore) {}

					if (tree == null)
						throw new InvalidInputException("Unable to determine id for tree at " + StringUtils.getShortLocationString(start));

					return compute();
				}

				private Map<Vector, BlockData> compute() {
					Iterator<Location> iterator = locations.iterator();

					BlockVector3 origin = tree.worldedit().toBlockVector3(tree.getRegion().getCenter());

					while (iterator.hasNext()) {
						Location location = iterator.next();

						int x = location.getBlockX() - origin.x();
						int y = location.getBlockY() - origin.y();
						int z = location.getBlockZ() - origin.z();

						Block block = location.getBlock();
						if (block.getType().isEmpty())
							continue;

						blocks.put(new Vector(x, y, z), block.getBlockData());
					}

					return blocks;
				}

				@SneakyThrows
				private void recurse(Location location) {
					if (locations.contains(location))
						return;

					// We're already async, so block this thread
					startLocation.getWorld().getChunkAtAsync(location).get();
					final Block block = location.getBlock();
					if (block.getType() == Material.AIR || MaterialTag.WOOL.isTagged(block))
						return;

					if (Dev.GRIFFIN.isOnline())
						DebugDotCommand.play(Dev.GRIFFIN.getOnlinePlayer(), location);

					locations.add(location);
					startLocation = location;

					if (location.getY() == 4 && tree == null) {
						final var regions = new WorldGuardUtils(location).getRegionsLikeAt("tree_\\d+", location);
						if (!regions.isEmpty())
							tree = new Tree(Integer.parseInt(regions.iterator().next().getId().replace("tree_", "")));
					}

					for (BlockFace face : EnumUtils.valuesExcept(BlockFace.class, BlockFace.SELF))
						recurse(location.clone().add(face.getModX(), face.getModY(), face.getModZ()).toBlockLocation());
				}

			}

		}

	}

}
