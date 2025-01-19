package gg.projecteden.nexus.utils;

import com.fastasyncworldedit.core.extent.processor.lighting.RelightMode;
import com.fastasyncworldedit.core.regions.RegionWrapper;
import com.fastasyncworldedit.core.wrappers.WorldWrapper;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSessionBuilder;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
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
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.CompletableFutures;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.parchment.HasPlayer;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.enginehub.linbus.tree.LinTagType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class WorldEditUtils {
	@NonNull
	private final org.bukkit.World world;
	private final BukkitWorld bukkitWorld;
	public final World worldEditWorld;
	@Getter
	@Accessors(fluent = true)
	private final WorldGuardUtils worldguard;
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
		this.worldguard = new WorldGuardUtils(world);
	}

	public EditSessionBuilder getEditSessionBuilder() {
		return WorldEdit.getInstance().newEditSessionBuilder()
			.world(worldEditWorld)
			.allowedRegionsEverywhere()
			.relightMode(RelightMode.ALL)
			.fastMode(true);
	}

	public EditSession getEditSession() {
		return getEditSessionBuilder().build();
	}

	public File getSchematicFile(String fileName, boolean lookForExisting) {
		final String fileNoExtension = schematicsDirectory + fileName + ".";
		File file = new File(fileNoExtension + BuiltInClipboardFormat.SPONGE_SCHEMATIC.getPrimaryFileExtension());
		if (!file.exists() && lookForExisting)
			file = new File(fileNoExtension + BuiltInClipboardFormat.MCEDIT_SCHEMATIC.getPrimaryFileExtension());
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

	public Vector toVector(Location location) {
		return new Vector(location.getX(), location.getY(), location.getZ());
	}

	public Vector toVector(BlockVector3 vector3) {
		return new Vector(vector3.x(), vector3.y(), vector3.z());
	}

	public Vector3 toVector3(Location location) {
		return Vector3.at(location.getX(), location.getY(), location.getZ());
	}

	public BlockVector3 toBlockVector3(Location location) {
		return BlockVector3.at(location.getX(), location.getY(), location.getZ());
	}

	public BlockVector3 toBlockVector3(Vector3 vector) {
		return BlockVector3.at(vector.x(), vector.y(), vector.z());
	}

	public BlockVector3 toBlockVector3(Vector vector) {
		return BlockVector3.at(vector.getX(), vector.getY(), vector.getZ());
	}

	public Location toLocation(Vector3 vector) {
		return new Location(world, vector.x(), vector.y(), vector.z());
	}

	public Location toLocation(Vector vector) {
		return new Location(world, vector.getX(), vector.getY(), vector.getZ());
	}

	public Location toLocation(BlockVector3 vector) {
		return new Location(world, vector.x(), vector.y(), vector.z());
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

	public @Nullable List<String> getSignLines(BaseBlock baseBlock) {
		Material material = BukkitAdapter.adapt(baseBlock.getBlockType());
		if (material == null || !MaterialTag.SIGNS.isTagged(material))
			return null;

		List<String> lines = new ArrayList<>();
		String[] linesNBT = {"Text1", "Text2", "Text3", "Text4"};

		for (String nbtLine : linesNBT) {
			var nbt = baseBlock.getNbt();
			if (nbt != null)
				lines.add(nbt.getTag(nbtLine, LinTagType.stringTag()).value());
		}

		return lines;
	}

	public enum SelectionChangeDirectionType {
		HORIZONTAL {
			@Override
			BlockVector3[] getVectors() {
				return new BlockVector3[]{BlockVector3.UNIT_X, BlockVector3.UNIT_MINUS_X, BlockVector3.UNIT_Z, BlockVector3.UNIT_MINUS_Z};
			}
		},
		VERTICAL {
			@Override
			BlockVector3[] getVectors() {
				return new BlockVector3[]{BlockVector3.UNIT_Y, BlockVector3.UNIT_MINUS_Y};
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
		BlockVector3[] directions = directionType.apply(amount);

		if (changeType == SelectionChangeType.EXPAND)
			region.expand(directions);
		else if (changeType == SelectionChangeType.CONTRACT)
			region.contract(directions);

		getPlayer(player).setSelection(region);
		session.getRegionSelector(worldEditWorld).learnChanges();
		session.getRegionSelector(worldEditWorld).explainRegionAdjust(getPlayer(player), session);
	}

	@SneakyThrows
	public void changeSelection(HasPlayer player, RegionSelector selector) {
		LocalSession session = plugin.getSession(player.getPlayer());
		getPlayer(player).setSelection(selector);
		session.getRegionSelector(worldEditWorld).learnChanges();
		session.getRegionSelector(worldEditWorld).explainRegionAdjust(getPlayer(player), session);
	}

	/**
	 * Sets player's selection to one block
	 *
	 * @param player   player selection to modify
	 * @param location location to set primary and secondary coordinates
	 */
	public void setSelection(HasPlayer player, Location location) {
		setSelection(player, location, location);
	}

	/**
	 * Sets player's selection to one block
	 *
	 * @param player player selection to modify
	 * @param vector location to set primary and secondary coordinates
	 */
	public void setSelection(HasPlayer player, BlockVector3 vector) {
		setSelection(player, vector, vector);
	}

	/**
	 * Sets player's selection
	 *
	 * @param player player selection to modify
	 * @param min    primary point
	 * @param max    secondary point
	 */
	public void setSelection(HasPlayer player, Location min, Location max) {
		setSelection(player, toBlockVector3(min), toBlockVector3(max));
	}

	/**
	 * Sets player's selection
	 *
	 * @param player player selection to modify
	 * @param min    primary point
	 * @param max    secondary point
	 */
	public void setSelection(HasPlayer player, BlockVector3 min, BlockVector3 max) {
		setSelection(player, new CuboidRegion(min, max));
	}

	/**
	 * Sets player's selection
	 *
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
		return Material.valueOf(baseBlock.getBlockType().id().replace("minecraft:", "").toUpperCase());
	}

	public List<Block> getBlocks(ProtectedRegion region) {
		return getBlocks(worldguard.convert(region), new ArrayList<>());
	}

	public List<Block> getBlocks(Region region) {
		return getBlocks(region, Collections.emptyList());
	}

	public List<Block> getBlocks(Region region, Material material) {
		return getBlocks(region, Collections.singletonList(material));
	}

	public List<Block> getBlocks(Region region, List<Material> materials) {
		return getBlocks(region, block -> Nullables.isNullOrEmpty(materials) || materials.contains(block.getType()));
	}

	public List<Block> getBlocksPoly(ProtectedRegion region) {
		return getBlocksPoly(worldguard.convert(region));
	}

	public List<Block> getBlocksPoly(Region region) {
		Polygonal2DRegion polyRegion = (Polygonal2DRegion) region;
		return getBlocks(region, block -> polyRegion.contains(toBlockVector3(block.getLocation())));
	}

	public List<Block> getBlocks(Region region, Predicate<Block> predicate) {
		List<Block> blockList = new ArrayList<>();
		for (int x = region.getMinimumPoint().x(); x <= region.getMaximumPoint().x(); x++)
			for (int y = region.getMinimumPoint().y(); y <= region.getMaximumPoint().y(); y++)
				for (int z = region.getMinimumPoint().z(); z <= region.getMaximumPoint().z(); z++) {
					Block blockAt = world.getBlockAt(x, y, z);
					if (predicate == null || predicate.test(blockAt))
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
		return copy(worldguard.getRegion(min, max), null);
	}

	public CompletableFuture<Clipboard> copy(Region region) {
		return copy(region, null);
	}

	public CompletableFuture<Clipboard> copy(Region region, Paster paster) {
		return CompletableFuture.supplyAsync(() -> {
			synchronized (Nexus.getInstance()) {
				Consumer<String> debug = message -> {
					if (paster != null) paster.debug(message);
				};
				BlockVector3 min = region.getMinimumPoint();
				BlockVector3 max = region.getMaximumPoint();
				debug.accept("Copying " + min.x() + " " + min.y() + " " + min.z() + " -> " + max.x() + " " + max.y() + " " + max.z());

				Clipboard clipboard = new BlockArrayClipboard(region);
				try (EditSession editSession = getEditSession()) {
					ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboard, min);

					if (paster != null) {
						copy.setCopyingEntities(paster.entities);
						copy.setCopyingBiomes(paster.biomes);
					}

					debug.accept("Completing copy");
					Operations.completeBlindly(copy);
					debug.accept("Done copying");
				}

				clipboard.setOrigin(min);

				return clipboard;
			}
		}, Tasks::async);
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
		private Map<Chunk, Boolean> forceLoadedStates = new HashMap<>();

		private final UUID uuid = UUID.randomUUID();
		private final AtomicInteger i = new AtomicInteger(1000);

		public void debug(String message) {
			if (message != null) {
				final String id = StringUtils.left(uuid.toString(), 8) + " " + i.getAndIncrement() + " " + (Bukkit.isPrimaryThread() ? " sync" : "async") + " ";
				Nexus.debug(id + message);
			}
		}

		private long ticks;
		private CompletableFuture<Map<Location, BlockData>> computedBlocks;
		private CompletableFuture<List<Location>> locations;

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
			this.regionMask = Arrays.stream(regions).map(worldguard::getRegion).toArray(Region[]::new);
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
		 * @param time duration
		 * @return this
		 * @see Paster#buildQueue
		 * @see Paster#buildQueueClientSide
		 */
		public Paster duration(TickTime time) {
			return duration(time.get());
		}

		public Paster duration(long ticks) {
			this.ticks = ticks;
			return this;
		}

		public Paster inspect() {
			computeBlocks();
			return this;
		}

		/**
		 * Get the clipboard's completable future
		 *
		 * @return future
		 */
		private CompletableFuture<Clipboard> getClipboard() {
			if (clipboardFuture == null && clipboardRegion != null)
				clipboardFuture = copy(clipboardRegion, this);
			return clipboardFuture;
		}

		/**
		 * Pastes the clipboard using FAWE
		 *
		 * @return future
		 */
		public CompletableFuture<Void> pasteAsync() {
			return forceLoadChunks(true)
				.thenCompose($ -> getClipboard().thenAcceptAsync(clipboard -> {
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
				}, Tasks::async))
				.thenCompose($ -> forceLoadChunks(false));
		}

		private CompletableFuture<Void> forceLoadChunks(boolean state) {
			return getLocations().thenApply(locations -> locations.stream()
					.map(location -> world.getChunkAtAsync(location).thenApply(chunk -> {
						var finalState = state;
						if (state)
							forceLoadedStates.put(chunk, chunk.isForceLoaded());
						else
							finalState = forceLoadedStates.get(chunk);
						chunk.setForceLoaded(finalState);
						return CompletableFuture.completedFuture(null);
					})).toList())
				.thenAccept(CompletableFutures::joinAll);
		}

		/**
		 * Builds the clipboard using the bukkit API
		 *
		 * @return future
		 */
		public CompletableFuture<Void> build() {
			final CompletableFuture<Void> future = new CompletableFuture<>();

			computeBlocks().thenAccept(blocks ->
				Tasks.sync(() -> {
					debug("Building " + blocks.size() + " blocks");
					blocks.forEach((location, blockData) -> {
						debug("  Setting " + blockData.getMaterial() + " at " + StringUtils.getFlooredCoordinateString(location));
						location.getBlock().setBlockData(blockData);
					});
					debug("Finished building " + blocks.size() + " blocks");
					future.complete(null);
				}));

			return future;
		}

		/**
		 * Builds the clipboard for a certain player
		 *
		 * @param player player
		 */
		public void buildClientSide(HasPlayer player) {
			Player _player = player.getPlayer();
			computeBlocks().thenAccept(blocks -> blocks.forEach(_player::sendBlockChange));
		}

		/**
		 * Builds the clipboard over the specified duration
		 *
		 * @return future
		 */
		public CompletableFuture<Void> buildQueue() {
			return buildQueue(location -> () -> computeBlocks().thenAccept(blocks ->
				location.getBlock().setBlockData(blocks.get(location))));
		}

		/**
		 * Builds the clipboard for a certain player over the specified duration
		 *
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
					Queue<Location> queue = BlockUtils.createDistanceSortedQueue(toLocation(at));
					queue.addAll(blocks.keySet());

					int wait = 0;
					long blocksPerTick = Math.max(queue.size() / ticks, 1);
					long delay = Math.max(ticks / queue.size(), 1);

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

		public CompletableFuture<List<Location>> getLocations() {
			if (locations == null) {
				locations = new CompletableFuture<>();
				getClipboard().thenAcceptAsync(clipboard -> {
					debug("Clipboard completed");
					Iterator<BlockVector3> iterator = clipboard.iterator();

					BlockVector3 origin = clipboard.getOrigin();
					int relX = at.x() - origin.x();
					int relY = at.y() - origin.y();
					int relZ = at.z() - origin.z();

					List<Location> data = new ArrayList<>();

					while (iterator.hasNext())
						data.add(toLocation(iterator.next()).add(relX, relY, relZ));

					debug("Finished computing " + data.size() + " blocks");
					locations.complete(data);
				}, Tasks::async);
			}

			return locations;
		}

		public CompletableFuture<Map<Location, BlockData>> computeBlocks() {
			if (computedBlocks == null) {
				debug("Computing blocks");
				computedBlocks = new CompletableFuture<>();
				getClipboard().thenAcceptAsync(clipboard -> {
					debug("Clipboard completed");
					Iterator<BlockVector3> iterator = clipboard.iterator();

					BlockVector3 origin = clipboard.getOrigin();
					int relX = at.x() - origin.x();
					int relY = at.y() - origin.y();
					int relZ = at.z() - origin.z();

					Map<Location, BlockData> data = new HashMap<>();

					while (iterator.hasNext()) {
						BlockVector3 blockVector3 = iterator.next();
						BaseBlock baseBlock = blockVector3.getFullBlock(clipboard);
						if (baseBlock.getMaterial().isAir() && !air)
							continue;

						Location location = toLocation(blockVector3).add(relX, relY, relZ);
						final BlockData block = BukkitAdapter.adapt(baseBlock);

						debug("  Found " + block.getMaterial() + "  at " + StringUtils.getFlooredCoordinateString(toLocation(blockVector3)) + " (" + baseBlock.getAsString() + ")");
						data.put(location, block);
					}

					debug("Finished computing " + data.size() + " blocks");
					computedBlocks.complete(data);
				}, Tasks::async);
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

	// TODO: Doesn't work? schematic just pastes air
	@SneakyThrows
	public void save(String fileName, BlockVector3 min, BlockVector3 max) {
		CuboidRegion region = new CuboidRegion(worldEditWorld, min, max);
		new BlockArrayClipboard(region).save(getSchematicFile(fileName, false), BuiltInClipboardFormat.SPONGE_SCHEMATIC);
	}

	public CompletableFuture<Void> set(String region, BlockType blockType) {
		return set(worldguard.convert(worldguard.getProtectedRegion(region)), blockType);
	}

	public CompletableFuture<Void> set(Region region, BlockType blockType) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		Tasks.async(() -> {
			EditSession editSession = getEditSession();
			editSession.setBlocks(region, blockType.getDefaultState().toBaseBlock());
			editSession.flushQueue();
			future.complete(null);
		});
		return future;
	}

	public CompletableFuture<Void> replace(Region region, BlockType from, BlockType to) {
		return replace(region, Collections.singleton(from), Collections.singleton(to));
	}

	public CompletableFuture<Void> replace(Region region, Set<BlockType> from, Set<BlockType> to) {
		return replace(region, from, toRandomPattern(to));
	}

	public CompletableFuture<Void> replace(Region region, Set<BlockType> from, Map<BlockType, Double> pattern) {
		return replace(region, from, toRandomPattern(pattern));
	}

	public CompletableFuture<Void> replace(Region region, Set<BlockType> from, Pattern pattern) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		Tasks.async(() -> {
			EditSession editSession = getEditSession();
			editSession.replaceBlocks(region, toBaseBlocks(from), pattern);
			editSession.flushQueue();
			future.complete(null);
		});
		return future;
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
		session.getRegionSelector(region.getWorld()).learnChanges();
		set(region, Objects.requireNonNull(BlockTypes.AIR)).thenRun(() -> {
			region.contract(Direction.DOWN.toBlockVector().multiply(500));
			region.expand(Direction.UP.toBlockVector().multiply(64));
			region.contract(Direction.UP.toBlockVector().multiply(64));
			session.getRegionSelector(region.getWorld()).learnChanges();
			set(region, Objects.requireNonNull(BlockTypes.BEDROCK)).thenRun(() -> {
				region.expand(Direction.UP.toBlockVector().multiply(2));
				region.contract(Direction.UP.toBlockVector().multiply(1));
				session.getRegionSelector(region.getWorld()).learnChanges();
				set(region, Objects.requireNonNull(BlockTypes.DIRT)).thenRun(() -> {
					region.expand(Direction.UP.toBlockVector().multiply(1));
					region.contract(Direction.UP.toBlockVector().multiply(2));
					session.getRegionSelector(region.getWorld()).learnChanges();
					set(region, Objects.requireNonNull(BlockTypes.GRASS_BLOCK));
				});
			});
		});
	}

	public static Vector getSchematicOffset(Clipboard clipboard) {
		return new Vector(
			clipboard.getMinimumPoint().x() - clipboard.getOrigin().x(),
			clipboard.getMinimumPoint().y() - clipboard.getOrigin().y(),
			clipboard.getMinimumPoint().z() - clipboard.getOrigin().z());
	}

}
