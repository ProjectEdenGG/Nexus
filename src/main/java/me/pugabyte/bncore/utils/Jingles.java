package me.pugabyte.bncore.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Jingles {
	public static void ping(Player player) {
		player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
	}

	public static void rankup(Player player) {
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 0.749154F);
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 10F, 0.749154F);

		Tasks.wait(4, () -> {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 0.561231F);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 10F, 0.561231F);

			Tasks.wait(4, () -> {
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 0.629961F);
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 10F, 0.629961F);

				Tasks.wait(2, () -> {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 0.707107F);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 10F, 0.707107F);

					Tasks.wait(2, () -> {
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 10F, 0.840896F);
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 10F, 0.840896F);

						Tasks.wait(2, () -> {
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_FLUTE, 10F, 1.122462F);
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 10F, 1.122462F);
						});
					});
				});
			});
		});
	}
}
