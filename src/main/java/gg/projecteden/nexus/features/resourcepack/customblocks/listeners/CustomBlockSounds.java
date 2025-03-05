package gg.projecteden.nexus.features.resourcepack.customblocks.listeners;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocks.BlockAction;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocks.ReplacedSoundType;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocks.SoundAction;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocksLang;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.BlockUtils;
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

	@EventHandler
	public void on(BlockPlaceEvent event) {
		if (event.isCancelled()) return;

		updateAction(event.getPlayer(), BlockAction.PLACE);
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

		CustomBlocksLang.debug("\n&d&lEntityDamageEvent:");

		updateAction(player, BlockAction.FALL);
		tryPlaySound(player, SoundAction.FALL, block);
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
			CustomBlocksLang.debug("\n&d&lPlayerAnimationEvent:");
			tryPlaySound(player, SoundAction.HIT, block);
		}
	}

	// Handles Sound: BREAK
	@EventHandler
	public void on(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		updateAction(event.getPlayer(), BlockAction.BREAK);

		Block brokenBlock = event.getBlock();
		CustomBlock brokenCustomBlock = CustomBlock.from(brokenBlock);
		if (brokenCustomBlock == null) {
			CustomBlocksLang.debug("\n&d&lBlockBreakEvent:");
			tryPlaySound(event.getPlayer(), SoundAction.BREAK, brokenBlock);
		}
	}

	// Handles Sound: STEP
	@EventHandler
	public void on(SoundEvent event) {
		try {
			Block block = event.getEmitter().getLocation().getBlock();
			Block below = block.getRelative(BlockFace.DOWN);
			Block source = null;

			CustomBlock _customBlock = CustomBlock.from(block);
			if (_customBlock != null)
				source = block;
			else {
				_customBlock = CustomBlock.from(below);
				if (_customBlock != null)
					source = below;
			}

			net.kyori.adventure.sound.Sound sound = event.getSound();
			SoundAction soundAction = SoundAction.fromSound(sound);
			if (soundAction == null)
				return;

			if (!Nullables.isNullOrAir(source)) {
				if (soundAction != SoundAction.STEP)
					return;

				event.setCancelled(true);
				_customBlock.playSound(null, soundAction, source.getLocation());
				return;
			}

			ReplacedSoundType soundType = ReplacedSoundType.fromSound(sound.name().value());
			if (soundType == null)
				return;

			CustomBlocksLang.debug("\n&d&lSoundEvent:");
			if (tryPlaySound(null, soundAction, soundAction.getCustomSound(soundType), event.getEmitter().getLocation()))
				event.setCancelled(true);

		} catch (Exception ignored) {}
	}

	public static void tryPlaySound(Player player, SoundAction soundAction, Block block) {
		Sound defaultSound = NMSUtils.getSound(soundAction, block);
		CustomBlocksLang.debug("&b- Try play sound: action = " + StringUtils.camelCase(soundAction) + ", block = " + StringUtils.camelCase(block.getType()));
		if (defaultSound == null) {
			CustomBlocksLang.debug("&c<- couldn't find default sound");
			return;
		}

		CustomBlocksLang.debug("&e- default sound = " + defaultSound.getKey().getKey());

		CustomBlock customBlock = CustomBlock.from(block);
		ReplacedSoundType replacedSoundType = ReplacedSoundType.fromSound(defaultSound);
		if (replacedSoundType == null && customBlock == null) {
			CustomBlocksLang.debug("&a<- playing default sound");
			return; // already handled by CustomBlockNMSUtils#placeVanillaBlock
		}

		// get custom block sound
		String soundKey = defaultSound.getKey().getKey();
		if (customBlock != null)
			soundKey = customBlock.getSound(soundAction);

		tryPlaySound(player, soundAction, soundKey, block.getLocation());
	}

	public static boolean tryPlaySound(Player player, SoundAction soundAction, String soundKey, Location location) {
		soundKey = ReplacedSoundType.replaceMatching(soundKey);

		SoundBuilder soundBuilder = new SoundBuilder(soundKey)
			.location(location)
			.volume(soundAction.getVolume())
			.pitch(soundAction.getPitch());

		String locationStr = location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
		String cooldownType = "CustomSound_" + soundAction + "_" + locationStr;
		if (!(new CooldownService().check(UUIDUtils.UUID0, cooldownType, TickTime.TICK.x(3))))
			return false;

		CustomBlocksLang.debug(player, "&a- playing: " + soundKey);
		BlockUtils.playSound(soundBuilder);
		return true;
	}

}
