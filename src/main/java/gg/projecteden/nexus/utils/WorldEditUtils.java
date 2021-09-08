package gg.projecteden.nexus.utils;

import com.fastasyncworldedit.core.extent.processor.lighting.RelightMode;
import com.fastasyncworldedit.core.regions.RegionWrapper;
import com.fastasyncworldedit.core.util.EditSessionBuilder;
import com.fastasyncworldedit.core.wrappers.WorldWrapper;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.lexikiq.HasPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat.MCEDIT_SCHEMATIC;
import static com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat.SPONGE_SCHEMATIC;
import static gg.projecteden.nexus.utils.BlockUtils.createDistanceSortedQueue;
import static gg.projecteden.nexus.utils.StringUtils.getFlooredCoordinateString;
import static gg.projecteden.utils.StringUtils.left;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class WorldEditUtils {
	@NonNull
	private final org.bukkit.World world;
	private final BukkitWorld bukkitWorld;
	private final World worldEditWorld;
	@Getter
	private final WorldGuardUtils worldGuardUtils;
	@Getter
	private static final String schematicsDirectory = "plugins/FastAsyncWorldEdit/schematics/";
	@Getter
	@NotNull
	private static final WorldEditPlugin plugin = (WorldEditPlugin) Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit"));

	public WorldEditUtils(@NonNull org.bukkit.entity.Entity entity) {
		this(entity.getWorld());
	}

	public WorldEditUtils(@NonNull org.bukkit.Location location) {
		this(location.getWorld());
	}

	public WorldEditUtils(@NonNull org.bukkit.block.Block block) {
		this(block.getWorld());
	}

	public WorldEditUtils(@NonNull String world) {
		this(Objects.requireNonNull(Bukkit.getWorld(world)));
	}

	public WorldEditUtils(@NonNull org.bukkit.World world) {
		this.world = world;
		this.bukkitWorld = new BukkitWorld(world);
		this.worldEditWorld = WorldWrapper.wrap(bukkitWorld);
		this.worldGuardUtils = new WorldGuardUtils(world);
	}

	public EditSessionBuilder getEditSessionBuilder() {
		return new EditSessionBuilder(worldEditWorld)
			.allowedRegionsEverywhere()
			.relightMode(RelightMode.ALL)
			.fastmode(true);
	}

	public EditSession getEditSession() {
		return getEditSessionBuilder().build();
	}

	private File getSchematicFile(String fileName, boolean lookForExisting) {
		final String fileNoExtension = schematicsDirectory + fileName + ".";
		File file = new File(fileNoExtension + SPONGE_SCHEMATIC.getPrimaryFileExtension());
		if (!file.exists() && lookForExisting)
			file = new File(fileNoExtension + MCEDIT_SCHEMATIC.getPrimaryFileExtension());
		return file;
	}

	@SneakyThrows
	public Clipboard getSchematic(String fileName) {
		File file = getSchematicFile(fileName, true);
		if (!file.exists())
			throw new InvalidInputException("Schematic " + fileName + " does not exist");

		final ClipboardFormat format = ClipboardFormats.findByFile(file);
		if (format == null)
			throw new InvalidInputException("Could not determine clipboard format for " + fileName);

		return format.load(file);
	}

	public Vector3 toVector3(Location location) {
		return Vector3.at(location.getX(), location.getY(), location.getZ());
	}

	public BlockVector3 toBlockVector3(Location location) {
		return BlockVector3.at(location.getX(), location.getY(), location.getZ());
	}

	public BlockVector3 toBlockVector3(Vector3 vector) {
		return BlockVector3.at(vector.getX(), vector.getY(), vector.getZ());
	}

	public Location toLocation(Vector3 vector) {
		return new Location(world, vector.getX(), vector.getY(), vector.getZ());
	}

	public Location toLocation(BlockVector3 vector) {
		return new Location(world, vector.getX(), vector.getY(), vector.getZ());
	}

	public BukkitPlayer getPlayer(HasPlayer player) {
		return BukkitAdapter.adapt(player.getPlayer());
	}

	public Region getPlayerSelection(HasPlayer player) {
		return getPlayer(player).getSelection();
	}

	public void fixLight(Region region) {
		Tasks.async(() -> worldEditWorld.fixLighting(region.getChunks()));
	}

	public enum SelectionChangeDirectionType {
		HORIZONTAL {
			@Override
			BlockVector3[] getVectors() {
				return new BlockVector3[]{ BlockVector3.UNIT_X, BlockVector3.UNIT_MINUS_X, BlockVector3.UNIT_Z, BlockVector3.UNIT_MINUS_Z };
			}
		},
		VERTICAL {
			@Override
			BlockVector3[] getVectors() {
				return new BlockVector3[]{ BlockVector3.UNIT_Y, BlockVector3.UNIT_MINUS_Y };
			}
		},
		ALL {
			@Override
			BlockVector3[] getVectors() {
				List<BlockVector3> vectors = new ArrayList<>();
				Stream.of(HORIZONTAL, VERTICAL).map(value -> Arrays.asList(value.getVectors())).forEach(vectors::addAll);
				return vectors.toArray(BlockVector3[]::new);
			}
		};

		abstract BlockVector3[] getVectors();

		public BlockVector3[] apply(int amount) {
			return Stream.of(getVectors())
					.map(vector -> vector.multiply(amount))
					.toArray(BlockVector3[]::new);
		}
	}

	public enum SelectionChangeType {
		EXPAND,
		CONTRACT
	}

	@SneakyThrows
	public void changeSelection(HasPlayer player, SelectionChangeType changeType, SelectionChangeDirectionType directionType, int amount) {
		if (amount <= 0) return;
		LocalSession session = plugin.getSession(player.getPlayer());
		Region region = session.getSelection(worldEditWorld);
		long oldSize = region.getVolume();
		BlockVector3[] directions = directionType.apply(amount);

		if (changeType == SelectionChangeType.EXPAND)
			region.expand(directions);
		else if (changeType == SelectionChangeType.CONTRACT)
			region.contract(directions);

		getPlayer(player).setSelection(region);
		session.getRegionSelector(worldEditWorld).learnChanges();
		long newSize = region.getVolume();
		session.getRegionSelector(worldEditWorld).explainRegionAdjust(getPlayer(player), session);
	}

	/**
	 * Sets player's selection to one block
	 * @param player player selection to modify
	 * @param location location to set primary and secondary coordinates
	 */
	public void setSelection(HasPlayer player, Location location) {
		setSelection(player, location, location);
	}

	/**
	 * Sets player's selection to one block
	 * @param player player selection to modify
	 * @param vector location to set primary and secondary coordinates
	 */
	public void setSelection(HasPlayer player, BlockVector3 vector) {
		setSelection(player, vector, vector);
	}

	/**
	 * Sets player's selection
	 * @param player player selection to modify
	 * @param min primary point
	 * @param max secondary point
	 */
	public void setSelection(HasPlayer player, Location min, Location max) {
		setSelection(player, toBlockVector3(min), toBlockVector3(max));
	}

	/**
	 * Sets player's selection
	 * @param player player selection to modify
	 * @param min primary point
	 * @param max secondary point
	 */
	public void setSelection(HasPlayer player, BlockVector3 min, BlockVector3 max) {
		setSelection(player, new CuboidRegion(min, max));
	}

	/**
	 * Sets player's selection
	 * @param player player selection to modify
	 * @param region region to set
	 */
	public void setSelection(HasPlayer player, Region region) {
		Player _player = player.getPlayer();
		LocalSession session = plugin.getSession(_player);
		getPlayer(player).setSelection(region);
		com.sk89q.worldedit.entity.Player worldEditPlayer = plugin.wrapPlayer(_player);
		session.getRegionSelector(worldEditWorld).explainPrimarySelection(worldEditPlayer, session, region.getMinimumPoint());
		session.getRegionSelector(worldEditWorld).explainSecondarySelection(worldEditPlayer, session, region.getMaximumPoint());
	}

	public Material toMaterial(BaseBlock baseBlock) {
		return Material.valueOf(baseBlock.getBlockType().getId().replace("minecraft:", "").toUpperCase());
	}

	public List<Block> getBlocks(ProtectedRegion region) {
		return getBlocks(worldGuardUtils.convert(region), new ArrayList<>());
	}

	public List<Block> getBlocks(Region region) {
		return getBlocks(region, Collections.emptyList());
	}

	public List<Block> getBlocks(Region region, Material material) {
		return getBlocks(region, Collections.singletonList(material));
	}

	public List<Block> getBlocks(Region region, List<Material> materials) {
		List<Block> blockList = new ArrayList<>();
		for (int x = region.getMinimumPoint().getBlockX(); x <= region.getMaximumPoint().getBlockX(); x++)
			for (int y = region.getMinimumPoint().getBlockY(); y <= region.getMaximumPoint().getBlockY(); y++)
				for (int z = region.getMinimumPoint().getBlockZ(); z <= region.getMaximumPoint().getBlockZ(); z++) {
					Block blockAt = world.getBlockAt(x, y, z);
					if (Utils.isNullOrEmpty(materials) || materials.contains(blockAt.getType()))
						blockList.add(blockAt);
				}
		return blockList;
	}

	public Set<BaseBlock> toBaseBlocks(Set<BlockType> blockTypes) {
		return blockTypes.stream().map(blockType -> blockType.getDefaultState().toBaseBlock()).collect(Collectors.toSet());
	}

	public RandomPattern toRandomPattern(Set<BlockType> baseBlocks) {
		RandomPattern pattern = new RandomPattern();
		baseBlocks.forEach(baseBlock -> pattern.add(baseBlock, (float) 100 / baseBlocks.size()));
		return pattern;
	}

	public RandomPattern toRandomPattern(Map<BlockType, Double> materials) {
		RandomPattern pattern = new RandomPattern();
		materials.forEach(pattern::add);
		return pattern;
	}

	public Region region(Location min, Location max) {
		return region(toBlockVector3(min), toBlockVector3(max));
	}

	public Region region(BlockVector3 min, BlockVector3 max) {
		return new CuboidRegion(worldEditWorld, min, max);
	}

	public CompletableFuture<Clipboard> copy(Location min, Location max) {
		return copy(worldGuardUtils.getRegion(min, max), null);
	}

	public CompletableFuture<Clipboard> copy(Region region) {
		return copy(region, null);
	}

	public CompletableFuture<Clipboard> copy(Region region, Paster paster) {
		return CompletableFuture.supplyAsync(() -> {
			synchronized (Nexus.getInstance()) {
				Consumer<String> debug = message -> { if (paster != null) paster.debug(message); };
				debug.accept("Copying");

				Clipboard clipboard = new BlockArrayClipboard(region);
				try (EditSession editSession = getEditSession()) {
					ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());

					if (paster != null) {
						copy.setCopyingEntities(paster.entities);
						copy.setCopyingBiomes(paster.biomes);
					}

					debug.accept("Completing copy");
					Operations.completeBlindly(copy); // deadlocking
					debug.accept("Done copying");
				}

				return clipboard;
			}
		});
	}

	public Paster paster() {
		return paster(null);
	}

	public Paster paster(String message) {
		final Paster paster = new Paster();
		paster.debug(message);
		return paster;
	}

	@Data
	@NoArgsConstructor
	public class Paster {
		private CompletableFuture<Clipboard> clipboardFuture;
		private Region clipboardRegion;
		private BlockVector3 at;
		private boolean air = true;
		private boolean entities = false;
		private boolean biomes = false;
		private Transform transform;
		private Region[] regionMask = new Region[]{RegionWrapper.GLOBAL()};

		private final UUID uuid = UUID.randomUUID();
		private final AtomicInteger i = new AtomicInteger(1000);

		public void debug(String message) {
			if (message != null) {
				final String id = left(uuid.toString(), 8) + " " + i.getAndIncrement() + " " + (Bukkit.isPrimaryThread() ? " sync" : "async") + " ";
				Nexus.debug(id + message);
			}
		}

		private int ticks;
		private CompletableFuture<Map<Location, BlockData>> computedBlocks;

		public Paster file(String fileName) {
			return clipboard(getSchematic(fileName));
		}

		public Paster clipboard(Player player) {
			return clipboard(getPlayerSelection(player));
		}

		public Paster clipboard(Clipboard clipboard) {
			this.clipboardFuture = CompletableFuture.completedFuture(clipboard);
			return this;
		}

		public Paster clipboard(Location min, Location max) {
			this.clipboardRegion = region(min, max);
			return this;
		}

		public Paster clipboard(BlockVector3 min, BlockVector3 max) {
			this.clipboardRegion = region(min, max);
			return this;
		}

		public Paster clipboard(Region region) {
			this.clipboardRegion = region;
			return this;
		}

		public Paster regionMask(String... regions) {
			this.regionMask = Arrays.stream(regions).map(worldGuardUtils::getRegion).toArray(Region[]::new);
			return this;
		}

		public Paster regionMask(Region... regions) {
			this.regionMask = regions;
			return this;
		}

		public Paster at(Location location) {
			return at(toBlockVector3(location));
		}

		public Paster at(BlockVector3 vector) {
			this.at = vector;
			return this;
		}

		public Paster air(boolean air) {
			this.air = air;
			return this;
		}

		public Paster entities(boolean entities) {
			this.entities = entities;
			return this;
		}

		public Paster biomes(boolean biomes) {
			this.biomes = biomes;
			return this;
		}

		public Paster transform(Transform transform) {
			this.transform = transform;
			return this;
		}

		/**
		 * Duration during which to build the clipboard
		 *
		 * @see Paster#buildQueue
		 * @see Paster#buildQueueClientSide
		 * @param time duration
		 * @return this
		 */
		public Paster duration(TickTime time) {
			return duration(time.get());
		}

		public Paster duration(int ticks) {
			this.ticks = ticks;
			return this;
		}

		public Paster inspect() {
			computeBlocks();
			return this;
		}

		/**
		 * Get the clipboard's completable future
		 * @return future
		 */
		private CompletableFuture<Clipboard> getClipboard() {
			if (clipboardFuture == null && clipboardRegion != null)
				clipboardFuture = copy(clipboardRegion, this);
			return clipboardFuture;
		}

		/**
		 * Pastes the clipboard using FAWE
		 * @return future
		 */
		public CompletableFuture<Void> pasteAsync() {
			return getClipboard().thenComposeAsync(clipboard -> {
				debug("Pasting");
				try (EditSession editSession = getEditSessionBuilder().allowedRegions(regionMask).build()) {
					debug("Extent: " + editSession.getExtent().getClass().getSimpleName());
					if (transform == null)
						clipboard.paste(editSession, at, air, entities, biomes);
					else
						clipboard.paste(editSession, at, air, transform);
					debug("Done pasting");
				} catch (WorldEditException ex) {
					ex.printStackTrace();
				}

				return null;
			});
		}

		/**
		 * Builds the clipboard using the bukkit API
		 * @return future
		 */
		public CompletableFuture<Void> build() {
			final CompletableFuture<Void> future = new CompletableFuture<>();

			computeBlocks().thenAccept(blocks ->
				Tasks.sync(() -> {
					debug("Building " + blocks.size() + " blocks");
					blocks.forEach((location, blockData) -> {
						debug("  Setting " + blockData.getMaterial() + " at " + getFlooredCoordinateString(location));
						location.getBlock().setBlockData(blockData);
					});
					debug("Finished building " + blocks.size() + " blocks");
					future.complete(null);
				}));

			return future;
		}

		/**
		 * Builds the clipboard for a certain player
		 * @param player player
		 */
		public void buildClientSide(HasPlayer player) {
			Player _player = player.getPlayer();
			computeBlocks().thenAccept(blocks -> blocks.forEach(_player::sendBlockChange));
		}

		/**
		 * Builds the clipboard over the specified duration
		 * @return future
		 */
		public CompletableFuture<Void> buildQueue() {
			return buildQueue(location -> () -> computeBlocks().thenAccept(blocks ->
				location.getBlock().setBlockData(blocks.get(location))));
		}

		/**
		 * Builds the clipboard for a certain player over the specified duration
		 * @param player player
		 * @return future
		 */
		public CompletableFuture<Void> buildQueueClientSide(HasPlayer player) {
			return buildQueue(location -> () -> computeBlocks().thenAccept(blocks ->
				player.getPlayer().sendBlockChange(location.getBlock().getLocation(), blocks.get(location))));
		}

		private CompletableFuture<Void> buildQueue(Function<Location, Runnable> action) {
			CompletableFuture<Void> future = new CompletableFuture<>();
			Tasks.async(() ->
				computeBlocks().thenAccept(blocks -> {
					Queue<Location> queue = createDistanceSortedQueue(toLocation(at));
					queue.addAll(blocks.keySet());

					int wait = 0;
					int blocksPerTick = Math.max(queue.size() / ticks, 1);
					int delay = Math.max(ticks / queue.size(), 1);

					queueLoop:
					while (true) {
						wait += delay;
						for (int i = 0; i < blocksPerTick; i++) {
							Location poll = queue.poll();
							if (poll == null)
								break queueLoop;
							Tasks.wait(wait, action.apply(poll));
						}
					}

					Tasks.wait(++wait, () -> future.complete(null));
				}));

			return future;
		}

		public CompletableFuture<List<FallingBlock>> spawnFallingBlocks() {
			final CompletableFuture<List<FallingBlock>> future = new CompletableFuture<>();

			computeBlocks().thenAccept(blocks ->
				future.complete(new ArrayList<>() {{
					blocks.forEach((location, blockData) -> {
						if (!MaterialTag.ALL_AIR.isTagged(blockData.getMaterial()))
							add(spawnFallingBlock(location, blockData));
					});
				}}));

			return future;
		}

		private FallingBlock spawnFallingBlock(Location location, BlockData blockData) {
			FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(LocationUtils.getCenteredLocation(location), blockData);
			fallingBlock.setDropItem(false);
			fallingBlock.setGravity(false);
			fallingBlock.setInvulnerable(true);
			return fallingBlock;
		}

		public CompletableFuture<Map<Location, BlockData>> computeBlocks() {
			if (computedBlocks == null) {
				debug("Computing blocks");
				computedBlocks = new CompletableFuture<>();
				getClipboard().thenAcceptAsync(clipboard -> {
					debug("Clipboard completed");
					Iterator<BlockVector3> iterator = clipboard.iterator();

					BlockVector3 origin = clipboard.getOrigin();
					int relX = at.getBlockX() - origin.getBlockX();
					int relY = at.getBlockY() - origin.getBlockY();
					int relZ = at.getBlockZ() - origin.getBlockZ();

					Map<Location, BlockData> data = new HashMap<>();

					while (iterator.hasNext()) {
						BlockVector3 blockVector3 = iterator.next();
						BaseBlock baseBlock = blockVector3.getFullBlock(clipboard);
						if (baseBlock.getMaterial().isAir() && !air)
							continue;

						Location location = toLocation(blockVector3).add(relX, relY, relZ);
						final BlockData block = BukkitAdapter.adapt(baseBlock);

						debug("  Found " + block.getMaterial() + "  at " + getFlooredCoordinateString(toLocation(blockVector3)) + " (" + baseBlock.getAsString() + ")");
						data.put(location, block);
					}

					debug("Finished computing " + data.size() + " blocks");
					computedBlocks.complete(data);
				});
			}

			return computedBlocks;
		}

	}

	public void save(String fileName, Location min, Location max) {
		save(fileName, toBlockVector3(min), toBlockVector3(max));
	}

	public void save(String fileName, Region region) {
		save(fileName, region.getMinimumPoint(), region.getMaximumPoint());
	}

	@SneakyThrows
	public void save(String fileName, BlockVector3 min, BlockVector3 max) {
		CuboidRegion region = new CuboidRegion(worldEditWorld, min, max);
		new BlockArrayClipboard(region).save(getSchematicFile(fileName, false), SPONGE_SCHEMATIC);
	}

	public void set(String region, BlockType blockType) {
		set(worldGuardUtils.convert(worldGuardUtils.getProtectedRegion(region)), blockType);
	}

	public void set(Region region, BlockType blockType) {
		EditSession editSession = getEditSession();
		editSession.setBlocks(region, blockType.getDefaultState().toBaseBlock());
		editSession.flushQueue();
	}

	public void replace(Region region, BlockType from, BlockType to) {
		replace(region, Collections.singleton(from), Collections.singleton(to));
	}

	public void replace(Region region, Set<BlockType> from, Set<BlockType> to) {
		replace(region, from, toRandomPattern(to));
	}

	public void replace(Region region, Set<BlockType> from, Map<BlockType, Double> pattern) {
		replace(region, from, toRandomPattern(pattern));
	}

	public void replace(Region region, Set<BlockType> from, Pattern pattern) {
		EditSession editSession = getEditSession();
		editSession.replaceBlocks(region, toBaseBlocks(from), pattern);
		editSession.flushQueue();
	}

	public Region expandAll(Region region, int amount) {
		region.expand(SelectionChangeDirectionType.ALL.apply(amount));
		return region;
	}

	public Region contractAll(Region region, int amount) {
		region.contract(SelectionChangeDirectionType.ALL.apply(amount));
		return region;
	}

	@SneakyThrows
	public void fixFlat(LocalSession session, Region region) {
		region.expand(Direction.UP.toBlockVector().multiply(500));
		region.expand(Direction.DOWN.toBlockVector().multiply(500));
		set(region, Objects.requireNonNull(BlockTypes.AIR));
		region.expand(Direction.DOWN.toBlockVector().multiply(500));
		region.contract(Direction.DOWN.toBlockVector().multiply(500));
		session.getRegionSelector(region.getWorld()).learnChanges();
		set(region, Objects.requireNonNull(BlockTypes.BEDROCK));
		region.expand(Direction.UP.toBlockVector().multiply(3));
		region.contract(Direction.UP.toBlockVector().multiply(1));
		session.getRegionSelector(region.getWorld()).learnChanges();
		set(region, Objects.requireNonNull(BlockTypes.GRASS_BLOCK));
	}

}
