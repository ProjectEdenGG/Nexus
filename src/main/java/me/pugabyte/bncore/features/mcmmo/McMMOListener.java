package me.pugabyte.bncore.features.mcmmo;

import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.Koda;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.utils.CitizensUtils;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.TreeSpecies;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.Crops;
import org.bukkit.material.Sapling;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;

// TODO: Fix beetroot, melons stems, melons, pumpkin stems and pumpkins

public class McMMOListener implements Listener {

	public McMMOListener() {
		BNCore.registerListener(this);
		scheduler();
	}

	@EventHandler
	public void onMcMMOLevelUp(McMMOPlayerLevelUpEvent event) {
		if (event.getSkillLevel() == 100)
			Koda.say(event.getPlayer().getName() + " reached level 100 in " + camelCase(event.getSkill().name()) + "! Congratulations!");
		if (UserManager.getOfflinePlayer(event.getPlayer()).getPowerLevel() == 1300)
			Koda.say(event.getPlayer().getName() + " has mastered all their skills! Congratulations!");

		Tasks.async(() -> {
			List<PlayerStat> topThree = mcMMO.getDatabaseManager().readLeaderboard(null, 1, 3);
			if (topThree.size() != 3)
				BNCore.warn("McMMO leaderboard query did not return 3 results");
			else
				Tasks.sync(() -> {
					OfflinePlayer first = Utils.getPlayer(topThree.get(0).name);
					OfflinePlayer second = Utils.getPlayer(topThree.get(1).name);
					OfflinePlayer third = Utils.getPlayer(topThree.get(2).name);
					CitizensUtils.updateNameAndSkin(2706, new Nerd(first).getRank().getChatColor() + first.getName());
					CitizensUtils.updateNameAndSkin(2705, new Nerd(second).getRank().getChatColor() + second.getName());
					CitizensUtils.updateNameAndSkin(2704, new Nerd(third).getRank().getChatColor() + third.getName());
				});
		});
	}

	void scheduler() {
		Tasks.repeat(0, 10, () -> {
			Collection<? extends Player> players = Bukkit.getOnlinePlayers();
			players.forEach(player -> {
				// If player is wearing boots
				if (Utils.isNullOrAir(player.getInventory().getBoots()))
					return;

				// If player is wearing gold boots
				if (!player.getInventory().getBoots().getType().equals(Material.GOLDEN_BOOTS))
					return;

				// if player is in survival
				WorldGroup world = WorldGroup.get(player);
				if (!world.equals(WorldGroup.SURVIVAL))
					return;

				// if boots has lore
				ItemStack boots = player.getInventory().getBoots();
				ItemMeta meta = boots.getItemMeta();
				List<String> lore = meta.getLore();
				if (lore == null)
					return;

				// if lore on boots contains "bonemeal boots"
				if (!(String.join(",", lore).contains("Bonemeal Boots")))
					return;

				// Loop all blocks in radius x of player
				Location playerLoc = player.getLocation();
				int radius = 5;
				for (int x = -radius; x <= radius; x++) {
					for (int z = -radius; z <= radius; z++) {
						for (int y = -radius; y <= radius; y++) {
							// 33% chance
							if (Utils.randomInt(1, 3) >= 2)
								continue;

							Block block = playerLoc.getBlock().getRelative(x, y, z);
							Material blockType = block.getType();

							// If loop block is farmland
							if (blockType.equals(Material.FARMLAND)) {

								// if block above dirt, is crops
								Block blockAbove = block.getRelative(0, 1, 0);
								if (blockAbove.getType().toString().toLowerCase().contains("beetroot"))
									continue;

								if (!isCrop(blockAbove.getType()))
									continue;

								Crops crop = (Crops) blockAbove.getState().getData();
								if (crop.getState() == CropState.RIPE)
									continue;

								int i = Arrays.asList(CropState.values()).indexOf(crop.getState());
								CropState newState = Arrays.asList(CropState.values()).get(i + 1);
								crop.setState(newState);

								showParticle(player, blockAbove.getLocation());

								// if loop block is sapling
							} else if (MaterialTag.SAPLINGS.isTagged(blockType) || Arrays.asList(Material.BROWN_MUSHROOM, Material.RED_MUSHROOM).contains(blockType)) {
								TreeType treeType = getTreeType(block.getLocation());
								if (treeType == null)
									continue;

								Location treeLoc = block.getLocation();
								Location megaLoc = getMegaTree(block);

								if (megaLoc != null) {
									treeType = getMegaVariant(treeType);
									treeLoc = megaLoc;
								} else {
									if (Utils.randomInt(1, 5) == 1)
										treeType = getVariant(treeType);
								}

								boolean isSapling = Tag.SAPLINGS.isTagged(blockType);
								block.setType(Material.AIR);
								if (block.getWorld().generateTree(treeLoc, treeType))
									showParticle(player, block.getLocation());
								else {
									block.setType(blockType);
									if (isSapling) {
										Sapling sapling = (Sapling) block.getState().getData();
										block.getState().getData().setData(sapling.getSpecies().getData());
									}
								}

							} else if (blockType.equals(Material.SUGAR_CANE)) {
								Block ground = block.getRelative(0, -1, 0);
								if (ground.getType().equals(Material.SUGAR_CANE)) {
									ground = block.getRelative(0, -2, 0);
									if (ground.getType().equals(Material.SUGAR_CANE))
										ground = block.getRelative(0, -3, 0);
								}
								if (ground.getType().equals(Material.SUGAR_CANE))
									continue;

								Location groundLoc = ground.getLocation();


								Block above = block.getRelative(0, 1, 0);
								if (groundLoc.distance(above.getLocation()) > 3)
									continue;

								// If the block above the sugarcane is air or sugarcane
								if ((above.getType().equals(Material.AIR) || above.getType().equals(Material.SUGAR_CANE))) {
									if (!above.getType().equals(Material.AIR)) {
										above = above.getRelative(0, 1, 0);
										if (groundLoc.distance(above.getLocation()) > 3)
											continue;
									}

									if (above.getType().equals(Material.AIR)) {
										above.setType(Material.SUGAR_CANE);
										showParticle(player, above.getLocation());
									}
								}
							} else if (blockType.equals(Material.CACTUS)) {
								Block ground = block.getRelative(0, -1, 0);
								if (ground.getType().equals(Material.CACTUS)) {
									ground = block.getRelative(0, -2, 0);
									if (ground.getType().equals(Material.CACTUS))
										ground = block.getRelative(0, -3, 0);
								}
								if (ground.getType().equals(Material.CACTUS))
									continue;

								Location groundLoc = ground.getLocation();


								Block above = block.getRelative(0, 1, 0);
								if (groundLoc.distance(above.getLocation()) > 3)
									continue;

								// If the block above the sugarcane is air or sugarcane
								if ((above.getType().equals(Material.AIR) || above.getType().equals(Material.CACTUS))) {
									if (!above.getType().equals(Material.AIR)) {
										above = above.getRelative(0, 1, 0);
										if (groundLoc.distance(above.getLocation()) > 3)
											continue;
									}

									if (above.getType().equals(Material.AIR)) {
										above.setType(Material.CACTUS);
										showParticle(player, above.getLocation());
									}
								}
							}
							else if (blockType.equals(Material.COCOA)) {
								CocoaPlant cocoaPlant = (CocoaPlant) block.getState().getData();
								cocoaPlant.setSize(nextCocoaSize(cocoaPlant.getSize()));
								//  block.setData(cocoaPlant.getData()); TODO: Don't need this on 1.13?
							}
						}
					}
				}

			});
		});
	}

	void showParticle(Player player, Location location) {
		if (Utils.randomInt(1, 4) <= 2)
			player.spawnParticle(Particle.VILLAGER_HAPPY, location, 5, 0.5, 0.5, 0.5, 0.01);
	}

	CocoaPlant.CocoaPlantSize nextCocoaSize(CocoaPlant.CocoaPlantSize size) {
		switch (size) {
			case SMALL:
				return CocoaPlant.CocoaPlantSize.MEDIUM;
			case MEDIUM:
				return CocoaPlant.CocoaPlantSize.LARGE;
		}
		return size;
	}

	boolean isCrop(Material material) {
		switch (material) {
			case POTATOES:
			case CARROTS:
			case BEETROOTS:
			case WHEAT:
			case PUMPKIN_STEM:
			case MELON_STEM:
				return true;
		}
		return false;
	}

	private TreeType getTreeType(TreeSpecies treeSpecies) {
		switch (treeSpecies.getData()) {
			case 0:
				return TreeType.TREE;
			case 1:
				return TreeType.REDWOOD;
			case 2:
				return TreeType.BIRCH;
			case 3:
				return TreeType.SMALL_JUNGLE;
			case 4:
				return TreeType.ACACIA;
			case 5:
				return TreeType.DARK_OAK;
		}
		return null;
	}

	private TreeType getVariant(TreeType treeType) {
		switch (treeType) {
			case TREE:
				return TreeType.BIG_TREE;
			case BIRCH:
				return TreeType.TALL_BIRCH;
			case REDWOOD:
				return TreeType.TALL_REDWOOD;
			case SMALL_JUNGLE:
				return TreeType.COCOA_TREE;
		}
		return treeType;
	}

	private TreeType getMegaVariant(TreeType treeType) {
		switch (treeType) {
			case REDWOOD:
				return TreeType.MEGA_REDWOOD;
			case SMALL_JUNGLE:
				return TreeType.JUNGLE;
		}
		return treeType;
	}

	private Location getMegaTree(Block block) {
		if (!Tag.SAPLINGS.isTagged(block.getType()))
			return null;

		Location start = block.getLocation();

		if (start.getX() < 0) start.add(.5, 0, 0);
		if (start.getZ() < 0) start.add(0, 0, .5);

		TreeType treeType = getTreeType(start);
		if (treeType == null)
			return null;

		List<BlockFace> ordinals = Arrays.asList(BlockFace.NORTH_WEST, BlockFace.SOUTH_WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST);

		Location northwest = null;
		for (BlockFace blockFace : ordinals) {
			Location corner = start.clone().getBlock().getRelative(blockFace).getLocation();

			if (treeType != getTreeType(corner))
				continue;

			Location x = start.clone().add(blockFace.getModX(), 0, 0);
			Location z = start.clone().add(0, 0, blockFace.getModZ());

			if (getTreeType(x) == treeType && getTreeType(z) == treeType) {
				int minX = (int) Math.min(start.getX(), x.getX());
				int minZ = (int) Math.min(start.getZ(), z.getZ());

				northwest = new Location(start.getWorld(), minX, start.getY(), minZ);
				northwest = Utils.getBlockCenter(northwest);
				break;
			}
		}
		return northwest;
	}

	private TreeType getTreeType(Location location) {
		if ((location.getBlock().getState().getData() instanceof Sapling)) {
			Sapling sapling = (Sapling) location.getBlock().getState().getData();
			return getTreeType(sapling.getSpecies());
		} else if (location.getBlock().getType().equals(Material.BROWN_MUSHROOM)) {
			return TreeType.BROWN_MUSHROOM;
		} else if (location.getBlock().getType().equals(Material.RED_MUSHROOM)) {
			return TreeType.RED_MUSHROOM;
		}
		return null;
	}
}