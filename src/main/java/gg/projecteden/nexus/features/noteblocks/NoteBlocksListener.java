package gg.projecteden.nexus.features.noteblocks;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.noteblock.NoteBlockData;
import gg.projecteden.nexus.models.noteblock.NoteBlockTracker;
import gg.projecteden.nexus.models.noteblock.NoteBlockTrackerService;
import gg.projecteden.nexus.utils.Nullables;
import org.apache.commons.collections4.SetUtils;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.NotePlayEvent;

import java.util.Set;

import static gg.projecteden.nexus.features.noteblocks.NoteBlocks.debug;

public class NoteBlocksListener implements Listener {
	private static final NoteBlockTrackerService trackerService = new NoteBlockTrackerService();
	private static NoteBlockTracker tracker;
	private static final Set<BlockFace> cardinalFaces = Set.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);
	private static final Set<BlockFace> cornerFaces = Set.of(BlockFace.NORTH_WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_WEST, BlockFace.SOUTH_EAST);
	private static final Set<BlockFace> neighborFaces = SetUtils.union(cardinalFaces, cornerFaces);

	public NoteBlocksListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;

		Block above = event.getBlock().getRelative(BlockFace.UP);
		if (CustomBlocks.isCustomNoteBlock(above))
			NoteBlocks.getData(above, true);
	}

//	@EventHandler
//	public void on(BlockRedstoneEvent event){
//		if(event.getOldCurrent() != 0 || event.getNewCurrent() == 0)
//			return;
//
//		Block redstone = event.getBlock();
//		debug("BlockRedstoneEvent: Material=" + redstone.getType());
//
//		for (BlockFace face : neighborFaces) {
//			Block block = redstone.getRelative(face);
//			if(block.equals(redstone))
//				continue;
//
//			if(Nullables.isNullOrAir(block))
//				continue;
//
//			if(!(block.getType().equals(Material.NOTE_BLOCK)))
//				continue;
//
//			NoteBlock noteBlock = (NoteBlock) block.getBlockData();
//			if(!noteBlock.getInstrument().equals(Instrument.PIANO) || noteBlock.getNote().getId() != 0)
//				continue;
//
//			debug("Cardinal NoteBlock, playing note");
//			callNotePlayEvent(block);
//		}
//	}

	@EventHandler
	public void on(BlockPhysicsEvent event) {
		if (event.isCancelled())
			return;

		Block eventBlock = event.getBlock();
		if (!CustomBlocks.isCustomNoteBlock(eventBlock))
			return;

		NoteBlock noteBlock = (NoteBlock) eventBlock.getBlockData();
		debug("PhysicsEvent: " + noteBlock);

		if (noteBlock.isPowered()) {
			debug(" Powered:");
			if (eventBlock.isBlockPowered())
				debug("  Block");
			if (eventBlock.isBlockIndirectlyPowered())
				debug("  Indirectly Block");
			if (noteBlock.isPowered())
				debug("  NoteBlock");

			// double check
			debug(" Double Checking:");
			if (isPowered(eventBlock)) {
				NoteBlocks.play(eventBlock, NoteBlocks.getData(eventBlock));
			}
		}

		NoteBlockData data = NoteBlocks.getData(eventBlock);
		data.setPowered(noteBlock.isPowered());

		// reset eventBlock
		noteBlock.setInstrument(data.getBlockInstrument());
		noteBlock.setNote(new Note(data.getBlockStep()));
		noteBlock.setPowered(false);
		event.getBlock().setBlockData(noteBlock, false);
	}

	private boolean isPowered(Block eventBlock) {
		for (BlockFace face : cardinalFaces) {
			Block block = eventBlock.getRelative(face);
			if (Nullables.isNullOrAir(block))
				continue;

			if (block.isBlockPowered()) {
				debug("  Block Powered");
				return true;
			}

			if (block.getType().equals(Material.REDSTONE_BLOCK)) {
				debug("  Redstone Block");
				return true;
			}

			if (block.getBlockData() instanceof Powerable powerable) {
				if (powerable.isPowered()) {
					debug("  Is Powered");
					return true;
				}
			}
		}

//		for (BlockFace face : cornerFaces) {
//			Block block = eventBlock.getRelative(face);
//			if(Nullables.isNullOrAir(block))
//				continue;
//		}

		return false;
	}

	// on player interaction or redstone
	@EventHandler
	public void onPlayNote(NotePlayEvent event) {
//		if (event.isCancelled())
//			return;

		event.setCancelled(true);

//		Block block = event.getBlock();
//		Block above = block.getRelative(BlockFace.UP);

//		String version = Bukkit.getMinecraftVersion();
//		if(version.matches("1.19[.]?[0-9]*")) {
//			if(MaterialTag.WOOL.isTagged(above) || MaterialTag.WOOL_CARPET.isTagged(above))
//			return;
//		} else if(!Nullables.isNullOrAir(above))
//			return;

//		NoteBlockData data = validateData(block, true);

//		Location loc = block.getLocation();
//		String cooldownType = "noteblock_" + block.getWorld().getName() + "_" + loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
//		if (!(new CooldownService().check(UUIDUtils.UUID0, cooldownType, TickTime.TICK))) {
//			debug("NotePlayEvent: on cooldown, cancelling");
//			return;
//		}

//		debug("NotePlayEvent: Powered=" + data.isPowered() + ", playing note");
//		data.play(block.getLocation());
	}


}
