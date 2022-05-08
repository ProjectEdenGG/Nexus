package gg.projecteden.nexus.features.customblocks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customblocks.CustomBlocks.BlockAction;
import gg.projecteden.nexus.features.customblocks.CustomBlocks.SoundAction;
import gg.projecteden.nexus.features.customblocks.events.NoteBlockChangePitchEvent;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.common.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.common.ICustomBlock.PistonPushAction;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.CustomNoteBlockData;
import gg.projecteden.nexus.models.customblock.CustomTripwireData;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import me.lexikiq.event.sound.LocationNamedSoundEvent;
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
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.debug;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class CustomBlocksListener implements Listener {
	Map<Player, BlockAction> playerActionMap = new ConcurrentHashMap<>();

	public CustomBlocksListener() {
		Nexus.registerListener(this);
	}

	// Recipe Stuff
	@EventHandler
	public void on(ResourcePackUpdateCompleteEvent ignored) {
		for (CustomBlock customBlock : CustomBlock.values())
			customBlock.registerRecipes();
	}

	@EventHandler
	public void on(PrepareItemCraftEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player)) return;

		ItemStack result = event.getInventory().getResult();
		if (Nullables.isNullOrAir(result))
			return;

		CustomBlock customBlock = CustomBlock.fromItemstack(result);
		if (CustomBlock.NOTE_BLOCK == customBlock)
			event.getInventory().setResult(customBlock.get().getItemStack());

		CustomBlockUtils.unlockRecipe(player, result.getType());
	}

	@EventHandler
	public void on(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		CustomBlockUtils.unlockRecipe(player, event.getItem().getItemStack().getType());
	}

	@EventHandler
	public void on(InventoryClickEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player)) return;

		final Inventory inventory = event.getClickedInventory();
		if (inventory == null || inventory.getType() == InventoryType.PLAYER)
			return;

		final ItemStack item = player.getItemOnCursor();
		if (isNullOrAir(item))
			return;

		CustomBlockUtils.unlockRecipe(player, item.getType());
	}

	//

	@EventHandler
	public void on(NotePlayEvent event) {
		event.setCancelled(true);

		CustomBlock customBlock = CustomBlock.fromBlock(event.getBlock());
		if (CustomBlock.NOTE_BLOCK != customBlock)
			return;

		NoteBlock noteBlock = (NoteBlock) event.getBlock().getBlockData();
		NoteBlockUtils.play(noteBlock, event.getBlock().getLocation(), true);
	}

	@EventHandler
	public void on(InventoryCreativeEvent event) {
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

	// Handles Sound: STEP
	@EventHandler
	public void on(LocationNamedSoundEvent event) {
		Block block = event.getLocation().getBlock();
		Block below = block.getRelative(BlockFace.DOWN);
		Block source = null;

		CustomBlock _customBlock = CustomBlock.fromBlock(block);
		if (_customBlock != null)
			source = block;
		else {
			_customBlock = CustomBlock.fromBlock(below);
			if (_customBlock != null)
				source = below;
		}

		if (!Nullables.isNullOrAir(source)) {
			SoundAction soundAction = SoundAction.fromSound(event.getSound());
			if (soundAction == null)
				return;

			if (soundAction != SoundAction.STEP)
				return;

			event.setCancelled(true);
			_customBlock.playSound(soundAction, source.getLocation());
			return;
		}

		if (CustomBlockUtils.playDefaultSounds(event.getSound(), event.getLocation()))
			event.setCancelled(true);
	}

	// Handles Sound: FALL
	@EventHandler
	public void on(EntityDamageEvent event) {
		if (event.isCancelled())
			return;

		if (!(event.getEntity() instanceof Player player))
			return;

		if (!event.getCause().equals(DamageCause.FALL))
			return;

		Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		if (Nullables.isNullOrAir(block))
			return;

		playerActionMap.put(player, BlockAction.FALL);
		CustomBlockUtils.tryPlayDefaultSound(SoundAction.FALL, block);
	}

	@EventHandler
	public void on(BlockDamageEvent event) {
		if (event.isCancelled())
			return;

		playerActionMap.put(event.getPlayer(), BlockAction.HIT);
	}

	// Handles Sound: HIT
	@EventHandler
	public void on(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		Block block = player.getTargetBlockExact(5);
		if (block == null)
			return;

		if (!playerActionMap.containsKey(player))
			return;

		if (playerActionMap.get(player) == BlockAction.HIT) {
//			debug("PlayerAnimationEvent");
			CustomBlockUtils.tryPlayDefaultSound(SoundAction.HIT, block);
		}
	}

	@EventHandler
	public void on(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;

		playerActionMap.put(event.getPlayer(), BlockAction.PLACE);

		Block block = event.getBlockPlaced();
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

	// Handles Sound: BREAK
	@EventHandler
	public void on(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		playerActionMap.put(event.getPlayer(), BlockAction.BREAK);

		Block block = event.getBlock();
		CustomBlock _customBlock = CustomBlock.fromBlock(block);
		if (_customBlock == null) {
//			debug("BreakBlock: CustomBlock == null");
			CustomBlockUtils.tryPlayDefaultSound(SoundAction.BREAK, block);
			return;
		}

		Location location = block.getLocation();
		_customBlock.breakBlock(location, false);

		// change drops
		event.setDropItems(false);

		ItemStack newDrop = _customBlock.get().getItemStack();
		Material customType = _customBlock.get().getVanillaItemMaterial();

		for (ItemStack drop : block.getDrops()) {
			if (customType == drop.getType()) {
				if (!GameModeWrapper.of(event.getPlayer()).isCreative())
					location.getWorld().dropItemNaturally(location, newDrop);
			}
		}
	}

	// Handles Sound: PLACE
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

		CustomBlock customBlock = CustomBlock.fromBlock(clickedBlock);
		// Place
		if (isSpawningEntity(event, clickedBlock, customBlock)) {
			playerActionMap.put(player, BlockAction.UNKNOWN);
			return;
		}
		if (isPlacingBlock(event, clickedBlock, customBlock)) {
			playerActionMap.put(player, BlockAction.PLACE);
			return;
		}

		playerActionMap.put(player, BlockAction.INTERACT);

		if (customBlock != null) {
			boolean isChangingPitch = isChangingPitch(action, sneaking, itemInHand);
			if (isChangingPitch) {
				event.setCancelled(true);

				if (CustomBlock.NOTE_BLOCK == customBlock) {
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

	Set<Material> handleMaterials = Set.of(Material.NOTE_BLOCK, Material.TRIPWIRE);
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void on(BlockPhysicsEvent event) {
		Block eventBlock = event.getBlock();
		Material material = eventBlock.getType();
		if (handleMaterials.contains(material)) {
			resetBlockData(event, eventBlock, false);
		}

		Block aboveBlock = eventBlock.getRelative(BlockFace.UP);
		if (handleMaterials.contains(aboveBlock.getType())) {

			while (handleMaterials.contains(aboveBlock.getType())) {
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
						_customBlock.breakBlock(block, true);
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

		if (blockData instanceof NoteBlock noteBlock) {
			BlockFace facing = ((CustomNoteBlockData) data.getExtraData()).getFacing();

			ICustomNoteBlock customNoteBlock = (ICustomNoteBlock) customBlock;

			boolean powered = noteBlock.isPowered();
			noteBlock = (NoteBlock) customNoteBlock.getBlockData(facing);
			noteBlock.setPowered(powered);

			NoteBlockData noteBlockData = ((CustomNoteBlockData) data.getExtraData()).getNoteBlockData();
			noteBlockData.setPowered(powered);

			block.setBlockData(noteBlock, false);
		} else if (blockData instanceof Tripwire tripwire) {
			BlockFace facing = ((CustomTripwireData) data.getExtraData()).getFacing();
			ICustomTripwire customTripwire = (ICustomTripwire) customBlock;

			boolean powered = customTripwire.isPowered(facing);
			if (customTripwire.isIgnorePowered()) {
				powered = tripwire.isPowered();
			}

			tripwire = (Tripwire) customBlock.getBlockData(facing);
			tripwire.setPowered(powered);

			// Fixes the player detection issue, but causes endless physics updates
//			if(powered) {
//				block.setBlockData(tripwire, true);
//				NMSUtils.applyPhysics(block.getLocation());
//				return;
//			}

			block.setBlockData(tripwire, false);
			event.setCancelled(true);
		}

		block.getState().update(true, doPhysics);
	}

	private boolean isSpawningEntity(PlayerInteractEvent event, Block clickedBlock, CustomBlock customBlock) {
		Action action = event.getAction();
		if (!action.equals(Action.RIGHT_CLICK_BLOCK)) {
			return false;
		}

		Player player = event.getPlayer();
		BlockFace clickedFace = event.getBlockFace();
		Block inFront = clickedBlock.getRelative(clickedFace);
		boolean isInteractable = clickedBlock.getType().isInteractable() || MaterialTag.INTERACTABLES.isTagged(inFront);
		if (CustomBlock.NOTE_BLOCK != customBlock) {
			isInteractable = false;
		}

		if (!player.isSneaking() && isInteractable)
			return false;

		ItemStack itemInHand = event.getItem();
		if (isNullOrAir(itemInHand)) {
			return false;
		}

		return MaterialTag.SPAWNS_ENTITY.isTagged(itemInHand.getType());
	}

	private boolean isPlacingBlock(PlayerInteractEvent event, Block clickedBlock, CustomBlock customBlock) {
		Action action = event.getAction();
		if (!action.equals(Action.RIGHT_CLICK_BLOCK)) {
			return false;
		}

		Player player = event.getPlayer();
		BlockFace clickedFace = event.getBlockFace();
		Block preBlock = clickedBlock.getRelative(clickedFace);
		boolean clickedCustomBlock = false;
		boolean isInteractable = clickedBlock.getType().isInteractable() || MaterialTag.INTERACTABLES.isTagged(preBlock);

		if (customBlock != null) {
			clickedCustomBlock = true;
			if (CustomBlock.NOTE_BLOCK != customBlock) {
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
		if (Material.NOTE_BLOCK == material)
			isPlacingCustomBlock = true;
		else if (material.equals(ICustomBlock.itemMaterial)) {
			int modelId = CustomModelData.of(itemInHand);
			if (!CustomBlock.modelIdMap.containsKey(modelId)) {
//				debug(" isPlacingBlock: unknown modelId: " + modelId);
				return false;
			} else
				isPlacingCustomBlock = true;
		} else if (!material.equals(Material.REDSTONE_WIRE) && (!material.isBlock() && !material.isSolid())) {
//			debug(" isPlacingBlock: not a block: " + material);
			return false;
		}

		if (!isNullOrAir(preBlock)) {
//			debug(" isPlacingBlock: preBlock is not air");
			return false;
		}

		if (isPlacingCustomBlock) {
			if (preBlock.getLocation().toCenterLocation().getNearbyLivingEntities(0.5).size() > 0) {
//				debug(" isPlacingBlock: entity in way");
				return false;
			}

			CustomBlock _customBlock = CustomBlock.fromItemstack(itemInHand);
			if (_customBlock == null) {
//				debug(" isPlacingBlock: customBlock == null");
				return false;
			}

			if (!_customBlock.placeBlock(player, preBlock, clickedBlock, clickedFace, itemInHand)) {
//				debug(" isPlacingBlock: CustomBlock#PlaceBlock == false");
				return false;
			}

			event.setCancelled(true);
		} else {
			if (!clickedCustomBlock) {
//				debug(" isPlacingBlock: Didn't click on a custom block");
				return false;
			}

			if (!player.isSneaking()) {
				BlockData blockData = material.createBlockData();
				BlockFace blockFace = event.getBlockFace();
				if (blockData instanceof Directional directional) {
					try {
						directional.setFacing(blockFace);
//						debug(" isPlacingBlock: set facing direction to " + blockFace);
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
//						debug(" isPlacingBlock: set attached face to " + attachedFace);
					} catch (Exception ignored) {}
				}

				if (!BlockUtils.tryPlaceEvent(player, preBlock, clickedBlock, material, blockData)) {
//					debug(" isPlacingBlock: PlaceBlock event was cancelled");
					return false;
				}

//				debug(" isPlacingBlock: playing place sound");
				BlockUtils.playSound(SoundAction.PLACE, preBlock);
			}

		}

//		debug(" isPlacingBlock: true");
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
}
