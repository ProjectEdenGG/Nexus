package gg.projecteden.nexus.features.noteblocks;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.noteblock.NoteBlockData;
import gg.projecteden.nexus.models.noteblock.NoteBlockTracker;
import gg.projecteden.nexus.models.noteblock.NoteBlockTrackerService;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import gg.projecteden.utils.UUIDUtils;
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
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NoteBlocksListener implements Listener {
	private static final NoteBlockTrackerService trackerService = new NoteBlockTrackerService();
	private static NoteBlockTracker tracker;

	public NoteBlocksListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(BlockPhysicsEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlock();
		if (!block.getType().equals(Material.NOTE_BLOCK))
			return;

		NoteBlockData data = validateData(block);

		NoteBlock noteBlock = (NoteBlock) block.getBlockData();
		noteBlock.setInstrument(data.getBlockInstrument());
		noteBlock.setNote(new Note(data.getBlockStep()));
		noteBlock.setPowered(false);

		event.getBlock().setBlockData(noteBlock);
	}

	@EventHandler
	public void on(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;

		Block above = event.getBlock().getRelative(BlockFace.UP);
		if (!above.getType().equals(Material.NOTE_BLOCK))
			return;

		validateData(above, true);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlock();
		if (!block.getType().equals(Material.NOTE_BLOCK))
			return;

		Location location = block.getLocation();
		tracker = trackerService.fromWorld(location);
		NoteBlockData data = tracker.get(location);
		if (!data.exists())
			return;

		NoteBlocks.remove(location);
	}


	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.useInteractedBlock() == Result.DENY || event.useItemInHand() == Result.DENY)
			return;

		if (!EquipmentSlot.HAND.equals(event.getHand()))
			return;

		Action action = event.getAction();
		if (!action.equals(Action.RIGHT_CLICK_BLOCK))
			return;

		Player player = event.getPlayer();
		ItemStack itemInHand = event.getItem();
		Block clickedBlock = event.getClickedBlock();
		if (isPlacingNoteBlock(player, itemInHand, clickedBlock, event.getBlockFace())) {
			event.setCancelled(true);
			return;
		}

		boolean sneaking = player.isSneaking();
		if (sneaking && !Nullables.isNullOrAir(itemInHand) && itemInHand.getType().isBlock())
			return;

		if (Nullables.isNullOrAir(clickedBlock) || !clickedBlock.getType().equals(Material.NOTE_BLOCK))
			return;

		event.setCancelled(true);

		Location location = clickedBlock.getLocation();
		NoteBlockData data = validateData(clickedBlock, true);

		NoteBlocks.changePitch(sneaking, location, data);
	}

	// on player interaction or redstone
	@EventHandler
	public void onPlayNote(NotePlayEvent event) {
		if (event.isCancelled())
			return;

		event.setCancelled(true);

		Block block = event.getBlock();
		Location loc = block.getLocation();
		String cooldownType = "noteblock_" + block.getWorld().getName() + "_" + loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
		if (!(new CooldownService().check(UUIDUtils.UUID0, cooldownType, TickTime.TICK)))
			return;

		NoteBlockData data = validateData(block, true);
		data.play(block.getLocation());
	}

	private NoteBlockData validateData(Block block) {
		return validateData(block, false);
	}

	@NotNull
	private NoteBlockData validateData(Block block, boolean reset) {
		Location location = block.getLocation();
		tracker = trackerService.fromWorld(location);
		NoteBlockData data = tracker.get(location);
		if (!data.exists()) {
			Dev.WAKKA.send("No data exists for that location, creating");
			data = NoteBlocks.put(UUIDUtils.UUID0, location);

			if (reset) {
				NoteBlock noteBlock = (NoteBlock) Material.NOTE_BLOCK.createBlockData();
				noteBlock.setInstrument(Instrument.PIANO);
				noteBlock.setNote(new Note(0));
				block.setBlockData(noteBlock);
			}
		}

		return data;
	}

	private boolean isPlacingNoteBlock(Player player, ItemStack itemInHand, Block clickedBlock, BlockFace clickedFace) {
		boolean isHoldingNoteBlock = !Nullables.isNullOrAir(itemInHand) && itemInHand.getType().equals(Material.NOTE_BLOCK);
		boolean clickedABlock = !Nullables.isNullOrAir(clickedBlock);
		if (isHoldingNoteBlock && clickedABlock) {
			Block block = clickedBlock.getRelative(clickedFace);
			if (Nullables.isNullOrAir(block)) {
				if (placedBlock(player, itemInHand, block, clickedBlock)) {
					ItemUtils.subtract(player, itemInHand);
					return true;
				}
			}
		}

		return false;
	}

	private boolean placedBlock(Player player, ItemStack itemInHand, Block placeBlock, Block clickedBlock) {
		Location location = placeBlock.getLocation();
		tracker = trackerService.fromWorld(location);
		NoteBlockData data = tracker.get(location);
		if (data.exists()) {
			PlayerUtils.send(player, "&cA note block should already exist in that location, report this to an admin.");
			PlayerUtils.send(player, "&cAdmin debug: " + StringUtils.getShortLocationString(location) + " | " + data);
			return false;
		}

		// TODO: setup correct block data for custom blocks using CustomModelData
		int modelDate = CustomModelData.of(itemInHand);

		NoteBlock noteBlock = (NoteBlock) Material.NOTE_BLOCK.createBlockData();
		noteBlock.setInstrument(Instrument.PIANO);
		noteBlock.setNote(new Note(0));
		if (BlockUtils.tryPlaceEvent(player, placeBlock, clickedBlock, Material.NOTE_BLOCK, noteBlock)) {
			NoteBlocks.put(player, location);
			return true;
		}

		return false;
	}
}
