package gg.projecteden.nexus.features.customblocks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.ICustomBlock;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.NMSUtils.SoundType;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import me.lexikiq.event.sound.LocationNamedSoundEvent;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
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
import java.util.stream.Collectors;

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.debug;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class CustomBlocksListener implements Listener {
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
		if (customBlock != null && customBlock.equals(CustomBlock.NOTE_BLOCK))
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

		if (!CustomBlocks.isCustomNoteBlock(event.getBlock()))
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

		if (!item.getType().equals(Material.NOTE_BLOCK))
			return;

		Player player = (Player) event.getWhoClicked();
		Block block = player.getTargetBlockExact(5);
		if (isNullOrAir(block))
			return;

		if (!CustomBlocks.isCustom(block))
			return;

		NoteBlock noteBlock = (NoteBlock) block.getBlockData();
		CustomBlock customBlock = CustomBlock.fromNoteBlock(noteBlock);
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

	// Handles Sound: STEP, PLACE
	@EventHandler
	public void on(LocationNamedSoundEvent event) {
		Block block = event.getLocation().getBlock();
		Block below = block.getRelative(BlockFace.DOWN);
		Block source = null;
		if (CustomBlocks.isCustom(block))
			source = block;
		else if (CustomBlocks.isCustom(below))
			source = below;


		if (source != null) {
			NoteBlock noteBlock = (NoteBlock) source.getBlockData();
			CustomBlock _customBlock = CustomBlock.fromNoteBlock(noteBlock);
			if (_customBlock == null) {
				debug("SoundEvent: CustomBlock == null");
				return;
			}

			SoundType soundType = SoundType.fromSound(event.getSound());
			if (soundType == null)
				return;

			event.setCancelled(true);
			_customBlock.playSound(soundType, source.getLocation());
			return;
		}

		if (event.getSound().getKey().getKey().startsWith("block.wood.")) {
			event.setCancelled(true);
			CustomBlockUtils.playDefaultWoodSounds(event.getSound(), event.getLocation());
		}
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

		CustomBlockUtils.tryPlayDefaultWoodSound(SoundType.FALL, block);
	}


	// Handles Sound: HIT
	@EventHandler
	public void on(PlayerAnimationEvent event) {
		Block block = event.getPlayer().getTargetBlockExact(5);
		if (block == null)
			return;

		CustomBlockUtils.tryPlayDefaultWoodSound(SoundType.HIT, block);
	}

	@EventHandler
	public void on(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlockPlaced();
		Block above = block.getRelative(BlockFace.UP);

		if (CustomBlocks.isCustomNoteBlock(above)) {
			NoteBlock noteBlock = (NoteBlock) above.getBlockData();
			CustomBlockData data = CustomBlockUtils.getData(noteBlock, above.getLocation());
			if (data != null)
				data.getNoteBlockData(above, true);
		}
	}

	// Handles Sound: BREAK
	@EventHandler
	public void on(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlock();
		if (!CustomBlocks.isCustom(block)) {
			CustomBlockUtils.tryPlayDefaultWoodSound(SoundType.BREAK, block);
			return;
		}

		NoteBlock noteBlock = (NoteBlock) block.getBlockData();
		CustomBlock _customBlock = CustomBlock.fromNoteBlock(noteBlock);
		if (_customBlock == null) {
			debug("BreakBlock: CustomBlock == null");
			return;
		}

		Location location = block.getLocation();
		CustomBlockUtils.breakBlockDatabase(location);

		// change drops
		event.setDropItems(false);
		ICustomBlock customBlock = _customBlock.get();

		for (ItemStack drop : block.getDrops()) {
			if (drop.getType().equals(Material.NOTE_BLOCK)) {
				if (!GameModeWrapper.of(event.getPlayer()).isCreative())
					location.getWorld().dropItemNaturally(location, customBlock.getItemStack());
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

		// Place
		if (isSpawningEntity(event, clickedBlock)) {
			return;
		}
		if (isPlacingBlock(event, clickedBlock)) {
			return;
		}

		if (CustomBlocks.isCustom(clickedBlock)) {
			boolean isChangingPitch = isChangingPitch(action, sneaking, itemInHand);
			if (isChangingPitch) {
				event.setCancelled(true);

				if (CustomBlocks.isCustomNoteBlock(clickedBlock)) {
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
		if (material == Material.NOTE_BLOCK) {
			resetBlockData(eventBlock);
			eventBlock.getState().update(true, false);
		}

		Block aboveBlock = eventBlock.getRelative(BlockFace.UP);
		if (aboveBlock.getType().equals(Material.NOTE_BLOCK)) {

			while (aboveBlock.getType() == Material.NOTE_BLOCK) {
				resetBlockData(aboveBlock);

				// Leave this as (true, true) -> (true, false) will crash the server
				aboveBlock.getState().update(true, true);

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
		blocks = blocks.stream().filter(CustomBlocks::isCustom).collect(Collectors.toList());
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
			if (!customBlock.isPistonPushable()) {
				debug("PistonEvent: " + _customBlock.name() + " cannot be moved by pistons");
				return false;
			}

			Location curLoc = block.getLocation().toBlockLocation();
			Location newLoc = block.getRelative(direction).getLocation().toBlockLocation();
			moveBlocks.put(data, new Pair<>(curLoc, newLoc));
		}

		// Move blocks
		CustomBlockUtils.pistonMove(piston, moveBlocks);

		return true;
	}

	private void resetBlockData(Block block) {
		org.bukkit.block.data.type.NoteBlock noteBlock = (org.bukkit.block.data.type.NoteBlock) block.getBlockData();
		Instrument instrument;
		Note note;
		boolean powered = false;

		CustomBlockData data = CustomBlockUtils.getData(noteBlock, block.getLocation());
		if (data == null) {
			return;
		}

		CustomBlock _customBlock = data.getCustomBlock();
		if (_customBlock == null) {
			return;
		}

		BlockFace facing = data.getFacing();

		instrument = _customBlock.getNoteBlockInstrument(facing);
		note = _customBlock.getNoteBlockNote(facing);

		NoteBlockData noteBlockData = data.getNoteBlockData();
		if (data.isNoteBlock()) {
			powered = noteBlock.isPowered();
			noteBlockData.setPowered(powered);
		}

		noteBlock.setInstrument(instrument);
		noteBlock.setNote(note);
		noteBlock.setPowered(powered);
		block.setBlockData(noteBlock, false);
	}

	private boolean isSpawningEntity(PlayerInteractEvent event, Block clickedBlock) {
		Action action = event.getAction();
		if (!action.equals(Action.RIGHT_CLICK_BLOCK)) {
			return false;
		}

		Player player = event.getPlayer();
		BlockFace clickedFace = event.getBlockFace();
		Block inFront = clickedBlock.getRelative(clickedFace);
		boolean isInteractable = clickedBlock.getType().isInteractable() || MaterialTag.INTERACTABLES.isTagged(inFront);
		if (!CustomBlocks.isCustomNoteBlock(clickedBlock)) {
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

	private boolean isPlacingBlock(PlayerInteractEvent event, Block clickedBlock) {
		Action action = event.getAction();
		if (!action.equals(Action.RIGHT_CLICK_BLOCK)) {
			return false;
		}

		Player player = event.getPlayer();
		BlockFace clickedFace = event.getBlockFace();
		Block preBlock = clickedBlock.getRelative(clickedFace);
		boolean clickedCustomBlock = false;
		boolean isInteractable = clickedBlock.getType().isInteractable() || MaterialTag.INTERACTABLES.isTagged(preBlock);
		if (CustomBlocks.isCustom(clickedBlock)) {
			clickedCustomBlock = true;
			if (!CustomBlocks.isCustomNoteBlock(clickedBlock)) {
				isInteractable = false;
			}
		}

		if (!player.isSneaking() && isInteractable) {
			debug("not sneaking & isInteractable");
			return false;
		}

		ItemStack itemInHand = event.getItem();
		if (isNullOrAir(itemInHand)) {
			debug("item in hand is null or air");
			return false;
		}

		Material material = itemInHand.getType();
		boolean isPlacingCustomBlock = false;
		if (material.equals(ICustomBlock.blockMaterial))
			isPlacingCustomBlock = true;
		else if (material.equals(ICustomBlock.itemMaterial)) {
			int modelData = CustomModelData.of(itemInHand);
			if (!CustomBlock.modelDataMap.containsKey(modelData)) {
				debug(" unknown modelData: " + modelData);
				return false;
			} else
				isPlacingCustomBlock = true;
		} else if (!material.equals(Material.REDSTONE_WIRE) && (!material.isBlock() && !material.isSolid())) {
			debug(" not a block: " + material);
			return false;
		}

		if (!isNullOrAir(preBlock)) {
			debug(" preBlock is not air");
			return false;
		}

		if (isPlacingCustomBlock) {
			if (preBlock.getLocation().toCenterLocation().getNearbyLivingEntities(0.5).size() > 0) {
				debug(" entity in way");
				return false;
			}

			CustomBlock _customBlock = CustomBlock.fromItemstack(itemInHand);
			if (_customBlock == null) {
				debug(" customBlock == null");
				return false;
			}

			if (!_customBlock.placeBlock(player, preBlock, clickedBlock, clickedFace, itemInHand)) {
				return false;
			}

			event.setCancelled(true);
		} else {
			if (!clickedCustomBlock)
				return false;

			if (!BlockUtils.tryPlaceEvent(player, preBlock, clickedBlock, material))
				return false;

			debug("CustomBlocks: playing place sound");
			BlockUtils.playSound(SoundType.PLACE, preBlock);
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
		if (data == null || !data.isNoteBlock())
			return;

		NoteBlockData noteBlockData = data.getNoteBlockData(location.getBlock(), true);
		if (noteBlockData == null)
			return;

		// TODO: throw event
		NoteBlockUtils.changePitch(sneaking, location, noteBlockData);
	}
}
