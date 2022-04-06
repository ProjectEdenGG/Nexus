package gg.projecteden.nexus.features.noteblocks;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.noteblock.NoteBlockData;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
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

import static gg.projecteden.nexus.features.noteblocks.NoteBlocks.debug;

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

		ICustomBlock customBlock = _customBlock.get();
		String sound = event.getSound().getKey().getKey();
		if (event.getPlayer() != null)
			event.getPlayer().sendMessage("SoundEvent: " + customBlock.getName() + " - " + sound);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlock();
		if (!CustomBlocks.isCustom(block))
			return;

		debug("BreakBlockEvent:");

		CustomBlock _customBlock = CustomBlock.fromNoteBlock(block);
		if (_customBlock == null) {
			return;
		}

		if (_customBlock.equals(CustomBlock.NOTE_BLOCK)) {
			debug("  breaking custom note block");
			NoteBlocks.breakBlock(block.getLocation());
		} else {
			debug("  breaking custom block");
		}

		// change drops
		event.setDropItems(false);
		ICustomBlock customBlock = _customBlock.get();
		Location location = block.getLocation();

		for (ItemStack drop : block.getDrops()) {
			if (drop.getType().equals(Material.NOTE_BLOCK)) {
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

		debug("InteractEvent:");

		// Place
		if (isPlacingCustomBlock(player, action, itemInHand, clickedBlock, event.getBlockFace())) {
			event.setCancelled(true);
			debug(" placing custom block, cancelling");
			return;
		}

		if (CustomBlocks.isCustom(clickedBlock)) {
			debug(" is custom block");
			boolean isChangingPitch = isChangingPitch(action, sneaking, itemInHand);

			if (CustomBlocks.isCustomNoteBlock(clickedBlock)) {
				debug(" is real note block");
				// Interact
				if (isInteractingNoteBlock(clickedBlock, action)) {
					debug("  interacting");
					return;
				}

				// Change Pitch
				if (isChangingPitch) {
					debug("  changing pitch, cancelling");
					event.setCancelled(true);
					changePitch(clickedBlock, sneaking);
				}
			} else {
				if (isChangingPitch) {
					debug("  cancelling change pitch, cancelling");
					event.setCancelled(true);
					return;
				}

				// TODO?
			}
		}
	}

	private boolean isPlacingCustomBlock(Player player, Action action, ItemStack itemInHand, Block clickedBlock, BlockFace clickedFace) {
		if (!action.equals(Action.RIGHT_CLICK_BLOCK))
			return false;

		if (Nullables.isNullOrAir(itemInHand) || !itemInHand.getType().equals(Material.NOTE_BLOCK))
			return false;

		Block block = clickedBlock.getRelative(clickedFace);
		if (!Nullables.isNullOrAir(block))
			return false;

		CustomBlock _customBlock = CustomBlock.fromItemstack(itemInHand);
		if (_customBlock == null)
			return false;

		ICustomBlock customBlock = _customBlock.get();
		NoteBlock noteBlock = (NoteBlock) Material.NOTE_BLOCK.createBlockData();
		Instrument instrument = customBlock.getNoteBlockInstrument();
		int step = customBlock.getNoteBlockStep();

		// TODO: sideways

		Dev.WAKKA.send("Placing Custom Block: " + customBlock + " | " + instrument + " | " + step);

		noteBlock.setInstrument(instrument);
		noteBlock.setNote(new Note(step));

		if (!BlockUtils.tryPlaceEvent(player, block, clickedBlock, Material.NOTE_BLOCK, noteBlock)) {
			Dev.WAKKA.send("tryPlaceEvent = false");
			return false;
		}

		if (CustomBlock.NOTE_BLOCK.equals(_customBlock)) {
			NoteBlocks.placeBlock(player, block.getLocation());
		}

		ItemUtils.subtract(player, itemInHand);
		return true;
	}

	private boolean isInteractingNoteBlock(Block clickedBlock, Action action) {
		if (!action.equals(Action.LEFT_CLICK_BLOCK))
			return false;

		NoteBlocks.play(clickedBlock);
		return true;
	}

	private boolean isChangingPitch(Action action, boolean sneaking, ItemStack itemInHand) {
		if (!action.equals(Action.RIGHT_CLICK_BLOCK))
			return false;

		return !sneaking || Nullables.isNullOrAir(itemInHand) || !itemInHand.getType().isBlock();
	}

	private void changePitch(Block clickedBlock, boolean sneaking) {
		Location location = clickedBlock.getLocation();
		NoteBlockData data = NoteBlocks.getData(clickedBlock, true);

		NoteBlocks.changePitch(sneaking, location, data);
	}
}
