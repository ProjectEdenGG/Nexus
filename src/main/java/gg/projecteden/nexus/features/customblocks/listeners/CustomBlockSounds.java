package gg.projecteden.nexus.features.customblocks.listeners;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.customblocks.CustomBlocks.BlockAction;
import gg.projecteden.nexus.features.customblocks.CustomBlocks.SoundAction;
import gg.projecteden.nexus.features.customblocks.NoteBlockUtils;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.Nullables;
import me.lexikiq.event.sound.LocationNamedSoundEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerAnimationEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomBlockSounds implements Listener {
	private static final Map<Player, BlockAction> playerActionMap = new ConcurrentHashMap<>();

	public CustomBlockSounds() {
		Nexus.registerListener(this);
	}

	public static void placingEntity(Player player) {
		playerActionMap.put(player, BlockAction.UNKNOWN);
	}

	// Handles Sound: PLACE
	public static void placingBlock(Player player) {
		CustomBlockSounds.playerActionMap.put(player, BlockAction.PLACE);
	}

	public static void interacting(Player player) {
		CustomBlockSounds.playerActionMap.put(player, BlockAction.INTERACT);
	}

	@EventHandler
	public void on(BlockDamageEvent event) {
		if (event.isCancelled()) return;

		playerActionMap.put(event.getPlayer(), BlockAction.HIT);
	}

	@EventHandler
	public void on(BlockPlaceEvent event) {
		if (event.isCancelled()) return;

		playerActionMap.put(event.getPlayer(), BlockAction.PLACE);
	}

	@EventHandler
	public void on(NotePlayEvent event) {
		event.setCancelled(true);

		CustomBlock customBlock = CustomBlock.fromBlock(event.getBlock());
		if (CustomBlock.NOTE_BLOCK != customBlock)
			return;

		NoteBlock noteBlock = (NoteBlock) event.getBlock().getBlockData();
		NoteBlockUtils.play(noteBlock, event.getBlock().getLocation(), true);
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
			CustomBlockUtils.tryPlayDefaultSound(SoundAction.HIT, block);
		}
	}

	// Handles Sound: BREAK
	@EventHandler
	public void on(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		playerActionMap.put(player, BlockAction.BREAK);

		Block brokenBlock = event.getBlock();
		CustomBlock brokenCustomBlock = CustomBlock.fromBlock(brokenBlock);
		if (brokenCustomBlock == null)
			CustomBlockUtils.tryPlayDefaultSound(SoundAction.BREAK, brokenBlock);
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


}
