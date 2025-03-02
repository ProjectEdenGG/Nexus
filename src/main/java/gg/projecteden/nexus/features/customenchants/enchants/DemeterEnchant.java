package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Hangable;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class DemeterEnchant extends CustomEnchant implements Listener {

	@Override
	public int getMaxLevel() {
		return 1;
	}

	static {
		Tasks.repeat(0, TickTime.SECOND.x(1), () -> {
			OnlinePlayers.getAll().forEach(player -> {
				if (!wearingBonemealBoots(player))
					return;

				Location playerLoc = player.getLocation();
				int radius = 5;
				List<Block> blocksNearby = BlockUtils.getBlocksInRadius(playerLoc, radius);
				for (Block block : blocksNearby) {
					// TODO Fuel
					if (RandomUtils.chanceOf(80))
						continue;

					Material blockType = block.getType();

					if (blockType.equals(Material.FARMLAND) || blockType.equals(Material.COCOA)) {
						Block crop = block.getRelative(0, 1, 0);
						if (blockType.equals(Material.COCOA))
							crop = block;

						if (growCrop(crop))
							showParticle(player, crop.getLocation());
					} else if (MaterialTag.ALL_SAPLINGS.isTagged(blockType)) {
						if (growTree(block))
							showParticle(player, block.getLocation());
					} else if (blockType.equals(Material.SUGAR_CANE) || blockType.equals(Material.CACTUS)) {
						if (growMulti(block))
							showParticle(player, block.getRelative(0, 1, 0).getLocation());
					}
				}
			});
		});
	}

	static boolean wearingBonemealBoots(Player player) {
		var boots = player.getInventory().getBoots();
		if (isNullOrAir(boots))
			return false;

		WorldGroup world = WorldGroup.of(player);
		if (!world.equals(WorldGroup.SURVIVAL))
			return false;

		return boots.containsEnchantment(Enchant.DEMETER);
	}

	static boolean growCrop(Block block) {
		BlockData blockData = block.getBlockData();
		if (!(blockData instanceof Ageable ageable)) return false;

		int maxAge = ageable.getMaximumAge();
		int age = ageable.getAge();
		if (age != maxAge) {
			++age;
			ageable.setAge(age);
			block.setBlockData(ageable);

			return true;
		}

		if (!block.getType().equals(Material.MELON_STEM) && !block.getType().equals(Material.PUMPKIN_STEM))
			return false;

		List<BlockFace> cardinals = Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

		Material placeType = Material.PUMPKIN;
		Material stemType = Material.ATTACHED_PUMPKIN_STEM;
		if (block.getType().equals(Material.MELON_STEM)) {
			placeType = Material.MELON;
			stemType = Material.ATTACHED_MELON_STEM;
		}

		for (BlockFace cardinal : cardinals) {
			Material cardinalType = block.getRelative(cardinal).getType();
			if (cardinalType.equals(placeType))
				return false;
		}

		List<BlockFace> possibleFaces = new ArrayList<>();
		for (BlockFace cardinal : cardinals) {
			Block cardinalBlock = block.getRelative(cardinal);
			Material below = cardinalBlock.getRelative(0, -1, 0).getType();
			MaterialTag growBlocks = new MaterialTag(MaterialTag.ALL_DIRT).exclude(Material.DIRT_PATH);
			if (growBlocks.isTagged(below) && cardinalBlock.getType().equals(Material.AIR))
				possibleFaces.add(cardinal);
		}

		if (possibleFaces.isEmpty())
			return false;

		BlockFace randomFace = RandomUtils.randomElement(possibleFaces);
		block.getRelative(randomFace).setType(placeType);
		block.setType(stemType);
		blockData = block.getBlockData();
		Directional directional = (Directional) blockData;
		directional.setFacing(randomFace);
		block.setBlockData(directional);

		return true;
	}

	private static final List<TreeType> MEGA_REQUIRED = List.of(TreeType.DARK_OAK, TreeType.PALE_OAK, TreeType.PALE_OAK_CREAKING);

	static boolean growTree(Block block) {
		var treeType = getTreeType(block);
		if (treeType == null)
			return false;

		var treeLocation = List.of(block.getLocation());
		var megaLocation = getMegaTree(block);
		var megaVariant = getMegaVariant(treeType);

		if (megaVariant != null && megaLocation != null && megaLocation.size() == 4) {
			treeType = megaVariant;
			treeLocation = megaLocation;
		} else {
			if (MEGA_REQUIRED.contains(treeType))
				return false;

			if (RandomUtils.chanceOf(20))
				treeType = getNormalVariant(treeType);
		}

		var blockData = new ArrayList<BlockData>();
		treeLocation.forEach(location -> {
			blockData.add(location.getBlock().getBlockData());
			location.getBlock().setType(Material.AIR);
		});

		if (!block.getWorld().generateTree(treeLocation.getFirst(), treeType)) {
			var iterator = blockData.iterator();
			treeLocation.forEach(location -> location.getBlock().setBlockData(iterator.next()));
			return false;
		}
		return true;
	}

	static boolean growMulti(Block block) {
		Material blockType = block.getType();

		Block ground = block.getRelative(0, -1, 0);
		if (ground.getType().equals(blockType)) {
			ground = block.getRelative(0, -2, 0);
			if (ground.getType().equals(blockType))
				ground = block.getRelative(0, -3, 0);
		}

		if (ground.getType().equals(blockType))
			return false;

		Location groundLoc = ground.getLocation();
		Block above = block.getRelative(0, 1, 0);
		if (Distance.distance(groundLoc, above).gt(3))
			return false;

		if (above.getType().equals(Material.AIR) || above.getType().equals(blockType)) {
			if (!above.getType().equals(Material.AIR)) {
				above = above.getRelative(0, 1, 0);
				if (Distance.distance(groundLoc, above).gt(3))
					return false;
			}

			if (above.getType().equals(Material.AIR)) {
				above.setType(blockType);
			} else {
				return false;
			}
		}
		return true;
	}

	static void showParticle(Player player, Location location) {
		if (RandomUtils.chanceOf(50))
			player.spawnParticle(Particle.HAPPY_VILLAGER, location, 5, 0.5, 0.5, 0.5, 0.01);
	}

	static List<Location> getMegaTree(Block block) {
		Location start = block.getLocation().toBlockLocation();

		if (start.getX() < 0) start.add(.5, 0, 0);
		if (start.getZ() < 0) start.add(0, 0, .5);

		TreeType treeType = getTreeType(block);
		if (treeType == null)
			return null;

		List<BlockFace> ordinals = Arrays.asList(BlockFace.NORTH_WEST, BlockFace.SOUTH_WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST);

		List<Location> locations = new ArrayList<>();
		Location location;
		for (BlockFace blockFace : ordinals) {
			Location corner = start.clone().getBlock().getRelative(blockFace).getLocation();

			if (treeType != getTreeType(corner.getBlock()))
				continue;

			Location x = start.clone().add(blockFace.getModX(), 0, 0);
			Location z = start.clone().add(0, 0, blockFace.getModZ());

			if (treeType == getTreeType(x.getBlock()) && treeType == getTreeType(z.getBlock())) {
				// TODO Cleanup
				int minX = (int) Math.min(start.getX(), x.getX());
				int minZ = (int) Math.min(start.getZ(), z.getZ());

				location = new Location(start.getWorld(), minX, start.getY(), minZ);
				location = LocationUtils.getBlockCenter(location);
				locations.add(location);

				minX = (int) Math.max(start.getX(), x.getX());
				minZ = (int) Math.min(start.getZ(), z.getZ());

				location = new Location(start.getWorld(), minX, start.getY(), minZ);
				location = LocationUtils.getBlockCenter(location);
				locations.add(location);

				minX = (int) Math.min(start.getX(), x.getX());
				minZ = (int) Math.max(start.getZ(), z.getZ());

				location = new Location(start.getWorld(), minX, start.getY(), minZ);
				location = LocationUtils.getBlockCenter(location);
				locations.add(location);

				minX = (int) Math.max(start.getX(), x.getX());
				minZ = (int) Math.max(start.getZ(), z.getZ());

				location = new Location(start.getWorld(), minX, start.getY(), minZ);
				location = LocationUtils.getBlockCenter(location);
				locations.add(location);
				break;
			}
		}
		return locations;
	}

	public static TreeType getTreeType(Block block) {
		Material material = block.getType();
		if (material == Material.OAK_SAPLING)
			return TreeType.TREE;
		if (material == Material.SPRUCE_SAPLING)
			return TreeType.REDWOOD;
		if (material == Material.BIRCH_SAPLING)
			return TreeType.BIRCH;
		if (material == Material.JUNGLE_SAPLING)
			return TreeType.SMALL_JUNGLE;
		if (material == Material.RED_MUSHROOM)
			return TreeType.RED_MUSHROOM;
		if (material == Material.BROWN_MUSHROOM)
			return TreeType.BROWN_MUSHROOM;
		if (material == Material.ACACIA_SAPLING)
			return TreeType.ACACIA;
		if (material == Material.DARK_OAK_SAPLING)
			return TreeType.DARK_OAK;
		if (material == Material.CRIMSON_FUNGUS)
			return TreeType.CRIMSON_FUNGUS;
		if (material == Material.WARPED_FUNGUS)
			return TreeType.WARPED_FUNGUS;
		if (material == Material.AZALEA || material == Material.FLOWERING_AZALEA)
			return TreeType.AZALEA;
		if (material == Material.MANGROVE_PROPAGULE && block.getBlockData() instanceof Hangable hangable && !hangable.isHanging())
			return TreeType.TALL_MANGROVE;
		if (material == Material.CHERRY_SAPLING)
			return TreeType.CHERRY;
		if (material == Material.PALE_OAK_SAPLING)
			return TreeType.PALE_OAK;

		return null;
	}

	public static TreeType getNormalVariant(TreeType treeType) {
		return switch (treeType) {
			case TREE -> TreeType.BIG_TREE;
			case BIRCH -> TreeType.TALL_BIRCH;
			case REDWOOD -> TreeType.TALL_REDWOOD;
			case TALL_MANGROVE -> TreeType.MANGROVE;
			case SMALL_JUNGLE -> TreeType.COCOA_TREE;
			default -> treeType;
		};
	}

	public static TreeType getMegaVariant(TreeType treeType) {
		return switch (treeType) {
			case REDWOOD -> TreeType.MEGA_REDWOOD;
			case SMALL_JUNGLE -> TreeType.JUNGLE;
			case DARK_OAK -> TreeType.DARK_OAK;
			case PALE_OAK -> TreeType.PALE_OAK;
			default -> null;
		};
	}

}
