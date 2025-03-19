package gg.projecteden.nexus.features.resourcepack.customblocks.listeners;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockNMSUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocks.BlockAction;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocks.SoundAction;
import gg.projecteden.nexus.features.resourcepack.customblocks.NoteBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock.CustomBlockType;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.ICustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.IPistonActions.PistonAction;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.ICompostable;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.misc.FloweringMossBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IWaterLogged;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.nexus.utils.protection.ProtectionUtils;
import gg.projecteden.parchment.event.block.CustomBlockUpdateEvent;
import gg.projecteden.parchment.event.block.CustomBlockUpdateEvent.UpdateType;
import io.papermc.paper.event.player.PlayerPickItemEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import org.bukkit.ExplosionResult;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class CustomBlockListener implements Listener {

	public CustomBlockListener() {
		Nexus.registerListener(this);

		new CustomBlockSounds();
		new ConversionListener();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(PlayerInteractEvent event) {
		if (event.isCancelled()) return;
		if (event.useInteractedBlock() == Result.DENY || event.useItemInHand() == Result.DENY) return;
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Block clickedBlock = event.getClickedBlock();
		if (Nullables.isNullOrAir(clickedBlock)) return;

		// TODO: Disable tripwire customblocks
		if (ICustomTripwire.isNotEnabled() && clickedBlock.getType() == Material.TRIPWIRE)
			return;
		//

		Player player = event.getPlayer();
		CustomBlockUtils.debug(player, "&d&lPlayerInteractEvent:", true);

		CustomBlock clickedCustomBlock = CustomBlock.from(clickedBlock);

		if (isPlacingBlock(event, clickedBlock, clickedCustomBlock)) {
			CustomBlockSounds.updateAction(player, BlockAction.PLACE);
			CustomBlockUtils.debug(player, "&d<- done, placed block");
			return;
		}

		ItemStack itemInHand = event.getItem();
		Action action = event.getAction();

		// Item In Hand
		if (Nullables.isNotNullOrAir(itemInHand)) {
			CustomBlock itemCustomBlock = CustomBlock.from(itemInHand);
			if (itemCustomBlock != null) {
				CustomBlockUtils.debug(player, "&e- On Use While Holding");
				if (itemCustomBlock.get().onUseWhileHolding(event, player, action, clickedBlock, itemInHand)) {
					CustomBlockUtils.debug(player, "&d<- cancelled = " + event.isCancelled() + " | done, end");
					return;
				} else {
					CustomBlockUtils.debug(player, "&c<- no changes");
				}
			}
		}

		CustomBlockUtils.debug(player, "&e- interacted with block");
		CustomBlockSounds.updateAction(player, BlockAction.INTERACT);

		if (clickedCustomBlock != null) {
			boolean isItemNull = Nullables.isNullOrAir(itemInHand);
			ICustomBlock iCustomBlock = clickedCustomBlock.get();

			if (action == Action.RIGHT_CLICK_BLOCK) {
				if (isItemNull) {
					CustomBlockUtils.debug(player, "&b- right click without item");
					if (iCustomBlock.onRightClickedWithoutItem(player, clickedCustomBlock, clickedBlock)) {
						event.setCancelled(true);
						CustomBlockUtils.debug(player, "&d- cancelling event");
					} else {
						CustomBlockUtils.debug(player, "&c<- no changes");
					}
				} else {
					CustomBlockUtils.debug(player, "&b- right click with item");
					if (iCustomBlock.onRightClickedWithItem(player, clickedCustomBlock, clickedBlock, itemInHand)) {
						event.setCancelled(true);
						CustomBlockUtils.debug(player, "&d- cancelling event");
					} else {
						CustomBlockUtils.debug(player, "&c<- no changes");
					}
				}
			} else if (action == Action.LEFT_CLICK_BLOCK) {
				if (isItemNull) {
					CustomBlockUtils.debug(player, "&b- left click without item");
					if (iCustomBlock.onLeftClickedWithoutItem(player, clickedCustomBlock, clickedBlock)) {
						CustomBlockUtils.debug(player, "&d- don't cancel event");
					} else {
						CustomBlockUtils.debug(player, "&c<- no changes");
					}
				} else {
					CustomBlockUtils.debug(player, "&b- left click with item");
					if (!iCustomBlock.onLeftClickedWithItem(player, clickedCustomBlock, clickedBlock, itemInHand)) {
						CustomBlockUtils.debug(player, "&d- don't cancel event");
					} else {
						CustomBlockUtils.debug(player, "&c<- no changes");
					}
				}
			}
		}

		CustomBlockUtils.debug(player, "&d<- done, end");
	}

	@EventHandler
	public void on(PlayerPickItemEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
			return;

		Location location = event.getLocation();
		if (location == null)
			return;

		CustomBlock customBlock = CustomBlock.from(location.getBlock());
		if (customBlock == null)
			return;

		event.setCancelled(true);

		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();
		int targetSlot = event.getTargetSlot();

		ItemStack customBlockItem = customBlock.get().getItemStack();

		// Check if picked block is in hotbar already
		int contentSlot = 0;
		ItemStack[] hotbarContents = PlayerUtils.getHotbarContents(player);
		for (ItemStack content : hotbarContents) {
			if (!Nullables.isNullOrAir(content) && ItemUtils.isModelMatch(content, customBlockItem)) {
				inventory.setHeldItemSlot(contentSlot);
				return;
			}
			contentSlot++;
		}

		inventory.setHeldItemSlot(targetSlot);
		inventory.setItem(targetSlot, customBlockItem);
	}

	@EventHandler
	public void on(CustomBlockUpdateEvent event) {
		// TODO: Disable tripwire customblocks
		if (ICustomTripwire.isNotEnabled() && event.getBlock() instanceof Tripwire)
			return;
		//

		if (event.getUpdateType() != UpdateType.POWERED && event.getUpdateType() != UpdateType.PITCH) {
			event.setCancelled(true);
			return;
		}

		CustomBlock customBlock = CustomBlock.from(event.getLocation().getBlock());
		if (customBlock != CustomBlock.NOTE_BLOCK) {
			event.setCancelled(true);
			return;
		}

		Location location = event.getLocation();
		if (location == null)
			return;

		if (!(event.getBlock() instanceof NoteBlock noteBlock))
			return;

		if (noteBlock.getInstrument() != Instrument.PIANO) {
			event.setCancelled(true);
			return;
		}

		CustomBlockUtils.broadcastDebug("CustomBlockUpdateEvent: Instrument=" + noteBlock.getInstrument() + ", Note=" + noteBlock.getNote().getId() + ", Powered=" + noteBlock.isPowered());

		boolean isPowered = noteBlock.isPowered();
		ServerLevel serverLevel = NMSUtils.toNMS(location.getWorld());
		BlockPos blockPos = NMSUtils.toNMS(location);
		boolean hasNeighborSignal = serverLevel.hasNeighborSignal(blockPos);
		if (!isPowered && hasNeighborSignal) {
			CustomBlockUtils.broadcastDebug("Playing NoteBlock: Instrument=" + noteBlock.getInstrument() + ", Note=" + noteBlock.getNote().getId() + ", Powered=" + noteBlock.isPowered());
			NoteBlockUtils.play(noteBlock, location, true, null);
		}
	}

	@EventHandler
	public void on(ResourcePackUpdateCompleteEvent ignored) {
		if (Nexus.getEnv() != Env.TEST)
			return;

		for (CustomBlock customBlock : CustomBlock.values())
			customBlock.registerRecipes();
	}

	@EventHandler
	public void on(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block placedBlock = event.getBlockPlaced();
		// TODO: Disable tripwire customblocks
		if (ICustomTripwire.isNotEnabled() && placedBlock.getType() == Material.TRIPWIRE)
			return;
		//

		// fix clientside tripwire changes
		CustomBlockUtils.fixTripwireNearby(player, placedBlock, new HashSet<>(List.of(placedBlock.getLocation())));
	}

	@EventHandler
	public void on(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Block brokenBlock = event.getBlock();
		// TODO: Disable tripwire customblocks
		if (ICustomTripwire.isNotEnabled() && brokenBlock.getType() == Material.TRIPWIRE)
			return;
		//

		if (Nullables.isNullOrAir(brokenBlock))
			return;

		Player player = event.getPlayer();
		ItemStack tool = player.getInventory().getItemInMainHand();

		CustomBlockUtils.debug(player, "&d&lBlockBreakEvent: BreakBlock", true);

		CustomBlock brokenCustomBlock = CustomBlock.from(brokenBlock);
		if (brokenCustomBlock != null) {
			event.setDropItems(false);
			CustomBlockUtils.debug(player, "&e- disabling drops");
		} else {
			CustomBlockUtils.fixLight(event, player, brokenBlock);
		}

		CustomBlockUtils.breakBlock(brokenBlock, brokenCustomBlock, player, tool, true);
		CustomBlockUtils.debug(player, "&d<- done, end", true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void on(BlockPhysicsEvent event) {
		Block block = event.getBlock();
		Material material = block.getType();
		// TODO: Disable tripwire customblocks
		if (ICustomTripwire.isNotEnabled() && material == Material.TRIPWIRE)
			return;
		//

		CustomBlock customBlock = CustomBlock.from(block);
		if (customBlock == null)
			return;

		if (CustomBlockType.getBlockMaterials().contains(material)) {
			sendBlockDataUpdate(block, customBlock);
		}

		sendBlockDataUpdates(block, BlockFace.UP);
		sendBlockDataUpdates(block, BlockFace.DOWN);
	}

	private void sendBlockDataUpdates(Block block, BlockFace face) {
		block = block.getRelative(face);
		while (CustomBlockType.getBlockMaterials().contains(block.getType())) {
			CustomBlock _customBlock = CustomBlock.from(block);
			if (_customBlock == null)
				return;

			sendBlockDataUpdate(block, _customBlock);
			block = block.getRelative(face);
		}
	}

	private void sendBlockDataUpdate(Block block, CustomBlock customBlock) {
		ICustomBlock iCustomBlock = customBlock.get();
		BlockFace facing = BlockFace.UP;
		if (iCustomBlock instanceof ICustomNoteBlock iCustomNoteBlock) {
			facing = iCustomNoteBlock.getFacing(block);
		}

		BlockData blockData = iCustomBlock.getBlockData(facing, block.getRelative(BlockFace.DOWN));
		if (iCustomBlock instanceof gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.misc.NoteBlock) {
			NoteBlock noteBlock = (NoteBlock) block.getBlockData();
			NoteBlock _noteBlock = (NoteBlock) blockData;
			_noteBlock.setNote(noteBlock.getNote());
			blockData = _noteBlock;
		}

		Location locationFinal = block.getLocation();
		BlockData blockDataFinal = blockData;
		Tasks.wait(1, () -> OnlinePlayers.where().world(locationFinal.getWorld()).forEach(player -> player.sendBlockChange(locationFinal, blockDataFinal)));
	}

	@EventHandler
	public void on(BlockFertilizeEvent event) {
		if (event.getBlock().getType() != Material.MOSS_BLOCK)
			return;

		Material material = CustomBlock.FLOWERING_MOSS_BLOCK.getType().getBlockMaterial();
		BlockData blockData = CustomBlock.FLOWERING_MOSS_BLOCK.get().getBlockData(BlockFace.UP, null);

		event.getBlocks().forEach(state -> {
			if (state.getType() != Material.MOSS_BLOCK)
				return;

			if (RandomUtils.chanceOf(FloweringMossBlock.FERTILIZE_CHANCE)) {
				state.setType(material);
				state.setBlockData(blockData);
			}
		});
	}

	@SuppressWarnings("UnstableApiUsage")
	@EventHandler
	public void on(EntityExplodeEvent event) {
		if (!List.of(ExplosionResult.DESTROY, ExplosionResult.DESTROY_WITH_DECAY).contains(event.getExplosionResult()))
			return;

		List<ExplodedCustomBlock> explodedBlocks = new ArrayList<>();
		for (Block block : new ArrayList<>(event.blockList())) {
			if (Nullables.isNullOrAir(block))
				continue;

			CustomBlock customBlock = CustomBlock.from(block);
			if (customBlock == null)
				continue;

			event.blockList().remove(block);
			explodedBlocks.add(new ExplodedCustomBlock(block, customBlock, event.getEntityType()));
		}

		if (explodedBlocks.isEmpty())
			return;

		Map<Block, net.minecraft.world.item.ItemStack> droppedItems = explodeBlocks(explodedBlocks);
		Tasks.wait(1, () -> {
			droppedItems.forEach((block, item) ->
				block.getWorld().dropItemNaturally(block.getLocation().toCenterLocation(), item.getBukkitStack()));
		});
	}

	@Getter
	@AllArgsConstructor
	private static class ExplodedCustomBlock {
		Block block;
		CustomBlock customBlock;
		EntityType sourceType;
	}

	private Map<Block, net.minecraft.world.item.ItemStack> explodeBlocks(List<ExplodedCustomBlock> explodedBlocks) {
		// Log
		for (ExplodedCustomBlock explodedBlock : explodedBlocks) {
			Block block = explodedBlock.getBlock();
			CustomBlockUtils.logRemoval("#" + explodedBlock.getSourceType().getName(), block.getLocation(), block, explodedBlock.getCustomBlock());
		}

		// Set to temp material
		for (ExplodedCustomBlock explodedBlock : explodedBlocks) {
			explodedBlock.getBlock().setType(Material.BARRIER, false); // Don't update the physics, & needs to be non-air
		}

		// Get drops
		Map<Block, net.minecraft.world.item.ItemStack> droppedItems = new HashMap<>();
		for (ExplodedCustomBlock explodedBlock : explodedBlocks) {
			Block block = explodedBlock.getBlock();
			CustomBlock customBlock = explodedBlock.getCustomBlock();

			net.minecraft.world.item.ItemStack droppedItem = NMSUtils.toNMS(customBlock.get().getItemStack());
			new HashMap<>(droppedItems).forEach((_block, _item) -> {
				if (ItemEntity.areMergable(droppedItem, _item)) {
					_item = ItemEntity.merge(_item, droppedItem, 16);
					droppedItems.put(_block, _item);
				}
			});

			if (!droppedItem.isEmpty())
				droppedItems.put(block, droppedItem);
		}

		// Set material to air, update physics
		for (ExplodedCustomBlock explodedBlock : explodedBlocks) {
			explodedBlock.getBlock().setType(Material.AIR, true);
		}

		return droppedItems;
	}

	//

	private boolean isPlacingBlock(PlayerInteractEvent event, Block clickedBlock, CustomBlock clickedCustomBlock) {
		Player player = event.getPlayer();
		CustomBlockUtils.debug(player, "&b- is placing block?");

		Action action = event.getAction();
		if (!action.equals(Action.RIGHT_CLICK_BLOCK)) {
			CustomBlockUtils.debug(player, "&c<- action != " + StringUtils.camelCase(Action.RIGHT_CLICK_BLOCK));
			return false;
		}


		BlockFace clickedFace = event.getBlockFace();
		Block preBlock = clickedBlock.getRelative(clickedFace);
		boolean didClickedCustomBlock = false;
		boolean isInteractable = clickedBlock.getType().isInteractable() || MaterialTag.INTERACTABLES.isTagged(preBlock);

		if (clickedCustomBlock != null) {
			didClickedCustomBlock = true;
			if (CustomBlock.NOTE_BLOCK != clickedCustomBlock) {
				isInteractable = false;
			}
		}

		CustomBlockUtils.debug(player, "&e- interactable = " + isInteractable);

		if (!player.isSneaking() && isInteractable) {
			CustomBlockUtils.debug(player, "&c<- not sneaking & isInteractable");
			return false;
		}

		ItemStack itemInHand = event.getItem();
		EquipmentSlot hand = EquipmentSlot.HAND;
		if (Nullables.isNullOrAir(itemInHand)) {
			itemInHand = event.getPlayer().getInventory().getItemInOffHand();
			hand = EquipmentSlot.OFF_HAND;
			if (Nullables.isNullOrAir(itemInHand)) {
				CustomBlockUtils.debug(player, "&c<- item in hand is null or air");
				return false;
			}
		}

		Material material = itemInHand.getType();
		boolean isPlacingCustomBlock = false;

		// Check decoration
		if (DecorationConfig.of(itemInHand) != null) {
			CustomBlockUtils.debug(player, "&c<- item in hand is a decoration");
			return true;

		// Check replaced vanilla items
		} else if (CustomBlockType.getItemMaterials().contains(material)) {
			isPlacingCustomBlock = true;

			// Check paper (Custom Blocks backup)
		} else if (material.equals(ICustomBlock.itemMaterial)) {
			String modelId = Model.of(itemInHand);
			if (!CustomBlock.modelIdMap.containsKey(modelId)) {
				CustomBlockUtils.debug(player, "&c<- unknown modelId: " + modelId);
				return false;
			} else
				isPlacingCustomBlock = true;
		}

		if (isPlacingCustomBlock) {
			if (placeCustomBlock(clickedBlock, player, hand, clickedFace, preBlock, itemInHand)) {
				event.setCancelled(true);
				CustomBlockUtils.logPlacement(player, preBlock, CustomBlock.from(itemInHand));
			}
		} else
			return placeVanillaBlock(event, player, hand, preBlock, didClickedCustomBlock, material, itemInHand);

		return true;
	}

	private boolean placeCustomBlock(Block clickedBlock, Player player, EquipmentSlot hand, BlockFace clickedFace, Block preBlock, ItemStack itemInHand) {
		CustomBlockUtils.debug(player, "&e- placing custom block");
		if (!preBlock.getLocation().toCenterLocation().getNearbyLivingEntities(0.5).isEmpty()) {
			CustomBlockUtils.debug(player, "&c<- entity in way");
			return false;
		}

		CustomBlock customBlock = CustomBlock.from(itemInHand);
		if (customBlock == null) {
			CustomBlockUtils.debug(player, "&c<- customBlock == null");
			return false;
		}

		ICustomBlock iCustomBlock = customBlock.get();
		Block underneath = preBlock.getRelative(BlockFace.DOWN);

		// Modify variables
		if (iCustomBlock instanceof IWaterLogged) {
			CustomBlockUtils.debug(player, "&e- CustomBlock instance of IWaterLogged");

			// if placing block in 1 depth water
			if (preBlock.getType() == Material.WATER && Nullables.isNullOrAir(preBlock.getRelative(BlockFace.UP))) {
				clickedBlock = preBlock;
				preBlock = preBlock.getRelative(BlockFace.UP);

				// if placing block above water
			} else if (underneath.getType() == Material.WATER && Nullables.isNullOrAir(preBlock)) {
				clickedBlock = underneath;

			} else if (!Nullables.isNullOrAir(preBlock)) {
				CustomBlockUtils.debug(player, "&c<- preBlock (" + StringUtils.camelCase(preBlock.getType()) + ") is not null/air");
				return false;
			}
		} else {
			if (MaterialTag.REPLACEABLE.isTagged(clickedBlock.getType())) {
				CustomBlockUtils.debug(player, "&e- clickedBlock is replaceable, adjusted placement");
				preBlock = clickedBlock;
			}
		}

		// Additional checks
		if (iCustomBlock.canNotPlace(clickedBlock, player, clickedFace, preBlock, underneath, itemInHand))
			return false;

		// place block
		if (!customBlock.placeBlock(player, hand, preBlock, clickedBlock, clickedFace, itemInHand)) {
			CustomBlockUtils.debug(player, "&c<- CustomBlock#PlaceBlock == false");
			return false;
		}

		return true;
	}

	private boolean placeVanillaBlock(PlayerInteractEvent event, Player player, EquipmentSlot hand, Block preBlock,
									  boolean didClickedCustomBlock, Material material, ItemStack itemStack) {
		CustomBlockUtils.debug(player, "&e- placing vanilla block");

		if (!didClickedCustomBlock) {
			CustomBlockUtils.debug(player, "&c<- didn't click on a custom block");
			return false;
		}

		if (player.isSneaking())
			return true;

		if (!ProtectionUtils.canBuild(player, preBlock)) {
			CustomBlockUtils.debug(player, "&c<- cannot build here");
			return false;
		}

		Block placedBlock = CustomBlockNMSUtils.tryPlaceVanillaBlock(player, itemStack);
		if (placedBlock == null) {
			CustomBlockUtils.debug(player, "&c<- cannot place this block here");
			return false;
		}

		if (hand == EquipmentSlot.HAND)
			player.swingMainHand();
		else if (hand == EquipmentSlot.OFF_HAND)
			player.swingOffHand();

		CustomBlockUtils.logPlacementVanilla(player, placedBlock);
		CustomBlockUtils.debug(player, "&a<- placed block: " + StringUtils.camelCase(material));
		CustomBlockSounds.tryPlaySound(player, SoundAction.PLACE, preBlock);

		ItemUtils.subtract(player, event.getItem());

		if (placedBlock.getState() instanceof Sign sign)
			player.openSign(sign, Side.FRONT);

		return true;
	}
}
