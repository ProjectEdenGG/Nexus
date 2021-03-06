package me.pugabyte.nexus.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.menus.mutemenu.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.models.mutemenu.MuteMenuUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.Collection;

@SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
public class SoundUtils {
	private static final float volume = 0.5F;

	public static void playSoundAll(Sound sound, float volume, float pitch) {
		Bukkit.getOnlinePlayers().forEach(player -> playSound(player, sound, volume, pitch));
	}

	public static void playSound(Player player, Sound sound) {
		playSound(player, sound, SoundCategory.MASTER);
	}

	public static void playSound(Player player, Sound sound, SoundCategory category) {
		playSound(player, sound, category, volume, 1);
	}

	public static void playSound(Player player, Sound sound, float volume, float pitch) {
		playSound(player, sound, SoundCategory.MASTER, volume, pitch);
	}

	public static void playSound(Player player, Sound sound, SoundCategory category, float volume, float pitch) {
		player.playSound(player.getLocation(), sound, category, volume, pitch);
	}

	public static void playSound(Location location, Sound sound) {
		playSound(location, sound, SoundCategory.MASTER);
	}

	public static void playSound(Location location, Sound sound, SoundCategory category) {
		playSound(location, sound, category, volume, 1);
	}

	public static void playSound(Location location, Sound sound, float volume, float pitch) {
		playSound(location, sound, SoundCategory.MASTER, volume, pitch);
	}

	public static void playSound(Location location, Sound sound, SoundCategory category, float volume, float pitch) {
		location.getWorld().playSound(location, sound, category, volume, pitch);
	}

	public static void playSound(Player player, SoundArgs soundArgs) {
		if (soundArgs.getCategory() == null)
			soundArgs.setCategory(SoundCategory.MASTER);

		playSound(player, soundArgs.getSound(), soundArgs.getCategory(), soundArgs.getVolume(), soundArgs.getPitch());
	}

	public static void stopSound(Player player, Sound sound) {
		stopSound(player, sound, null);
	}

	public static void stopSound(Player player, Sound sound, SoundCategory category) {
		player.stopSound(sound, category);
	}

	public enum Jingle {
		PING {
			@Override
			public void play(Player player) {
				playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, volume, 1);
			}
		},

		RANKUP {
			@Override
			public void play(Player player) {
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
			public void play(Player player) {
				if (MuteMenuUser.hasMuted(player, MuteMenuItem.FIRSTJOIN))
					return;

				int wait = 0;
				Tasks.wait(wait += 0, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.RECORDS, .3F, 0.561231F);
					playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.RECORDS, .3F, 0.561231F);
				});
				Tasks.wait(wait += 2, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.RECORDS, .3F, 0.629961F);
					playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.RECORDS, .3F, 0.629961F);
				});
				Tasks.wait(wait += 2, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.RECORDS, .3F, 0.561231F);
					playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.RECORDS, .3F, 0.561231F);
				});
				Tasks.wait(wait += 2, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.RECORDS, .3F, 0.840896F);
					playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.RECORDS, .3F, 0.840896F);
				});
			}
		},

		JOIN {
			@Override
			public void play(Player player) {
				if (MuteMenuUser.hasMuted(player, MuteMenuItem.JQ))
					return;

				int wait = 0;
				Tasks.wait(wait += 0, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, .5F, 0.5F));
				Tasks.wait(wait += 2, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, .5F, 0.667420F));
				Tasks.wait(wait += 2, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, .5F, 0.749154F));
				Tasks.wait(wait += 2, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, .5F, 1F));
			}
		},

		QUIT {
			@Override
			public void play(Player player) {
				if (MuteMenuUser.hasMuted(player, MuteMenuItem.JQ))
					return;

				int wait = 0;
				Tasks.wait(wait += 0, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, .5F, 0.707107F));
				Tasks.wait(wait += 4, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, .5F, 0.629961F));
				Tasks.wait(wait += 4, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, .5F, 0.707107F));
				Tasks.wait(wait += 4, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, .5F, 0.529732F));
			}
		},

		BATTLESHIP_MISS {
			@Override
			public void play(Player player) {
				int wait = 0;
				Tasks.wait(wait += 0, () -> playSound(player, Sound.UI_TOAST_IN, volume, 1));
				Tasks.wait(wait += 9, () -> playSound(player, Sound.ENTITY_GENERIC_SPLASH, volume, 1));
			}
		},

		BATTLESHIP_HIT {
			@Override
			public void play(Player player) {
				int wait = 0;
				Tasks.wait(wait += 0, () -> playSound(player, Sound.UI_TOAST_IN, volume, 1));
				Tasks.wait(wait += 9, () -> playSound(player, Sound.ENTITY_GENERIC_EXPLODE, volume, 1));
				Tasks.wait(wait += 8, () -> playSound(player, Sound.BLOCK_FIRE_AMBIENT, volume, 0.1F));
			}
		},

		BATTLESHIP_SINK {
			@Override
			public void play(Player player){
				int wait = 0;
				Tasks.wait(wait, () -> {
					playSound(player, Sound.ENTITY_GENERIC_EXPLODE, volume, 1);
					playSound(player, Sound.ENTITY_GENERIC_SPLASH, volume, 1);
				});
				Tasks.wait(wait += RandomUtils.randomInt(2, 5), () -> {
					playSound(player, Sound.ENTITY_GENERIC_EXPLODE, volume, 1);
					playSound(player, Sound.BLOCK_FIRE_AMBIENT, volume, 0.1F);
				});
				Tasks.wait(wait += RandomUtils.randomInt(2, 5), () -> {
					playSound(player, Sound.ENTITY_GENERIC_EXPLODE, volume, 1);
					playSound(player, Sound.ENTITY_GENERIC_SPLASH, volume, 1);
				});
				Tasks.wait(wait += RandomUtils.randomInt(2, 5), () -> playSound(player, Sound.ENTITY_GENERIC_EXPLODE, volume, 1));
				Tasks.wait(wait += RandomUtils.randomInt(1, 3), () -> playSound(player, Sound.ENTITY_GENERIC_EXPLODE, volume, 1));
				Tasks.wait(wait += RandomUtils.randomInt(1, 4), () -> playSound(player, Sound.ENTITY_GENERIC_SPLASH, volume, 1));
			}
		},

		PUGMAS_TREE_FELLER {
			@Override
			public void play(Player player) {
				Tasks.wait(0, () -> {
					playSound(player, Sound.ENTITY_ARMOR_STAND_BREAK, volume, randomPitch());
				});
				Tasks.wait(1, () -> {
					playSound(player, Sound.ENTITY_ARMOR_STAND_BREAK, volume, randomPitch());
				});
				Tasks.wait(2, () -> {
					playSound(player, Sound.ENTITY_ARMOR_STAND_BREAK, volume, randomPitch());
				});
				Tasks.wait(3, () -> {
					playSound(player, Sound.ENTITY_ARMOR_STAND_BREAK, volume, randomPitch());
					playSound(player, Sound.BLOCK_CROP_BREAK, volume, 0.1F);
				});
				Tasks.wait(4, () -> {
					playSound(player, Sound.ENTITY_ARMOR_STAND_BREAK, volume, randomPitch());
					playSound(player, Sound.BLOCK_SHROOMLIGHT_STEP, volume, 0.1F);
				});
				Tasks.wait(5, () -> {
					playSound(player, Sound.ENTITY_ARMOR_STAND_BREAK, volume, randomPitch());
					playSound(player, Sound.ENTITY_HORSE_SADDLE, volume, 0.1F);
				});
				Tasks.wait(6, () -> {
					playSound(player, Sound.ENTITY_ARMOR_STAND_BREAK, volume, 2);
				});
			}
		},
		CRATE_OPEN {
			@Override
			public void play(Player player) {
				int wait = 3;
				Tasks.wait(wait += 0, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(3));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(7));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(10));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_SNARE, .5F, getPitch(24));
				});
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(3)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(5)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(6)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(7)));
				Tasks.wait(wait += 3, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(5));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(9));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(12));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_SNARE, .5F, getPitch(24));
				});
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(5)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(7)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(8)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(9)));
				Tasks.wait(wait += 3, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(7));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(10));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(14));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_SNARE, .5F, getPitch(24));
				});
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(7)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(9)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(10)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(11)));
				Tasks.wait(wait += 3, () -> {
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(9));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(13));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(16));
					playSound(player, Sound.BLOCK_NOTE_BLOCK_SNARE, .5F, getPitch(24));
				});
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(9)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(11)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(12)));
				Tasks.wait(wait += 3, () -> playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(13)));
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

		public abstract void play(Player player);

		public void play(Collection<? extends Player> players) {
			players.forEach(this::play);
		}

		public void playAll() {
			play(Bukkit.getOnlinePlayers());
		}

		void play(Player player, Sound intrument, int step) {
			play(player, intrument, getPitch(step));
		}

		void play(Player player, Sound intrument, float pitch) {
			playSound(player, intrument, SoundCategory.RECORDS, volume, pitch);
		}
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
