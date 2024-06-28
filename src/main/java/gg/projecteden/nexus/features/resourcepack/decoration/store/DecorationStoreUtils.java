package gg.projecteden.nexus.features.resourcepack.decoration.store;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.store.DecorationStoreManager.StoreType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition.BedInteractionData;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationInteractData.MAX_RADIUS;
import static gg.projecteden.nexus.features.resourcepack.decoration.store.BuyableData.isBuyable;
import static gg.projecteden.nexus.utils.Nullables.isNotNullOrAir;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class DecorationStoreUtils {
	public static final int REACH_DISTANCE = 6;
	public static final List<EntityType> glowTypes = List.of(EntityType.ITEM_FRAME);

	public static boolean isApplicableBlock(Player player, Block targetBlock, ItemStack targetBlockItem, StoreType storeType) {
		DecorationStoreManager.debug(player, "checking if applicable block");
		if (isNullOrAir(targetBlock)) {
			DecorationStoreManager.debug(player, "  target block == null");
			return false;
		}

		if (!MaterialTag.PLAYER_SKULLS.isTagged(targetBlock)) {
			DecorationStoreManager.debug(player, "  not a skull 1");
			return false;
		}

		if (isNullOrAir(targetBlockItem)) {
			DecorationStoreManager.debug(player, "  item is null");
			return false;
		}

		if (!isBuyable(player, targetBlockItem, storeType.currency)) {
			DecorationStoreManager.debug(player, "  not buyable");
			return false;
		}

		if (!(targetBlock.getState() instanceof Skull skull)) {
			DecorationStoreManager.debug(player, "  not a skull 2");
			return false;
		}

		if (skull.getPlayerProfile() == null) {
			DecorationStoreManager.debug(player, "  skull does not have a skin");
			return false;
		}

		if (!new WorldGuardUtils(targetBlock).isInRegion(targetBlock.getLocation(), storeType.glowRegionId)) {
			DecorationStoreManager.debug(player, "  not in region");
			return false;
		}

		DecorationStoreManager.debug(player, "  is applicable block");
		return true;
	}

	public static boolean isApplicableEntity(Player player, Entity targetEntity, ItemStack targetEntityItem, StoreType storeType) {
		DecorationStoreManager.debug(player, "checking if applicable entity");
		if (targetEntity == null) {
			DecorationStoreManager.debug(player, "  target entity == null");
			return false;
		}

		if (!glowTypes.contains(targetEntity.getType())) {
			DecorationStoreManager.debug(player, "  not a glow type");
			return false;
		}

		if (isNullOrAir(targetEntityItem)) {
			DecorationStoreManager.debug(player, "  item is null");
			return false;
		}

		if (!isBuyable(player, targetEntityItem, storeType.currency)) {
			DecorationStoreManager.debug(player, "  not buyable");
			return false;
		}

		if (!new WorldGuardUtils(targetEntity).isInRegion(targetEntity.getLocation(), storeType.glowRegionId)) {
			DecorationStoreManager.debug(player, "  not in region");
			return false;
		}

		DecorationStoreManager.debug(player, "  is applicable entity");
		return true;
	}

	public static ItemStack getTargetEntityItem(Entity entity) {
		if (entity == null)
			return null;

		ItemStack itemStack = null;
		if (entity instanceof ItemFrame itemFrame) {
			itemStack = itemFrame.getItem();
		}

		return itemStack;
	}

	public static Block getTargetBlock(Player player) {
		return player.getTargetBlockExact(REACH_DISTANCE);
	}

	public static ItemStack getTargetBlockItem(Block block) {
		return ItemUtils.getItem(block);
	}

	public static Entity getTargetEntity(Player player) {
		DecorationStoreManager.debug(player, "getTargetEntity:");
		Entity targetEntity = player.getTargetEntity(REACH_DISTANCE, false);
		if (targetEntity != null) {
			DecorationStoreManager.debug(player, "found entity 1");
			return targetEntity;
		}

		targetEntity = PlayerUtils.getTargetItemFrame(player, 10, Map.of(BlockFace.DOWN, 1));
		if (targetEntity != null) {
			DecorationStoreManager.debug(player, "found entity 2");
			return targetEntity;
		}

		// Target Decoration
		Block targetBlock = player.getTargetBlockExact(REACH_DISTANCE);
		if (isNotNullOrAir(targetBlock)) {

			// Exact
			ItemFrame itemFrame = checkForDecoration(player, targetBlock);
			if (itemFrame == null) {
				Block inFront = targetBlock.getRelative(player.getFacing().getOppositeFace());
				if (inFront.getType().equals(Material.LIGHT)) {
					// In Front
					itemFrame = checkForDecoration(player, inFront);
				}
			}

			// Additions
			if (itemFrame == null) {
				// BedAdditions
				if (MaterialTag.BEDS.isTagged(targetBlock)) {
					BedInteractionData bedData = new BedInteractionData(player, targetBlock, null, true);
					if (!bedData.getAdditionsLeft().isEmpty()) {
						itemFrame = bedData.getAdditionsLeft().keySet().stream().toList().get(0);
					}
				}
			}

			if (itemFrame != null) {
				DecorationStoreManager.debug(player, "is decoration");
				return itemFrame;
			}
		}

		DecorationStoreManager.debug(player, "No entities found");
		return null;
	}

	private static ItemFrame checkForDecoration(Player player, Block block) {
		if (Nullables.isNullOrAir(block))
			return null;

		DecorationStoreManager.debug(player, "Target Block: " + block.getType());

		BlockFace facing = BlockFace.UP;
		if (block.getType().equals(Material.LIGHT))
			facing = player.getFacing();

		ItemFrame itemFrame = (ItemFrame) DecorationUtils.getItemFrame(block, MAX_RADIUS, facing, player, false);
		if (itemFrame == null)
			return null;

		DecorationStoreManager.debug(player, "found an item frame");
		DecorationConfig config = DecorationConfig.of(itemFrame);
		if (config == null)
			return null;

		return itemFrame;
	}
}
