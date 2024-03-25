package gg.projecteden.nexus.features.customblocks;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.customblocks.listeners.CustomBlockListener;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock.CustomBlockType;
import gg.projecteden.nexus.features.customblocks.worldedit.WorldEditListener;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.CustomBlockTracker;
import gg.projecteden.nexus.models.customblock.CustomBlockTrackerService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
	TODO:
		- Release NoteBlock Custom Blocks
		-
		- Bugs:
			- Moving custom blocks with piston is duplicating them sometimes --> disabled piston movement until fixed
			- Note Blocks play twice when powered via repeaters or comparators
		-
		- Cannot Fix:
			- Custom blocks may flash (canceled update of instrument changing) when placing blocks near them (clientside only) --> Titan
			- Players arm will swing on interact w/ custom blocks (clientside only?) --> Titan
 */

/*
	TODO POST RELEASE:
		- Fix piston movements
		- Maybe add more advanced worldedit handling, such as setting directionals, and other "block states"
		- Tripwire implementation:
			- Tripwire blocks are being replaced to cross, if you're standing inside of them when you break them
			- tripwire cross is spawnable, and also spawns paper ?
			- Breaking tripwire needs properly update nearby tripwire crosses to tripwire if suitable, and fix database issue
			- SendBlockChange
			-
			- Misc:
				- Add lotus lilly flower & how to obtain
				- flower + fungus cover -> how to obtain --> maybe make bonemeal spawn it?
				- Make fungus cover 3d?
 */

@Environments(Env.TEST)
public class CustomBlocks extends Feature {
	@Override
	public void onStart() {
		CustomBlock.init();
		new CustomBlockListener();
		janitor();

		WorldEditListener.register();
	}

	@Override
	public void onStop() {
		WorldEditListener.unregister();
	}

	private static void janitor() {
		Tasks.repeat(0, TickTime.MINUTE.x(3), () -> {
			debug("&3CustomBlock Janitor:");
			CustomBlockTrackerService trackerService = new CustomBlockTrackerService();
			for (CustomBlockTracker tracker : new ArrayList<>(trackerService.getAll())) {
				World world = tracker.getWorld();
				if (world == null || world.getLoadedChunks().length == 0)
					continue;

				Map<Location, CustomBlockData> locationMap = tracker.getLocationMap();
				Map<Location, String> forRemoval = new HashMap<>();

				for (Location location : locationMap.keySet()) {
					if (!location.isChunkLoaded())
						continue;

					Block block = location.getBlock();
					if (Nullables.isNullOrAir(block)) {
						forRemoval.put(location, "block is null or air");
						continue;
					}

					if (!CustomBlockType.getBlockMaterials().contains(block.getType())) {
						forRemoval.put(location, "block type is not a handled type");
						continue;
					}

					CustomBlockData data = locationMap.get(location);

					CustomBlock customBlock = CustomBlock.from(location.getBlock());
					if (customBlock == null) {
						forRemoval.put(location, "current block is not CustomBlock");
						continue;
					}

					if (data.getCustomBlock() != customBlock)
						forRemoval.put(location, "dbCustomBlock != blockCustomBlock");
				}

				if (forRemoval.isEmpty()) {
					debug(" &3No entries to clean up in world: &e" + world.getName());
					continue;
				}

				debug("");
				debug(" &3Clearing up &e" + forRemoval.size() + " &3entries in world &e" + world.getName() + "&3...");
				for (Location location : forRemoval.keySet()) {
					tracker.remove(location);

					String locationStr = StringUtils.getShortLocationString(location);
					debug(new JsonBuilder("&3- ").group()
						.next(StringUtils.getJsonLocation("&3&l[&e" + locationStr + "&3&l]", location.toCenterLocation())).hover("Click to teleport").group()
						.next(" &3because &e" + forRemoval.get(location)).group()
					);
				}

				trackerService.save(tracker);
				debug("");
			}
		});
	}

	@Getter
	@Setter
	private static boolean debug = false;

	public static void debug(String message) {
		if (debug)
			List.of(Dev.WAKKA, Dev.GRIFFIN).forEach(dev -> dev.send(message));
	}

	public static void debug(JsonBuilder jsonBuilder) {
		if (debug)
			List.of(Dev.WAKKA, Dev.GRIFFIN).forEach(jsonBuilder::send);
	}

	@AllArgsConstructor
	public enum ReplacedSoundType {
		WOOD("block.wood.", "custom.block.wood."),
		STONE("block.stone.", "custom.block.stone."),
		;

		final String defaultSound;
		final String replacedSound;

		public boolean matches(String value) {
			return value.startsWith(defaultSound) || value.startsWith(replacedSound);
		}

		public String replace(String value) {
			if (value.startsWith(defaultSound)) {
				String ending = value.replaceFirst(defaultSound, "");
				return replacedSound + ending;
			}

			return value;
		}

		public static @NonNull String replaceMatching(String soundKey) {
			for (ReplacedSoundType replacedSoundType : values()) {
				if (replacedSoundType.matches(soundKey)) {
					return replacedSoundType.replace(soundKey);
				}
			}

			return soundKey;
		}

		public static @Nullable ReplacedSoundType fromSound(String soundKey) {
			for (ReplacedSoundType replacedSoundType : values()) {
				if (replacedSoundType.matches(soundKey))
					return replacedSoundType;
			}

			return null;
		}

		public static @Nullable ReplacedSoundType fromSound(Sound sound) {
			return fromSound(sound.key().value());
		}

		public static @Nullable ReplacedSoundType fromSound(Key sound) {
			return fromSound(sound.value());
		}

		public static @Nullable ReplacedSoundType fromSound(net.kyori.adventure.sound.Sound sound) {
			return fromSound(sound.name());
		}
	}

	@AllArgsConstructor
	public enum SoundAction {
		BREAK(1.0),
		STEP(0.2),
		PLACE(1.0),
		HIT(0.5),
		FALL(1.0),
		;

		@Getter
		private final double volume;

		public String getCustomSound(ReplacedSoundType soundType) {
			return "custom.block." + soundType.name().toLowerCase() + "." + this.name().toLowerCase();
		}

		public static @Nullable CustomBlocks.SoundAction fromSound(String soundKey) {
			if (soundKey.endsWith(".step"))
				return SoundAction.STEP;
			else if (soundKey.endsWith(".hit"))
				return SoundAction.HIT;
			else if (soundKey.endsWith(".place"))
				return SoundAction.PLACE;
			else if (soundKey.endsWith(".break"))
				return SoundAction.BREAK;
			else if (soundKey.endsWith(".fall"))
				return SoundAction.FALL;

			return null;
		}

		public static @Nullable CustomBlocks.SoundAction fromSound(Key sound) {
			return fromSound(sound.value());
		}

		public static @Nullable CustomBlocks.SoundAction fromSound(Sound sound) {
			return fromSound(sound.getKey());
		}

		public static @Nullable CustomBlocks.SoundAction fromSound(net.kyori.adventure.sound.Sound sound) {
			return fromSound(sound.name());
		}
	}

	public enum BlockAction {
		UNKNOWN,
		INTERACT,
		HIT,
		BREAK,
		PLACE,
		FALL,
	}
}
