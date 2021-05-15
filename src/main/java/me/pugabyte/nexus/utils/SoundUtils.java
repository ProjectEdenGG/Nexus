package me.pugabyte.nexus.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.lexikiq.HasPlayer;
import me.lexikiq.PlayerLike;
import me.pugabyte.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.models.mutemenu.MuteMenuUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.Collection;

@SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
public class SoundUtils {
	private static final float defaultVolume = 0.5F;

	public static void playSoundAll(Sound sound, float volume, float pitch) {
		Bukkit.getOnlinePlayers().forEach(player -> playSound(player, sound, volume, pitch));
	}

	public static void playSound(HasPlayer player, Sound sound) {
		playSound(player, sound, SoundCategory.MASTER);
	}

	public static void playSound(HasPlayer player, Sound sound, SoundCategory category) {
		playSound(player, sound, category, defaultVolume, 1);
	}

	public static void playSound(HasPlayer player, Sound sound, float volume, float pitch) {
		playSound(player, sound, SoundCategory.MASTER, volume, pitch);
	}

	public static void playSound(HasPlayer player, Sound sound, SoundCategory category, float volume, float pitch) {
		Player _player = player.getPlayer();
		_player.playSound(_player.getLocation(), sound, category, volume, pitch);
	}

	public static void playSound(Location location, Sound sound) {
		playSound(location, sound, SoundCategory.MASTER);
	}

	public static void playSound(Location location, Sound sound, SoundCategory category) {
		playSound(location, sound, category, defaultVolume, 1);
	}

	public static void playSound(Location location, Sound sound, float volume, float pitch) {
		playSound(location, sound, SoundCategory.MASTER, volume, pitch);
	}

	public static void playSound(Location location, Sound sound, SoundCategory category, float volume, float pitch) {
		location.getWorld().playSound(location, sound, category, volume, pitch);
	}

	public static void playSound(HasPlayer player, SoundArgs soundArgs) {
		if (soundArgs.getCategory() == null)
			soundArgs.setCategory(SoundCategory.MASTER);

		playSound(player, soundArgs.getSound(), soundArgs.getCategory(), soundArgs.getVolume(), soundArgs.getPitch());
	}

	public static void stopSound(HasPlayer player, Sound sound) {
		stopSound(player, sound, null);
	}

	public static void stopSound(HasPlayer player, Sound sound, SoundCategory category) {
		player.getPlayer().stopSound(sound, category);
	}

	public enum Jingle {
		PING {
			@Override
			public void play(PlayerLike player) {
				if (MuteMenuUser.hasMuted(player, MuteMenuItem.ALERTS))
					return;

				float volume = getMuteMenuVolume(player, MuteMenuItem.ALERTS, defaultVolume);

				playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, volume, 1);
			}
		},

		RANKUP {
			@Override
			public void play(PlayerLike player) {
				if (MuteMenuUser.hasMuted(player, MuteMenuItem.RANK_UP))
					return;

				float volume = getMuteMenuVolume(player, MuteMenuItem.RANK_UP, defaultVolume);

				int wait = 0;
				Tasks.wait(wait += 0, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, volume, 0.749154F);
					playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.RECORDS, volume, 0.749154F);
				});
				Tasks.wait(wait += 4, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, volume, 0.561231F);
					playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.RECORDS, volume, 0.561231F);
				});
				Tasks.wait(wait += 4, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, volume, 0.629961F);
					playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.RECORDS, volume, 0.629961F);
				});
				Tasks.wait(wait += 2, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, volume, 0.707107F);
					playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.RECORDS, volume, 0.707107F);
				});
				Tasks.wait(wait += 2, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, volume, 0.840896F);
					playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.RECORDS, volume, 0.840896F);
				});
				Tasks.wait(wait += 2, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.RECORDS, volume, 1.122462F);
					playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.RECORDS, volume, 1.122462F);
				});
			}
		},

		FIRST_JOIN {
			@Override
			public void play(PlayerLike player) {
				if (MuteMenuUser.hasMuted(player, MuteMenuItem.FIRST_JOIN_SOUND))
					return;

				float volume = getMuteMenuVolume(player, MuteMenuItem.FIRST_JOIN_SOUND, defaultVolume);

				int wait = 0;
				Tasks.wait(wait += 0, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.RECORDS, volume, 0.561231F);
					playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.RECORDS, volume, 0.561231F);
				});
				Tasks.wait(wait += 2, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.RECORDS, volume, 0.629961F);
					playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.RECORDS, volume, 0.629961F);
				});
				Tasks.wait(wait += 2, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.RECORDS, volume, 0.561231F);
					playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.RECORDS, volume, 0.561231F);
				});
				Tasks.wait(wait += 2, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.RECORDS, volume, 0.840896F);
					playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.RECORDS, volume, 0.840896F);
				});
			}
		},

		JOIN {
			@Override
			public void play(PlayerLike player) {
				if (MuteMenuUser.hasMuted(player, MuteMenuItem.JOIN_QUIT))
					return;

				float volume = getMuteMenuVolume(player, MuteMenuItem.JOIN_QUIT_SOUNDS, defaultVolume);

				int wait = 0;
				Tasks.wait(wait += 0, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, volume, 0.5F));
				Tasks.wait(wait += 2, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, volume, 0.667420F));
				Tasks.wait(wait += 2, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, volume, 0.749154F));
				Tasks.wait(wait += 2, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, volume, 1F));
			}
		},

		QUIT {
			@Override
			public void play(PlayerLike player) {
				if (MuteMenuUser.hasMuted(player, MuteMenuItem.JOIN_QUIT))
					return;

				float volume = getMuteMenuVolume(player, MuteMenuItem.JOIN_QUIT_SOUNDS, defaultVolume);

				int wait = 0;
				Tasks.wait(wait += 0, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, volume, 0.707107F));
				Tasks.wait(wait += 4, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, volume, 0.629961F));
				Tasks.wait(wait += 4, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, volume, 0.707107F));
				Tasks.wait(wait += 4, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, volume, 0.529732F));
			}
		},

		BATTLESHIP_MISS {
			@Override
			public void play(PlayerLike player) {
				int wait = 0;
				Tasks.wait(wait += 0, () -> playSound(player, Sound.UI_TOAST_IN, defaultVolume, 1));
				Tasks.wait(wait += 9, () -> playSound(player, Sound.ENTITY_GENERIC_SPLASH, defaultVolume, 1));
			}
		},

		BATTLESHIP_HIT {
			@Override
			public void play(PlayerLike player) {
				int wait = 0;
				Tasks.wait(wait += 0, () -> playSound(player, Sound.UI_TOAST_IN, defaultVolume, 1));
				Tasks.wait(wait += 9, () -> playSound(player, Sound.ENTITY_GENERIC_EXPLODE, defaultVolume, 1));
				Tasks.wait(wait += 8, () -> playSound(player, Sound.BLOCK_FIRE_AMBIENT, defaultVolume, 0.1F));
			}
		},

		BATTLESHIP_SINK {
			@Override
			public void play(PlayerLike player){
				int wait = 0;
				Tasks.wait(wait, () -> {
					playSound(player, Sound.ENTITY_GENERIC_EXPLODE, defaultVolume, 1);
					playSound(player, Sound.ENTITY_GENERIC_SPLASH, defaultVolume, 1);
				});
				Tasks.wait(wait += RandomUtils.randomInt(2, 5), () -> {
					playSound(player, Sound.ENTITY_GENERIC_EXPLODE, defaultVolume, 1);
					playSound(player, Sound.BLOCK_FIRE_AMBIENT, defaultVolume, 0.1F);
				});
				Tasks.wait(wait += RandomUtils.randomInt(2, 5), () -> {
					playSound(player, Sound.ENTITY_GENERIC_EXPLODE, defaultVolume, 1);
					playSound(player, Sound.ENTITY_GENERIC_SPLASH, defaultVolume, 1);
				});
				Tasks.wait(wait += RandomUtils.randomInt(2, 5), () -> playSound(player, Sound.ENTITY_GENERIC_EXPLODE, defaultVolume, 1));
				Tasks.wait(wait += RandomUtils.randomInt(1, 3), () -> playSound(player, Sound.ENTITY_GENERIC_EXPLODE, defaultVolume, 1));
				Tasks.wait(wait += RandomUtils.randomInt(1, 4), () -> playSound(player, Sound.ENTITY_GENERIC_SPLASH, defaultVolume, 1));
			}
		},

		PUGMAS_TREE_FELLER {
			@Override
			public void play(PlayerLike player) {
				Tasks.wait(0, () -> {
					playSound(player, Sound.ENTITY_ARMOR_STAND_BREAK, defaultVolume, randomPitch());
				});
				Tasks.wait(1, () -> {
					playSound(player, Sound.ENTITY_ARMOR_STAND_BREAK, defaultVolume, randomPitch());
				});
				Tasks.wait(2, () -> {
					playSound(player, Sound.ENTITY_ARMOR_STAND_BREAK, defaultVolume, randomPitch());
				});
				Tasks.wait(3, () -> {
					playSound(player, Sound.ENTITY_ARMOR_STAND_BREAK, defaultVolume, randomPitch());
					playSound(player, Sound.BLOCK_CROP_BREAK, defaultVolume, 0.1F);
				});
				Tasks.wait(4, () -> {
					playSound(player, Sound.ENTITY_ARMOR_STAND_BREAK, defaultVolume, randomPitch());
					playSound(player, Sound.BLOCK_SHROOMLIGHT_STEP, defaultVolume, 0.1F);
				});
				Tasks.wait(5, () -> {
					playSound(player, Sound.ENTITY_ARMOR_STAND_BREAK, defaultVolume, randomPitch());
					playSound(player, Sound.ENTITY_HORSE_SADDLE, defaultVolume, 0.1F);
				});
				Tasks.wait(6, () -> {
					playSound(player, Sound.ENTITY_ARMOR_STAND_BREAK, defaultVolume, 2);
				});
			}
		},
		CRATE_OPEN {
			@Override
			public void play(PlayerLike player) {
				int wait = 3;
				Tasks.wait(wait += 0, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(3));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(7));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(10));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_SNARE, .5F, getPitch(24));
				});
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, defaultVolume, getPitch(3)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, defaultVolume, getPitch(5)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, defaultVolume, getPitch(6)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, defaultVolume, getPitch(7)));
				Tasks.wait(wait += 3, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(5));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(9));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(12));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_SNARE, .5F, getPitch(24));
				});
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, defaultVolume, getPitch(5)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, defaultVolume, getPitch(7)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, defaultVolume, getPitch(8)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, defaultVolume, getPitch(9)));
				Tasks.wait(wait += 3, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(7));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(10));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(14));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_SNARE, .5F, getPitch(24));
				});
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, defaultVolume, getPitch(7)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, defaultVolume, getPitch(9)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, defaultVolume, getPitch(10)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, defaultVolume, getPitch(11)));
				Tasks.wait(wait += 3, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(9));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(13));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(16));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_SNARE, .5F, getPitch(24));
				});
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, defaultVolume, getPitch(9)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, defaultVolume, getPitch(11)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, defaultVolume, getPitch(12)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, defaultVolume, getPitch(13)));
				Tasks.wait(wait += 3, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(13));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(17));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(8));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_SNARE, .5F, getPitch(24));
				});
				Tasks.wait(wait += 3, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(20));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(12));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(15));
				});
			}
		};

		public abstract void play(PlayerLike player);

		public void play(Collection<? extends HasPlayer> players) {
			players.stream().map(HasPlayer::getPlayer).forEach(player -> play((PlayerLike) player));
		}

		public void playAll() {
			play(Bukkit.getOnlinePlayers());
		}

		void play(HasPlayer player, Sound intrument, int step) {
			play(player, intrument, getPitch(step));
		}

		void play(HasPlayer player, Sound intrument, float pitch) {
			playSound(player, intrument, SoundCategory.RECORDS, defaultVolume, pitch);
		}
	}

	private static float getMuteMenuVolume(PlayerLike player, MuteMenuItem item, float defaultVolume) {
		float volume = defaultVolume;
		Integer customVolume = MuteMenuUser.getVolume(player, item);
		if (customVolume != null)
			volume = customVolume / 50.0F;
		return volume;
	}

	public static float randomPitch() {
		return (float) RandomUtils.randomDouble(0.1, 2);
	}

	public static float getPitch(int step) {
		return (float) Math.pow(2, ((-12 + step) / 12.0));
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class SoundArgs {
		@NonNull
		Sound sound;
		SoundCategory category;
		@NonNull
		Float volume;
		@NonNull
		Float pitch;
		int delay = 0;

		public SoundArgs(@NonNull Sound sound, float volume, float pitch, int delay) {
			this.sound = sound;
			this.volume = volume;
			this.pitch = pitch;
			this.delay = delay;
		}

		public SoundArgs(@NonNull Sound sound, SoundCategory category, float volume, float pitch) {
			this.sound = sound;
			this.category = category;
			this.volume = volume;
			this.pitch = pitch;
		}
	}

}
