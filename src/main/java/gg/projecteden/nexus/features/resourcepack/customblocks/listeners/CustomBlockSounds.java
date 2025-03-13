package gg.projecteden.nexus.features.resourcepack.customblocks.listeners;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocks.BlockAction;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocks.ReplacedSoundType;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocks.SoundAction;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.Debug.DebugType;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.parchment.event.sound.SoundEvent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

@SuppressWarnings("removal")
public class CustomBlockSounds implements Listener {
	private static final Map<Player, BlockAction> playerActionMap = new ConcurrentHashMap<>();

	public CustomBlockSounds() {
		Nexus.registerListener(this);
	}

	public static void updateAction(Player player, BlockAction action) {
		playerActionMap.put(player, action);
	}

	@EventHandler
	public void on(BlockDamageEvent event) {
		if (event.isCancelled()) return;

		updateAction(event.getPlayer(), BlockAction.HIT);
	}

	// Handles Sound: PLACE
	@EventHandler
	public void on(BlockPlaceEvent event) {
		if (event.isCancelled()) return;

		Player player = event.getPlayer();
		updateAction(player, BlockAction.PLACE);

		Block placedBlock = event.getBlock();
		if (Nullables.isNullOrAir(placedBlock))
			return;

		if (CustomBlock.from(placedBlock) == null) {
			CustomBlockUtils.debug(player, DebugType.CUSTOM_BLOCKS_SOUNDS, "&d&lBlockPlaceEvent:", true);
			tryPlaySound(player, SoundAction.PLACE, placedBlock);
			CustomBlockUtils.debug(player, DebugType.CUSTOM_BLOCKS_SOUNDS, "&d<- done, end");
		}
	}

	@EventHandler // Handle this via Custom Blocks
	public void on(NotePlayEvent event) {
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

		CustomBlockUtils.debug(player, "&d&lEntityDamageEvent:", true);
		updateAction(player, BlockAction.FALL);
		tryPlaySound(player, SoundAction.FALL, block);
		CustomBlockUtils.debug(player, "&d<- done, end");
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
//			CustomBlockUtils.debug(player, "&d&lPlayerAnimationEvent:", true);
			tryPlaySound(player, SoundAction.HIT, block);
//			CustomBlockUtils.debug(player, "&d<- done, end");
		}
	}

	// Handles Sound: BREAK
	@EventHandler
	public void on(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		updateAction(player, BlockAction.BREAK);

		Block brokenBlock = event.getBlock();
		if (Nullables.isNullOrAir(brokenBlock))
			return;

		if (CustomBlock.from(brokenBlock) == null) {
			CustomBlockUtils.debug(player, DebugType.CUSTOM_BLOCKS_SOUNDS, "&d&lBlockBreakEvent: Sounds", true);
			tryPlaySound(player, SoundAction.BREAK, brokenBlock);
			CustomBlockUtils.debug(player, DebugType.CUSTOM_BLOCKS_SOUNDS, "&d<- done, end");
		}
	}

	// Handles Sound: STEP
	@EventHandler
	public void on(SoundEvent event) {
		net.kyori.adventure.sound.Sound sound = event.getSound();
		SoundAction soundAction = SoundAction.fromSound(sound);
		if (soundAction != SoundAction.STEP)
			return;

		Block block = event.getEmitter().getLocation().getBlock();
		Block below = block.getRelative(BlockFace.DOWN);
		Block source = null;

		// Custom Block
		CustomBlock customBlock = CustomBlock.from(block);
		if (customBlock != null)
			source = block;
		else {
			customBlock = CustomBlock.from(below);
			if (customBlock != null)
				source = below;
		}

		if (customBlock != null) {
			event.setCancelled(true);
			customBlock.playSound(null, soundAction, source.getLocation());
			return;
		}

		// Vanilla Block
		ReplacedSoundType soundType = ReplacedSoundType.fromSound(sound.name().value());
		if (soundType == null)
			return;

		if (tryPlaySound(null, soundAction, soundAction.getCustomSound(soundType), event.getEmitter().getLocation()))
			event.setCancelled(true);
	}

	public static void tryPlaySound(Player source, SoundAction soundAction, Block block) {
		Sound defaultSound = NMSUtils.getSound(soundAction, block);
		CustomBlockUtils.debug(source, DebugType.CUSTOM_BLOCKS_SOUNDS, "&b- try play sound: action = " + StringUtils.camelCase(soundAction) + ", block = " + StringUtils.camelCase(block.getType()));
		if (defaultSound == null) {
			CustomBlockUtils.debug(source, DebugType.CUSTOM_BLOCKS_SOUNDS, "&c<- couldn't find default sound");
			return;
		}

		CustomBlockUtils.debug(source, DebugType.CUSTOM_BLOCKS_SOUNDS, "&e- default sound = " + defaultSound.getKey().getKey());

		CustomBlock customBlock = CustomBlock.from(block);
		ReplacedSoundType replacedSoundType = ReplacedSoundType.fromSound(defaultSound);
		if (replacedSoundType == null && customBlock == null) {
			CustomBlockUtils.debug(source, DebugType.CUSTOM_BLOCKS_SOUNDS, "&a<- playing default sound");
			return; // already handled by CustomBlockNMSUtils#placeVanillaBlock
		}

		// get custom block sound
		String soundKey = defaultSound.getKey().getKey();
		if (customBlock != null)
			soundKey = customBlock.getSound(soundAction);

		tryPlaySound(source, soundAction, soundKey, block.getLocation());
	}

	public static boolean tryPlaySound(Player source, SoundAction soundAction, String soundKey, Location location) {
		boolean silent = source != null && Vanish.isVanished(source);
		soundKey = ReplacedSoundType.replaceMatching(soundKey);

		SoundBuilder soundBuilder = new SoundBuilder(soundKey)
			.location(location)
			.volume(soundAction.getVolume())
			.pitch(soundAction.getPitch());

		if (silent)
			soundBuilder.receiver(source);

		String locationStr = location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
		String cooldownType = "CustomSound_" + soundAction + "_" + locationStr;
		if (!(new CooldownService().check(UUIDUtils.UUID0, cooldownType, TickTime.TICK.x(3))))
			return false;

		CustomBlockUtils.debug(source, DebugType.CUSTOM_BLOCKS_SOUNDS, "&a<- action = " + StringUtils.camelCase(soundAction) + " | key = " + soundKey);
		BlockUtils.playSound(soundBuilder);
		return true;
	}

}
