package gg.projecteden.nexus.features.customblocks;

import com.sk89q.worldedit.WorldEdit;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.customblocks.listeners.CustomBlockListener;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock.CustomBlockType;
import gg.projecteden.nexus.features.customblocks.worldedit.CustomBlockParser;
import gg.projecteden.nexus.features.customblocks.worldedit.WorldEditListener;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.CustomBlockTracker;
import gg.projecteden.nexus.models.customblock.CustomBlockTrackerService;
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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
	TODO:
		- WorldEdit handling
		- Auto-Tool support
		- BlockPhysicsEvent
		- Tripwire implementation:
			- Placing string needs properly update nearby tripwire to tripwire cross if suitable
			- If placed next to hook, and both hooks exist, attach the hooks
		- 6 tick delay between block break
		- Misc:
			- Add lotus lilly flower & how to obtain
			- flower + fungus cover -> how to obtain
			- Make fungus cover 3d?
		- //
		- Cannot Fix:
			- Custom blocks may flash when placing blocks near them (clientside only) --> Titan
			- Players arm will swing on interact w/ custom blocks (clientside only?) --> Titan
 */

@Environments(Env.TEST)
public class CustomBlocks extends Feature {
	@Override
	public void onStart() {
		new CustomBlockListener();
		janitor();

		WorldEditListener.register();
		WorldEdit.getInstance().getBlockFactory().register(new CustomBlockParser(WorldEdit.getInstance()));
	}

	@Override
	public void onStop() {
		WorldEditListener.unregister();
	}

	private void janitor() {
		Tasks.repeat(0, TickTime.MINUTE.x(3), () -> {
			CustomBlockTrackerService trackerService = new CustomBlockTrackerService();
			for (CustomBlockTracker tracker : new ArrayList<>(trackerService.getAll())) {
				World world = tracker.getWorld();
				if (world == null || world.getLoadedChunks().length == 0)
					continue;

				Map<Location, CustomBlockData> locationMap = tracker.getLocationMap();
				Set<Location> forRemoval = new HashSet<>();

				checkLocation:
				for (Location location : locationMap.keySet()) {
					if (!location.isChunkLoaded())
						continue;

					Block block = location.getBlock();
					if (Nullables.isNullOrAir(block) || !CustomBlockType.getBlockMaterials().contains(block.getType())) {
						forRemoval.add(location);
						continue;
					}

					CustomBlockData dbData = locationMap.get(location);
					CustomBlockType dbType = dbData.getType();

					Material blockMaterial = block.getType();

					for (CustomBlockType customType : CustomBlockType.values()) {
						if (customType == dbType && customType.getBlockMaterial() == blockMaterial) {
							continue checkLocation;
						}
					}

					forRemoval.add(location);
				}

				for (Location location : forRemoval) {
					tracker.remove(location);
					debug("Custom Block Janitor: removing data at " + StringUtils.getShortLocationString(location));
				}
				trackerService.save(tracker);
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
