package gg.projecteden.nexus.features.customblocks.listeners;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.customblocks.CustomBlocks.BlockAction;
import gg.projecteden.nexus.features.customblocks.CustomBlocks.SoundAction;
import gg.projecteden.nexus.features.customblocks.NoteBlockUtils;
import gg.projecteden.nexus.features.customblocks.events.NoteBlockChangePitchEvent;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock.CustomBlockType;
import gg.projecteden.nexus.features.customblocks.models.common.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.common.ICustomBlock.PistonPushAction;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.IWaterLogged;
import gg.projecteden.nexus.features.customblocks.models.tripwire.incremental.IIncremental;
import gg.projecteden.nexus.features.customblocks.models.tripwire.tall.ITall;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.CustomNoteBlockData;
import gg.projecteden.nexus.models.customblock.CustomTripwireData;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.FaceAttachable.AttachedFace;
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
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.debug;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class CustomBlockListener implements Listener {
	public CustomBlockListener() {
		Nexus.registerListener(this);

		new CustomBlockSounds();
	}

	@EventHandler
	public void on(ResourcePackUpdateCompleteEvent ignored) {
		for (CustomBlock customBlock : CustomBlock.values())
			customBlock.registerRecipes();
	}

	@EventHandler
	public void onCreativePickBlock(InventoryCreativeEvent event) {
		SlotType slotType = event.getSlotType();
		if (!slotType.equals(SlotType.QUICKBAR))
			return;

		ItemStack item = event.getCursor();
		if (isNullOrAir(item))
			return;

		if (!CustomBlock.CustomBlockType.getItemMaterials().contains(item.getType()))
			return;

		Player player = (Player) event.getWhoClicked();
		Block block = player.getTargetBlockExact(5);
		if (isNullOrAir(block))
			return;

		CustomBlock customBlock = CustomBlock.fromBlock(block);
		if (customBlock == null) {
			debug("CreativePickBlock: CustomBlock == null");
			return;
		}

		if (customBlock == CustomBlock.TALL_SUPPORT) {
			CustomBlock _customBlock = CustomBlock.fromBlock(block.getRelative(BlockFace.DOWN));
			if (_customBlock != null)
				customBlock = _customBlock;
		}

		ItemStack newItem = customBlock.get().getItemStack();
		final ItemStack mainHand = player.getInventory().getItemInMainHand();
		if (newItem.equals(mainHand)) {
			event.setCancelled(true);
			return;
		}

		if (PlayerUtils.selectHotbarItem(player, newItem)) {
			event.setCancelled(true);
			return;
		}

		event.setCursor(newItem);
	}

	@EventHandler
	public void on(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlockPlaced();

		// fix clientside tripwire changes
		fixTripwireNearby(event.getPlayer(), block, new HashSet<>(List.of(block.getLocation())));

		// fix instrument changing

		Block above = block.getRelative(BlockFace.UP);

		CustomBlock customBlock = CustomBlock.fromBlock(above);
		if (CustomBlock.NOTE_BLOCK == customBlock) {
			NoteBlock noteBlock = (NoteBlock) above.getBlockData();
			CustomBlockData data = CustomBlockUtils.getData(noteBlock, above.getLocation());
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

		Player player = event.getPlayer();
		Block brokenBlock = event.getBlock();
		if (Nullables.isNullOrAir(brokenBlock))
			return;

		CustomBlock brokenCustomBlock = CustomBlock.fromBlock(brokenBlock);
		if (brokenCustomBlock == null)
			return;

		event.setDropItems(false);
		int amount = 1;

		Set<Location> fixedTripwire = new HashSet<>(List.of(brokenBlock.getLocation()));

		if (CustomBlock.TALL_SUPPORT == brokenCustomBlock) {
			debug("Broke tall support");
			breakBlock(player, brokenBlock, brokenCustomBlock, false, amount, true, true);

			Block blockUnder = brokenBlock.getRelative(BlockFace.DOWN);
			CustomBlock under = CustomBlock.fromBlock(blockUnder);

			if (under != null) {
				fixedTripwire.add(blockUnder.getLocation());
				debug("Underneath: " + under.name());
				breakBlock(player, blockUnder, under, true, amount, false, true);
				blockUnder.setType(Material.AIR);
			}

			fixTripwireNearby(player, brokenBlock, new HashSet<>(fixedTripwire));
			return;
		}


		if (brokenCustomBlock.get() instanceof IIncremental incremental) {
			amount = incremental.getIndex() + 1;

		} else if (brokenCustomBlock.get() instanceof ITall) {
			debug("Broke isTall");
			Block blockAbove = brokenBlock.getRelative(BlockFace.UP);
			CustomBlock above = CustomBlock.fromBlock(blockAbove);

			if (CustomBlock.TALL_SUPPORT == above) {
				debug("Breaking tall support above");

				breakBlock(player, blockAbove, above, false, amount, false, true);
				blockAbove.setType(Material.AIR);
			}
		}

		breakBlock(player, brokenBlock, brokenCustomBlock, true, amount, true, true);
		fixTripwireNearby(player, brokenBlock, new HashSet<>(fixedTripwire));
	}

	private void breakBlock(Player player, Block block, CustomBlock customBlock, boolean dropItem, int amount, boolean playSound, boolean spawnParticle) {
		debug("Breaking block: " + customBlock.name());
		customBlock.breakBlock(player, block, false, playSound, spawnParticle);

		if (!dropItem)
			return;

		// change drops
		ItemStack newDrop = new ItemBuilder(customBlock.get().getItemStack()).amount(amount).build();
		Material customType = customBlock.get().getVanillaItemMaterial();
		Location location = block.getLocation();

		for (ItemStack drop : block.getDrops()) {
			if (customType == drop.getType()) {
				if (!GameModeWrapper.of(player).isCreative())
					location.getWorld().dropItemNaturally(location, newDrop);
			}
		}
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (event.useInteractedBlock() == Result.DENY || event.useItemInHand() == Result.DENY)
			return;

		if (!EquipmentSlot.HAND.equals(event.getHand()))
			return;

		Action action = event.getAction();
		Player player = event.getPlayer();
		ItemStack itemInHand = event.getItem();
		boolean sneaking = player.isSneaking();
		Block clickedBlock = event.getClickedBlock();

		if (isNullOrAir(clickedBlock))
			return;

		CustomBlock clickedCustomBlock = CustomBlock.fromBlock(clickedBlock);
		// Place
		if (isSpawningEntity(event, clickedBlock, clickedCustomBlock)) {
			debug("is spawning entity: cancel=" + event.isCancelled());
			CustomBlockSounds.updateAction(player, BlockAction.UNKNOWN);
			return;
		}

		if (isIncrementingBlock(event, clickedBlock, clickedCustomBlock)) {
			debug("is incrementing block");
			CustomBlockSounds.updateAction(player, BlockAction.PLACE);
			return;
		}

		if (isPlacingBlock(event, clickedBlock, clickedCustomBlock)) {
			debug("is placing block");
			CustomBlockSounds.updateAction(player, BlockAction.PLACE);
			return;
		}

		debug("is interacting block");
		CustomBlockSounds.updateAction(player, BlockAction.INTERACT);

		if (clickedCustomBlock != null) {
			boolean isChangingPitch = isChangingPitch(action, sneaking, itemInHand);
			if (isChangingPitch) {
				event.setCancelled(true);

				if (CustomBlock.NOTE_BLOCK == clickedCustomBlock) {
					NoteBlock noteBlock = (NoteBlock) clickedBlock.getBlockData();
					changePitch(noteBlock, clickedBlock.getLocation(), sneaking);
				}
				return;
			}

			if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void on(BlockPhysicsEvent event) {
		Block eventBlock = event.getBlock();
		Material material = eventBlock.getType();
		if (CustomBlockType.getBlockMaterials().contains(material)) {
			resetBlockData(event, eventBlock, false);
		}

		Block aboveBlock = eventBlock.getRelative(BlockFace.UP);
		if (CustomBlockType.getBlockMaterials().contains(aboveBlock.getType())) {

			while (CustomBlockType.getBlockMaterials().contains(aboveBlock.getType())) {
				// Leave this as true, false will crash the server
				resetBlockData(event, aboveBlock, true);

				aboveBlock = aboveBlock.getRelative(BlockFace.UP);
			}
		}
	}

	@EventHandler
	public void on(BlockPistonExtendEvent event) {
		if (event.isCancelled())
			return;

		if (!onPistonEvent(event.getBlock(), event.getBlocks(), event.getDirection()))
			event.setCancelled(true);
	}

	@EventHandler
	public void on(BlockPistonRetractEvent event) {
		if (event.isCancelled())
			return;

		if (!onPistonEvent(event.getBlock(), event.getBlocks(), event.getDirection()))
			event.setCancelled(true);
	}

	//

	private boolean onPistonEvent(Block piston, List<Block> blocks, BlockFace direction) {
		blocks = blocks.stream().filter(block -> CustomBlock.fromBlock(block) != null).collect(Collectors.toList());
		Map<CustomBlockData, Pair<Location, Location>> moveBlocks = new HashMap<>();

		// initial checks
		for (Block block : blocks) {
			NoteBlock noteBlock = (NoteBlock) block.getBlockData();
			CustomBlockData data = CustomBlockUtils.getData(noteBlock, block.getLocation().toBlockLocation());
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
						debug("PistonEvent: " + _customBlock.name() + " cannot be moved by pistons");
						return false;
					}
					case BREAK -> {
						debug("PistonEvent: " + _customBlock.name() + " broke because of a piston");
						_customBlock.breakBlock(null, block, true, true, true);
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

	private void resetBlockData(BlockPhysicsEvent event, Block block, boolean doPhysics) {
		BlockData blockData = event.getChangedBlockData();
		if (!block.getBlockData().matches(blockData))
			return;

		CustomBlockData data = CustomBlockUtils.getData(blockData, block.getLocation());
		if (data == null)
			return;

		CustomBlock _customBlock = data.getCustomBlock();
		if (_customBlock == null)
			return;

		ICustomBlock customBlock = _customBlock.get();
		Block underneath = block.getRelative(BlockFace.DOWN);

		if (blockData instanceof NoteBlock noteBlock) {
			BlockFace facing = ((CustomNoteBlockData) data.getExtraData()).getFacing();

			ICustomNoteBlock customNoteBlock = (ICustomNoteBlock) customBlock;

			boolean powered = noteBlock.isPowered();
			Instrument instrument = noteBlock.getInstrument();

			noteBlock = (NoteBlock) customNoteBlock.getBlockData(facing, underneath);
			noteBlock.setPowered(powered);

			NoteBlockData noteBlockData = ((CustomNoteBlockData) data.getExtraData()).getNoteBlockData();
			noteBlockData.setPowered(noteBlock.isPowered());

			if (CustomBlock.NOTE_BLOCK != _customBlock)
				event.setCancelled(true);

			// the instrument should never change
			if (noteBlock.getInstrument() != instrument)
				event.setCancelled(true);

			block.setBlockData(noteBlock, false);

		} else if (blockData instanceof Tripwire tripwire) {
			BlockFace facing = ((CustomTripwireData) data.getExtraData()).getFacing();
			ICustomTripwire customTripwire = (ICustomTripwire) customBlock;

			boolean powered = customTripwire.isPowered(facing, underneath.getType());
			if (customTripwire.isIgnorePowered()) {
				powered = tripwire.isPowered();
			}

			tripwire = (Tripwire) customBlock.getBlockData(facing, underneath);
			tripwire.setPowered(powered);

			event.setCancelled(true);
			block.setBlockData(tripwire, false);
		} else
			return;

		block.getState().update(true, doPhysics);
	}

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
		if (isNullOrAir(itemInHand)) {
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

		CustomBlock customBlockItem = CustomBlock.fromItemstack(itemInHand);
		if (clickedCustomBlock == null || customBlockItem == null)
			return false;

		if (!(clickedCustomBlock.get() instanceof IIncremental incremental))
			return false;

		List<Integer> modelIdList = incremental.getModelIdList();
		if (!modelIdList.contains(customBlockItem.get().getModelId()))
			return false;

		// increment block

		int ndx = incremental.getIndex() + 1;
		if (ndx >= modelIdList.size())
			return false;

		int newModelId = modelIdList.get(ndx);
		CustomBlock update = CustomBlock.fromModelId(newModelId);
		if (update == null)
			return false;

		player.swingMainHand();
		clickedCustomBlock.updateBlock(player, update, clickedBlock);
		if (!GameModeWrapper.of(player).isCreative())
			itemInHand.subtract();

		return true;
	}

	private boolean isPlacingBlock(PlayerInteractEvent event, Block clickedBlock, CustomBlock clickedCustomBlock) {
		Action action = event.getAction();
		if (!action.equals(Action.RIGHT_CLICK_BLOCK)) {
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

		if (!player.isSneaking() && isInteractable) {
//			debug(" isPlacingBlock: not sneaking & isInteractable");
			return false;
		}

		ItemStack itemInHand = event.getItem();
		if (isNullOrAir(itemInHand)) {
//			debug(" isPlacingBlock: item in hand is null or air");
			return false;
		}

		Material material = itemInHand.getType();
		boolean isPlacingCustomBlock = false;

		// Check replaced vanilla items
		if (CustomBlockType.getItemMaterials().contains(material))
			isPlacingCustomBlock = true;

			// Check paper
		else if (material.equals(ICustomBlock.itemMaterial)) {
			int modelId = CustomModelData.of(itemInHand);
			if (!CustomBlock.modelIdMap.containsKey(modelId)) {
				debug(" isPlacingBlock: unknown modelId: " + modelId);
				return false;
			} else
				isPlacingCustomBlock = true;

			// Return if non-block, excluding redstone wire
		} else if (!material.isBlock() && !material.isSolid()) {
			if (!material.equals(Material.REDSTONE_WIRE)) {
				debug(" isPlacingBlock: not a block: " + material);
				return false;
			}
		}

		if (isPlacingCustomBlock) {
			if (placedCustomBlock(clickedBlock, player, clickedFace, preBlock, itemInHand))
				event.setCancelled(true);
		} else
			return placedVanillaBlock(event, clickedBlock, player, preBlock, didClickedCustomBlock, material);

		return true;
	}

	private boolean placedCustomBlock(Block clickedBlock, Player player, BlockFace clickedFace, Block preBlock, ItemStack itemInHand) {
		debug("Placing custom block");
		if (preBlock.getLocation().toCenterLocation().getNearbyLivingEntities(0.5).size() > 0) {
//			debug(" isPlacingBlock: entity in way");
			return false;
		}

		CustomBlock _customBlock = CustomBlock.fromItemstack(itemInHand);
		if (_customBlock == null) {
			debug(" isPlacingBlock: customBlock == null");
			return false;
		}

		ICustomBlock customBlock = _customBlock.get();
		if (customBlock instanceof IWaterLogged) {
			Block underneath = preBlock.getRelative(BlockFace.DOWN);

			// if placing block in 1 depth water
			if (preBlock.getType() == Material.WATER && Nullables.isNullOrAir(preBlock.getRelative(BlockFace.UP))) {
				clickedBlock = preBlock;
				preBlock = preBlock.getRelative(BlockFace.UP);

				// if placing block above water
			} else if (underneath.getType() == Material.WATER && Nullables.isNullOrAir(preBlock)) {
				clickedBlock = underneath;

			} else if (!isNullOrAir(preBlock)) {
				return false;
			}
		} else {
			if (!isNullOrAir(preBlock)) {
				return false;
			}
		}

		// place block

		if (!_customBlock.placeBlock(player, preBlock, clickedBlock, clickedFace, itemInHand)) {
//			debug(" isPlacingBlock: CustomBlock#PlaceBlock == false");
			return false;
		}

		return true;
	}

	private boolean placedVanillaBlock(PlayerInteractEvent event, Block clickedBlock, Player player, Block preBlock, boolean didClickedCustomBlock, Material material) {
		debug("Placing vanilla block");

		if (!isNullOrAir(preBlock)) {
//			debug(" isPlacingBlock: preBlock is not air");
			return false;
		}

		if (!didClickedCustomBlock) {
//			debug(" isPlacingBlock: Didn't click on a custom block");
			return false;
		}

		if (!player.isSneaking()) {
			BlockData blockData = material.createBlockData();
			BlockFace blockFace = event.getBlockFace();
			if (blockData instanceof Directional directional) {
				try {
					directional.setFacing(blockFace);
//					debug(" isPlacingBlock: set facing direction to " + blockFace);
				} catch (Exception ignored) {}
			}

			if (blockData instanceof FaceAttachable faceAttachable) {
				AttachedFace attachedFace = AttachedFace.WALL;
				switch (blockFace) {
					case UP -> attachedFace = AttachedFace.FLOOR;
					case DOWN -> attachedFace = AttachedFace.CEILING;
				}

				try {
					faceAttachable.setAttachedFace(attachedFace);
//					debug(" isPlacingBlock: set attached face to " + attachedFace);
				} catch (Exception ignored) {}
			}

			if (!BlockUtils.tryPlaceEvent(player, preBlock, clickedBlock, material, blockData)) {
//				debug(" isPlacingBlock: PlaceBlock event was cancelled");
				return false;
			}

//			debug(" isPlacingBlock: playing place sound");
			BlockUtils.playSound(SoundAction.PLACE, preBlock);
		}

		return true;
	}

	private boolean isChangingPitch(Action action, boolean sneaking, ItemStack itemInHand) {
		if (!action.equals(Action.RIGHT_CLICK_BLOCK))
			return false;

		return !sneaking || isNullOrAir(itemInHand) || !itemInHand.getType().isBlock();
	}

	private void changePitch(NoteBlock noteBlock, Location location, boolean sneaking) {
		CustomBlockData data = CustomBlockUtils.getData(noteBlock, location);
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

	private void fixTripwireNearby(Player player, Block current, Set<Location> visited) {
		for (BlockFace face : CustomBlockUtils.getNeighborFaces()) {
			Block neighbor = current.getRelative(face);
			Location location = neighbor.getLocation();

			if (visited.contains(location))
				continue;

			visited.add(location);

			if (Nullables.isNullOrAir(neighbor))
				continue;

			CustomBlock customBlock = CustomBlock.fromBlock(neighbor);
			if (customBlock == null || !(customBlock.get() instanceof ICustomTripwire))
				continue;

			Block underneath = neighbor.getRelative(BlockFace.DOWN);
			BlockFace facing = CustomBlockUtils.getFacing(customBlock, neighbor.getBlockData(), underneath);

			BlockData blockData = customBlock.get().getBlockData(facing, underneath);
			Tasks.wait(1, () -> player.sendBlockChange(location, blockData));

			fixTripwireNearby(player, neighbor, visited);
		}
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
