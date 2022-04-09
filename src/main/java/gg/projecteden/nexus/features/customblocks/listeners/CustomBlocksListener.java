package gg.projecteden.nexus.features.customblocks.listeners;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customblocks.CustomBlocks;
import gg.projecteden.nexus.features.customblocks.NoteBlockUtils;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock.SoundType;
import gg.projecteden.nexus.features.customblocks.models.ICustomBlock;
import gg.projecteden.nexus.models.noteblock.NoteBlockData;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
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
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CustomBlocksListener implements Listener {

	public CustomBlocksListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onSoundEvent(LocationNamedSoundEvent event) {
		Block block = event.getLocation().getBlock();
		Block below = block.getRelative(BlockFace.DOWN);
		Block source;
		if (CustomBlocks.isCustom(block))
			source = block;
		else if (CustomBlocks.isCustom(below))
			source = below;
		else
			return;

		CustomBlock _customBlock = CustomBlock.fromNoteBlock((NoteBlock) source.getBlockData());
		if (_customBlock == null)
			return;

		String sound = event.getSound().getKey().getKey();
		SoundType type;
		if (sound.endsWith(".step"))
			type = SoundType.STEP;
		else if (sound.endsWith(".hit"))
			type = SoundType.HIT;
		else if (sound.endsWith(".place"))
			type = SoundType.PLACE;
		else if (sound.endsWith(".break"))
			type = SoundType.BREAK;
		else
			return;

		_customBlock.playSound(type, source);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlock();
		if (!CustomBlocks.isCustom(block))
			return;

		CustomBlock _customBlock = CustomBlock.fromNoteBlock(block);
		if (_customBlock == null) {
			return;
		}

		if (_customBlock.equals(CustomBlock.NOTE_BLOCK)) {
			NoteBlockUtils.breakBlock(block.getLocation());
		}

		// change drops
		event.setDropItems(false);
		ICustomBlock customBlock = _customBlock.get();
		Location location = block.getLocation();

		for (ItemStack drop : block.getDrops()) {
			if (drop.getType().equals(Material.NOTE_BLOCK)) {
				if (!GameModeWrapper.of(event.getPlayer()).isCreative())
					location.getWorld().dropItemNaturally(location, customBlock.getItemStack());
			}
		}
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

		ICustomBlock customBlock = _customBlock.get();
		Instrument instrument = customBlock.getNoteBlockInstrument();
		int step = customBlock.getNoteBlockStep();

		// TODO: sideways

		NoteBlock noteBlock = (NoteBlock) Material.NOTE_BLOCK.createBlockData();
		noteBlock.setInstrument(instrument);
		noteBlock.setNote(new Note(step));

		if (!BlockUtils.tryPlaceEvent(player, block, clickedBlock, Material.NOTE_BLOCK, noteBlock, false, new ItemStack(Material.NOTE_BLOCK))) {
			return false;
		}

		if (CustomBlock.NOTE_BLOCK.equals(_customBlock)) {
			NoteBlockUtils.placeBlock(player, block.getLocation());
		}

		_customBlock.playSound(SoundType.PLACE, block);

		ItemUtils.subtract(player, itemInHand);
		return true;
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
