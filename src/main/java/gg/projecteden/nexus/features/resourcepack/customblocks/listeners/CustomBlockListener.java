package gg.projecteden.nexus.features.resourcepack.customblocks.listeners;

import com.mojang.datafixers.util.Pair;
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
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IRequireDirt;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IRequireSupport;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IWaterLogged;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.IIncremental;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tall.ITall;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.protection.ProtectionUtils;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.CustomNoteBlockData;
import gg.projecteden.nexus.models.customblock.CustomTripwireData;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.parchment.event.block.CustomBlockUpdateEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

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
	public void on(CustomBlockUpdateEvent event) { // Parchment Event
		// TODO: Disable tripwire customblocks
		if (ICustomTripwire.isNotEnabled() && event.getBlock() instanceof Tripwire)
			return;
		//

		Location location = event.getLocation();
		if (location != null && updateDatabase(location))
			event.setCancelled(true);

		if (event.getUpdateType() != CustomBlockUpdateEvent.UpdateType.POWERED) {
			event.setCancelled(true);
			return;
		}

		if (location == null)
			return;

		if (!(event.getBlock() instanceof NoteBlock noteBlock))
			return;

		boolean isPowered = noteBlock.isPowered();
		ServerLevel serverLevel = NMSUtils.toNMS(location.getWorld());
		BlockPos blockPos = NMSUtils.toNMS(location);
		boolean hasNeighborSignal = serverLevel.hasNeighborSignal(blockPos);
		if (!isPowered && hasNeighborSignal) {
			NoteBlockUtils.play(noteBlock, location, true);
		}
	}

	private boolean updateDatabase(Location location) {
		CustomBlock worldBlock = CustomBlock.from(location.getBlock());
		CustomBlockData databaseBlock = CustomBlockUtils.getData(location);

		if (worldBlock == null) {
			boolean delete = false;
			if (databaseBlock.exists()) {
				CustomBlocksLang.debug("data exists at this location but is not in world, deleting");
				delete = true;
			}

			if (delete) {
				CustomBlockUtils.breakBlockDatabase(location);
				return true;
			}
			return false;
		}

		boolean fix = false;
		if (!databaseBlock.exists()) {
			CustomBlocksLang.debug("data doesn't exist at this location, creating");
			fix = true;
		} else if (!databaseBlock.getCustomBlock().get().equals(worldBlock.get())) {
			CustomBlocksLang.debug("incorrect data exists at this location, fixing");
			fix = true;
		}

		if (fix) {
			CustomBlockUtils.createData(location, worldBlock.get().getCustomBlock(), BlockFace.UP);
			return true;
		}

		return false;
	}

	@EventHandler
	public void on(ResourcePackUpdateCompleteEvent ignored) {
		if (Nexus.getEnv() != Env.TEST)
			return;

		for (CustomBlock customBlock : CustomBlock.values())
			customBlock.registerRecipes();
	}

	// Does not work -- Creative Pick Block is Clientside
//	@EventHandler
//	public void onCreativePickBlock(InventoryCreativeEvent event) {
//		SlotType slotType = event.getSlotType();
//		if (!slotType.equals(SlotType.QUICKBAR))
//			return;
//
//		ItemStack item = event.getCursor();
//		if (Nullables.isNullOrAir(item))
//			return;
//
//		if (!CustomBlock.CustomBlockType.getItemMaterials().contains(item.getType()))
//			return;
//
//		Player player = (Player) event.getWhoClicked();
//		Block block = player.getTargetBlockExact(5);
//		if (Nullables.isNullOrAir(block))
//			return;
//
//		CustomBlock customBlock = CustomBlock.from(block);
//		if (customBlock == null) {
//			CustomBlocksLang.debug("CreativePickBlock: CustomBlock == null");
//			return;
//		}
//
//		if (customBlock == CustomBlock.TALL_SUPPORT) {
//			CustomBlock _customBlock = CustomBlock.from(block.getRelative(BlockFace.DOWN));
//			if (_customBlock != null)
//				customBlock = _customBlock;
//		}
//
//		ItemStack newItem = customBlock.get().getItemStack();
//		final ItemStack mainHand = player.getInventory().getItemInMainHand();
//		if (newItem.equals(mainHand)) {
//			CustomBlocksLang.debug("CreativePickBlock:  CustomBlock is the same as item in mainHand, cancelling");
//			event.setCancelled(true);
//			return;
//		}
//
//		if (PlayerUtils.selectHotbarItem(player, newItem)) {
//			CustomBlocksLang.debug("CreativePickBlock:  Selecting hotbar item & cancelling");
//			event.setCancelled(true);
//			return;
//		}
//
//		CustomBlocksLang.debug("CreativePickBlock:  Spawning item on cursor");
//		event.setCursor(newItem);
//	}

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

		// fix instrument changing

		Block above = block.getRelative(BlockFace.UP);

		CustomBlock customBlock = CustomBlock.from(above);
		if (CustomBlock.NOTE_BLOCK == customBlock) {
			NoteBlock noteBlock = (NoteBlock) above.getBlockData();
			CustomBlockData data = CustomBlockUtils.getDataOrCreate(above.getLocation(), noteBlock);
			if (data == null)
				return;

			CustomNoteBlockData customNoteBlockData = (CustomNoteBlockData) data.getExtraData();
			customNoteBlockData.getNoteBlockData(above, true);
		}
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

		CustomBlockUtils.breakBlock(brokenBlock, brokenCustomBlock, player, tool);
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

		if (clickedCustomBlock != null && updateDatabase(clickedBlockLoc))
			event.setCancelled(true);

		// Place
		if (isSpawningEntity(event, clickedBlock, clickedCustomBlock)) {
			CustomBlockSounds.updateAction(player, BlockAction.UNKNOWN);
			CustomBlocksLang.debug("&d<- done, spawned entity: cancel=" + event.isCancelled());
			return;
		}

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

					changePitch(noteBlock, clickedBlockLoc, sneaking);
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

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void on(BlockPhysicsEvent event) {
		Block eventBlock = event.getBlock();
		Material material = eventBlock.getType();
		// TODO: Disable tripwire customblocks
		if (ICustomTripwire.isNotEnabled() && material == Material.TRIPWIRE)
			return;
		//

		if (CustomBlockType.getBlockMaterials().contains(material)) {
			resetBlockData(event, eventBlock);
		}

		Block aboveBlock = eventBlock.getRelative(BlockFace.UP);
		if (CustomBlockType.getBlockMaterials().contains(aboveBlock.getType())) {

			while (CustomBlockType.getBlockMaterials().contains(aboveBlock.getType())) {
				resetBlockData(event, aboveBlock);

				aboveBlock = aboveBlock.getRelative(BlockFace.UP);
			}
		}
	}

	private void resetBlockData(BlockPhysicsEvent event, Block block) {
		BlockData blockData = event.getChangedBlockData();
		if (!block.getBlockData().matches(blockData))
			return;

		CustomBlockData data = CustomBlockUtils.getDataOrCreate(block.getLocation(), blockData);
		if (data == null)
			return;

		CustomBlock _customBlock = data.getCustomBlock();
		if (_customBlock == null)
			return;

		ICustomBlock customBlock = _customBlock.get();
		Block underneath = block.getRelative(BlockFace.DOWN);

		final BlockData finalData;
		if (blockData instanceof NoteBlock noteBlock) {
			BlockFace facing = ((CustomNoteBlockData) data.getExtraData()).getFacing();

			ICustomNoteBlock customNoteBlock = (ICustomNoteBlock) customBlock;

			boolean powered = noteBlock.isPowered();
			Instrument instrument = noteBlock.getInstrument();

			noteBlock = (NoteBlock) customNoteBlock.getBlockData(facing, underneath);
			NoteBlockData noteBlockData = ((CustomNoteBlockData) data.getExtraData()).getNoteBlockData();

			//debug("Block Physics Event");

			if (CustomBlock.NOTE_BLOCK == _customBlock) {
				noteBlock.setPowered(powered);
				noteBlockData.setPowered(powered);
				//debug("Powered == " + powered);
			} else {
				noteBlock.setPowered(noteBlockData.isPowered());

				//debug("canceling event: is not noteblock");
				event.setCancelled(true);
			}

			if (noteBlock.getInstrument() != instrument) {
				//debug("canceling event: instrument changed");
				event.setCancelled(true);
			}

			finalData = noteBlock;
		} else if (blockData instanceof org.bukkit.block.data.type.Tripwire tripwire) {
			// TODO: Disable tripwire customblocks
			if (ICustomTripwire.isNotEnabled())
				return;
			//

			BlockFace facing = ((CustomTripwireData) data.getExtraData()).getFacing();
			ICustomTripwire customTripwire = (ICustomTripwire) customBlock;

			boolean powered = customTripwire.isPowered(facing, underneath.getType());
			if (customTripwire.isIgnorePowered()) {
				powered = tripwire.isPowered();
			}

			tripwire = (org.bukkit.block.data.type.Tripwire) customBlock.getBlockData(facing, underneath);
			tripwire.setPowered(powered);

			//debug("canceling event: is tripwire");
			event.setCancelled(true);

			finalData = tripwire;
		} else
			return;

		block.getState().update(true, false); // needs to be (true, false)
		Tasks.wait(1, () -> block.setBlockData(finalData, false));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(BlockPistonExtendEvent event) {
		if (event.isCancelled())
			return;

		if (!onPistonEvent(event.getBlock(), event.getBlocks(), event.getDirection()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(BlockPistonRetractEvent event) {
		if (event.isCancelled())
			return;

		if (!onPistonEvent(event.getBlock(), event.getBlocks(), event.getDirection()))
			event.setCancelled(true);
	}

	private boolean onPistonEvent(Block piston, List<Block> blocks, BlockFace direction) {
		CustomBlocksLang.debug("PistonEvent");
		blocks = blocks.stream().filter(block -> CustomBlock.from(block) != null).collect(Collectors.toList());
		Map<CustomBlockData, Pair<Location, Location>> moveBlocks = new HashMap<>();

		// initial checks
		for (Block block : blocks) {
			// TODO: Disable tripwire customblocks
			if (ICustomTripwire.isNotEnabled() && block.getType() == Material.TRIPWIRE)
				continue;
			//

			CustomBlockData data = CustomBlockUtils.getDataOrCreate(block.getLocation().toBlockLocation(), block.getBlockData());
			if (data == null)
				continue;

			CustomBlock _customBlock = data.getCustomBlock();
			if (_customBlock == null)
				continue;

			ICustomBlock customBlock = _customBlock.get();
			PistonPushAction pistonAction = customBlock.getPistonPushedAction();
			if (!pistonAction.equals(PistonPushAction.MOVE)) {
				switch (pistonAction) {
					case PREVENT -> {
						CustomBlocksLang.debug("PistonEvent: " + _customBlock.name() + " cannot be moved by pistons");
						return false;
					}
					case BREAK -> {
						CustomBlocksLang.debug("PistonEvent: " + _customBlock.name() + " broke because of a piston");
						CustomBlockUtils.breakBlock(block, _customBlock, null, null);
						continue;
					}
				}
			}

			Location curLoc = block.getLocation().toBlockLocation();
			Location newLoc = block.getRelative(direction).getLocation().toBlockLocation();
			moveBlocks.put(data, new Pair<>(curLoc, newLoc));
		}

		// Move blocks
		CustomBlockUtils.pistonMove(piston, moveBlocks);

		return true;
	}

	//

	private boolean isSpawningEntity(PlayerInteractEvent event, Block clickedBlock, CustomBlock clickedCustomBlock) {
		Action action = event.getAction();
		if (!action.equals(Action.RIGHT_CLICK_BLOCK)) {
			return false;
		}

		Player player = event.getPlayer();
		BlockFace clickedFace = event.getBlockFace();
		Block inFront = clickedBlock.getRelative(clickedFace);
		boolean isInteractable = clickedBlock.getType().isInteractable() || MaterialTag.INTERACTABLES.isTagged(inFront);
		if (CustomBlock.NOTE_BLOCK != clickedCustomBlock) {
			isInteractable = false;
		}

		if (!player.isSneaking() && isInteractable) {
			return false;
		}

		ItemStack itemInHand = event.getItem();
		if (Nullables.isNullOrAir(itemInHand)) {
			return false;
		}

		return MaterialTag.SPAWNS_ENTITY.isTagged(itemInHand.getType());
	}

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

		// TODO: CHECK FOR DECORATION

		// Check replaced vanilla items
		if (CustomBlockType.getItemMaterials().contains(material))
			isPlacingCustomBlock = true;

			// Check paper (Custom Blocks backup)
		else if (material.equals(ICustomBlock.itemMaterial)) {
			String modelId = Model.of(itemInHand);
			if (!CustomBlock.modelIdMap.containsKey(modelId)) {
				CustomBlocksLang.debug("&c<- unknown modelId: " + modelId);
				return false;
			} else
				isPlacingCustomBlock = true;

			// Return if non-block, excluding redstone wire
		} else if (!material.isBlock() && !material.isSolid()) {
			if (!material.equals(Material.REDSTONE)) {
				CustomBlocksLang.debug("&c<- not a block: " + material);
				return false;
			}
		}

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

		if (!MaterialTag.REPLACEABLE.isTagged(preBlock.getType())) {
			CustomBlocksLang.debug("&c<- preBlock is not replaceable");
			return false;
		}

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
		return true;
	}

	private boolean isChangingPitch(Action action, boolean sneaking, ItemStack itemInHand) {
		if (!action.equals(Action.RIGHT_CLICK_BLOCK))
			return false;

		return !sneaking || Nullables.isNullOrAir(itemInHand) || !itemInHand.getType().isBlock();
	}

	private void changePitch(NoteBlock noteBlock, Location location, boolean sneaking) {
		CustomBlockData data = CustomBlockUtils.getDataOrCreate(location, noteBlock);
		if (data == null)
			return;

		Block block = location.getBlock();

		CustomNoteBlockData customNoteBlockData = (CustomNoteBlockData) data.getExtraData();
		NoteBlockData noteBlockData = customNoteBlockData.getNoteBlockData(block, true);
		if (noteBlockData == null)
			return;

		NoteBlockChangePitchEvent event = new NoteBlockChangePitchEvent(block);
		if (event.callEvent())
			NoteBlockUtils.changePitch(sneaking, location, noteBlockData);
	}



	// TODO
//	public void updateConnectedTripwire(Player player, CustomBlock customBlock, Location origin){
//		if (customBlock != CustomBlock.TRIPWIRE && customBlock != CustomBlock.TRIPWIRE_CROSS)
//			return;
//
//		Block originBlock = origin.getBlock();
//		BlockFace facing = CustomBlockUtils.getFacing(customBlock, originBlock.getBlockData(), originBlock.getRelative(BlockFace.DOWN));
//
//		Set<BlockFace> faces = Set.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);
//		Set<Location> updated = new HashSet<>();
//		Set<Location> hooks = new HashSet<>();
//
//		// update tripwire
//		for (BlockFace face : faces) {
//			Block neighbor = originBlock.getRelative(face);
//			Location neighborLoc = neighbor.getLocation();
//
//			if(updated.contains(neighborLoc))
//				continue;
//
//			CustomBlock _customBlock = CustomBlock.fromBlock(neighbor);
//			if(_customBlock == null) {
//				if (neighbor.getType() == Material.TRIPWIRE_HOOK) {
//					hooks.add(neighborLoc);
//				}
//				continue;
//			}
//
//			BlockFace _facing = CustomBlockUtils.getFacing(_customBlock, neighbor.getBlockData(), neighbor.getRelative(BlockFace.DOWN));
//
//			if(_customBlock == CustomBlock.TRIPWIRE) {
//				if (_facing != facing) {
//					updated.add(neighborLoc);
//					customBlock.updateBlock(player, CustomBlock.TRIPWIRE_CROSS, originBlock);
//				}
//			} else if(_customBlock == CustomBlock.TRIPWIRE_CROSS) {
//				Map<Location, BlockFace> neighbors = new HashMap<>();
//				for (BlockFace _face : faces) {
//					Block crossNeighbor = neighbor.getRelative(_face);
//					CustomBlock neighborCustomBlock = CustomBlock.fromBlock(crossNeighbor);
//					if(neighborCustomBlock == null)
//						continue;
//
//					if(neighborCustomBlock == CustomBlock.TRIPWIRE || neighborCustomBlock == CustomBlock.TRIPWIRE_CROSS){
//						BlockFace __facing = CustomBlockUtils.getFacing(_customBlock, neighbor.getBlockData(), neighbor.getRelative(BlockFace.DOWN));
//						neighbors.put(crossNeighbor.getLocation(), __facing);
//					}
//				}
//
//				if(neighbors.isEmpty()){
//					updated.add(neighborLoc);
//					customBlock.updateBlock(player, CustomBlock.TRIPWIRE, originBlock);
//				} else {
//					Set<BlockFace> directions = new HashSet<>(neighbors.values());
//					if(directions.size() == 1){
//						BlockFace neighborFace = directions.stream().toList().get(0);
//						if(neighborFace == facing)
//							//
//					}
//				}
//			}
//		}
//
//		// update hooks
//	}
//
//	public void updateConnectedTripwire1(Location origin){
//
//		Set<Location> tripwire = updateConnectedTripwire(new HashSet<>(), origin, new HashSet<>());
//		// TODO: using tripwire, properly setup the hooks
//	}
//
//	public Set<Location> updateConnectedTripwire(Set<Location> visited, Location current, Set<Location> tripwire){
//		for (BlockFace face : CustomBlockUtils.getNeighborFaces()) {
//			if(face == BlockFace.UP || face == BlockFace.DOWN)
//				continue;
//
//			Block neighbor = current.getBlock().getRelative(face);
//			Location location = neighbor.getLocation();
//
//			if (visited.contains(location))
//				continue;
//
//			visited.add(location);
//
//			if (Nullables.isNullOrAir(neighbor))
//				continue;
//
//			CustomBlock customBlock = CustomBlock.fromBlock(neighbor);
//			if (customBlock != CustomBlock.TRIPWIRE && customBlock != CustomBlock.TRIPWIRE_CROSS)
//				continue;
//
//			tripwire.add(location);
//
//		}
//
//		return visited;
//	}
}
