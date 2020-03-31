package me.pugabyte.bncore.utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collection;

@SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
public class SoundUtils {

	public static void playSoundAll(Sound sound, float volume, float pitch) {
		Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), sound, volume, pitch));
	}

	public enum Jingle {
		PING {
			@Override
			public void play(Player player) {
				player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
			}
		},

		RANKUP {
			@Override
			public void play(Player player) {
				int wait = 0;
				Tasks.wait(wait += 0, () -> {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1, 0.749154F);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 0.749154F);
				});
				Tasks.wait(wait += 4, () -> {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1, 0.561231F);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 0.561231F);
				});
				Tasks.wait(wait += 4, () -> {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1, 0.629961F);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 0.629961F);
				});
				Tasks.wait(wait += 2, () -> {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1, 0.707107F);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 0.707107F);
				});
				Tasks.wait(wait += 2, () -> {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1, 0.840896F);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 0.840896F);
				});
				Tasks.wait(wait += 2, () -> {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_FLUTE, 1, 1.122462F);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 1.122462F);
				});
			}
		},

		FIRST_JOIN {
			@Override
			public void play(Player player) {
				int wait = 0;
				Tasks.wait(wait += 0, () -> {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_CHIME, .05F, 0.561231F);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, .05F, 0.561231F);
				});
				Tasks.wait(wait += 2, () -> {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_CHIME, .05F, 0.629961F);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, .05F, 0.629961F);
				});
				Tasks.wait(wait += 2, () -> {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_CHIME, .05F, 0.561231F);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, .05F, 0.561231F);
				});
				Tasks.wait(wait += 2, () -> {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_CHIME, .05F, 0.840896F);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, .05F, 0.840896F);
				});
			}
		},

		JOIN {
			@Override
			public void play(Player player) {
				int wait = 0;
				Tasks.wait(wait += 0, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, .05F, 0.5F));
				Tasks.wait(wait += 2, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, .05F, 0.667420F));
				Tasks.wait(wait += 2, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, .05F, 0.749154F));
				Tasks.wait(wait += 2, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, .05F, 1F));
			}
		},

		QUIT {
			@Override
			public void play(Player player) {
				int wait = 0;
				Tasks.wait(wait += 0, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, .05F, 0.707107F));
				Tasks.wait(wait += 4, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, .05F, 0.629961F));
				Tasks.wait(wait += 4, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, .05F, 0.707107F));
				Tasks.wait(wait += 4, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, .05F, 0.529732F));
			}
		},

		BATTLESHIP_MISS {
			@Override
			public void play(Player player){
				int wait = 0;
				Tasks.wait(wait += 0, () -> player.playSound(player.getLocation(), Sound.UI_TOAST_IN, 1, 1));
				Tasks.wait(wait += 9, () -> player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1, 1));
			}
		},

		BATTLESHIP_HIT {
			@Override
			public void play(Player player){
				int wait = 0;
				Tasks.wait(wait += 0, () -> player.playSound(player.getLocation(), Sound.UI_TOAST_IN, 1, 1));
				Tasks.wait(wait += 9, () -> player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1));
				Tasks.wait(wait += 8, () -> player.playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 1, 0.1F));
			}
		},

		BATTLESHIP_SINK {
			@Override
			public void play(Player player){
				int wait = 0;
				Tasks.wait(wait, () -> {
					player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
					player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1, 1);
				});
				Tasks.wait(wait += Utils.randomInt(2, 5), () -> {
					player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
					player.playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 1, 0.1F);
				});
				Tasks.wait(wait += Utils.randomInt(2, 5), () -> {
					player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
					player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1, 1);
				});
				Tasks.wait(wait += Utils.randomInt(2, 5), () -> player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1));
				Tasks.wait(wait += Utils.randomInt(1, 3), () -> player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1));
				Tasks.wait(wait += Utils.randomInt(1, 4), () -> player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1, 1));
			}
		};

		public abstract void play(Player player);

		public void play(Collection<? extends Player> players) {
			players.forEach(this::play);
		}

		public void playAll() {
			play(Bukkit.getOnlinePlayers());
		}
	}

}
