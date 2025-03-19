package gg.projecteden.nexus.features.resourcepack.customblocks;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.features.resourcepack.customblocks.customblockbreaking.CustomBlockBreaking;
import gg.projecteden.nexus.features.resourcepack.customblocks.listeners.CustomBlockListener;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.worldedit.WrappedWorldEdit;
import gg.projecteden.nexus.framework.features.Feature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;

/*
	TODO:
		- Test server bug signs
		- Test conversion again
		- Remove "TODO CUSTOM BLOCKS: REMOVE"
 */

/*
	TODO POST RELEASE:
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
		- Maybe add more advanced worldedit handling, such as setting directionals, and other "block states"
 */

@Environments(Env.TEST)
public class CustomBlocksFeature extends Feature {
	@Override
	public void onStart() {
		CustomBlock.init();
		new CustomBlockListener();

		CustomBlockBreaking.init();

		WrappedWorldEdit.init();
		WrappedWorldEdit.registerParser();
	}

	@SuppressWarnings("removal")
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

	@SuppressWarnings("removal")
	@Getter
	@RequiredArgsConstructor
	public enum SoundAction {
		BREAK(1.0),
		STEP(0.15),
		PLACE(1.0),
		HIT(0.4, 0.5),
		FALL(1.0),
		;

		private final double volume;
		private double pitch = 1.0;

		SoundAction(double volume, double pitch) {
			this.volume = volume;
			this.pitch = pitch;
		}

		public String getCustomSound(ReplacedSoundType soundType) {
			return "custom.block." + soundType.name().toLowerCase() + "." + this.name().toLowerCase();
		}

		public static @Nullable CustomBlocksFeature.SoundAction fromSound(String soundKey) {
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

		public static @Nullable CustomBlocksFeature.SoundAction fromSound(Key sound) {
			return fromSound(sound.value());
		}

		public static @Nullable CustomBlocksFeature.SoundAction fromSound(Sound sound) {
			return fromSound(sound.getKey());
		}

		public static @Nullable CustomBlocksFeature.SoundAction fromSound(net.kyori.adventure.sound.Sound sound) {
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
