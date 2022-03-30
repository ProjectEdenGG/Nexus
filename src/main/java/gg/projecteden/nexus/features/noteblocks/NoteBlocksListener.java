package gg.projecteden.nexus.features.noteblocks;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.noteblock.NoteBlockData;
import gg.projecteden.nexus.models.noteblock.NoteBlockTracker;
import gg.projecteden.nexus.models.noteblock.NoteBlockTrackerService;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/*
	TODO:
		Fix: Note block BlockData being changed when either:
			- its being activated via redstone
			- a vanilla instrument block is placed underneath it
 */
public class NoteBlocksListener implements Listener {
	private static final NoteBlockTrackerService trackerService = new NoteBlockTrackerService();
	private static NoteBlockTracker tracker;

	public NoteBlocksListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlockPlaced();
		if (!block.getType().equals(Material.NOTE_BLOCK))
			return;

		Location location = block.getLocation();
		tracker = trackerService.get(location);
		Player player = event.getPlayer();
		NoteBlockData data = tracker.get(location);
		if (data.exists()) {
			PlayerUtils.send(player, "&cA note block should already exist in that location, report this to an admin.");
			PlayerUtils.send(player, "&cAdmin debug: " + StringUtils.getShortLocationString(location) + " | " + data);
			return;
		}

		NoteBlocks.place(player, block);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlock();
		if (!block.getType().equals(Material.NOTE_BLOCK))
			return;

		Location location = block.getLocation();
		tracker = trackerService.get(location);
		NoteBlockData data = tracker.get(location);
		if (!data.exists())
			return;

		NoteBlocks.remove(location);
	}


	@EventHandler
	public void onChangePitch(PlayerInteractEvent event) {
		if (event.useInteractedBlock() == Result.DENY || event.useItemInHand() == Result.DENY)
			return;

		if (!EquipmentSlot.HAND.equals(event.getHand()))
			return;

		ItemStack itemInHand = event.getItem();
		Player player = event.getPlayer();
		boolean sneaking = player.isSneaking();
		if (!Nullables.isNullOrAir(itemInHand) && itemInHand.getType().isBlock() && sneaking)
			return;

		Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block) || !block.getType().equals(Material.NOTE_BLOCK))
			return;

		Action action = event.getAction();
		if (!action.equals(Action.RIGHT_CLICK_BLOCK) && !(sneaking && action.equals(Action.LEFT_CLICK_BLOCK)))
			return;

		Location location = block.getLocation();
		tracker = trackerService.get(location);
		NoteBlockData data = tracker.get(location);
		if (!data.exists()) {
			PlayerUtils.send(player, "&cError: No note blocks should exist at that location");
			return;
		}

		event.setCancelled(true);

		NoteBlocks.changePitch(player.isSneaking(), action, location);
	}

	// on player interaction or redstone
	@EventHandler
	public void onPlayNote(NotePlayEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlock();
		Location location = block.getLocation();

		NoteBlockData data = tracker.get(location);
		if (!data.exists())
			return;

		event.setCancelled(true);
		data.play(location);
	}
}
