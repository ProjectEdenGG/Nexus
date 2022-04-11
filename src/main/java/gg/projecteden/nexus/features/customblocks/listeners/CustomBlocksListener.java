package gg.projecteden.nexus.features.customblocks.listeners;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.customblocks.CustomBlocks;
import gg.projecteden.nexus.features.customblocks.NoteBlockUtils;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock.SoundType;
import gg.projecteden.nexus.features.customblocks.models.ICustomBlock;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.nexus.utils.GameModeWrapper;
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
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.debug;
import static gg.projecteden.nexus.features.customblocks.CustomBlocks.isCustomNoteBlock;

public class CustomBlocksListener implements Listener {
	public CustomBlocksListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onCreativePickBlock(InventoryCreativeEvent event) {
		SlotType slotType = event.getSlotType();
		if (!slotType.equals(SlotType.QUICKBAR))
			return;

		ItemStack item = event.getCursor();
		if (Nullables.isNullOrAir(item))
			return;

		if (!item.getType().equals(Material.NOTE_BLOCK))
			return;

		Player player = (Player) event.getWhoClicked();
		Block block = player.getTargetBlockExact(5);
		if (Nullables.isNullOrAir(block))
			return;

		if (!CustomBlocks.isCustom(block) || CustomBlocks.isCustomNoteBlock(block))
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

	@EventHandler
	public void onSoundEvent(LocationNamedSoundEvent event) {
		if (true) // TODO: wait until SoundEvents are fixed
			return;

		Block block = event.getLocation().getBlock();
		Block below = block.getRelative(BlockFace.DOWN);
		Block source;
		if (CustomBlocks.isCustom(block))
			source = block;
		else if (CustomBlocks.isCustom(below))
			source = below;
		else
			return;

		NoteBlock noteBlock = (NoteBlock) source.getBlockData();
		CustomBlock _customBlock = CustomBlock.fromNoteBlock(noteBlock);
		if (_customBlock == null) {
			debug("SoundEvent: CustomBlock == null");
			return;
		}

		debug("SoundEvent: " + _customBlock.name() + " - " + event.getSound().getKey().getKey());
		SoundType soundType = _customBlock.getSoundType(event.getSound());
		if (soundType == null)
			return;

		debug(" soundType: " + soundType + ", playing sound...");
		event.setCancelled(true);
		_customBlock.playSound(soundType, source);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlock();
		if (!CustomBlocks.isCustom(block))
			return;

		NoteBlock noteBlock = (NoteBlock) block.getBlockData();
		CustomBlock _customBlock = CustomBlock.fromNoteBlock(noteBlock);
		if (_customBlock == null) {
			debug("BreakBlock: CustomBlock == null");
			return;
		}

		Location location = block.getLocation();
		CustomBlockUtils.breakBlock(_customBlock, location);
		if (!_customBlock.equals(CustomBlock.NOTE_BLOCK)) {
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
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPhysics(BlockPhysicsEvent event) {
		Block eventBlock = event.getBlock();
		Material material = eventBlock.getType();
		if (material == Material.NOTE_BLOCK) {
			reset(eventBlock);
			eventBlock.getState().update(true, false);
		}

		Block aboveBlock = eventBlock.getRelative(BlockFace.UP);
		if (aboveBlock.getType().equals(Material.NOTE_BLOCK)) {

			while (aboveBlock.getType() == Material.NOTE_BLOCK) {
				reset(aboveBlock);
				aboveBlock.getState().update(true, false);
				aboveBlock = aboveBlock.getRelative(BlockFace.UP);
			}
		}
	}

	private void reset(Block block) {
		org.bukkit.block.data.type.NoteBlock noteBlock = (org.bukkit.block.data.type.NoteBlock) block.getBlockData();
		Instrument instrument;
		Note note;
		boolean powered = false;

		CustomBlockData customBlockData = CustomBlockUtils.getData(noteBlock, block.getLocation(), false);
		if (customBlockData == null) {
			return;
		}

		instrument = customBlockData.getBlockInstrument();
		note = customBlockData.getBlockNote();

		if (isCustomNoteBlock(block)) {
			NoteBlockData noteBlockData = NoteBlockUtils.getData(block);
			powered = noteBlock.isPowered();
			noteBlockData.setPowered(powered);
			debug(" Set powered: " + powered);
		}

		noteBlock.setInstrument(instrument);
		noteBlock.setNote(note);
		noteBlock.setPowered(powered);
		block.setBlockData(noteBlock, false);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.useInteractedBlock() == Result.DENY || event.useItemInHand() == Result.DENY)
			return;

		if (!EquipmentSlot.HAND.equals(event.getHand()))
			return;

		Action action = event.getAction();
		Player player = event.getPlayer();
		ItemStack itemInHand = event.getItem();
		boolean sneaking = player.isSneaking();
		Block clickedBlock = event.getClickedBlock();

		if (Nullables.isNullOrAir(clickedBlock))
			return;

		// Place
		if (isPlacingCustomBlock(player, action, itemInHand, clickedBlock, event.getBlockFace())) {
			event.setCancelled(true);
			return;
		}

		if (CustomBlocks.isCustom(clickedBlock)) {
			boolean isChangingPitch = isChangingPitch(action, sneaking, itemInHand);

			if (CustomBlocks.isCustomNoteBlock(clickedBlock)) {
				// Interact
				if (isInteractingNoteBlock(clickedBlock, action)) {
					return;
				}

				// Change Pitch
				if (isChangingPitch) {
					event.setCancelled(true);
					changePitch(clickedBlock, sneaking);
				}
			} else {
				if (isChangingPitch)
					event.setCancelled(true);

			}
		}
	}

	private boolean isPlacingCustomBlock(Player player, Action action, ItemStack itemInHand, Block clickedBlock, BlockFace clickedFace) {
		if (!action.equals(Action.RIGHT_CLICK_BLOCK))
			return false;

		if (Nullables.isNullOrAir(itemInHand))
			return false;

		Material material = itemInHand.getType();
		if (!material.equals(Material.NOTE_BLOCK) && !material.equals(Material.PAPER)) {
			return false;
		}

		Block block = clickedBlock.getRelative(clickedFace);
		if (!Nullables.isNullOrAir(block)) {
			return false;
		}

		if (!player.isSneaking() && block.getType().isInteractable()) {
			return false;
		}

		CustomBlock _customBlock = CustomBlock.fromItemstack(itemInHand);
		if (_customBlock == null) {
			return false;
		}

		return _customBlock.placeBlock(player, block, clickedBlock, clickedFace, itemInHand);
	}

	private boolean isInteractingNoteBlock(Block clickedBlock, Action action) {
		if (!action.equals(Action.LEFT_CLICK_BLOCK))
			return false;

		NoteBlockUtils.play(clickedBlock, true);
		return true;
	}

	private boolean isChangingPitch(Action action, boolean sneaking, ItemStack itemInHand) {
		if (!action.equals(Action.RIGHT_CLICK_BLOCK))
			return false;

		return !sneaking || Nullables.isNullOrAir(itemInHand) || !itemInHand.getType().isBlock();
	}

	private void changePitch(Block clickedBlock, boolean sneaking) {
		Location location = clickedBlock.getLocation();
		NoteBlockData data = NoteBlockUtils.getData(clickedBlock, true);

		NoteBlockUtils.changePitch(sneaking, location, data);
	}
}
