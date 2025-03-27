package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.utils.SoundUtils.SoundAction;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomToolBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.IHarvestable;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.Debug.DebugType;
import gg.projecteden.nexus.utils.LocationUtils.Axis;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.parchment.HasPlayer;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("removal")
public class BlockUtils {

	public static Queue<Location> createDistanceSortedQueue(Location origin) {
		return new PriorityQueue<>((loc1, loc2) -> (int) (loc1.distanceSquared(origin) - loc2.distanceSquared(origin)));
	}

	public static void updateBlockProperty(Block block, String key, String newValue) {
		block.setBlockData(getBlockDataWithNewValue(block, key, newValue));
	}

	public static String getBlockProperty(Block block, String key) {
		return getBlockProperties(block).getOrDefault(key, null);
	}

	public static boolean containsBlockProperty(Block block, String key) {
		return getBlockProperty(block, key) != null;
	}

	public static HashMap<String, String> getBlockProperties(Block block) {
		return getBlockProperties(block.getState().getBlockData().getAsString());
	}

	public static HashMap<String, String> getBlockProperties(BlockData blockData) {
		return getBlockProperties(blockData.getAsString());
	}

	public static HashMap<String, String> getBlockProperties(String blockDataString) {
		HashMap<String, String> blockDataVariables = new HashMap<>();
		String[] variables = blockDataString.replace("]", "").split("\\[");
		String[] variableList = variables.length > 1 ? variables[1].split(",") : null;
		if (variableList != null)
			for (String s : variableList) blockDataVariables.put(s.split("=")[0], s.split("=")[1]);
		return blockDataVariables;
	}

	public static BlockData getBlockDataWithNewValue(Block block, String key, String newValue) {
		HashMap<String, String> variables = getBlockProperties(block);
		if (variables.containsKey(key.toLowerCase())) variables.put(key, newValue.toLowerCase());
		return getBlockDataFromList(block, variables);
	}

	public static BlockData getBlockDataFromList(Block block, HashMap<String, String> variables) {
		return getBlockDataFromList(block.getType(), variables);
	}

	public static BlockData getBlockDataFromList(Material material, HashMap<String, String> variables) {
		if (material == null) return null;
		if (variables != null && !variables.isEmpty()) {
			return Bukkit.createBlockData(generateBlockDataString(material, variables));
		} else {
			return Bukkit.createBlockData(material);
		}
	}

	public static String generateBlockDataString(Material material, HashMap<String, String> values) {
		if (values != null && !values.isEmpty()) {
			StringBuilder vsb = new StringBuilder();
			Iterator<String> i = values.keySet().iterator();
			while (i.hasNext()) {
				String v = i.next();
				vsb.append(String.format("%s=%s", v, i.hasNext() ? values.get(v) + "," : values.get(v)));
			}
			return String.format("minecraft:%s[%s]", material.toString().toLowerCase(), vsb.toString());
		} else {
			return String.format("minecraft:%s", material.toString()).toLowerCase();
		}
	}

	public static List<Block> getAdjacentBlocks(Block block) {
		return getAdjacentBlocks(block, List.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN));
	}

	public static List<Block> getAdjacentBlocks(Block block, boolean includeAir) {
		return getAdjacentBlocks(block, List.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN), includeAir);
	}

	public static List<Block> getAdjacentBlocks(Block block, List<BlockFace> faces) {
		return getAdjacentBlocks(block, faces, false);
	}

	public static List<Block> getAdjacentBlocks(Block block, List<BlockFace> faces, boolean includeAir) {
		List<Block> result = new ArrayList<>();
		for (BlockFace face : faces) {
			Block adjacent = block.getRelative(face);

			if (includeAir)
				result.add(adjacent);
			else if (Nullables.isNotNullOrAir(adjacent))
				result.add(adjacent);
		}

		return result;
	}

	public static List<Block> getBlocksInRadius(Location start, int radius) {
		return getBlocksInRadius(start.getBlock(), radius, radius, radius);
	}

	public static List<Block> getBlocksInRadius(Location start, int xRadius, int yRadius, int zRadius) {
		return getBlocksInRadius(start.getBlock(), xRadius, yRadius, zRadius);
	}

	public static List<Block> getBlocksInRadius(Block start, int radius) {
		return getBlocksInRadius(start, radius, radius, radius);
	}

	public static List<Block> getBlocksInRadius(Block start, int xRadius, int yRadius, int zRadius) {
		List<Block> blocks = new ArrayList<>();
		for (int x = -xRadius; x <= xRadius; x++)
			for (int z = -zRadius; z <= zRadius; z++)
				for (int y = -yRadius; y <= yRadius; y++)
					blocks.add(start.getRelative(x, y, z));
		return blocks;
	}

	/*
	Doesnt work:

	fences
	gates
	walls
	sea pickles
	pot
	hanging on all vines
	in a cobweb
	on a ladder (and on top)
	end rods
	trapdoors (uses the block underneath)
	carpet (uses the block underneath)
	scaffolding
	lily pads
	composter (inside)
	skulls (on floor & wall)
	in water & lava
	snow layers (all levels, layers=1 & 2, uses the block underneath)
	 */

	public static Block getBlockStandingOn(HasPlayer hasPlayer) {
		Player player = hasPlayer.getPlayer();
		Location below = player.getLocation().add(0, -.25, 0);
		Block block = below.getBlock();
		if (block.getType().isSolid())
			return block;

		List<BlockFace> priority = new HashMap<BlockFace, Double>() {{
			put(BlockFace.NORTH, below.getZ() - Math.floor(below.getZ()));
			put(BlockFace.EAST, Math.abs(below.getX() - Math.ceil(below.getX())));
			put(BlockFace.SOUTH, Math.abs(below.getZ() - Math.ceil(below.getZ())));
			put(BlockFace.WEST, below.getX() - Math.floor(below.getX()));
		}}.entrySet().stream()
				.filter(direction -> direction.getValue() < .3)
				.sorted(Map.Entry.comparingByValue())
				.map(Map.Entry::getKey)
				.limit(2)
				.collect(Collectors.toList());

		if (priority.size() == 2)
			priority.add(getBlockFaceBetween(priority.get(0), priority.get(1)));

		for (BlockFace blockFace : priority) {
			Block relative = block.getRelative(blockFace);
			if (relative.getType().isSolid())
				return relative;
		}

		return null;
	}

	public static BlockFace getDirection(Block from, Block to) {
		return getDirection(from.getLocation(), to.getLocation());
	}

	public static BlockFace getDirection(Location from, Location to) {
		Axis axis = Axis.of(from, to);
		if (axis == null)
			throw new InvalidInputException("Locations not aligned on an axis, cannot determine direction");

		switch (axis) {
			case X:
				if ((from.getZ() - to.getZ()) > 0)
					return BlockFace.NORTH;
				else
					return BlockFace.SOUTH;
			case Y:
				if (from.getY() - to.getY() > 0)
					return BlockFace.DOWN;
				else
					return BlockFace.UP;
			case Z:
				if (from.getX() - to.getX() > 0)
					return BlockFace.WEST;
				else
					return BlockFace.EAST;
		}

		throw new InvalidInputException("Cannot determine direction");
	}

	public static BlockFace getBlockFaceBetween(BlockFace face1, BlockFace face2) {
		int x = face1.getModX() + face2.getModX();
		int y = face1.getModY() + face2.getModY();
		int z = face1.getModZ() + face2.getModZ();
		for (BlockFace face : BlockFace.values())
			if (face.getModX() == x && face.getModY() == y && face.getModZ() == z)
				return face;

		return null;
	}

	public static boolean tryPlaceEvent(@NotNull Player player, @NotNull Block block, @NotNull Block placedAgainst, Material material) {
		return tryPlaceEvent(player, block, placedAgainst, material, null, true, player.getInventory().getItemInMainHand());
	}

	public static boolean tryPlaceEvent(@NotNull Player player, @NotNull Block block, @NotNull Block placedAgainst, Material material, BlockData blockData) {
		return tryPlaceEvent(player, block, placedAgainst, material, blockData, true, player.getInventory().getItemInMainHand());
	}

	public static boolean tryPlaceEvent(@NotNull Player player, @NotNull Block blockPlacement, @NotNull Block placedAgainst,
										@NotNull Material material, @Nullable BlockData data, boolean applyPhysics, ItemStack itemInHand) {
		// copies current data to send in event and to restore if event is cancelled
		BlockState currentState = blockPlacement.getState();
		Material currentMaterial = blockPlacement.getType();
		BlockData currentData = currentState.getBlockData();

		blockPlacement.setType(material, applyPhysics);
		if (data != null)
			blockPlacement.setBlockData(data, applyPhysics);

		// ensure no plugins are blocking placing here
		BlockPlaceEvent event = new BlockPlaceEvent(blockPlacement, blockPlacement.getState(), placedAgainst, itemInHand, player, true, EquipmentSlot.HAND);
		if (!event.callEvent() || !event.canBuild()) {
			blockPlacement.setType(currentMaterial, false);
			blockPlacement.setBlockData(currentData, false); // revert blockPlacement
			return false;
		}

		return true;
	}

	@SuppressWarnings({"UnusedReturnValue", "RedundantIfStatement"})
	public static boolean tryBreakEvent(@NotNull Player player, @NotNull Block block, boolean dropItems) {
		BlockBreakEvent event = new BlockBreakEvent(block, player);
		event.setDropItems(dropItems);
		if (!event.callEvent() || event.isCancelled())
			return false;

		return true;
	}

	@SuppressWarnings("RedundantIfStatement")
	public static boolean tryInteractEvent(Player player, Action action, Block block, BlockFace blockFace) {
		PlayerInteractEvent event = new PlayerInteractEvent(player, action, null, block, blockFace);
		if (!event.callEvent() || event.useInteractedBlock() == Result.DENY || event.useInteractedBlock() == Result.DENY)
			return false;

		return true;
	}

	public static void playSound(@Nullable Sound sound, @NonNull Location location) {
		if (sound == null)
			return;

		playSound(sound.getKey().getKey(), location);
	}

	public static void playSound(String sound, @NonNull Location location) {
		if (sound == null)
			return;

		playSound(new SoundBuilder(sound).location(location));
	}

	public static void playSound(SoundAction soundAction, @NonNull Block block) {
		Sound soundString = NMSUtils.getSound(soundAction, block);
		if (soundString == null)
			return;

		Location location = block.getLocation().toCenterLocation();

		playSound(new SoundBuilder(soundString).location(location).volume(soundAction.getVolume()));
	}

	public static void playSound(SoundBuilder soundBuilder) {
		soundBuilder.category(SoundCategory.BLOCKS).play();
	}

	public static BlockFace getCardinalBlockFace(Player player) {
		BlockFace facing = player.getFacing();
		if (cardinals.contains(facing))
			return facing;

		int ndx = Math.round(player.getLocation().getYaw() / 90f) & 0x3;
		return cardinals.get(ndx);
	}

	public static BlockFace getNextCardinalBlockFace(BlockFace blockFace) {
		int ndx = cardinals.indexOf(blockFace);
		ndx = (ndx == (cardinals.size() - 1) ? 0 : ++ndx);
		return cardinals.get(ndx);
	}

	public static float getBlastResistance(Block block) {
		return block.getType().getBlastResistance();
	}

	public static float getBlockHardness(Block block) {
		CustomBlock customBlock = CustomBlock.from(block);
		if (customBlock != null)
			return (float) customBlock.get().getBlockHardness();

		return block.getType().getHardness();
	}

	public static boolean hasDrops(Player player, Block block, ItemStack tool) {
		CustomToolBlock changedBlock = CustomToolBlock.of(block);
		if (changedBlock != null) {
			return changedBlock.canHarvestWith(tool, player);
		}

		return !block.getDrops(tool, player).stream()
			.filter(Nullables::isNotNullOrAir)
			.toList().isEmpty();
	}

	public static boolean canHarvestWith(Block block, ItemStack tool, Player debugger) {
		// check custom blocks
		CustomBlock customBlock = CustomBlock.from(block);
		if (customBlock != null) {
			IHarvestable iHarvestable = customBlock.get();
			boolean result = iHarvestable.canHarvestWith(tool, debugger);
			Debug.log(debugger, DebugType.CUSTOM_BLOCK_DAMAGE, "custom block canHarvestWith: " + result);
			return result;
		}

		// check changed vanilla blocks
		CustomToolBlock changedBlock = CustomToolBlock.of(block);
		if (changedBlock != null) {
			boolean result = changedBlock.canHarvestWith(tool, debugger);
			Debug.log(debugger, DebugType.CUSTOM_BLOCK_DAMAGE, "changed vanilla block canHarvestWith: " + result);
			return changedBlock.canHarvestWith(tool, debugger);
		}

		boolean preferred = ItemUtils.isPreferredTool(tool, block, debugger);
		Debug.log(debugger, DebugType.CUSTOM_BLOCK_DAMAGE, "vanilla block canHarvestWith: " + preferred);
		return preferred;
	}

	public static int getBlockBreakTime(Player player, org.bukkit.inventory.ItemStack tool, org.bukkit.block.Block block) {
		return (int) Math.ceil(1 / getBlockDamage(player, tool, block));
	}

	public static float getBlockDamage(Player player, org.bukkit.inventory.ItemStack tool, org.bukkit.block.Block block) {
		float blockHardness = getBlockHardness(block);
		float speedMultiplier = NMSUtils.getDestroySpeed(block, tool);
		Debug.log(player, DebugType.CUSTOM_BLOCK_DAMAGE, "speedMultiplier: " + speedMultiplier);
		boolean canHarvest = canHarvestWith(block, tool, player);
		boolean hasDrops = hasDrops(player, block, tool);

		Debug.log(player, DebugType.CUSTOM_BLOCK_DAMAGE, "getBlockDamage for " + StringUtils.camelCase(block.getType()));
		return getBlockDamage(player, tool, blockHardness, speedMultiplier, canHarvest, hasDrops);
	}

	// https://minecraft.fandom.com/wiki/Breaking#Calculation
	@SuppressWarnings("deprecation")
	public static float getBlockDamage(Player player, org.bukkit.inventory.ItemStack tool, float blockHardness, float speedMultiplier, boolean isUsingCorrectTool, boolean hasDrops) {
		Debug.log(player, DebugType.CUSTOM_BLOCK_DAMAGE, "getBlockDamage: hardness=" + blockHardness + " | speed=" + speedMultiplier + " | isUsingCorrectTool=" + isUsingCorrectTool + " | hasDrops=" + hasDrops);

		if (blockHardness == -1) {
			Debug.log(player, DebugType.CUSTOM_BLOCK_DAMAGE, "cannot break, damage = " + -1);
			return -1;
		}

		if (isUsingCorrectTool) {
			if (!hasDrops) {
				speedMultiplier = 1;
				Debug.log(player, DebugType.CUSTOM_BLOCK_DAMAGE, "can't harvest, speed multiplier = 1");
			}

			if (!Nullables.isNullOrAir(tool)) {
				if (tool.getItemMeta().hasEnchants()) {
					Map<Enchantment, Integer> enchants = tool.getItemMeta().getEnchants();
					if (enchants.containsKey(Enchant.EFFICIENCY)) {
						speedMultiplier += (float) (Math.pow(enchants.get(Enchant.EFFICIENCY), 2) + 1);
						Debug.log(player, DebugType.CUSTOM_BLOCK_DAMAGE, "tool has efficiency, speed = " + speedMultiplier);
					}
				}
			}
		}

		if (!player.getActivePotionEffects().isEmpty()) {
			int hasteLevel = 0;
			int fatigueLevel = 0;
			for (PotionEffect potionEffect : player.getActivePotionEffects()) {
				int amplifier = potionEffect.getAmplifier();
				if (potionEffect.getType().equals(PotionEffectType.HASTE)) {
					if (amplifier > hasteLevel)
						hasteLevel = amplifier;
				} else if (potionEffect.getType().equals(PotionEffectType.MINING_FATIGUE)) {
					if (amplifier > fatigueLevel)
						fatigueLevel = amplifier;
				}
			}

			if (hasteLevel > 0) {
				speedMultiplier *= (float) ((0.2 * hasteLevel) + 1);
				Debug.log(player, DebugType.CUSTOM_BLOCK_DAMAGE, "player has haste, speed = " + speedMultiplier);
			}

			if (fatigueLevel > 0) {
				speedMultiplier *= (float) Math.pow(0.3, Math.min(fatigueLevel, 4));
				Debug.log(player, DebugType.CUSTOM_BLOCK_DAMAGE, "player has mining fatigue, speed = " + speedMultiplier);
			}
		}

		org.bukkit.inventory.ItemStack helmet = player.getInventory().getHelmet();
		if (!Nullables.isNullOrAir(helmet) && helmet.getItemMeta().hasEnchants()) {
			boolean hasAquaAffinity = false;

			@NotNull Map<Enchantment, Integer> enchants = helmet.getItemMeta().getEnchants();
			if (enchants.containsKey(Enchant.AQUA_AFFINITY))
				hasAquaAffinity = true;

			if (player.isInWater() && !hasAquaAffinity) {
				speedMultiplier /= 5;
				Debug.log(player, DebugType.CUSTOM_BLOCK_DAMAGE, "player is in water without aqua affinity, speed = " + speedMultiplier);
			}
		}

		if (!player.isOnGround()) {
			speedMultiplier /= 5;
			Debug.log(player, DebugType.CUSTOM_BLOCK_DAMAGE, "player is not on ground, speed = " + speedMultiplier);
		}

		float damage = speedMultiplier / blockHardness;

		if (isUsingCorrectTool) {
			damage /= 30;
			Debug.log(player, DebugType.CUSTOM_BLOCK_DAMAGE, "correct tool, damage = " + damage);
		} else {
			damage /= 100;
			Debug.log(player, DebugType.CUSTOM_BLOCK_DAMAGE, "wrong tool, damage = " + damage);
		}

		// Instant Breaking:
		if (damage > 1) {
			damage = 1;
			Debug.log(player, DebugType.CUSTOM_BLOCK_DAMAGE, "instant break, damage = " + damage);
		}

		Debug.log(player, DebugType.CUSTOM_BLOCK_DAMAGE, "getBlockDamage: hardness=" + blockHardness + " | speed=" + speedMultiplier + " | " + "Damage: " + damage);
		Debug.log(player, DebugType.CUSTOM_BLOCK_DAMAGE, "---");
		return damage;
	}

	public static List<Location> getBlocksInChunk(Chunk chunk, Material material) {
		return getBlocksInChunk(chunk, blockData -> blockData.getMaterial() == material);
	}

	public static List<Location> getBlocksInChunk(Chunk chunk, Material material, int max) {
		return getBlocksInChunk(chunk, blockData -> blockData.getMaterial() == material, max);
	}

	public static List<Location> getBlocksInChunk(Chunk chunk, Predicate<BlockData> predicate) {
		return getBlocksInChunk(chunk, predicate, Integer.MAX_VALUE);
	}

	public static List<Location> getBlocksInChunk(Chunk chunk, Predicate<BlockData> predicate, int max) {
		final World world = chunk.getWorld();
		final ChunkSnapshot snapshot = chunk.getChunkSnapshot();

		return new ArrayList<>() {{
			all:
			for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++)
				for (int x = 0; x < 16; x++)
					for (int z = 0; z < 16; z++) {
						final BlockData blockData = snapshot.getBlockData(x, y, z);
						if (Nullables.isNullOrAir(blockData.getMaterial()))
							continue;

						if (!predicate.test(blockData))
							continue;

						Location location = new Location(world, (snapshot.getX() << 4) + x, y, (snapshot.getZ() << 4) + z);
						if (!world.getWorldBorder().isInside(location))
							continue;

						add(location);

						if (size() >= max)
							break all;
					}
		}};
	}

	@Getter
	public static final List<BlockFace> cardinals = List.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

	public static BlockFace rotateClockwise(BlockFace face) {
		return rotate(face, cardinals, true);
	}

	public static BlockFace rotateCounterClockwise(BlockFace face) {
		return rotate(face, cardinals, false);
	}

	public static BlockFace rotate(BlockFace face, List<BlockFace> faces, boolean clockwise) {
		int size = faces.size() - 1;

		int index = (faces.indexOf(face) - 1);
		if (index < 0)
			index = size;

		if (clockwise) {
			index = (faces.indexOf(face) + 1);
			if (index > size)
				index = 0;
		}

		return faces.get(index);
	}
}
