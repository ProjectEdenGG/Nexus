package gg.projecteden.nexus.features.customblocks;

import com.sk89q.worldedit.WorldEdit;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.features.customblocks.listeners.CustomBlockListener;
import gg.projecteden.nexus.features.customblocks.worldedit.CustomBlockParser;
import gg.projecteden.nexus.features.customblocks.worldedit.WorldEditListener;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/*
	TODO:
		- Future Conversions on chunk generate/load, itemstacks & blocks
		- WorldEdit handling
		- Auto-Tool support
		- BlockPhysicsEvent
		- Tripwire implementation:
			- Placing string needs properly update nearby tripwire to tripwire cross if suitable
			- If placed next to hook, and both hooks exist, attach the hooks
		- 6 tick delay between block break
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

		WorldEditListener.register();
		WorldEdit.getInstance().getBlockFactory().register(new CustomBlockParser(WorldEdit.getInstance()));
	}

	@Override
	public void onStop() {
		WorldEditListener.unregister();
	}

	@Getter
	@Setter
	private static boolean debug = false;

	public static void debug(String message) {
		if (debug)
			List.of(Dev.WAKKA, Dev.GRIFFIN).forEach(dev -> dev.send(message));
	}

	public enum ReplacedSoundType {
		WOOD,
		STONE,
		;

		public static @Nullable CustomBlocks.ReplacedSoundType fromSound(String soundKey) {
			soundKey = soundKey.toLowerCase().replaceAll("_", ".");
			if (soundKey.startsWith("block." + ReplacedSoundType.WOOD.name().toLowerCase() + "."))
				return WOOD;
			else if (soundKey.startsWith("block." + ReplacedSoundType.STONE.name().toLowerCase() + "."))
				return STONE;

			return null;
		}

		public static @Nullable CustomBlocks.ReplacedSoundType fromSound(Key sound) {
			return fromSound(sound.value());
		}

		public static @Nullable CustomBlocks.ReplacedSoundType fromSound(Sound sound) {
			return fromSound(sound.name());
		}

		public static @Nullable CustomBlocks.ReplacedSoundType fromSound(net.kyori.adventure.sound.Sound sound) {
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