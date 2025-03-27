package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.parchment.HasPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@SuppressWarnings({"UnusedAssignment"})
public class SoundUtils {

	public static void stopSound(HasPlayer player, Sound sound) {
		stopSound(player, sound, null);
	}

	public static void stopSound(HasPlayer player, Sound sound, SoundCategory category) {
		player.getPlayer().stopSound(sound, category);
	}

	public static void playSound(Block block, SoundAction soundAction) {
		Sound placeSound = NMSUtils.getSound(soundAction, block);
		if (placeSound == null)
			return;

		new SoundBuilder(placeSound)
			.location(block)
			.volume(soundAction.getVolume())
			.pitch(soundAction.getPitch())
			.play();
	}

	public enum Jingle {
		PING {
			@Override
			public void play(HasPlayer player) {
				if (MuteMenuUser.hasMuted(player.getPlayer(), MuteMenuItem.ALERTS))
					return;

				float volume = getMuteMenuVolume(player, MuteMenuItem.ALERTS);

				new SoundBuilder(Sound.ENTITY_ARROW_HIT_PLAYER).receiver(player).volume(volume).play();
			}
		},

		RANKUP {
			@Override
			public void play(HasPlayer player) {
				if (MuteMenuUser.hasMuted(player.getPlayer(), MuteMenuItem.RANK_UP))
					return;

				float volume = getMuteMenuVolume(player, MuteMenuItem.RANK_UP);

				SoundBuilder harp = new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_HARP).receiver(player).category(SoundCategory.RECORDS).volume(volume);
				SoundBuilder bell = new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).receiver(player).category(SoundCategory.RECORDS).volume(volume);
				SoundBuilder flute = new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_FLUTE).receiver(player).category(SoundCategory.RECORDS).volume(volume);

				int wait = 0;
				Tasks.wait(wait += 0, () -> {
					harp.pitchStep(7).play();
					bell.pitchStep(7).play();
				});
				Tasks.wait(wait += 4, () -> {
					harp.pitchStep(2).play();
					bell.pitchStep(2).play();
				});
				Tasks.wait(wait += 4, () -> {
					harp.pitchStep(4).play();
					bell.pitchStep(4).play();
				});
				Tasks.wait(wait += 2, () -> {
					harp.pitchStep(6).play();
					bell.pitchStep(6).play();
				});
				Tasks.wait(wait += 2, () -> {
					harp.pitchStep(9).play();
					bell.pitchStep(9).play();
				});
				Tasks.wait(wait += 2, () -> {
					flute.pitchStep(14).play();
					bell.pitchStep(14).play();
				});
			}
		},

		FIRST_JOIN {
			@Override
			public void play(HasPlayer player) {
				if (MuteMenuUser.hasMuted(player.getPlayer(), MuteMenuItem.FIRST_JOIN_SOUND))
					return;

				float volume = getMuteMenuVolume(player, MuteMenuItem.FIRST_JOIN_SOUND);

				SoundBuilder chime = new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_CHIME).receiver(player).category(SoundCategory.RECORDS).volume(volume);
				SoundBuilder bell = new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).receiver(player).category(SoundCategory.RECORDS).volume(volume);

				int wait = 0;
				Tasks.wait(wait += 0, () -> {
					chime.pitchStep(2).play();
					bell.pitchStep(2).play();
				});
				Tasks.wait(wait += 2, () -> {
					chime.pitchStep(4).play();
					bell.pitchStep(4).play();
				});
				Tasks.wait(wait += 2, () -> {
					chime.pitchStep(2).play();
					bell.pitchStep(2).play();
				});
				Tasks.wait(wait += 2, () -> {
					chime.pitchStep(9).play();
					bell.pitchStep(9).play();
				});
			}
		},

		JOIN {
			@Override
			public void play(HasPlayer player) {
				if (MuteMenuUser.hasMuted(player.getPlayer(), MuteMenuItem.JOIN_QUIT))
					return;

				float volume = getMuteMenuVolume(player, MuteMenuItem.JOIN_QUIT_SOUNDS);

				SoundBuilder harp = new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_HARP).receiver(player).category(SoundCategory.RECORDS).volume(volume);

				int wait = 0;
				Tasks.wait(wait += 0, () -> harp.pitchStep(0).play());
				Tasks.wait(wait += 2, () -> harp.pitchStep(5).play());
				Tasks.wait(wait += 2, () -> harp.pitchStep(7).play());
				Tasks.wait(wait += 2, () -> harp.pitchStep(12).play());
			}
		},

		QUIT {
			@Override
			public void play(HasPlayer player) {
				if (MuteMenuUser.hasMuted(player.getPlayer(), MuteMenuItem.JOIN_QUIT))
					return;

				float volume = getMuteMenuVolume(player, MuteMenuItem.JOIN_QUIT_SOUNDS);

				SoundBuilder harp = new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_HARP).receiver(player).category(SoundCategory.RECORDS).volume(volume);

				int wait = 0;
				Tasks.wait(wait += 0, () -> harp.pitchStep(6).play());
				Tasks.wait(wait += 4, () -> harp.pitchStep(4).play());
				Tasks.wait(wait += 4, () -> harp.pitchStep(6).play());
				Tasks.wait(wait += 4, () -> harp.pitchStep(1).play());
			}
		},

		BATTLESHIP_MISS {
			@Override
			public void play(HasPlayer player) {
				int wait = 0;
				Tasks.wait(wait += 0, () -> new SoundBuilder(Sound.UI_TOAST_IN).receiver(player).volume(.5).play());
				Tasks.wait(wait += 9, () -> new SoundBuilder(Sound.ENTITY_GENERIC_SPLASH).receiver(player).volume(.5).play());
			}
		},

		BATTLESHIP_HIT {
			@Override
			public void play(HasPlayer player) {
				int wait = 0;
				Tasks.wait(wait += 0, () -> new SoundBuilder(Sound.UI_TOAST_IN).receiver(player).volume(.5).play());
				Tasks.wait(wait += 9, () -> new SoundBuilder(Sound.ENTITY_GENERIC_EXPLODE).receiver(player).volume(.5).play());
				Tasks.wait(wait += 8, () -> new SoundBuilder(Sound.BLOCK_FIRE_AMBIENT).receiver(player).volume(.5).play());
			}
		},

		BATTLESHIP_SINK {
			@Override
			public void play(HasPlayer player) {
				SoundBuilder explode = new SoundBuilder(Sound.ENTITY_GENERIC_EXPLODE).receiver(player).volume(.5);
				SoundBuilder splash = new SoundBuilder(Sound.ENTITY_GENERIC_SPLASH).receiver(player).volume(.5);
				SoundBuilder fire = new SoundBuilder(Sound.BLOCK_FIRE_AMBIENT).receiver(player).volume(.5).pitch(.1);

				int wait = 0;
				Tasks.wait(wait, () -> {
					explode.play();
					splash.play();
				});
				Tasks.wait(wait += RandomUtils.randomInt(2, 5), () -> {
					explode.play();
					fire.play();
				});
				Tasks.wait(wait += RandomUtils.randomInt(2, 5), () -> {
					explode.play();
					splash.play();
				});
				Tasks.wait(wait += RandomUtils.randomInt(2, 5), explode::play);
				Tasks.wait(wait += RandomUtils.randomInt(1, 3), explode::play);
				Tasks.wait(wait += RandomUtils.randomInt(1, 4), splash::play);
			}
		},

		TREE_FELLER {
			@Override
			public void play(HasPlayer player) {
				SoundBuilder armorStandBreak = new SoundBuilder(Sound.ENTITY_ARMOR_STAND_BREAK).receiver(player).volume(.5);

				Tasks.wait(0, () -> armorStandBreak.pitch(randomPitch()).play());
				Tasks.wait(1, () -> armorStandBreak.pitch(randomPitch()).play());
				Tasks.wait(2, () -> armorStandBreak.pitch(randomPitch()).play());
				Tasks.wait(3, () -> {
					armorStandBreak.pitch(randomPitch()).play();
					new SoundBuilder(Sound.BLOCK_CROP_BREAK).receiver(player).volume(.5).pitch(.1).play();
				});
				Tasks.wait(4, () -> {
					armorStandBreak.pitch(randomPitch()).play();
					new SoundBuilder(Sound.BLOCK_SHROOMLIGHT_STEP).receiver(player).volume(.5).pitch(.1).play();
				});
				Tasks.wait(5, () -> {
					armorStandBreak.pitch(randomPitch()).play();
					new SoundBuilder(Sound.ENTITY_HORSE_SADDLE).receiver(player).volume(.5).pitch(.1).play();
				});
				Tasks.wait(6, () -> armorStandBreak.pitch(2).play());
			}
		},
		CRATE_OPEN {
			@Override
			public void play(Location location) {
				SoundBuilder harp = new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_HARP).volume(.6).location(location);
				SoundBuilder snare = new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_SNARE).volume(.5).location(location);

				int wait = 3;
				Tasks.wait(wait += 0, () -> {
					harp.pitchStep(3).play();
					harp.pitchStep(7).play();
					harp.pitchStep(10).play();
					snare.pitchStep(24).play();
				});
				Tasks.wait(wait += 3, () -> harp.pitchStep(3).play());
				Tasks.wait(wait += 3, () -> harp.pitchStep(5).play());
				Tasks.wait(wait += 3, () -> harp.pitchStep(6).play());
				Tasks.wait(wait += 3, () -> harp.pitchStep(7).play());
				Tasks.wait(wait += 3, () -> {
					harp.pitchStep(5).play();
					harp.pitchStep(9).play();
					harp.pitchStep(12).play();
					snare.pitchStep(24).play();
				});
				Tasks.wait(wait += 3, () -> harp.pitchStep(5).play());
				Tasks.wait(wait += 3, () -> harp.pitchStep(7).play());
				Tasks.wait(wait += 3, () -> harp.pitchStep(8).play());
				Tasks.wait(wait += 3, () -> harp.pitchStep(9).play());
				Tasks.wait(wait += 3, () -> {
					harp.pitchStep(7).play();
					harp.pitchStep(10).play();
					harp.pitchStep(14).play();
					snare.pitchStep(24).play();
				});
				Tasks.wait(wait += 3, () -> harp.pitchStep(7).play());
				Tasks.wait(wait += 3, () -> harp.pitchStep(9).play());
				Tasks.wait(wait += 3, () -> harp.pitchStep(10).play());
				Tasks.wait(wait += 3, () -> harp.pitchStep(11).play());
				Tasks.wait(wait += 3, () -> {
					harp.pitchStep(9).play();
					harp.pitchStep(13).play();
					harp.pitchStep(16).play();
					snare.pitchStep(24).play();
				});
				Tasks.wait(wait += 3, () -> harp.pitchStep(9).play());
				Tasks.wait(wait += 3, () -> harp.pitchStep(11).play());
				Tasks.wait(wait += 3, () -> harp.pitchStep(12).play());
				Tasks.wait(wait += 3, () -> harp.pitchStep(13).play());
				Tasks.wait(wait += 3, () -> {
					harp.pitchStep(13).play();
					harp.pitchStep(17).play();
					harp.pitchStep(8).play();
					snare.pitchStep(24).play();
				});
				Tasks.wait(wait += 3, () -> {
					harp.pitchStep(20).play();
					harp.pitchStep(12).play();
					harp.pitchStep(15).play();
				});
			}

			@Override
			public void play(HasPlayer player) {
				play(player.getPlayer().getLocation());
			}
		},
		SABOTAGE_VOTE {
			@Override
			public void play(HasPlayer player) {
				new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_CHIME).receiver(player).volume(.8).pitch(1.7).play();
				Tasks.wait(3, () -> new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_CHIME).receiver(player).volume(.8).pitch(2).play());
			}
		},
		SABOTAGE_MEETING {
			@Override
			public void play(HasPlayer player) {
				new SoundBuilder(Sound.BLOCK_BELL_USE).receiver(player).pitch(.8).play();
				new SoundBuilder(Sound.BLOCK_BELL_RESONATE).receiver(player).volume(.25).play();

				SoundBuilder bell = new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).receiver(player).volume(.6);
				Tasks.wait(7, () -> bell.pitch(.2).play());
				Tasks.wait(12, () -> bell.pitch(.6).play());
				Tasks.wait(17, () -> bell.pitch(.8).play());
			}

			@Override
			public void play(Location location) {
			}
		};

		private static Location getFinalLocation(HasPlayer player, Location location) {
			if (location == null)
				return player.getPlayer().getLocation();
			return location;
		}

		public void play(Location location) {}

		public void play(HasPlayer player) {}

		public void play(Collection<? extends HasPlayer> players) {
			players.stream().map(HasPlayer::getPlayer).forEach(this::play);
		}

		public void playAll() {
			play(OnlinePlayers.getAll());
		}
	}

	public static float getMuteMenuVolume(HasPlayer player, MuteMenuItem item) {
		float volume = .5f;
		Integer customVolume = MuteMenuUser.getVolume(player.getPlayer(), item);
		if (customVolume != null)
			volume = customVolume / 50f;
		return volume;
	}

	public static float randomPitch() {
		return (float) RandomUtils.randomDouble(.1, 2);
	}

	public static int randomStep() {
		return RandomUtils.randomInt(0, 24);
	}

	public static float getPitch(int step) {
		return (float) Math.pow(2, ((-12 + step) / 12f));
	}

	@SuppressWarnings("removal")
	@Getter
	@RequiredArgsConstructor
	public enum SoundAction {
		BREAK(1.0, 0.8),
		STEP(0.15, 1.0),
		PLACE(1.0, 0.8),
		HIT(0.4, 0.5),
		FALL(1.0, 0.75),
		;

		private final double volume;
		private double pitch;

		SoundAction(double volume, double pitch) {
			this.volume = volume;
			this.pitch = pitch;
		}

		public String getCustomSound(ReplacedSoundType soundType) {
			return "custom.block." + soundType.name().toLowerCase() + "." + this.name().toLowerCase();
		}

		public static @Nullable SoundAction fromSound(String soundKey) {
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

		public static @Nullable SoundAction fromSound(Key sound) {
			return fromSound(sound.value());
		}

		public static @Nullable SoundAction fromSound(Sound sound) {
			return fromSound(sound.getKey());
		}

		public static @Nullable SoundAction fromSound(net.kyori.adventure.sound.Sound sound) {
			return fromSound(sound.name());
		}
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
}
