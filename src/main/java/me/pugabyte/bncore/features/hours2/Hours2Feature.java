package me.pugabyte.bncore.features.hours2;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.features.chat.Koda;
import me.pugabyte.bncore.models.hours2.Hours2;
import me.pugabyte.bncore.models.hours2.Hours2Service;
import me.pugabyte.bncore.models.nerd.Rank;
import me.pugabyte.bncore.utils.SoundUtils.Jingle;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.Utils.runConsoleCommand;

public class Hours2Feature {
	final int INTERVAL = 5;

	public Hours2Feature() {
		Tasks.repeatAsync(10, Time.SECOND.x(INTERVAL), () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				try {
					if (AFK.get(player).isAfk()) continue;

					Hours2Service service = new Hours2Service();
					Hours2 hours2 = service.get(player);
					hours2.increment(INTERVAL);
					service.update(hours2);

					if (Rank.getHighestRank(player) == Rank.GUEST) {
						if (hours2.getTotal() > (60 * 60 * 24)) {
							Tasks.sync(() -> {

								BNCore.log("[Hours2] Promoting " + player.getName() + " to Member");
								if (true) return;

								runConsoleCommand("lp user " + player.getName() + " parent set " + Rank.MEMBER.name());
								Koda.say("Congrats on Member rank, " + player.getName() + "!");
								Jingle.RANKUP.play(player);
								player.sendMessage("");
								player.sendMessage("");
								player.sendMessage(colorize("&e&lCongratulations! &3You have been promoted to &fMember&3 for " +
										"playing for &e24 hours &3in-game. You are now eligible for &c/trusted&3."));
								player.sendMessage("");
								player.sendMessage(colorize("&6&lThank you for flying Bear Nation!"));
							});
						}
					}
				} catch (Exception ex) {
					BNCore.warn("Error in Hours scheduler: " + ex.getMessage());
				}
			}
		});
	}

}
