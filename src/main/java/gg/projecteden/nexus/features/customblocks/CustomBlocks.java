package gg.projecteden.nexus.features.customblocks;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.utils.Env;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/*
	TODO:
		- Tripwire implementation:
			- Fix/Remove break particles
			- Directional placing
				- note blocks need the blockface of the block they are placed against (current & intended)
				- tripwire needs the facing of the player
			- Placing string needs to place TRIPWIRE and not TALL_SUPPORT
		- Sounds --> Do some more testing
		- Vanished handling, proper interactions and what not
		- Future Conversions on chunk generate/load, itemstacks & blocks
		- CustomBlockState handling, like bird houses
		- Lots of testing
		- //
		- Appropriate tool & mining speed --> CustomBlockBreaking
		- //
		- Cannot Fix:
			- Custom blocks may flash when placing blocks near them (clientside only) --> Titan
			- Players arm will swing on interact w/ custom blocks (clientside only?) --> Titan
 */

@Environments(Env.TEST)
public class CustomBlocks extends Feature {
	@Override
	public void onStart() {
		new CustomBlocksListener();
	}

	public static void debug(String message) {
		List<Dev> devs = List.of(Dev.WAKKA, Dev.GRIFFIN);
		for (Dev dev : devs) {
			if (dev.isOnline())
				dev.send(message);
		}
	}

	public enum SoundType {
		WOOD,
		STONE,
		;

		public static @Nullable SoundType fromSound(Sound sound) {
			String soundKey = sound.getKey().getKey();
			if (soundKey.startsWith("block." + SoundType.WOOD.name().toLowerCase() + "."))
				return WOOD;
			else if (soundKey.startsWith("block." + SoundType.STONE.name().toLowerCase() + "."))
				return STONE;

			return null;
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

		public String getCustomSound(SoundType soundType) {
			return "custom.block." + soundType.name().toLowerCase() + "." + this.name().toLowerCase();
		}

		public static @Nullable CustomBlocks.SoundAction fromSound(Sound sound) {
			String soundKey = sound.getKey().getKey();
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
