package me.pugabyte.bncore.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

@SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
public class Jingles {
	public static void ping(Player player) {
		player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
	}

	public static void rankup(Player player) {
		int wait = 0;
		Tasks.wait(wait += 0, () -> {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 0.749154F);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 10F, 0.749154F);
		});
		Tasks.wait(wait += 4, () -> {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 0.561231F);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 10F, 0.561231F);
		});
		Tasks.wait(wait += 4, () -> {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 0.629961F);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 10F, 0.629961F);
		});
		Tasks.wait(wait += 2, () -> {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 0.707107F);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 10F, 0.707107F);
		});
		Tasks.wait(wait += 2, () -> {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 0.840896F);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 10F, 0.840896F);
		});
		Tasks.wait(wait += 2, () -> {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_FLUTE, 10F, 1.122462F);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 10F, 1.122462F);
		});
	}

	public static void firstJoin(Player player) {
		int wait = 0;
		Tasks.wait(wait += 0, () -> {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_CHIME, 10F, 0.561231F);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 10F, 0.561231F);
		});
		Tasks.wait(wait += 2, () -> {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_CHIME, 10F, 0.629961F);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 10F, 0.629961F);
		});
		Tasks.wait(wait += 2, () -> {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_CHIME, 10F, 0.561231F);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 10F, 0.561231F);
		});
		Tasks.wait(wait += 2, () -> {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_CHIME, 10F, 0.840896F);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 10F, 0.840896F);
		});
	}

	public static void join(Player player) {
		int wait = 0;
		Tasks.wait(wait += 0, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 0.5F));
		Tasks.wait(wait += 2, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 0.667420F));
		Tasks.wait(wait += 2, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 0.749154F));
		Tasks.wait(wait += 2, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 1F));
	}

	public static void quit(Player player) {
		int wait = 0;
		Tasks.wait(wait += 0, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 0.707107F));
		Tasks.wait(wait += 4, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 0.629961F));
		Tasks.wait(wait += 4, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 0.707107F));
		Tasks.wait(wait += 4, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 0.529732F));
	}

}
