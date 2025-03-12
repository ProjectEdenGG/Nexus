package gg.projecteden.nexus.features.resourcepack.customblocks.listeners;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockNMSUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocks.BlockAction;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocks.SoundAction;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocksLang;
import gg.projecteden.nexus.features.resourcepack.customblocks.NoteBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.events.NoteBlockChangePitchEvent;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock.CustomBlockType;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.ICustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.ICustomBlock.PistonPushAction;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IRequireDirt;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IRequireSupport;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IWaterLogged;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.IIncremental;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tall.ITall;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.nexus.utils.protection.ProtectionUtils;
import gg.projecteden.parchment.event.block.CustomBlockUpdateEvent;
import io.papermc.paper.event.player.PlayerPickItemEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import org.bukkit.ExplosionResult;
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
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomBlockListener implements Listener {

	public CustomBlockListener() {
		Nexus.registerListener(this);

		new CustomBlockSounds();
		new ConversionListener();
	}

	@EventHandler
	public void on(PlayerPickItemEvent event) {
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
	public void on(CustomBlockUpdateEvent event) { // Parchment Event
		// TODO: Disable tripwire customblocks
		if (ICustomTripwire.isNotEnabled() && event.getBlock() instanceof Tripwire)
			return;
		//

		if (event.getUpdateType() != CustomBlockUpdateEvent.UpdateType.POWERED) {
			event.setCancelled(true);
			return;
		}

		if (CustomBlock.from(event.getLocation().getBlock()) != CustomBlock.NOTE_BLOCK) {
			event.setCancelled(true);
			return;
		}

		Location location = event.getLocation();
		if (location == null)
			return;

		if (!(event.getBlock() instanceof NoteBlock noteBlock))
			return;

		if (noteBlock.getInstrument() != Instrument.PIANO)
			return;

		CustomBlocksLang.debug("CustomBlockUpdateEvent: Instrument=" + noteBlock.getInstrument() + ", Note=" + noteBlock.getNote().getId() + ", Powered=" + noteBlock.isPowered());

		boolean isPowered = noteBlock.isPowered();
		ServerLevel serverLevel = NMSUtils.toNMS(location.getWorld());
		BlockPos blockPos = NMSUtils.toNMS(location);
		boolean hasNeighborSignal = serverLevel.hasNeighborSignal(blockPos);
		if (!isPowered && hasNeighborSignal) {
			CustomBlocksLang.debug("Playing NoteBlock: Instrument=" + noteBlock.getInstrument() + ", Note=" + noteBlock.getNote().getId() + ", Powered=" + noteBlock.isPowered());
			NoteBlockUtils.play(noteBlock, location, true);
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
		if (event.isCancelled())
			return;

		Block block = event.getBlockPlaced();
		// TODO: Disable tripwire customblocks
		if (ICustomTripwire.isNotEnabled() && block.getType() == Material.TRIPWIRE)
			return;
		//

		// fix clientside tripwire changes
		CustomBlockUtils.fixTripwireNearby(event.getPlayer(), block, new HashSet<>(List.of(block.getLocation())));
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

		CustomBlock brokenCustomBlock = CustomBlock.from(brokenBlock);
		if (brokenCustomBlock != null)
			event.setDropItems(false);

		CustomBlockUtils.breakBlock(brokenBlock, brokenCustomBlock, player, tool, true);
	}

	private void updateDatabase(Location location) {
		if (!_updateDatabase(location))
			CustomBlocksLang.debug("- no changes");
	}

	private boolean _updateDatabase(Location location) {
		CustomBlocksLang.debug("updating database at location");
		CustomBlock noteBlockWorld = CustomBlock.from(location.getBlock());

		if (noteBlockWorld == null) {
			CustomBlocksLang.debug("- data does not exist in world, delete from database");
			CustomBlockUtils.breakNoteBlockInDatabase(location);
			return true;
		}

		if (noteBlockWorld == CustomBlock.NOTE_BLOCK) {
			BlockData blockData = noteBlockWorld.get().getBlockData(BlockFace.UP, location.getBlock().getRelative(BlockFace.DOWN));
			CustomBlocksLang.debug("- data exists in world");

			NoteBlockData data = CustomBlockUtils.getNoteBlockData(location);
			if (data == null) {
				CustomBlocksLang.debug("-- no data exists at this location, fixing");
				CustomBlockUtils.placeNoteBlockInDatabase(location, blockData);
				return true;
			}

			if (!CustomBlock.NOTE_BLOCK.equals(noteBlockWorld)) {
				CustomBlocksLang.debug("-- incorrect data exists at this location, fixing");
				CustomBlockUtils.placeNoteBlockInDatabase(location, blockData);
				return true;
			}
		}

		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(PlayerInteractEvent event) {
		if (event.isCancelled())
			return;

		if (event.useInteractedBlock() == Result.DENY || event.useItemInHand() == Result.DENY)
			return;

		if (!EquipmentSlot.HAND.equals(event.getHand()))
			return;

		Block clickedBlock = event.getClickedBlock();

		if (Nullables.isNullOrAir(clickedBlock))
			return;

		Action action = event.getAction();
		Player player = event.getPlayer();
		ItemStack itemInHand = event.getItem();
		boolean sneaking = player.isSneaking();

		// TODO: Disable tripwire customblocks
		if (ICustomTripwire.isNotEnabled() && clickedBlock.getType() == Material.TRIPWIRE)
			return;
		//

		CustomBlocksLang.debug("\n&d&lPlayerInteractEvent:");

		Location clickedBlockLoc = clickedBlock.getLocation();
		CustomBlock clickedCustomBlock = CustomBlock.from(clickedBlock);

		if (clickedCustomBlock != null) {
			updateDatabase(clickedBlockLoc);
		}

		// Place
		if (isIncrementingBlock(event, clickedBlock, clickedCustomBlock)) {
			CustomBlockSounds.updateAction(player, BlockAction.PLACE);
			CustomBlocksLang.debug("&d<- done, incremented block");
			return;
		}

		if (isPlacingBlock(event, clickedBlock, clickedCustomBlock)) {
			CustomBlockSounds.updateAction(player, BlockAction.PLACE);
			CustomBlocksLang.debug("&d<- done, placed block");
			return;
		}

		CustomBlocksLang.debug("&e- interacted with block");
		CustomBlockSounds.updateAction(player, BlockAction.INTERACT);

		if (clickedCustomBlock != null) {
			if (CustomBlock.NOTE_BLOCK == clickedCustomBlock) {
				CustomBlocksLang.debug("&e-- is a note block");
				NoteBlock noteBlock = (NoteBlock) clickedBlock.getBlockData();

				if (isChangingPitch(action, sneaking, itemInHand)) {
					CustomBlocksLang.debug("&e<- is changing pitch");
					event.setCancelled(true);

					changePitch(player, noteBlock, clickedBlockLoc, sneaking);
					CustomBlocksLang.debug("&d<- done, changed pitch");
					return;
				}

				boolean isPlayingNote = action.equals(Action.LEFT_CLICK_BLOCK);
				if (isPlayingNote) {
					CustomBlocksLang.debug("&e<- is playing note");
					NoteBlockUtils.play(noteBlock, clickedBlockLoc, true);
				}
			}

			if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
				event.setCancelled(true);
				CustomBlocksLang.debug("&e<- action == " + action + ", cancelling");
			}
		}

		CustomBlocksLang.debug("&d<- done, end");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(BlockPistonExtendEvent event) {
		if (event.isCancelled())
			return;

		if (!onPistonEvent(event.getBlocks()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(BlockPistonRetractEvent event) {
		if (event.isCancelled())
			return;

		if (!onPistonEvent(event.getBlocks()))
			event.setCancelled(true);
	}

	private boolean onPistonEvent(List<Block> blocks) {
		CustomBlocksLang.debug("PistonEvent");
		blocks = blocks.stream().filter(block -> CustomBlock.from(block) != null).collect(Collectors.toList());

		// initial checks
		for (Block block : blocks) {
			// TODO: Disable tripwire customblocks
			if (ICustomTripwire.isNotEnabled() && block.getType() == Material.TRIPWIRE)
				continue;
			//

			CustomBlock customBlock = CustomBlock.from(block);
			if (customBlock == null)
				continue;

			ICustomBlock iCustomBlock = customBlock.get();
			CustomBlocksLang.debug("CustomBlock: " + iCustomBlock.getItemName());
			PistonPushAction pistonAction = iCustomBlock.getPistonPushedAction();
			CustomBlocksLang.debug("PistonPushAction: " + pistonAction);
			switch (pistonAction) {
				case PREVENT -> {
					CustomBlocksLang.debug("PistonEvent: " + customBlock.name() + " cannot be moved by pistons");
					return false;
				}
				case BREAK -> {
					CustomBlocksLang.debug("PistonEvent: " + customBlock.name() + " broke because of a piston");
					CustomBlockUtils.breakBlock(block, customBlock, null, null, true);
					continue;
				}
			}
		}

		return true;
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

		// Update database
		List<Location> explodedLocations = explodedBlocks.stream().map(explodedCustomBlock -> explodedCustomBlock.getBlock().getLocation()).toList();
//		CustomBlockUtils.breakBlocksDatabase(explodedLocations);

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

	private boolean isIncrementingBlock(PlayerInteractEvent event, Block clickedBlock, CustomBlock clickedCustomBlock) {
		Action action = event.getAction();
		if (!action.equals(Action.RIGHT_CLICK_BLOCK))
			return false;

		Player player = event.getPlayer();
		if (player.isSneaking())
			return false;

		ItemStack itemInHand = event.getItem();
		if (Nullables.isNullOrAir(itemInHand))
			return false;

		CustomBlock customBlockItem = CustomBlock.from(itemInHand);
		if (clickedCustomBlock == null || customBlockItem == null)
			return false;

		if (!(clickedCustomBlock.get() instanceof IIncremental incremental))
			return false;

		List<String> modelIdList = incremental.getModelIdList();
		if (!modelIdList.contains(customBlockItem.get().getModel()))
			return false;

		// increment block

		int ndx = incremental.getIndex() + 1;
		if (ndx >= modelIdList.size())
			return false;

		String newModelId = modelIdList.get(ndx);
		CustomBlock update = CustomBlock.from(newModelId);
		if (update == null)
			return false;

		player.swingMainHand();
		clickedCustomBlock.updateBlock(player, update, clickedBlock);
		if (!GameModeWrapper.of(player).isCreative())
			itemInHand.subtract();

		return true;
	}

	private boolean isPlacingBlock(PlayerInteractEvent event, Block clickedBlock, CustomBlock clickedCustomBlock) {
		CustomBlocksLang.debug("&b- Is placing block?");

		Action action = event.getAction();
		if (!action.equals(Action.RIGHT_CLICK_BLOCK)) {
			CustomBlocksLang.debug("&c<- Action != " + Action.RIGHT_CLICK_BLOCK);
			return false;
		}

		Player player = event.getPlayer();
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

		CustomBlocksLang.debug("&e- Interactable = " + isInteractable);

		if (!player.isSneaking() && isInteractable) {
			CustomBlocksLang.debug("&c<- not sneaking & isInteractable");
			return false;
		}

		ItemStack itemInHand = event.getItem();
		if (Nullables.isNullOrAir(itemInHand)) {
			CustomBlocksLang.debug("&c<- item in hand is null or air");
			return false;
		}

		Material material = itemInHand.getType();
		boolean isPlacingCustomBlock = false;

		// Check decoration
		if (DecorationConfig.of(itemInHand) != null) {
			CustomBlocksLang.debug("&c<- item in hand is a decoration");
			return true;

		// Check replaced vanilla items
		} else if (CustomBlockType.getItemMaterials().contains(material)) {
			isPlacingCustomBlock = true;

			// Check paper (Custom Blocks backup)
		} else if (material.equals(ICustomBlock.itemMaterial)) {
			String modelId = Model.of(itemInHand);
			if (!CustomBlock.modelIdMap.containsKey(modelId)) {
				CustomBlocksLang.debug("&c<- unknown modelId: " + modelId);
				return false;
			} else
				isPlacingCustomBlock = true;
		}

			// Return if non-block, excluding redstone wire
//		} else if (!material.isBlock() && !material.isSolid()) {
//			if (!material.equals(Material.REDSTONE)) {
//				CustomBlocksLang.debug("&c<- not a block: " + material);
//				return false;
//			}
//		}

		if (isPlacingCustomBlock) {
			if (placedCustomBlock(clickedBlock, player, clickedFace, preBlock, itemInHand)) {
				event.setCancelled(true);
				CustomBlockUtils.logPlacement(player, preBlock, CustomBlock.from(itemInHand));
			}
		} else
			return placedVanillaBlock(event, clickedBlock, player, preBlock, didClickedCustomBlock, material, itemInHand);

		return true;
	}

	private boolean placedCustomBlock(Block clickedBlock, Player player, BlockFace clickedFace, Block preBlock, ItemStack itemInHand) {
		CustomBlocksLang.debug("&e- placing custom block");
		if (!preBlock.getLocation().toCenterLocation().getNearbyLivingEntities(0.5).isEmpty()) {
			CustomBlocksLang.debug("&c<- entity in way");
			return false;
		}

		CustomBlock _customBlock = CustomBlock.from(itemInHand);
		if (_customBlock == null) {
			CustomBlocksLang.debug("&c<- customBlock == null");
			return false;
		}

		ICustomBlock customBlock = _customBlock.get();
		Block underneath = preBlock.getRelative(BlockFace.DOWN);

		// IWaterlogged
		if (customBlock instanceof IWaterLogged) {
			CustomBlocksLang.debug("&e- CustomBlock instance of IWaterLogged");

			// if placing block in 1 depth water
			if (preBlock.getType() == Material.WATER && Nullables.isNullOrAir(preBlock.getRelative(BlockFace.UP))) {
				clickedBlock = preBlock;
				preBlock = preBlock.getRelative(BlockFace.UP);

				// if placing block above water
			} else if (underneath.getType() == Material.WATER && Nullables.isNullOrAir(preBlock)) {
				clickedBlock = underneath;

			} else if (!Nullables.isNullOrAir(preBlock)) {
				CustomBlocksLang.debug("&c<- preBlock (" + StringUtils.camelCase(preBlock.getType()) + ") is not null/air");
				return false;
			}
		} else {
			if (!MaterialTag.REPLACEABLE.isTagged(preBlock.getType())) {
				CustomBlocksLang.debug("&c<- preBlock (" + StringUtils.camelCase(preBlock.getType()) + ") is not replaceable");
				return false;
			}
		}

		// ITall
		if (customBlock instanceof ITall) {
			CustomBlocksLang.debug("&e- CustomBlock instance of IWaterLogged");
			Block above = preBlock.getRelative(BlockFace.UP);

			boolean placeTallSupport = false;
			if (!(customBlock instanceof IWaterLogged))
				placeTallSupport = true;
			else if (clickedBlock.getType() != Material.WATER)
				placeTallSupport = true;

			if (placeTallSupport && !Nullables.isNullOrAir(above)) {
				CustomBlocksLang.debug("&c<- above (" + StringUtils.camelCase(preBlock.getType()) + ") is not null/air");
				return false;
			}
		}

		// IRequireSupport
		if (customBlock instanceof IRequireSupport && !(customBlock instanceof IWaterLogged)) {
			CustomBlocksLang.debug("&e- CustomBlock instance of IRequireSupport and not IWaterLogged");
			if (!underneath.isSolid()) {
				CustomBlocksLang.debug("&c<- underneath (" + StringUtils.camelCase(preBlock.getType()) + ") is not solid");
				return false;
			}
		}

		// IRequireDirt
		if (customBlock instanceof IRequireDirt) {
			CustomBlocksLang.debug("&e- CustomBlock instance of IRequireDirt");
			if (!MaterialTag.DIRT.isTagged(underneath.getType())) {
				CustomBlocksLang.debug("&c<- underneath (" + StringUtils.camelCase(preBlock.getType()) + ") is not a dirt type");
				return false;
			}
		}

		// place block
		if (!_customBlock.placeBlock(player, preBlock, clickedBlock, clickedFace, itemInHand)) {
			CustomBlocksLang.debug("&c<- CustomBlock#PlaceBlock == false");
			return false;
		}

		return true;
	}

	private boolean placedVanillaBlock(PlayerInteractEvent event, Block clickedBlock, Player player, Block preBlock,
									   boolean didClickedCustomBlock, Material material, ItemStack itemStack) {
		CustomBlocksLang.debug("&e- placing vanilla block");

//		if (!MaterialTag.REPLACEABLE.isTagged(preBlock.getType())) {
//			CustomBlocksLang.debug("&c<- preBlock is not replaceable");
//			return false;
//		}

		if (!didClickedCustomBlock) {
			CustomBlocksLang.debug("&c<- didn't click on a custom block");
			return false;
		}

		if (player.isSneaking())
			return true;

		if (!ProtectionUtils.canBuild(player, preBlock)) {
			CustomBlocksLang.debug("&c<- cannot build here");
			return false;
		}

		BlockData fixedBlockData = CustomBlockNMSUtils.tryPlaceVanillaBlock(player, itemStack);
		if (fixedBlockData == null) {
			CustomBlocksLang.debug("&c<- cannot place this block here");
			return false;
		}

		CustomBlocksLang.debug("&a<- placed block: " + StringUtils.camelCase(material));
		CustomBlockSounds.tryPlaySound(player, SoundAction.PLACE, preBlock);

		ItemUtils.subtract(player, event.getItem());

		if (preBlock.getState() instanceof Sign sign)
			player.openSign(sign, Side.FRONT);

		return true;
	}

	private boolean isChangingPitch(Action action, boolean sneaking, ItemStack itemInHand) {
		if (!action.equals(Action.RIGHT_CLICK_BLOCK))
			return false;

		return !sneaking || Nullables.isNullOrAir(itemInHand) || !itemInHand.getType().isBlock();
	}

	private void changePitch(Player player, NoteBlock noteBlock, Location location, boolean sneaking) {
		NoteBlockData data = CustomBlockUtils.getNoteBlockData(location);

		NoteBlockChangePitchEvent event = new NoteBlockChangePitchEvent(player, location.getBlock());
		if (event.callEvent())
			NoteBlockUtils.changePitch(player, sneaking, location, data);
	}
}
