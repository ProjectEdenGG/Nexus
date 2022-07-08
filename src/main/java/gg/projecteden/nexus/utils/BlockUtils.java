package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.features.customblocks.CustomBlocks.SoundAction;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.CustomToolBlock;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.LocationUtils.Axis;
import gg.projecteden.parchment.HasPlayer;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

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
		Block north = block.getRelative(BlockFace.NORTH);
		Block east = block.getRelative(BlockFace.EAST);
		Block south = block.getRelative(BlockFace.SOUTH);
		Block west = block.getRelative(BlockFace.WEST);
		Block up = block.getRelative(BlockFace.UP);
		Block down = block.getRelative(BlockFace.DOWN);
		List<Block> relatives = Arrays.asList(north, east, south, west, up, down);
		List<Block> adjacent = new ArrayList<>();
		for (Block relative : relatives) {
			if (!isNullOrAir(relative))
				adjacent.add(relative);
		}
		return adjacent;
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

	public static boolean tryBreakEvent(@NotNull Player player, @NotNull Block block, boolean dropItems) {
		BlockBreakEvent event = new BlockBreakEvent(block, player);
		event.setDropItems(dropItems);
		if (!event.callEvent() || event.isCancelled())
			return false;

		return true;
	}

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
		Sound sound = NMSUtils.getSound(soundAction, block);
		if (sound == null)
			return;

		Location location = block.getLocation().toCenterLocation();

		playSound(new SoundBuilder(sound).location(location).volume(soundAction.getVolume()));
	}

	public static void playSound(SoundBuilder soundBuilder) {
		soundBuilder.category(SoundCategory.BLOCKS).play();
	}

	public static final List<BlockFace> cardinalFaces = List.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

	public static BlockFace getCardinalBlockFace(Player player) {
		BlockFace facing = player.getFacing();
		if (cardinalFaces.contains(facing))
			return facing;

		int ndx = Math.round(player.getLocation().getYaw() / 90f) & 0x3;
		return cardinalFaces.get(ndx);
	}

	public static BlockFace getNextCardinalBlockFace(BlockFace blockFace) {
		int ndx = cardinalFaces.indexOf(blockFace);
		ndx = (ndx == (cardinalFaces.size() - 1) ? 0 : ++ndx);
		return cardinalFaces.get(ndx);
	}

	public static float getBlastResistance(Block block) {
		return block.getType().getBlastResistance();
	}

	public static float getBlockHardness(Block block) {
		CustomBlock customBlock = CustomBlock.fromBlock(block);
		if (customBlock != null)
			return (float) customBlock.get().getBlockHardness();

		return block.getType().getHardness();
	}

	public static boolean hasDrops(Player player, Block block, ItemStack tool) {
		CustomToolBlock changedBlock = CustomToolBlock.of(block);
		if (changedBlock != null) {
			return changedBlock.canHarvestWith(tool);
		}

		return block.getDrops(tool, player).stream()
			.filter(Nullables::isNotNullOrAir)
			.toList()
			.size() > 0;
	}

	public static boolean canHarvest(Block block, ItemStack tool) {
		CustomToolBlock changedBlock = CustomToolBlock.of(block);
		if (changedBlock != null) {
			return changedBlock.canHarvestWith(tool);
		}

		return block.isPreferredTool(tool);
	}

	public static int getBlockBreakTime(Player player, org.bukkit.inventory.ItemStack tool, org.bukkit.block.Block block) {
		return (int) Math.ceil(1 / getBlockDamage(player, tool, block));
	}

	public static float getBlockDamage(Player player, org.bukkit.inventory.ItemStack tool, org.bukkit.block.Block block) {
		float blockHardness = getBlockHardness(block);
		float speedMultiplier = NMSUtils.getDestroySpeed(block, tool);
		boolean canHarvest = canHarvest(block, tool);
		boolean hasDrops = hasDrops(player, block, tool);

		return getBlockDamage(player, tool, blockHardness, speedMultiplier, canHarvest, hasDrops);
	}

	public static float getBlockDamage(Player player, org.bukkit.inventory.ItemStack tool, float blockHardness, float speedMultiplier, boolean canHarvest, boolean hasDrops) {
		if (blockHardness == -1) {
			return -1;
		}

		if (canHarvest) {
			if (!hasDrops) {
				speedMultiplier = 1;
			}
		}

		// if (toolEfficiency): speedMultiplier += efficiencyLevel ^ 2 + 1
		if (!Nullables.isNullOrAir(tool)) {
			if (tool.getItemMeta().hasEnchants()) {
				Map<Enchantment, Integer> enchants = tool.getItemMeta().getEnchants();
				if (enchants.containsKey(Enchant.EFFICIENCY)) {
					speedMultiplier += Math.pow(enchants.get(Enchant.EFFICIENCY), 2) + 1;
				}
			}
		}

		if (!player.getActivePotionEffects().isEmpty()) {
			int hasteLevel = 0;
			int fatigueLevel = 0;
			for (PotionEffect potionEffect : player.getActivePotionEffects()) {
				int amplifier = potionEffect.getAmplifier();
				if (potionEffect.getType().equals(PotionEffectType.FAST_DIGGING)) {
					if (amplifier > hasteLevel)
						hasteLevel = amplifier;
				} else if (potionEffect.getType().equals(PotionEffectType.SLOW_DIGGING)) {
					if (amplifier > fatigueLevel)
						fatigueLevel = amplifier;
				}
			}

			if (hasteLevel > 0) {
				speedMultiplier *= (0.2 * hasteLevel) + 1;
			}

			if (fatigueLevel > 0) {
				speedMultiplier *= Math.pow(0.3, Math.min(fatigueLevel, 4));
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
			}
		}

		if (!player.isOnGround()) {
			speedMultiplier /= 5;
		}

		float damage = speedMultiplier / blockHardness;

		if (canHarvest) {
			damage /= 30;
		} else {
			damage /= 100;
		}

		// Instant Breaking:
		if (damage > 1) {
			return 0;
		}

		return damage;
	}
}
